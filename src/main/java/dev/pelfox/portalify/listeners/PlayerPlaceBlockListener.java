package dev.pelfox.portalify.listeners;

import dev.pelfox.portalify.Portalify;
import dev.pelfox.portalify.animation.PortalExteriorAnimator;
import dev.pelfox.portalify.data.TeleportPortalData;
import dev.pelfox.portalify.persistence.WorldDataContainer;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPlaceBlockListener implements Listener {
    private final Portalify plugin;

    public PlayerPlaceBlockListener(@NotNull Portalify plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerPlaceBlock(@NotNull BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        if (block.getType() != Material.END_PORTAL_FRAME) {
            return;
        }

        WorldDataContainer dataContainer = new WorldDataContainer(block.getWorld());
        if (dataContainer.isDataExists(block.getLocation())) {
            return;
        }

        dataContainer.saveData(block.getLocation(), new TeleportPortalData("", NamedTextColor.WHITE, block.getLocation(), null));
        this.plugin.registerAnimator(new PortalExteriorAnimator(this.plugin,  block.getLocation()));
    }
}
