package com.spydnel.backpacks;

import net.minecraft.world.item.ItemStack;

/**
 * Interface injected into LivingEntity for fast Accessories API access.
 * Eliminates reflection overhead when accessing backpacks in accessories slots.
 * When Accessories mod is not loaded, default implementations return empty/false values.
 */
public interface BackpackAccessoriesHelper {

    /**
     * Gets the backpack ItemStack from the accessories slot (if present).
     * Returns ItemStack.EMPTY if no backpack is equipped in accessories.
     * This method is implemented via mixin when Accessories is loaded, and is much faster than reflection.
     */
    default ItemStack backpacks$getAccessoriesBackpack() {
        // Default implementation when Accessories is not loaded
        return ItemStack.EMPTY;
    }

    /**
     * Checks if this entity is wearing a backpack in the accessories slot.
     * Returns false if Accessories mod is not loaded or no backpack equipped.
     * This method is implemented via mixin when Accessories is loaded, and is much faster than reflection.
     */
    default boolean backpacks$hasAccessoriesBackpack() {
        // Default implementation when Accessories is not loaded
        return false;
    }
}
