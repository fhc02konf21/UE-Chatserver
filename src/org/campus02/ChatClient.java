package org.campus02;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClient implements Runnable {

    private BufferedReader br;
    private PrintWriter pw;
    private Socket client;
    private ArrayList<ChatClient> chatClients;
    private String name;

    public ChatClient(Socket client, ArrayList<ChatClient> chatClients) throws IOException {
        this.client = client;
        this.chatClients = chatClients;
        this.br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.pw = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
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
                        this.name = cmds[1];
                        break;
                    case "msg":
                        for (ChatClient chatClient : chatClients) {
                            chatClient.sendMessage(cmds[1]);
                        }
                        break;
                    case "msgto":
                        for (ChatClient chatClient : chatClients) {
                            if (chatClient.getName().equalsIgnoreCase(cmds[1])) {
                                chatClient.sendMessage(cmds[2]);
                            }
                        }
                        break;
                    case "bye":
                        close();
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
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
