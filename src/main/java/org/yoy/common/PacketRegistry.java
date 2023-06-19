package org.yoy.common;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    private static int nextPacketId = 0;
    private static final Map<Integer, Class<? extends Packet>> ID_TO_PACKET = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> PACKET_TO_ID = new HashMap<>();
    public static void registerPacket(Class<? extends Packet> packet) {
        ID_TO_PACKET.put(nextPacketId, packet);
        PACKET_TO_ID.put(packet, nextPacketId);
        nextPacketId++;
    }
    public static void registerPackets() {
        registerPacket(PacketMessage.class);
        registerPacket(PacketAuthorization.class);
        registerPacket(PacketAuthorizationResult.class);
        registerPacket(PacketRegistration.class);
        registerPacket(PacketRegistrationResult.class);
        registerPacket(PacketPublicKey.class);
    }
    public static Packet getPacketById(int id) {
        try {
            return ID_TO_PACKET.get(id).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static int getIdByPacket(Packet packet) {
        return PACKET_TO_ID.get(packet.getClass());
    }
}
