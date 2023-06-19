package org.yoy.common;

import org.yoy.client.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketAuthorizationResult extends Packet {
    private int result;

    public PacketAuthorizationResult() {
    }

    public PacketAuthorizationResult(int result) {
        this.result = result;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(result);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        result = in.readInt();
    }

    @Override
    public void processAsClient(Client.ConnectionThread conn) {
        System.out.println(result);
    }
}
