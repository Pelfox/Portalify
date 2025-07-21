package dev.pelfox.portalify.animation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface IPortalAnimator {
    void animate();
    void cancelAnimation();
    boolean isSamePortal(@NotNull Location portalLocation);
}
