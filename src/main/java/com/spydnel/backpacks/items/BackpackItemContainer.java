package com.spydnel.backpacks.items;

import com.spydnel.backpacks.BackpackWearer;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.events.EntityInteractionEvents;
import com.spydnel.backpacks.networking.BackpackOpenPayload;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;

public class BackpackItemContainer extends SimpleContainer {
    LivingEntity target;
    Player player;
    ItemStack itemStack;
    Level level;
    private final Set<Player> viewers = new HashSet<>();

    public BackpackItemContainer(LivingEntity target, Player player) {
        super(27);
        this.target = target;
        this.player = player;
        // Get backpack from chest slot or accessories slot
        itemStack = getBackpackFromEntity(target);
        level = target.level();
    }

    private ItemStack getBackpackFromEntity(LivingEntity entity) {
        // Check chest slot first
        ItemStack chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItem.is(BPItems.BACKPACK)) {
            return chestItem;
        }

        // Check accessories slot if mod is loaded
        if (ModList.get().isLoaded("accessories")) {
            ItemStack accessoriesItem = getBackpackFromAccessories(entity);
            if (!accessoriesItem.isEmpty()) {
                return accessoriesItem;
            }
        }

        return ItemStack.EMPTY;
    }

    private static ItemStack getBackpackFromAccessories(LivingEntity entity) {
        try {
            Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
            return (ItemStack) integrationClass.getMethod("getBackpackFromAccessories", LivingEntity.class)
                .invoke(null, entity);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Refreshes the itemStack reference to ensure it's current.
     * Called when a new viewer accesses an existing container.
     */
    public void refreshItemStack() {
        ItemStack freshStack = getBackpackFromEntity(target);
        if (!freshStack.isEmpty() && freshStack.is(BPItems.BACKPACK)) {
            this.itemStack = freshStack;
        }
    }

    public boolean stillValid(Player player) {
        // Refresh itemStack reference to ensure it's current
        refreshItemStack();

        return
                target != null &&
                !target.isRemoved() &&
                itemStack != null &&
                !itemStack.isEmpty() &&
                itemStack.is(BPItems.BACKPACK) &&
                itemStack.has(DataComponents.CONTAINER) &&
                player.distanceTo(target) < 5;
    }

    public void setChanged() {
        // Update the actual backpack itemstack (works for both chest and accessories slots)
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));

        // Sync to all viewers in real-time
        if (!level.isClientSide) {
            for (Player viewer : viewers) {
                if (viewer instanceof ServerPlayer serverPlayer && viewer.containerMenu != null) {
                    // Trigger container sync by marking slots as changed
                    serverPlayer.containerMenu.broadcastChanges();
                }
            }
        }

        super.setChanged();
    }

    @Override
    public void startOpen(Player player) {
        boolean isFirstViewer = viewers.isEmpty();

        viewers.add(player);

        // Only load data from ItemStack if this is the first viewer
        // Subsequent viewers will see the live, potentially modified container
        if (isFirstViewer && itemStack.has(DataComponents.CONTAINER)) {
            itemStack.get(DataComponents.CONTAINER).copyInto(this.getItems());
        }

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new BackpackOpenPayload(true, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_OPEN.value(), SoundSource.PLAYERS);
        super.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        viewers.remove(player);

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, new BackpackOpenPayload(false, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_CLOSE.value(), SoundSource.PLAYERS);
        super.stopOpen(player);

        // Notify manager that this viewer closed the container
        BackpackContainerManager.onContainerClosed(target, this);
    }

    /**
     * Gets the number of players currently viewing this container
     */
    public int getViewerCount() {
        return viewers.size();
    }
}
