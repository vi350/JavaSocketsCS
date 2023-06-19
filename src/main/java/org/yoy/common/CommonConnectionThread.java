package org.yoy.common;

import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.*;

public abstract class CommonConnectionThread extends Thread {
    protected Socket socket;
    protected DataOutputStream out;
    protected DataInputStream in;
    private PrivateKey privateKey;
    private PublicKey otherSidePublicKey;

    public CommonConnectionThread(Socket clientSocket) {
        socket = clientSocket;
        try {
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());

            // Generate key pair
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            // Send public key to other side
            Packet packet = new PacketPublicKey(publicKey);
            out.writeInt(PacketRegistry.getIdByPacket(packet));
            packet.write(out);

            // Receive public key from other side
            while(otherSidePublicKey == null) {
                packet = PacketRegistry.getPacketById(in.readInt());
                packet.read(in);
                if (packet instanceof PacketPublicKey) {
                    otherSidePublicKey = ((PacketPublicKey) packet).getKey();
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writePacket(Packet packet) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            packet.write(new DataOutputStream(bytes));
            byte[] encrypted = encrypt(bytes.toByteArray());

            out.writeInt(PacketRegistry.getIdByPacket(packet));
            out.writeInt(encrypted.length);
            out.write(encrypted);
            bytes.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Packet readPacket() {
        try {
            Packet packet = PacketRegistry.getPacketById(in.readInt());
            int kek = in.readInt();
            // if (kek > 500) throw new RuntimeException(""); TODO: проверка на размер пакета
            byte[] encrypted = new byte[kek];
            in.readFully(encrypted);
            packet.read(new DataInputStream(new ByteArrayInputStream(decrypt(encrypted))));
            return packet;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] encrypt(byte[] notEncrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, otherSidePublicKey);
            return cipher.doFinal(notEncrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected byte[] decrypt(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
