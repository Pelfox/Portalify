package dev.pelfox.portalify.listeners;

import dev.pelfox.portalify.animation.PortalTeleportAnimator;
import dev.pelfox.portalify.data.TeleportPortalData;
import dev.pelfox.portalify.persistence.WorldDataContainer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMoveListener implements Listener {
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        final Location location = player.getLocation().subtract(0, 1, 0);
        if (!location.getBlock().getType().equals(Material.END_PORTAL_FRAME)) {
            return;
        }

        if (this.cooldowns.getOrDefault(player.getUniqueId(), 0L) > new Date().getTime()) {
            // TODO: show cooldown message to player
            return;
        }

        WorldDataContainer dataContainer = new WorldDataContainer(player.getWorld());
        TeleportPortalData portalData = dataContainer.getData(location);

        if (portalData == null || portalData.getDestination() == null) {
            return;
        }

        Location destination = portalData.getDestination();
        player.teleportAsync(destination.clone().add(0.5, 1.0, 0.5)).thenAccept(isTeleported -> {
            if (isTeleported) {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                this.cooldowns.put(player.getUniqueId(), new Date().getTime() + 5000);

                new PortalTeleportAnimator(destination).animate(); // TODO: through manager
            }
        });
    }
}
