package org.yoy.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PacketPublicKey extends Packet {
    private PublicKey key;

    public PacketPublicKey() {
    }

    public PacketPublicKey(PublicKey key) {
        this.key = key;
    }

    public PublicKey getKey() {
        return key;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeInt(key.getEncoded().length);
        out.write(key.getEncoded());
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        try {
            byte[] bytes = new byte[in.readInt()];
            in.readFully(bytes);
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(bytes);
            key = KeyFactory.getInstance("RSA").generatePublic(keySpecX509);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
