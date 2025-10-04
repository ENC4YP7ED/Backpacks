package com.spydnel.backpacks;

import com.spydnel.backpacks.networking.BackpackOpenPayload;
import com.spydnel.backpacks.networking.BackpackPayloadHandler;
import com.spydnel.backpacks.networking.OpenBackpackPayload;
import com.spydnel.backpacks.networking.OpenBackpackPayloadHandler;
import com.spydnel.backpacks.registry.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(Backpacks.MODID)
public class Backpacks
{
    public static final String MODID = "backpacks";

    public static final Logger LOGGER = LogUtils.getLogger();


    public Backpacks(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.register(Backpacks.class);

        BPDataAttatchments.ATTACHMENT_TYPES.register(modEventBus);
        BPBlocks.BLOCKS.register(modEventBus);
        BPItems.ITEMS.register(modEventBus);
        BPBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        BPSounds.SOUND_EVENTS.register(modEventBus);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Initialize Accessories integration if mod is loaded
            if (ModList.get().isLoaded("accessories")) {
                try {
                    Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration")
                        .getMethod("init")
                        .invoke(null);
                } catch (Exception e) {
                    LOGGER.error("Failed to load Accessories integration", e);
                }
            }
        });
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                BackpackOpenPayload.TYPE,
                BackpackOpenPayload.STREAM_CODEC,
                BackpackPayloadHandler::HandleClientData
        );
        registrar.playToServer(
                OpenBackpackPayload.TYPE,
                OpenBackpackPayload.STREAM_CODEC,
                OpenBackpackPayloadHandler::handleServerData
        );
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Register keybindings if Accessories is loaded
        if (ModList.get().isLoaded("accessories")) {
            try {
                Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesClientIntegration")
                    .getMethod("registerKeybindings", RegisterKeyMappingsEvent.class)
                    .invoke(null, event);
            } catch (Exception e) {
                LOGGER.error("Failed to register keybindings for Accessories integration", e);
            }
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Initialize Accessories client integration if mod is loaded
            if (ModList.get().isLoaded("accessories")) {
                try {
                    Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesClientIntegration")
                        .getMethod("init")
                        .invoke(null);
                } catch (Exception e) {
                    LOGGER.error("Failed to load Accessories client integration", e);
                }
            }
        });
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
                    return tintIndex == 0 ? -1 :DyedItemColor.getOrDefault(stack, -1);
                },
                BPItems.BACKPACK.value());
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
            event.accept(BPItems.BACKPACK);
    }
}
