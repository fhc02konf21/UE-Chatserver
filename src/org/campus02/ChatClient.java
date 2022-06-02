package org.campus02;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatClient implements Runnable {

    private BufferedReader br;
    private PrintWriter pw;
    private Socket client;
    private ArrayList<ChatClient> chatClients;
    private String name = "";
    private Logger logger;
    private HashMap<String, ChatClient> chatClientsMap;

    public ChatClient(Socket client,
                      ArrayList<ChatClient> chatClients,
                      Logger logger,
                      HashMap<String, ChatClient> chatClientsMap) {
        this.client = client;
        this.chatClients = chatClients;
        this.logger = logger;
        this.chatClientsMap = chatClientsMap;
        try {
            this.br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
            chatClients.add(this);
        } catch (IOException e) {
            this.close();
        }
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) {
        pw.println(message);
        pw.flush();
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = br.readLine()) != null) {
                final String[] cmds = input.split(":");
                switch (cmds[0]) {
                    case "name":
                        String clientName = cmds[1];
                        if (chatClientsMap.containsKey(clientName)) {
                            sendMessage("name already exists");
                        } else {
                            this.name = clientName;
                            chatClientsMap.put(clientName, this);
                            for (ChatClient chatClient : chatClients) {
                                chatClient.sendMessage("New client connected: " + clientName);
                            }
                        }
                        break;
                    case "msg":
                        String msg = cmds[1];
                        for (ChatClient chatClient : chatClients) {
                            if (chatClient != this) {
                                chatClient.sendMessage(msg);
                            }
                        }
                        logger.writeLogEntry(msg);
                        break;
                    case "msgto":
                        String recipient = cmds[1];
                        String message = cmds[2];

                        // finde client in der liste
//                        for (ChatClient chatClient : chatClients) {
//                            if (chatClient.getName().equalsIgnoreCase(recipient)) {
//                                chatClient.sendMessage(message);
//                            }
//                        }

                        if (chatClientsMap.containsKey(recipient)) {
                            chatClientsMap.get(recipient).sendMessage(message);
                        }

                        logger.writeLogEntry(message);
                        break;
                    case "list":
                        for (ChatClient chatClient : chatClients) {
                            sendMessage(chatClient.getName());
                        }
                        break;
                    case "bye":
                        client.close();
                        break;
                    default:
                        pw.println("wrong command");
                        pw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.pw.close();
            this.br.close();
            chatClients.remove(this);
            chatClientsMap.remove(this.name);
            for (ChatClient chatClient : chatClients) {
                chatClient.sendMessage(this.name + " leaves the chat");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
