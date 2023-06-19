package org.yoy.client;

import org.yoy.common.CommonConnectionThread;
import org.yoy.common.PacketAuthorization;
import org.yoy.common.PacketMessage;
import org.yoy.common.PacketRegistry;

import java.net.Socket;

public class Client {
//    static JFrame frame = new JFrame("Client");

    public static void main(String[] args) throws Exception {
        PacketRegistry.registerPackets();

//        frame.setPreferredSize(new Dimension(200, 100));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JLabel label = new JLabel("Connecting...");
//        frame.getContentPane().add(label);
//        frame.pack();
//
//        frame.setVisible(true);

        try (Socket clientSocket = new Socket("localhost",3333)) {
            ConnectionThread conn = new ConnectionThread(clientSocket);
            conn.start();
            conn.join();
        }
    }

    public static class ConnectionThread extends CommonConnectionThread {
        public ConnectionThread(Socket clientSocket) {
            super(clientSocket);
        }

        @Override
        public void run() {
            System.out.println("logging in");
            writePacket(new PacketMessage("hello"));
            writePacket(new PacketAuthorization("user", "password"));
            System.out.println("logged in");

            while (!socket.isClosed()) {
                try {
                    readPacket().processAsClient(this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
