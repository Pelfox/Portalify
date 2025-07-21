package dev.pelfox.portalify.animation;

import dev.pelfox.portalify.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.jetbrains.annotations.NotNull;

public class PortalTeleportAnimator implements IPortalAnimator{
    private final Location portalLocation;

    public PortalTeleportAnimator(@NotNull Location portalLocation) {
        this.portalLocation = portalLocation;
    }

    @Override
    public void animate() {
        ParticleUtils.drawCircle(30, 5, Particle.REVERSE_PORTAL, 0.5, this.portalLocation);
    }

    @Override
    public void cancelAnimation() {
        throw new UnsupportedOperationException("PortalTeleportAnimator doesn't support canceling animation.");
    }

    @Override
    public boolean isSamePortal(@NotNull Location portalLocation) {
        return this.portalLocation.equals(portalLocation);
    }
}
