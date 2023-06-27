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

import org.json.JSONException;
import org.json.JSONObject;

import com.chattingapplication.model.Account;
import com.chattingapplication.model.ChatRoom;
import com.chattingapplication.model.Request;
import com.chattingapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class ServerService {
    public static ArrayList<ClientHandleService> clientHandlers = new ArrayList<>();
    public static ArrayList<String> inProcessApi = new ArrayList<>();

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

    public static List<String> checkRaceCondition(String action, JSONObject requestJson) {
        return inProcessApi.stream().filter(s -> {
            JSONObject checkObject = new JSONObject(s);
            System.out.println(requestJson);
            return (checkObject.getString("createUser").equals(requestJson.getString("targetUser")) &&
            checkObject.getString("targetUser").equals(requestJson.getString("createUser")) &&
            checkObject.getString("action").equals(action));
        }).toList();
    }

    public static String createPrivateRoomRequest(ClientHandleService clientHandleService, String jsonString) throws IOException, InterruptedException {
        Gson gson = new Gson();
        JSONObject requestObject = new JSONObject(jsonString);

        List<String> filterList = checkRaceCondition("createPrivateRoom", requestObject);
        User createUser = gson.fromJson(requestObject.getString("createUser"), User.class);
        User targetUser  = gson.fromJson(requestObject.getString("targetUser"), User.class);

        String checkRoom = RequestService.getRequest(String.format("chat_room/%d/%d/true", createUser.getId(), targetUser.getId()));
        try {
            ChatRoom checkChatRoom = gson.fromJson(checkRoom, ChatRoom.class);
            System.out.println(checkRoom);
            return checkRoom;
        } catch (JsonSyntaxException ex) {
            if (filterList.size() == 0) {
                String api;
                try {
                    api = new JSONObject()
                        .put("action", "createPrivateRoom")
                        .put("createUser", requestObject.getString("createUser"))
                        .put("targetUser", requestObject.getString("targetUser"))
                        .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                inProcessApi.add(api);
                String responseBody = RequestService.postRequest(String.format("chat_room/create_private_room/%d/%d", createUser.getId(), targetUser.getId()), "");
                inProcessApi.remove(api);
                System.out.println(responseBody);
                return responseBody;
            } else {
                // loop until the api done
                do {
                    filterList = checkRaceCondition("createPrivateRoom", requestObject);
                } while (filterList.size() == 0);            
                String response = RequestService.getRequest(String.format("chat_room/%d/%d/true", createUser.getId(), targetUser.getId()));
                System.out.println(response);
                return response;
            }            
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
