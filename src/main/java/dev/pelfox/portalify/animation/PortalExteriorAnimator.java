package dev.pelfox.portalify.animation;

import dev.pelfox.portalify.Portalify;
import dev.pelfox.portalify.utils.ParticleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class PortalExteriorAnimator implements IPortalAnimator{
    public static final double EXTERIOR_RADIUS = 0.5;
    public static final int PARTICLE_COUNT = 10;

    private BukkitTask animationTask;

    private final Portalify plugin;
    private final Location portalLocation;

    public PortalExteriorAnimator(@NotNull Portalify plugin, @NotNull Location portalLocation) {
        this.plugin = plugin;
        this.portalLocation = portalLocation;
    }

    @Override
    public void animate() {
        if (this.animationTask != null) {
            return;
        }
        this.animationTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () ->
                ParticleUtils.drawCircle(PARTICLE_COUNT, 2, Particle.PORTAL, EXTERIOR_RADIUS, this.portalLocation), 0L, 20L);
    }

    @Override
    public void cancelAnimation() {
        this.animationTask.cancel();
        this.animationTask = null;
    }

    @Override
    public boolean isSamePortal(@NotNull Location portalLocation) {
        return this.portalLocation.equals(portalLocation);
    }
}
