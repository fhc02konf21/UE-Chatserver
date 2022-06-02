package org.campus02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatServer {

    public static void main(String[] args) throws IOException {
        ArrayList<ChatClient> chatClients = new ArrayList<>();
        HashMap<String, ChatClient> chatClientsMap = new HashMap<>();

        System.out.println("start serer on port 1111");
        final Logger logger = new Logger("log/logfile.log");
        try (ServerSocket server = new ServerSocket(1111)) {
            while (true) {
                System.out.println("wait for client...");
                Socket client = server.accept();
                System.out.println("client connected");
                final ChatClient chatClient = new ChatClient(client, chatClients, logger, chatClientsMap);
                new Thread(chatClient).start();
            }
        }
    }
}
