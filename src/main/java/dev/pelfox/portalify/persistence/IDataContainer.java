package dev.pelfox.portalify.persistence;

import dev.pelfox.portalify.data.PortalData;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDataContainer {
    boolean isDataExists(@NotNull Location location);
    @Nullable PortalData getData(@NotNull Location location);
    void saveData(@NotNull Location location, @NotNull PortalData data);
    void deleteData(@NotNull Location location);
    @NotNull List<PortalData> getPortalsData();
}
