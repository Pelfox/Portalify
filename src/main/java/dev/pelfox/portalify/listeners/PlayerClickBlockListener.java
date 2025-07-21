package dev.pelfox.portalify.listeners;

import dev.pelfox.portalify.utils.KeyBuilderUtils;
import dev.pelfox.portalify.Portalify;
import dev.pelfox.portalify.animation.PortalDestinationAnimator;
import dev.pelfox.portalify.data.TeleportPortalData;
import dev.pelfox.portalify.persistence.WorldDataContainer;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.event.player.PlayerCustomClickEvent;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("UnstableApiUsage")
public class PlayerClickBlockListener implements Listener {
    private final Map<UUID, Location> editingPortals = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerClickBlockListener.class);

    private final Portalify pluginInstance;

    public PlayerClickBlockListener(@NotNull Portalify pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onPlayerClickBlock(@NotNull PlayerInteractEvent event) {
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND) || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !block.getType().equals(Material.END_PORTAL_FRAME)) {
            return;
        }

        WorldDataContainer dataContainer = new WorldDataContainer(block.getWorld());
        TeleportPortalData portalData = dataContainer.getData(block.getLocation());

        if (portalData == null) {
            LOGGER.warn("Player clicked on an portal block without linked data at {}", block.getLocation());
            return;
        }

        final Player player = event.getPlayer();
        this.editingPortals.put(event.getPlayer().getUniqueId(), block.getLocation());

        // getting all other portals in the world to populate the destination options
        List<SingleOptionDialogInput.OptionEntry> portalsOptions = dataContainer.getPortalsData().stream()
                .filter(data -> !data.getId().equals(portalData.getId()))
                .map(data -> SingleOptionDialogInput.OptionEntry.create(
                        KeyBuilderUtils.keyFromLocation(data.getOrigin()).asString(),
                        !data.getId().isBlank() ? Component.text(data.getId()) : GlobalTranslator.render(Component.translatable("editor.buttons.no_name"), player.locale()), // TODO: replace with location?
                        portalData.getDestination() != null && data.getOrigin().equals(portalData.getDestination())
                ))
                .toList();

        List<DialogInput> inputs = new ArrayList<>();
        inputs.add(DialogInput.text("name", GlobalTranslator.render(Component.translatable("editor.inputs.name"), player.locale()))
                .initial(portalData.getId())
                .build());

        // if there are no other available portals, we don't show the destination input
        if (!portalsOptions.isEmpty()) {
            inputs.add(DialogInput.singleOption("destination", GlobalTranslator.render(Component.translatable("editor.inputs.destination"), player.locale()), portalsOptions)
                    .build());
        }

        Dialog portalEditorDialog = Dialog.create(builder -> builder.empty()
                .base(
                        DialogBase.builder(GlobalTranslator.render(Component.translatable("editor.window.title"), player.locale()))
                                .canCloseWithEscape(false)
                                .inputs(inputs)
                                .build()
                )
                .type(DialogType.confirmation(
                        ActionButton.builder(GlobalTranslator.render(Component.translatable("editor.buttons.save"), player.locale()))
                                .action(DialogAction.customClick(Key.key("portalify:save_changes"), null))
                                .build(),
                        ActionButton.builder(GlobalTranslator.render(Component.translatable("editor.buttons.cancel"), player.locale())).build()
                ))
        );
        event.getPlayer().showDialog(portalEditorDialog);
    }

    @EventHandler
    public void onPlayerDialogSave(@NotNull PlayerCustomClickEvent event) {
        if (!event.getIdentifier().asString().equals("portalify:save_changes")) {
            return;
        }

        DialogResponseView view = event.getDialogResponseView();
        if (view == null) {
            return;
        }

        if (!(event.getCommonConnection() instanceof PlayerGameConnection playerConnection)) {
            return;
        }

        Player player = playerConnection.getPlayer();
        if (!this.editingPortals.containsKey(player.getUniqueId())) {
            return;
        }

        Location portalLocation = this.editingPortals.get(player.getUniqueId());

        // trying to get the portal world, if it is not set, we use the player's world
        World portalWorld = portalLocation.getWorld();
        if (portalWorld == null) {
            portalWorld = player.getWorld();
        }

        WorldDataContainer dataContainer = new WorldDataContainer(portalWorld);
        TeleportPortalData portalData = dataContainer.getData(portalLocation);

        if (portalData == null) {
            LOGGER.warn("Player {} tried to save portal data, but it was not found at {}", player.getName(), portalLocation);
            return;
        }

        String newName = view.getText("name");
        String newDestination = view.getText("destination");

        // if the name is set, we update the portal's name
        if (newName != null && !newName.isBlank()) {
            portalData.setId(newName);
            player.sendMessage(Component.translatable("portals.data.updated.name", Component.text(newName)).color(NamedTextColor.GREEN));
        }

        // if the destination is set, we update the portal's destination
        if (newDestination != null && !newDestination.isBlank()) {
            final NamespacedKey newDestinationKey = NamespacedKey.fromString(newDestination);
            // TODO: exceptions -> player messages
            if (newDestinationKey == null) {
                throw new IllegalArgumentException("Invalid destination: " + newDestination);
            }

            if (!KeyBuilderUtils.isValidKey(newDestinationKey)) {
                throw new IllegalArgumentException("Invalid destination key: " + newDestinationKey);
            }

            Location newDestinationLocation = KeyBuilderUtils.locationFromKey(newDestinationKey);

            // check if the new destination is in the same world as the portal
            WorldDataContainer destinationDataContainer = dataContainer;
            if (!newDestinationLocation.getWorld().equals(portalLocation.getWorld())) {
                destinationDataContainer = new WorldDataContainer(newDestinationLocation.getWorld());
            }

            TeleportPortalData destinationData = destinationDataContainer.getData(newDestinationLocation);
            if (destinationData == null) {
                throw new RuntimeException("Destination portal does not exist: " + newDestinationLocation);
            }

            // cross updating the portal's destination and the destination's origin
            portalData.setDestination(newDestinationLocation);
            destinationData.setDestination(portalLocation);

            // save the updated data for both portal and destination
            destinationDataContainer.saveData(newDestinationLocation, destinationData);

            String formattedDestination = String.format("%.2f, %.2f, %.2f", portalData.getDestination().getX(), portalData.getDestination().getY(), portalData.getDestination().getZ());
            player.sendMessage(Component.translatable("portals.data.updated.destination", Component.text(formattedDestination)).color(NamedTextColor.GREEN));

            this.makePortalsAnimations(portalData, destinationData);
        }

        dataContainer.saveData(portalLocation, portalData);
        player.sendMessage(Component.translatable("portals.data.updated").color(NamedTextColor.GREEN));
    }

    private void updatePortalAnimations(@NotNull TeleportPortalData portal1, @NotNull TeleportPortalData portal2) {
        List<PortalDestinationAnimator> portal1DestinationAnimators = this.pluginInstance.getAnimatorsForPortal(portal1.getOrigin()).stream()
                .filter(animator -> animator instanceof PortalDestinationAnimator)
                .map(animator -> (PortalDestinationAnimator) animator)
                .toList();

        if (portal1DestinationAnimators.isEmpty()) {
            this.pluginInstance.registerAnimator(new PortalDestinationAnimator(this.pluginInstance, portal1.getOrigin(), portal2.getId()));
        } else {
            for (PortalDestinationAnimator destinationAnimator : portal1DestinationAnimators) {
                destinationAnimator.setDestinationPortalId(portal2.getId());
            }
        }
    }

    private void makePortalsAnimations(@NotNull TeleportPortalData portal1, @NotNull TeleportPortalData portal2) {
        this.updatePortalAnimations(portal1, portal2);
        this.updatePortalAnimations(portal2, portal1);
    }
}
