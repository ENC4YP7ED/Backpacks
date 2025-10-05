package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.BackpackAccessoriesHelper;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotMixin extends Slot {
    public ArmorSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(
            method = "mayPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    public void mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack item = this.getItem();
        boolean hasContainer = item.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(item.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);
        if (item.is(BPItems.BACKPACK) && hasContainer && !isEmpty) { cir.setReturnValue(false); }
    }

    /**
     * Prevent equipping a backpack in chest slot if already wearing one in accessories slot.
     * This ensures only one backpack can be equipped at a time.
     */
    @Inject(
            method = "mayPlace",
            at = @At("HEAD"),
            cancellable = true
    )
    public void mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        // Only apply to chest slot (index 2)
        if (this.index != 2) return;

        // Check if trying to place any backpack item
        if (!stack.is(BPItems.BACKPACK)) return;

        // If Accessories mod is loaded, check for backpack in accessories slots
        if (ModList.get().isLoaded("accessories")) {
            Container container = this.container;
            if (container instanceof net.minecraft.world.entity.player.Inventory inventory) {
                if (inventory.player instanceof BackpackAccessoriesHelper helper) {
                    ItemStack accessoryBackpack = helper.backpacks$getAccessoriesBackpack();
                    if (!accessoryBackpack.isEmpty()) {
                        // Already wearing a backpack in accessories - prevent equipping
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }
}
