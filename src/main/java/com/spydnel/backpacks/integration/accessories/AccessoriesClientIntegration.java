package com.spydnel.backpacks.integration.accessories;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class AccessoriesClientIntegration {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            BackpackAccessoryRenderer renderer = new BackpackAccessoryRenderer();

            // Initialize the model
            renderer.initModel(Minecraft.getInstance().getEntityModels());

            // Register the renderer
            AccessoriesRendererRegistry.registerRenderer(BPItems.BACKPACK.get(), () -> renderer);

            Backpacks.LOGGER.info("Accessories client integration enabled for Backpacks");
            initialized = true;
        } catch (Exception e) {
            Backpacks.LOGGER.error("Failed to initialize Accessories client integration", e);
        }
    }

    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        BackpackKeybindings.registerKeybindings(event);
    }
}
