package com.chattingapplication.service;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chattingapplication.model.Account;
import com.chattingapplication.model.ChatRoom;
import com.chattingapplication.model.Message;
import com.chattingapplication.model.Request;
import com.chattingapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ServerService {
    public static ArrayList<ClientHandleService> clientHandlers = new ArrayList<>();

    public static String socketReceive(ClientHandleService clientHandleService) {
        try {
            String bufferIn = clientHandleService.getdIn().readUTF();
            System.out.printf("RECEIVED: %s\n", bufferIn);
            return bufferIn;
        } catch (EOFException e) {
            clientHandleService.closeClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void socketSend(ClientHandleService clientHandleService, String responseName, String responseParam) {
        // System.out.printf("\n======\nDEBUG SOCKET SEND: \n%s\n%s\n%s", clientHandleService.getClientAccount().toString(), responseName, responseParam);
        String message;
        try {
            message = new JSONObject()
                    .put("responseFunction", responseName)
                    .put("responseParam", responseParam)
                    .toString();
            // System.out.printf("DEBUG MESSAGE %s\n", message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            System.out.printf("SENT: %s\n", message);
            clientHandleService.getdOut().writeUTF(message);
            clientHandleService.getdOut().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("DEBUG SEND DONE ");
    }

    public static void chattingRequest(ClientHandleService clientHandleService, String jsonString) throws JSONException, IOException, InterruptedException {
        JSONObject requestObject = new JSONObject(jsonString);
        saveMessage(clientHandleService, requestObject.getString("content"), requestObject.getLong("roomId"), requestObject.getLong("userId"));
    }

    public static void sendMessage(ClientHandleService clientHandleService, String messageJson) {
        Gson gson = new Gson();
        Message message = gson.fromJson(messageJson, Message.class);
        clientHandlers.stream()
        .filter(c -> c.getClientSocket() != clientHandleService.getClientSocket() &&
        c.getClientAccount().getUser() != null &&
        c.getClientAccount().getUser().getChatRooms().contains(message.getChatRoom()))
        .forEach(client -> {
            socketSend(client, "chattingResponse", messageJson);
        });
    }

    public static void updateRequest(ClientHandleService clientHandleService, String accountJson) throws JsonSyntaxException, IOException, InterruptedException {
        Gson gson = new Gson();
        clientHandleService.setClientAccount(gson.fromJson(accountJson, Account.class));
        updateCurrentClient(clientHandleService);
        System.out.printf("DEBUG: %s\n", clientHandleService.getClientAccount().toString());
        socketSend(clientHandleService, "updateResponse", gson.toJson(clientHandleService.getClientAccount()));
        onlineClient(clientHandleService);
        realTimeActiveStatus(clientHandleService, true);
    }

    public static void realTimeActiveStatus(ClientHandleService clientHandleService, boolean isOnline) {
        Gson gson = new Gson();
        clientHandlers.stream()
        .filter(c -> c.getClientSocket() != clientHandleService.getClientSocket() &&
        c.getClientAccount().getUser() != null)
        .forEach(client -> {
            socketSend(client, (isOnline ? "updateOnlineUser": "updateOfflineUser"), gson.toJson(clientHandleService.getClientAccount().getUser().getId()));
        });
    }

    public static void onlineClient(ClientHandleService clientHandleService) {
        Gson gson = new Gson();
        List<Long> onlineUsers = clientHandlers.stream()
        .filter(c -> c.getClientSocket() != clientHandleService.getClientSocket() &&
        c.getClientAccount().getUser() != null)
        .map(client -> client.getClientAccount().getUser().getId())
        .collect(Collectors.toList());
        socketSend(clientHandleService, "onlineUsers", gson.toJson(onlineUsers));
        System.out.println(onlineUsers);
    }

    public static void updateCurrentClient(ClientHandleService clientHandleService) throws JsonSyntaxException, IOException, InterruptedException {
        Gson gson = new Gson();
        String roomJson = RequestService.getRequest(String.format("chat_room/%d", clientHandleService.getClientAccount().getUser().getId()));
        JSONArray jsonArray = new JSONArray(roomJson);
        List<ChatRoom> listChat = gson.fromJson(jsonArray.toString(), new TypeToken<List<ChatRoom>>() {}.getType());
        clientHandleService.getClientAccount().getUser().setChatRooms(listChat);
    }

    public static String createPrivateRoomRequest(ClientHandleService clientHandleService, String jsonString) throws IOException, InterruptedException {
        Gson gson = new Gson();
        JSONObject requestObject = new JSONObject(jsonString);
        User createUser = gson.fromJson(requestObject.getString("createUser"), User.class);
        User targetUser  = gson.fromJson(requestObject.getString("targetUser"), User.class);
        String firstMessage = requestObject.getString("message");
        String newChatRoom;
        try {
            ClientHandleService targetClient = clientHandlers.stream().filter(c -> c.getClientAccount().getUser().getId().equals(targetUser.getId())).findFirst().get();
            ClientHandleService firstClient = (clientHandleService.getClientAccount().getUser().getId() < targetClient.getClientAccount().getUser().getId() ? clientHandleService: targetClient);
            ClientHandleService secondClient = (clientHandleService.getClientAccount().getUser().getId() < targetClient.getClientAccount().getUser().getId() ? targetClient: clientHandleService);
            newChatRoom = synchronizedCreatePrivateRoom(targetUser, createUser, clientHandleService, firstClient, secondClient, firstMessage);
            updateCurrentClient(targetClient);
            String responseRoom = RequestService.getRequest(String.format("chat_room/%d/%d/true", targetUser.getId(), createUser.getId()));
            socketSend(targetClient, "createPrivateRoomResponse", responseRoom);
        } catch (NoSuchElementException | NullPointerException e) {
            newChatRoom = createPrivateRoom(targetUser, createUser, clientHandleService, createUser.getId(), targetUser.getId(), firstMessage);
        }
        updateCurrentClient(clientHandleService);
        return "";
    }

    public static String synchronizedCreatePrivateRoom(User targetUser, User createUser, ClientHandleService clientHandleService ,ClientHandleService firstClient, ClientHandleService secondClient, String firstMessage) throws IOException, InterruptedException {
        synchronized (firstClient) {
            synchronized (secondClient) {
                return createPrivateRoom(targetUser, createUser, clientHandleService ,firstClient.getClientAccount().getUser().getId(), secondClient.getClientAccount().getUser().getId(), firstMessage);
            }
        }
    }

    public static String createPrivateRoom(User targetUser, User createUser, ClientHandleService clientHandleService, Long firstId, Long secondId, String firstMessage) throws IOException, InterruptedException {
        String checkRoom = RequestService.getRequest(String.format("chat_room/%d/%d/true", firstId, secondId));
        Gson gson = new Gson();
        try {
            ChatRoom checkChatRoom = gson.fromJson(checkRoom, ChatRoom.class);
            socketSend(clientHandleService, "createPrivateRoomResponse", checkRoom);
            saveMessage(clientHandleService, firstMessage, checkChatRoom.getId(), createUser.getId());
            return checkRoom;
        } catch (JsonSyntaxException ex) {
            String messageJson;
            try {
                messageJson = new JSONObject()
                        .put("content", firstMessage)
                        .toString();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            String responseBody = RequestService.postRequest(String.format("chat_room/create_private_room/%d/%d", createUser.getId(), targetUser.getId()), messageJson);
            socketSend(clientHandleService, "createPrivateRoomResponse", responseBody);
            return responseBody;
        }
    }

    public static void saveMessage(ClientHandleService clientHandleService, String content, Long roomId, Long userId) throws IOException, InterruptedException {
        String message;
        try {
            message = new JSONObject()
                .put("content", content)
                .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String messageResponse = RequestService.postRequest(String.format("message/%d/%d", roomId, userId), message);
        sendMessage(clientHandleService, messageResponse);
    }

    public static void handleRequest(ClientHandleService clientHandleService, String jsonRequest) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        Gson gson = new Gson();
        Request request = gson.fromJson(jsonRequest, Request.class);
        Method method = ServerService.class.getMethod(request.getRequestFunction(), ClientHandleService.class ,String.class);
        method.invoke(null, clientHandleService, request.getRequestParam());
    }

    public static void handleConnect(ServerSocket serverSocket) throws IOException {
        System.out.println("Server started!");
        // The server socket always wait for new client socket incoming
        // to start new client socket
        while (!serverSocket.isClosed()) {
            Socket clientSocket = serverSocket.accept();
            ClientHandleService clientHandleService = new ClientHandleService(clientSocket);
            clientHandlers.add(clientHandleService);
            Thread thread = new Thread(clientHandleService);
            thread.start();
        }
    }

    public static void removeClient(ClientHandleService clientHandleService) {
        clientHandleService.closeClientSocket();
        ServerService.clientHandlers.remove(clientHandleService);
    }


    public static void shutDownServer(ServerSocket serverSocket) throws IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }
}
