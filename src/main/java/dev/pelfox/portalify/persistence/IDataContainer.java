package dev.pelfox.portalify.persistence;

import dev.pelfox.portalify.data.TeleportPortalData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDataContainer {
    boolean isDataExists(@NotNull Location location);
    @Nullable TeleportPortalData getData(@NotNull Location location);
    void saveData(@NotNull Location location, @NotNull TeleportPortalData data);
    void deleteData(@NotNull Location location);
    @NotNull List<TeleportPortalData> getPortalsData();
}
