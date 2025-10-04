package com.spydnel.backpacks.integration.accessories;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AccessoriesIntegration {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            AccessoriesAPI.registerAccessory(BPItems.BACKPACK.get(), new BackpackAccessory());
            Backpacks.LOGGER.info("Accessories integration enabled for Backpacks");
            initialized = true;
        } catch (Exception e) {
            Backpacks.LOGGER.error("Failed to initialize Accessories integration", e);
        }
    }

    /**
     * Gets the backpack from the accessories slot if present
     */
    public static ItemStack getBackpackFromAccessories(LivingEntity entity) {
        if (!initialized) return ItemStack.EMPTY;

        try {
            var capability = AccessoriesCapability.get(entity);
            if (capability == null) return ItemStack.EMPTY;

            var accessories = capability.getEquipped(BPItems.BACKPACK.get());
            if (accessories.isEmpty()) return ItemStack.EMPTY;

            return accessories.getFirst().stack();
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Checks if the entity is wearing a backpack in accessories slot
     */
    public static boolean isWearingBackpackInAccessories(LivingEntity entity) {
        return !getBackpackFromAccessories(entity).isEmpty();
    }
}
