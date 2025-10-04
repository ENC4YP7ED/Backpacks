package com.spydnel.backpacks.integration.accessories;

import com.mojang.blaze3d.platform.InputConstants;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.networking.OpenBackpackPayload;
import com.spydnel.backpacks.registry.BPItems;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class BackpackKeybindings {

    public static final String CATEGORY = "key.categories." + Backpacks.MODID;

    public static KeyMapping openBackpackKey;

    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        openBackpackKey = new KeyMapping(
            "key." + Backpacks.MODID + ".open_backpack",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
        );
        event.register(openBackpackKey);
    }

    @EventBusSubscriber(modid = Backpacks.MODID, value = Dist.CLIENT)
    public static class KeyInputHandler {

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();

            if (mc.player == null || mc.screen != null) {
                return;
            }

            if (openBackpackKey != null && openBackpackKey.consumeClick()) {
                // Check if player has backpack in chest slot
                ItemStack chestItem = mc.player.getItemBySlot(EquipmentSlot.CHEST);
                boolean hasBackpackInChest = chestItem.is(BPItems.BACKPACK);

                // Check accessories slot
                boolean hasBackpackInAccessories = false;
                try {
                    Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
                    hasBackpackInAccessories = (boolean) integrationClass.getMethod("isWearingBackpackInAccessories", net.minecraft.world.entity.LivingEntity.class)
                        .invoke(null, mc.player);
                } catch (Exception e) {
                    // Accessories not loaded or error
                }

                // Only send packet if player has a backpack equipped (prefer accessories slot behavior)
                if (hasBackpackInAccessories || hasBackpackInChest) {
                    PacketDistributor.sendToServer(new OpenBackpackPayload());
                }
            }
        }
    }
}
