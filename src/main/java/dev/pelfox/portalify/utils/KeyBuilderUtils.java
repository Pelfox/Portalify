package dev.pelfox.portalify.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for serializing and deserializing locations to and from
 * {@link NamespacedKey} objects.
 */
public final class KeyBuilderUtils {
    // TODO: implement caching for created keys to avoid creating new ones every time

    /**
     * Creates a {@link NamespacedKey} from the given {@link Location}.
     *
     * @param location the Location to convert
     * @return the NamespacedKey created from the location
     */
    @NotNull
    public static NamespacedKey keyFromLocation(@NotNull Location location) {
        String formattedLocation = String.format("%s_%d_%d_%d", location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return new NamespacedKey("portalify", formattedLocation);
    }

    /**
     * Parses the location from the given {@link NamespacedKey}.
     *
     * @param key the NamespacedKey to parse
     * @return the Location object created from the key
     * @throws IllegalArgumentException if the key format is invalid
     */
    @NotNull
    public static Location locationFromKey(@NotNull NamespacedKey key) {
        String[] locationParts = key.getKey().split("_");
        if (locationParts.length != 4) {
            throw new IllegalArgumentException("Invalid key format: " + key);
        }
        World world = Bukkit.getWorld(locationParts[0]);
        if (world == null) {
            throw new IllegalArgumentException("World not found: " + locationParts[0]);
        }
        return new Location(world, Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2]), Double.parseDouble(locationParts[3]));
    }

    /**
     * Checks if the given {@link NamespacedKey} is valid.
     *
     * @param key the NamespacedKey to check
     * @return true if the key is valid, false otherwise
     */
    public static boolean isValidKey(@NotNull NamespacedKey key) {
        String[] locationParts = key.getKey().split("_");
        return key.namespace().equals("portalify") && locationParts.length == 4;
    }
}
