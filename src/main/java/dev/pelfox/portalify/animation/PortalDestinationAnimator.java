package dev.pelfox.portalify.animation;

import dev.pelfox.portalify.Portalify;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

public class PortalDestinationAnimator implements IPortalAnimator {
    private final Portalify plugin;
    private Entity destinationText;

    private final Location portalLocation;
    private String destinationPortalId;

    public PortalDestinationAnimator(@NotNull Portalify plugin, @NotNull Location portalLocation, @NotNull String destinationPortalId) {
        this.plugin = plugin;
        this.portalLocation = portalLocation;
        this.destinationPortalId = destinationPortalId;
    }

    public void setDestinationPortalId(@NotNull String destinationPortalId) {
        this.destinationPortalId = destinationPortalId;
        // re-animate
        this.cancelAnimation();
        this.animate();
    }

    public void removeAnimationEntity() {
        Location destinationTextLocation = this.portalLocation.clone().add(0.5, 1.0, 0.5);
        destinationTextLocation.getNearbyEntities(2, 2, 2).stream()
                .filter(entity -> entity instanceof TextDisplay)
                .forEach(Entity::remove);
    }

    @Override
    public void animate() {
        if (this.destinationText != null) {
            return;
        }

        Location destinationTextLocation = this.portalLocation.clone().add(0.5, 1.0, 0.5);
        this.removeAnimationEntity();

        this.destinationText = this.portalLocation.getWorld().spawn(destinationTextLocation, TextDisplay.class, entity -> {
            entity.text(Component.translatable("portals.animation.destination", Component.text(this.destinationPortalId)));
            entity.setBillboard(Display.Billboard.VERTICAL);
            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
            entity.setSeeThrough(false);
        });

        this.plugin.removeAnimator(this);
    }

    @Override
    public void cancelAnimation() {
        if (this.destinationText == null) {
            return;
        }
        this.destinationText.remove();
        this.destinationText = null;
    }

    @Override
    public boolean isSamePortal(@NotNull Location portalLocation) {
        return this.portalLocation.equals(portalLocation);
    }
}
