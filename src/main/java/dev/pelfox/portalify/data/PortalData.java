package dev.pelfox.portalify.data;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PortalData {
    private String name;
    private final Location origin;
    private @Nullable Location destination;

    public PortalData(@NotNull String name, @NotNull Location origin, @Nullable Location destination) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public Location getOrigin() {
        return this.origin;
    }

    @Nullable
    public Location getDestination() {
        return this.destination;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setDestination(@Nullable Location destination) {
        this.destination = destination;
    }
}
