package com.spydnel.backpacks.networking;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.items.BackpackContainerManager;
import com.spydnel.backpacks.items.BackpackItemContainer;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenBackpackPayloadHandler {

    public static void handleServerData(final OpenBackpackPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                // First check chest slot
                ItemStack backpack = serverPlayer.getItemBySlot(EquipmentSlot.CHEST);

                // If no backpack in chest slot and Accessories is loaded, check accessories slot
                if (!backpack.is(BPItems.BACKPACK) && ModList.get().isLoaded("accessories")) {
                    backpack = getBackpackFromAccessories(serverPlayer);
                }

                // Open backpack if found
                if (backpack.is(BPItems.BACKPACK)) {
                    // Use shared container manager - same instance for all viewers
                    BackpackItemContainer container = BackpackContainerManager.getOrCreateContainer(serverPlayer, serverPlayer);

                    if (!backpack.has(DataComponents.CONTAINER)) {
                        backpack.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                    }

                    serverPlayer.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new ShulkerBoxMenu(id, serverPlayer.getInventory(), container),
                        Component.translatable("container.backpack")
                    ));
                }
            }
        });
    }

    private static ItemStack getBackpackFromAccessories(ServerPlayer player) {
        try {
            Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
            return (ItemStack) integrationClass.getMethod("getBackpackFromAccessories", net.minecraft.world.entity.LivingEntity.class)
                .invoke(null, player);
        } catch (Exception e) {
            return ItemStack.EMPTY;
        }
    }
}
