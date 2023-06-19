package org.yoy.common;

import org.yoy.client.Client;
import org.yoy.server.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {
    public abstract void write(DataOutputStream out) throws IOException;
    public abstract void read(DataInputStream in) throws IOException;
    public void processAsServer(Server.ConnectionThread conn) {}
    public void processAsClient(Client.ConnectionThread conn) {}
}
