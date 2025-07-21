package dev.pelfox.portalify;

import dev.pelfox.portalify.animation.IPortalAnimator;
import dev.pelfox.portalify.animation.PortalExteriorAnimator;
import dev.pelfox.portalify.listeners.PlayerBreakBlockListener;
import dev.pelfox.portalify.listeners.PlayerClickBlockListener;
import dev.pelfox.portalify.listeners.PlayerMoveListener;
import dev.pelfox.portalify.listeners.PlayerPlaceBlockListener;
import dev.pelfox.portalify.utils.KeyBuilderUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Portalify extends JavaPlugin {
    private final List<IPortalAnimator> animators = new ArrayList<>();

    @Override
    public void onEnable() {
        I18nProvider.initializeI18n();

        this.registerRecipe();
        Bukkit.getPluginManager().registerEvents(new PlayerClickBlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPlaceBlockListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerBreakBlockListener(this), this);

        Bukkit.getPluginManager().registerEvents(new PlayerMoveListener(), this);
        Bukkit.getWorlds().forEach(world -> this.restartAnimations(world.getPersistentDataContainer()));
    }

    private void registerRecipe() {
        NamespacedKey portalKey = new NamespacedKey("portalify", "portal_frame");
        ItemStack item = ItemStack.of(Material.END_PORTAL_FRAME);
        item.editMeta(meta -> meta.displayName(Component.text("Teleportable Portal")
                .decoration(TextDecoration.ITALIC, false)
                .color(NamedTextColor.DARK_PURPLE)));

        ShapedRecipe recipe = new ShapedRecipe(portalKey, item);
        recipe.shape("   ", "ABA", "BBB");
        recipe.setIngredient('A', Material.ENDER_PEARL);
        recipe.setIngredient('B', Material.OBSIDIAN);

        this.getServer().addRecipe(recipe);
    }

    private void restartAnimations(@NotNull PersistentDataContainer container) {
        for (NamespacedKey key : container.getKeys()) {
            if (!KeyBuilderUtils.isValidKey(key)) {
                continue;
            }
            Location location = KeyBuilderUtils.locationFromKey(key);
            this.registerAnimator(new PortalExteriorAnimator(this, location));
        }
    }

    public void registerAnimator(@NotNull IPortalAnimator animator) {
        this.animators.add(animator);
        animator.animate();
    }

    @NotNull
    public List<? extends IPortalAnimator> getAnimatorsForPortal(@NotNull Location portalLocation) {
        return this.animators.stream()
                .filter(animator -> animator.isSamePortal(portalLocation))
                .toList();
    }

    public void removeAnimator(@NotNull IPortalAnimator animator) {
        this.animators.remove(animator);
    }
}
