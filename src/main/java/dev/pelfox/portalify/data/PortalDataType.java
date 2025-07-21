package dev.pelfox.portalify.data;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PortalDataType implements PersistentDataType<byte[], TeleportPortalData> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<TeleportPortalData> getComplexType() {
        return TeleportPortalData.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull TeleportPortalData complex, @NotNull PersistentDataAdapterContext context) {
        byte[] idBytes = complex.getId().getBytes(StandardCharsets.UTF_8);

        byte[] originWorldNameBytes = complex.getOrigin().getWorld().getName().getBytes(StandardCharsets.UTF_8);
        byte[] destinationWorldNameBytes = new byte[0];
        if (complex.getDestination() != null) {
            destinationWorldNameBytes = complex.getDestination().getWorld().getName().getBytes(StandardCharsets.UTF_8);
        }

        int totalBytes = (4 + idBytes.length) + (4 + originWorldNameBytes.length) + (4 + destinationWorldNameBytes.length) + 4 + 48;

        ByteBuffer bb = ByteBuffer.allocate(totalBytes);

        bb.putInt(idBytes.length);
        bb.put(idBytes);

        bb.putInt(complex.getColor().value()); // 4 bytes

        // origin
        bb.putInt(originWorldNameBytes.length);
        bb.put(originWorldNameBytes);

        bb.putDouble(complex.getOrigin().getX()); // 8 bytes
        bb.putDouble(complex.getOrigin().getY()); // 8 bytes
        bb.putDouble(complex.getOrigin().getZ()); // 8 bytes

        // destination
        bb.putInt(destinationWorldNameBytes.length);
        bb.put(destinationWorldNameBytes);
        if (complex.getDestination() != null) {
            bb.putDouble(complex.getDestination().getX());
            bb.putDouble(complex.getDestination().getY());
            bb.putDouble(complex.getDestination().getZ());
        } else {
            bb.putDouble(0);
            bb.putDouble(0);
            bb.putDouble(0);
        }

        return bb.array();
    }

    private String readString(@NotNull ByteBuffer bb) {
        int length = bb.getInt();
        if (length <= 0) return "";

        byte[] bytes = new byte[length];
        bb.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public @NotNull TeleportPortalData fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        ByteBuffer bb = ByteBuffer.wrap(primitive);

        String id = readString(bb);

        int color = bb.getInt();

        String originWorldName = readString(bb);
        double originX = bb.getDouble();
        double originY = bb.getDouble();
        double originZ = bb.getDouble();

        String destinationWorldName = readString(bb);
        double destinationX = bb.getDouble();
        double destinationY = bb.getDouble();
        double destinationZ = bb.getDouble();

        Location origin = new Location(
                Bukkit.getWorld(originWorldName),
                originX,
                originY,
                originZ
        );

        Location destination = null;

        // TODO: write a flag
        if (destinationX != 0 && destinationY != 0 && destinationZ != 0) {
            destination = new Location(
                    Bukkit.getWorld(destinationWorldName),
                    destinationX,
                    destinationY,
                    destinationZ
            );
        }

        return new TeleportPortalData(id, TextColor.color(color), origin, destination);
    }
}
