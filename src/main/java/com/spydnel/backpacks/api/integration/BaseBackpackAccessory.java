package com.spydnel.backpacks.api.integration;

import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Base class for Accessories API integration.
 * Addon mods should extend this to create custom accessory behavior for their backpacks.
 *
 * Features:
 * - Prevents dual-equipping in chest slot and accessory slot
 * - Customizable equip/unequip checks
 * - Integration with BackpackAccessoriesHelper mixin
 *
 * Override methods to customize:
 * - {@link #canUnequip(ItemStack, SlotReference)} - Custom unequip checks
 * - {@link #getBackpackItem()} - Specify which item this accessory is for
 * - {@link #allowDualEquip()} - Whether to allow chest slot + accessory slot
 */
public abstract class BaseBackpackAccessory implements Accessory {

    /**
     * Override to specify the backpack item this accessory is for.
     * Used for dual-equip prevention.
     */
    protected abstract Item getBackpackItem();

    /**
     * Override to allow dual-equipping in both chest slot and accessory slot.
     * Default is false (prevents dual-equip).
     */
    protected boolean allowDualEquip() {
        return false;
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        // Override in subclass to add custom equip behavior
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        // Override in subclass to add custom unequip behavior
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        if (!allowDualEquip() && reference.entity() instanceof LivingEntity livingEntity) {
            // Prevent equipping if already wearing in chest slot
            // Using DualEquipHelper ensures consistent behavior across all backpack types
            if (DualEquipHelper.isWearingBackpackInOtherSlot(livingEntity, getBackpackItem(), false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Override to customize unequip checks.
     * Default implementation always allows unequipping.
     */
    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        return true;
    }
}
