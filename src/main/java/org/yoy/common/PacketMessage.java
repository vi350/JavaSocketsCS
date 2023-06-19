package org.yoy.common;

import org.yoy.client.Client;
import org.yoy.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketMessage extends Packet{
    private String message;

    public PacketMessage() {
    }

    public PacketMessage(String message) {
        this.message = message;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(message);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        message = in.readUTF();
    }

    @Override
    public void processAsServer(Server.ConnectionThread conn) {
        System.out.println(message);
    }

    @Override
    public void processAsClient(Client.ConnectionThread conn) {
        System.out.println(message);
    }
}
