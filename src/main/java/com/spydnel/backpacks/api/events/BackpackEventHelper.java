package com.spydnel.backpacks.api.events;

import com.spydnel.backpacks.api.integration.AccessoriesHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Utility class for backpack event handling.
 * Provides helper methods for common event-related tasks in addon mods.
 *
 * Example usage:
 * <pre>
 * @SubscribeEvent
 * public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
 *     Player player = event.getEntity();
 *     LivingEntity target = (LivingEntity) event.getTarget();
 *
 *     if (!BackpackEventHelper.isBehind(player, target)) return;
 *
 *     ItemStack backpack = BackpackEventHelper.getBackpackFromEntity(target, MyItems.CUSTOM_BACKPACK);
 *     if (backpack != null) {
 *         // Handle interaction with custom backpack
 *     }
 * }
 * </pre>
 */
public class BackpackEventHelper {

    /**
     * Check if a player is positioned behind a target entity.
     * Used for detecting when players click on someone's back to access their backpack.
     *
     * @param player The player doing the interaction
     * @param target The target entity
     * @return true if player is behind target
     */
    public static boolean isBehind(Player player, LivingEntity target) {
        Vec3 playerPos = player.position();
        Vec3 targetPos = target.position();
        Vec3 targetLook = target.getLookAngle();

        Vec3 toPlayer = playerPos.subtract(targetPos).normalize();
        double dotProduct = targetLook.dot(toPlayer);

        return dotProduct > 0.5;
    }

    /**
     * Get a backpack from an entity, checking both chest slot and Accessories slots.
     *
     * @param entity The entity to check
     * @param backpackItem The specific backpack item to look for
     * @return The backpack ItemStack, or null if not wearing this backpack
     */
    public static ItemStack getBackpackFromEntity(LivingEntity entity, Item backpackItem) {
        // Check chest slot first
        ItemStack chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItem.is(backpackItem)) {
            return chestItem;
        }

        // Check Accessories slots if mod is loaded
        if (AccessoriesHelper.isAccessoriesLoaded()) {
            return AccessoriesHelper.getBackpackFromAccessories(entity, backpackItem);
        }

        return null;
    }

    /**
     * Check if an entity is wearing a specific backpack in any slot.
     *
     * @param entity The entity to check
     * @param backpackItem The backpack item to look for
     * @return true if wearing the backpack
     */
    public static boolean isWearingBackpack(LivingEntity entity, Item backpackItem) {
        return getBackpackFromEntity(entity, backpackItem) != null;
    }

    /**
     * Get any backpack from an entity (chest slot or accessories).
     * Returns the first backpack found that matches the given items.
     *
     * @param entity The entity to check
     * @param backpackItems The backpack items to look for
     * @return The first matching backpack ItemStack, or null if not wearing any
     */
    public static ItemStack getAnyBackpackFromEntity(LivingEntity entity, Item... backpackItems) {
        for (Item backpackItem : backpackItems) {
            ItemStack backpack = getBackpackFromEntity(entity, backpackItem);
            if (backpack != null) {
                return backpack;
            }
        }
        return null;
    }
}
