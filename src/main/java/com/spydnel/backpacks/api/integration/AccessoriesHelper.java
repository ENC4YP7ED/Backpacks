package com.spydnel.backpacks.api.integration;

import com.spydnel.backpacks.BackpackAccessoriesHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

/**
 * Utility class for Accessories API integration.
 * Provides helper methods for addon mods to interact with the Accessories API.
 *
 * Example usage:
 * <pre>
 * ItemStack backpack = AccessoriesHelper.getBackpackFromAccessories(player, MyItems.CUSTOM_BACKPACK);
 * if (backpack != null) {
 *     // Player is wearing custom backpack in accessories slot
 * }
 * </pre>
 */
public class AccessoriesHelper {

    /**
     * Check if the Accessories mod is loaded.
     */
    public static boolean isAccessoriesLoaded() {
        return ModList.get().isLoaded("accessories");
    }

    /**
     * Get a backpack item from the entity's accessories slots.
     * Returns null if not wearing a backpack or if Accessories mod is not loaded.
     *
     * Note: This returns any backpack in accessories, then checks if it matches the specified item.
     * For optimal performance when BackpackAccessoriesHelper mixin is available, it uses the mixin method.
     *
     * @param entity The entity to check
     * @param backpackItem The backpack item to look for
     * @return The backpack ItemStack, or null if not found
     */
    @Nullable
    public static ItemStack getBackpackFromAccessories(LivingEntity entity, Item backpackItem) {
        if (!isAccessoriesLoaded()) {
            return null;
        }

        // Use BackpackAccessoriesHelper mixin if available (fastest method)
        if (entity instanceof BackpackAccessoriesHelper helper) {
            ItemStack backpack = helper.backpacks$getAccessoriesBackpack();
            if (!backpack.isEmpty() && backpack.is(backpackItem)) {
                return backpack;
            }
        }

        return null;
    }

    /**
     * Check if the entity is wearing a specific backpack in accessories slots.
     *
     * @param entity The entity to check
     * @param backpackItem The backpack item to look for
     * @return true if wearing the backpack in accessories
     */
    public static boolean isWearingBackpackInAccessories(LivingEntity entity, Item backpackItem) {
        if (!isAccessoriesLoaded()) {
            return false;
        }

        if (entity instanceof BackpackAccessoriesHelper helper) {
            ItemStack backpack = helper.backpacks$getAccessoriesBackpack();
            return !backpack.isEmpty() && backpack.is(backpackItem);
        }

        return false;
    }
}
