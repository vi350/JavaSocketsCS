package org.yoy.server;

import io.github.cdimascio.dotenv.Dotenv;
import org.yoy.common.CommonConnectionThread;
import org.yoy.common.Packet;
import org.yoy.common.PacketRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// TODO: salt by id + login

public class Server {
    private static boolean accepting = true;
    private static Connection connection;

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();

        String url = "jdbc:postgresql://localhost:5433/" + dotenv.get("POSTGRESDB");
        String user = dotenv.get("POSTGRESUSER");
        String password = dotenv.get("POSTGRESPASSWORD");

        try {
            while (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        PacketRegistry.registerPackets();
        UserRegistry.loadUsers();

        new ListenerThread().start();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static class ListenerThread extends Thread {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(3333)) {
                while (accepting) {
                    Socket clientSocket = serverSocket.accept();
                    new ConnectionThread(clientSocket).start();
                    accepting = true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ConnectionThread extends CommonConnectionThread {
        private static UserRegistry.User user;
        public ConnectionThread(Socket clientSocket) {
            super(clientSocket);
        }

        public void setUser(UserRegistry.User user) {
            ConnectionThread.user = user;
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    Packet packet = readPacket();
                    packet.processAsServer(this);
                    if (user != null) {
                        System.out.println("already logged in");
                    } else System.out.println("still not logged in");
                } catch (Exception e) {
                    //TODO exception handling
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
