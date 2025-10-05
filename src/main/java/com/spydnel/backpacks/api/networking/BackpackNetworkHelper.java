package com.spydnel.backpacks.api.networking;

import com.spydnel.backpacks.api.events.BackpackEventHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Utility class for backpack networking.
 * Provides helper methods for handling backpack-related network packets in addon mods.
 *
 * Example usage:
 * <pre>
 * public static void handleOpenBackpackPacket(ServerPlayer player) {
 *     BackpackNetworkHelper.openBackpackInventory(
 *         player,
 *         MyItems.CUSTOM_BACKPACK,
 *         (id, inventory, p) -> new CustomBackpackMenu(id, inventory),
 *         Component.translatable("container.custom_backpack")
 *     );
 * }
 * </pre>
 */
public class BackpackNetworkHelper {

    /**
     * Open a backpack inventory for a player.
     * Checks both chest slot and Accessories slots.
     *
     * @param player The server player
     * @param backpackItem The backpack item to open
     * @param menuConstructor The menu constructor
     * @param title The menu title
     * @return true if backpack was found and opened
     */
    public static boolean openBackpackInventory(ServerPlayer player, Item backpackItem,
                                                 MenuConstructor menuConstructor, Component title) {
        ItemStack backpack = BackpackEventHelper.getBackpackFromEntity(player, backpackItem);
        if (backpack != null) {
            player.openMenu(new SimpleMenuProvider(menuConstructor, title));
            return true;
        }
        return false;
    }

    /**
     * Check if a player can open a backpack.
     * Verifies the player is wearing the backpack.
     *
     * @param player The player
     * @param backpackItem The backpack item
     * @return true if player can open the backpack
     */
    public static boolean canOpenBackpack(Player player, Item backpackItem) {
        return BackpackEventHelper.isWearingBackpack(player, backpackItem);
    }

    /**
     * Get the backpack ItemStack a player is currently wearing.
     *
     * @param player The player
     * @param backpackItem The backpack item to look for
     * @return The backpack ItemStack, or null if not wearing
     */
    @Nullable
    public static ItemStack getEquippedBackpack(Player player, Item backpackItem) {
        return BackpackEventHelper.getBackpackFromEntity(player, backpackItem);
    }
}
