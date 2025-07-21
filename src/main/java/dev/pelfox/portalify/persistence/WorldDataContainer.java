package dev.pelfox.portalify.persistence;

import dev.pelfox.portalify.utils.KeyBuilderUtils;
import dev.pelfox.portalify.data.PortalDataType;
import dev.pelfox.portalify.data.TeleportPortalData;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldDataContainer implements IDataContainer {
    private final PersistentDataContainer container;

    public WorldDataContainer(@NotNull World world) {
        this.container = world.getPersistentDataContainer();
    }

    @Override
    public boolean isDataExists(@NotNull Location location) {
        return this.container.has(KeyBuilderUtils.keyFromLocation(location));
    }

    @Override
    public @Nullable TeleportPortalData getData(@NotNull Location location) {
        return this.container.get(KeyBuilderUtils.keyFromLocation(location), new PortalDataType());
    }

    @Override
    public void saveData(@NotNull Location location, @NotNull TeleportPortalData data) {
        this.container.set(KeyBuilderUtils.keyFromLocation(location), new PortalDataType(), data);
    }

    @Override
    public void deleteData(@NotNull Location location) {
        // TODO: unlink all portals that are linked to this one
        this.container.remove(KeyBuilderUtils.keyFromLocation(location));
    }

    @Override
    public @NotNull List<TeleportPortalData> getPortalsData() {
        List<TeleportPortalData> portalsData = new ArrayList<>();
        for (NamespacedKey key : this.container.getKeys()) {
            if (!KeyBuilderUtils.isValidKey(key)) {
                continue;
            }
            portalsData.add(this.container.get(key, new PortalDataType()));
        }
        return portalsData;
    }
}
