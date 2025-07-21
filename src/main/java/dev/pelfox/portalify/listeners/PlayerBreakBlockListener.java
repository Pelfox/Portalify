package dev.pelfox.portalify.listeners;

import dev.pelfox.portalify.Portalify;
import dev.pelfox.portalify.animation.PortalDestinationAnimator;
import dev.pelfox.portalify.persistence.WorldDataContainer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerBreakBlockListener implements Listener {
    private final Portalify plugin;

    public PlayerBreakBlockListener(@NotNull Portalify plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerBreakBlock(@NotNull BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != org.bukkit.Material.END_PORTAL_FRAME) {
            return;
        }

        WorldDataContainer dataContainer = new WorldDataContainer(block.getWorld());
        if (!dataContainer.isDataExists(block.getLocation())) {
            return;
        }

        dataContainer.deleteData(block.getLocation());
        new PortalDestinationAnimator(this.plugin, block.getLocation(), "portal_destination_animator").removeAnimationEntity();
        this.plugin.getAnimatorsForPortal(block.getLocation()).forEach(animator -> {
            animator.cancelAnimation();
            this.plugin.removeAnimator(animator);
        });
    }
}
