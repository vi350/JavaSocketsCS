package org.yoy.common;

import org.yoy.server.Server;
import org.yoy.server.UserRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketAuthorization extends Packet {
    private String login;
    private String password;

    public PacketAuthorization() {
    }

    public PacketAuthorization(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(login);
        out.writeUTF(password);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        login = in.readUTF();
        password = in.readUTF();
    }

    @Override
    public void processAsServer(Server.ConnectionThread conn) {
        UserRegistry.User user = UserRegistry.validateUser(login, password);
        if (user != null) {
            conn.writePacket(new PacketAuthorizationResult(200));
            conn.setUser(user);
        } else conn.writePacket(new PacketAuthorizationResult(404));
    }
}
