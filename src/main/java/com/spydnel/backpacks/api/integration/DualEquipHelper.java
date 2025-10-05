package com.spydnel.backpacks.api.integration;

import com.spydnel.backpacks.BackpackAccessoriesHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * Helper class for preventing dual-equipping of backpacks.
 * Ensures only one backpack can be worn at a time (either in chest slot OR accessories slot).
 *
 * Usage in addon mods:
 * <pre>
 * @Override
 * public boolean canEquip(ItemStack stack, SlotReference reference) {
 *     // Prevent dual-equipping
 *     if (DualEquipHelper.isWearingBackpackInOtherSlot(reference.entity(), getBackpackItem(), false)) {
 *         return false;
 *     }
 *     return true;
 * }
 * </pre>
 */
public class DualEquipHelper {

    /**
     * Check if an entity is wearing a specific backpack in the opposite slot type.
     *
     * @param entity The entity to check
     * @param backpackItem The backpack item to look for
     * @param checkingAccessories If true, checks chest slot. If false, checks accessories slots.
     * @return true if wearing the backpack in the opposite slot
     */
    public static boolean isWearingBackpackInOtherSlot(LivingEntity entity, Item backpackItem, boolean checkingAccessories) {
        if (checkingAccessories) {
            // Checking from accessories - see if in chest slot
            ItemStack chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
            return chestItem.is(backpackItem);
        } else {
            // Checking from chest slot - see if in accessories
            if (!ModList.get().isLoaded("accessories")) {
                return false;
            }

            if (entity instanceof BackpackAccessoriesHelper helper) {
                ItemStack accessoryBackpack = helper.backpacks$getAccessoriesBackpack();
                return !accessoryBackpack.isEmpty() && accessoryBackpack.is(backpackItem);
            }
        }
        return false;
    }

    /**
     * Check if an entity is wearing ANY backpack in the opposite slot type.
     * Useful for preventing all backpack types from dual-equipping.
     *
     * @param entity The entity to check
     * @param checkingAccessories If true, checks chest slot. If false, checks accessories slots.
     * @return true if wearing any backpack in the opposite slot
     */
    public static boolean isWearingAnyBackpackInOtherSlot(LivingEntity entity, boolean checkingAccessories) {
        if (checkingAccessories) {
            // Checking from accessories - see if wearing anything in chest slot
            ItemStack chestItem = entity.getItemBySlot(EquipmentSlot.CHEST);
            return !chestItem.isEmpty();
        } else {
            // Checking from chest slot - see if wearing anything in accessories
            if (!ModList.get().isLoaded("accessories")) {
                return false;
            }

            if (entity instanceof BackpackAccessoriesHelper helper) {
                ItemStack accessoryBackpack = helper.backpacks$getAccessoriesBackpack();
                return !accessoryBackpack.isEmpty();
            }
        }
        return false;
    }
}
