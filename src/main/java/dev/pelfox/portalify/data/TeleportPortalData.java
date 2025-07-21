package dev.pelfox.portalify.data;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeleportPortalData {
//    @NotNull String id, @NotNull TextColor color, @NotNull Location origin, @Nullable Location destination;

    private String id;
    private TextColor color;
    private Location origin;
    private @Nullable Location destination;

    public TeleportPortalData(@NotNull String id, @NotNull TextColor color, @NotNull Location origin, @Nullable Location destination) {
        this.id = id;
        this.color = color;
        this.origin = origin;
        this.destination = destination;
    }

    public String getId() {
        return id;
    }

    public TextColor getColor() {
        return color;
    }

    public Location getOrigin() {
        return origin;
    }

    public @Nullable Location getDestination() {
        return destination;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public void setDestination(@Nullable Location destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TeleportPortalData{");
        sb.append("id='").append(id).append('\'');
        sb.append(", color=").append(color);
        sb.append(", origin=").append(origin);
        sb.append(", destination=").append(destination);
        sb.append('}');
        return sb.toString();
    }
}
