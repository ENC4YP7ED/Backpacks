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

    /**
     * Attempts to equip a backpack in the accessories slot.
     * @return true if successfully equipped, false otherwise
     */
    public static boolean tryEquipBackpack(LivingEntity entity, ItemStack backpack) {
        if (!initialized) return false;

        try {
            var capability = AccessoriesCapability.get(entity);
            if (capability == null) return false;

            // Check if already wearing a backpack
            if (isWearingBackpackInAccessories(entity)) {
                return false;
            }

            // Use attemptToEquipAccessory which modifies the stack in-place
            var result = capability.attemptToEquipAccessory(backpack, false);

            // If result is non-null, item was equipped
            return result != null;
        } catch (Exception e) {
            Backpacks.LOGGER.debug("Could not equip backpack to accessories slot", e);
            return false;
        }
    }

    /**
     * Attempts to remove backpack from accessories slot.
     * @return true if successfully removed, false otherwise
     */
    public static boolean tryRemoveBackpack(LivingEntity entity) {
        if (!initialized) return false;

        try {
            var capability = AccessoriesCapability.get(entity);
            if (capability == null) return false;

            // Get all equipped backpacks
            var equipped = capability.getEquipped(BPItems.BACKPACK.get());
            if (equipped.isEmpty()) return false;

            // Get the first equipped backpack's SlotEntryReference
            var slotEntryRef = equipped.getFirst();

            // Get the SlotReference from the SlotEntryReference
            var slotReference = slotEntryRef.reference();

            // Use SlotReference.setStack() to remove the item
            return slotReference.setStack(ItemStack.EMPTY);
        } catch (Exception e) {
            Backpacks.LOGGER.error("Failed to remove backpack from accessories slot", e);
            return false;
        }
    }
}
