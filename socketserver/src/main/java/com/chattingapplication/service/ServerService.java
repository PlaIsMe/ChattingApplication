package com.chattingapplication.service;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

import com.chattingapplication.model.Account;
import com.chattingapplication.model.ChatRoom;
import com.chattingapplication.model.Request;
import com.chattingapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

    public static void socketSend(ClientHandleService clientHandleService, String responseName, String responseParam, String responseClass) {
        String message;
        try {
            message = new JSONObject()
                    .put("responseFunction", responseName)
                    .put("responseParam", responseParam)
                    .put("responseClass", responseClass)
                    .toString();
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
    }

    public static void chattingRequest(ClientHandleService clientHandleService, String message) {
        for (ClientHandleService client: clientHandlers) {
            if (client.getClientSocket() != clientHandleService.getClientSocket()) {
                // socketSend(client, "chattingResponse", clientHandleService.getClientAccount().getEmail() + ": " + message);
            }
        }
    }

    public static void registerRequest(ClientHandleService clientHandleService, String jsonString) throws IOException, InterruptedException {
        String response = RequestService.postRequest("account/signup", jsonString);
        Gson gson = new Gson();
        try {
            Account currentAccount = gson.fromJson(response, Account.class);
            clientHandleService.setClientAccount(currentAccount);
        } catch (JsonSyntaxException e) {

        }
        ServerService.socketSend(clientHandleService, "registerResponse", response, "RegisterFragment");
    }

    public static void loginRequest(ClientHandleService clientHandleService, String jsonString) throws IOException, InterruptedException {
        String response = RequestService.postRequest("account/signin", jsonString);
        Gson gson = new Gson();
        try {
            Account currentAccount = gson.fromJson(response, Account.class);
            clientHandleService.setClientAccount(currentAccount);
        } catch (JsonSyntaxException e) {

        }
        ServerService.socketSend(clientHandleService, "loginResponse", response, "LoginFragment");
    }

    public static String createPrivateRoomRequest(ClientHandleService clientHandleService, String jsonString) throws IOException, InterruptedException {
        Gson gson = new Gson();
        JSONObject requestObject = new JSONObject(jsonString);
        User createUser = gson.fromJson(requestObject.getString("createUser"), User.class);
        User targetUser  = gson.fromJson(requestObject.getString("targetUser"), User.class);
        try {
            ClientHandleService targetClient = clientHandlers.stream().filter(c -> c.getClientAccount().getUser().getId().equals(targetUser.getId())).findFirst().get();
            ClientHandleService firstClient = (clientHandleService.getClientAccount().getUser().getId() < targetClient.getClientAccount().getUser().getId() ? clientHandleService: targetClient);
            ClientHandleService secondClient = (clientHandleService.getClientAccount().getUser().getId() < targetClient.getClientAccount().getUser().getId() ? targetClient: clientHandleService);
            return synchronizedCreatePrivateRoom(firstClient, secondClient);
        } catch (NoSuchElementException | NullPointerException e) {
            String responseBody = createPrivateRoom(createUser.getId(), targetUser.getId());
            System.out.println(responseBody);
            return responseBody;
        }
    }

    public static String synchronizedCreatePrivateRoom(ClientHandleService firstClient, ClientHandleService secondClient) throws IOException, InterruptedException {
        synchronized (firstClient) {
            synchronized (secondClient) {
                return createPrivateRoom(firstClient.getClientAccount().getUser().getId(), secondClient.getClientAccount().getUser().getId());
            }
        }
    }

    public static String createPrivateRoom(Long firstId, Long secondId) throws IOException, InterruptedException {
        String checkRoom = RequestService.getRequest(String.format("chat_room/%d/%d/true", firstId, secondId));
        try {
            Gson gson = new Gson();
            ChatRoom checkChatRoom = gson.fromJson(checkRoom, ChatRoom.class);
            return checkRoom;
        } catch (JsonSyntaxException ex) {
            String responseBody = RequestService.postRequest(String.format("chat_room/create_private_room/%d/%d", firstId, secondId), "");
            return responseBody;
        }
    }

    public static void handleRequest(ClientHandleService clientHandleService, String jsonRequest) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        System.out.println(jsonRequest);
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
