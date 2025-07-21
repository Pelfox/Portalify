package dev.pelfox.portalify.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PortalDataType implements PersistentDataType<byte[], PortalData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PortalDataType.class);

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<PortalData> getComplexType() {
        return PortalData.class;
    }

    private void writeString(@NotNull ByteBuffer bb, @NotNull String string) {
        byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
        bb.putInt(stringBytes.length);
        bb.put(stringBytes);
    }

    private void writeLocation(@NotNull ByteBuffer bb, @NotNull Location location) {
        this.writeString(bb, location.getWorld().getName());
        bb.putDouble(location.getX());
        bb.putDouble(location.getY());
        bb.putDouble(location.getZ());
    }

    private <T> void writeOptional(@NotNull ByteBuffer bb, @Nullable T value) {
        if (value == null) {
            bb.put((byte) 0);
            return;
        }

        bb.put((byte) 1);
        switch (value) {
            case String string -> this.writeString(bb, string);
            case Location location -> this.writeLocation(bb, location);
            default -> throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }
    }

    private int estimateStringSize(@NotNull String string) {
        return 4 + string.getBytes(StandardCharsets.UTF_8).length;
    }

    private int estimateLocationSize(@NotNull Location location) {
        return this.estimateStringSize(location.getWorld().getName()) + (8 * 3);
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull PortalData complex, @NotNull PersistentDataAdapterContext context) {
        int nameSize = this.estimateStringSize(complex.getName());
        int originSize = this.estimateLocationSize(complex.getOrigin());

        int destinationSize = 1;
        if (complex.getDestination() != null) {
            destinationSize += this.estimateLocationSize(complex.getDestination());
        }

        ByteBuffer bb = ByteBuffer.allocate(nameSize + originSize + destinationSize);
        this.writeString(bb, complex.getName());
        this.writeLocation(bb, complex.getOrigin());
        this.writeOptional(bb, complex.getDestination());
        return bb.array();
    }

    @NotNull
    private String readString(@NotNull ByteBuffer bb) {
        if (bb.remaining() < 4) {
            throw new IllegalArgumentException("Insufficient data for string");
        }

        int length = bb.getInt();
        if (length < 0 || length > bb.remaining()) {
            throw new IllegalArgumentException("Invalid string length: " + length);
        }

        byte[] bytes = new byte[length];
        bb.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @NotNull
    private Location readLocation(@NotNull ByteBuffer bb) {
        String worldName = this.readString(bb);

        if (bb.remaining() < (8 * 3)) {
            throw new IllegalArgumentException("Insufficient data for location");
        }

        double x = bb.getDouble();
        double y = bb.getDouble();
        double z = bb.getDouble();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            LOGGER.warn("Deserialized portal references missing world: {}", worldName);
        }

        return new Location(world, x, y, z);
    }

    @Nullable
    private <T> T readOptional(@NotNull Class<T> clazz, @NotNull ByteBuffer bb) {
        if (bb.remaining() < 1) {
            throw new IllegalArgumentException("Insufficient data");
        }

        if (bb.get() == 0) {
            return null;
        }

        if (clazz == String.class) {
            return (T) this.readString(bb);
        } else if (clazz == Location.class) {
            return (T) this.readLocation(bb);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + clazz);
        }
    }

    @Override
    public @NotNull PortalData fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        if (primitive.length < 1) {
            throw new IllegalArgumentException("Insufficient data");
        }

        ByteBuffer bb = ByteBuffer.wrap(primitive);
        String name = this.readString(bb);
        Location origin = this.readLocation(bb);
        Location destination = this.readOptional(Location.class, bb);
        return new PortalData(name, origin, destination);
    }
}
