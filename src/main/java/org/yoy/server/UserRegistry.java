package org.yoy.server;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class UserRegistry {
    public static final Map<String, User> users = new HashMap<>();

    public static void loadUsers() {

        String sql = "SELECT * FROM users";
        try (Statement statement = Server.getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String login = resultSet.getString("login");
                String hash = resultSet.getString("hash");
                login = login.substring(1).substring(0, login.length() - 2);
                hash = hash.substring(1).substring(0, hash.length() - 2);
                users.put(login, new User(login, hash));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User validateUser(String login, String password) {
        User user = users.get(login);
        if (user == null) return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            String hash = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();
            if (hash.equals(user.hash)) return user;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static boolean registerUser(String login, String password) {
        User user = users.get(login);
        if (user == null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                String hash = DatatypeConverter.printHexBinary(md.digest()).toLowerCase();


                String sql = "INSERT INTO users VALUES ('{" + login + "}', '{" + hash + " }');";
                try (Statement statement = Server.getConnection().createStatement()) {
                    statement.execute(sql);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                loadUsers();

                return true;
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    public static class User {
        public final String login;
        public final String hash;

        public User(String login, String hash) {
            this.login = login;
            this.hash = hash;
        }
    }
}
