package com.spydnel.backpacks.integration.accessories;

import com.spydnel.backpacks.BackpackWearer;
import com.spydnel.backpacks.items.BackpackContainerManager;
import com.spydnel.backpacks.items.BackpackItemContainer;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public class BackpackAccessory implements Accessory {

    @Override
    public void onEquip(ItemStack stack, SlotReference reference) {
        if (reference.entity() instanceof LivingEntity livingEntity && livingEntity instanceof BackpackWearer wearer) {
            // Initialize container component if needed
            if (!stack.has(DataComponents.CONTAINER)) {
                stack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            }
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference reference) {
        if (reference.entity() instanceof LivingEntity livingEntity) {
            // Immediately validate and kick ALL viewers when backpack is unequipped from accessories slot
            BackpackContainerManager.validateContainerForEntity(livingEntity);
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, SlotReference reference) {
        // Don't allow equipping if there's already a backpack in chest slot
        if (reference.entity() instanceof LivingEntity livingEntity) {
            ItemStack chestItem = livingEntity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if (chestItem.is(BPItems.BACKPACK)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack stack, SlotReference reference) {
        // Prevent unequipping if backpack contains items (same logic as armor slot)
        boolean hasContainer = stack.has(DataComponents.CONTAINER);
        boolean isEmpty = stack.get(DataComponents.CONTAINER) == null ||
                          stack.get(DataComponents.CONTAINER).equals(ItemContainerContents.EMPTY);

        if (hasContainer && !isEmpty) {
            return false;
        }

        return true;
    }

    public static void openBackpackMenu(Player player, LivingEntity wearer, ItemStack backpackStack) {
        if (!backpackStack.is(BPItems.BACKPACK)) return;

        // Use shared container manager - same instance for all viewers
        BackpackItemContainer container = BackpackContainerManager.getOrCreateContainer(wearer, player);

        if (!backpackStack.has(DataComponents.CONTAINER)) {
            backpackStack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }

        player.openMenu(new SimpleMenuProvider(
            (id, inv, p) -> new ShulkerBoxMenu(id, player.getInventory(), container),
            Component.translatable("container.backpack")
        ));
    }
}
