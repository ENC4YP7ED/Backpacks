package com.spydnel.backpacks.events;

import com.spydnel.backpacks.BackpackAccessoriesHelper;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.items.BackpackContainerManager;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber(modid = Backpacks.MODID)
public class EquipmentChangeEvents {

    /**
     * Listens for equipment changes and kicks viewers if backpack is unequipped
     */
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        EquipmentSlot slot = event.getSlot();

        // Check if this is the chest slot (where backpacks can be equipped)
        if (slot == EquipmentSlot.CHEST) {
            ItemStack from = event.getFrom();
            ItemStack to = event.getTo();

            // If a backpack was removed from chest slot
            if (from.is(BPItems.BACKPACK) && !to.is(BPItems.BACKPACK)) {
                // Immediately validate and kick viewers
                BackpackContainerManager.validateContainerForEntity(entity);
            }
        }
    }

    /**
     * Checks if entity is wearing backpack in accessories slot
     */
    private static boolean hasBackpackInAccessories(LivingEntity entity) {
        // Use mixin interface for fast access (no reflection!)
        if (entity instanceof BackpackAccessoriesHelper helper) {
            return helper.backpacks$hasAccessoriesBackpack();
        }
        return false;
    }
}
