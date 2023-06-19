package org.yoy.common;

import org.yoy.server.Server;
import org.yoy.server.UserRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketRegistration extends Packet {
    private String login;
    private String password;

    public PacketRegistration() {
    }

    public PacketRegistration(String login, String password) {
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
        // TODO: проверка на размер
        login = in.readUTF();
        password = in.readUTF();
    }

    @Override
    public void processAsServer(Server.ConnectionThread conn) {
        boolean registered = UserRegistry.registerUser(login, password);
        // TODO: no enums?
        if (registered)conn.writePacket(new PacketRegistrationResult(200));
        else conn.writePacket(new PacketAuthorizationResult(404));
    }
}
