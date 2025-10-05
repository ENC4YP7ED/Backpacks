package com.spydnel.backpacks.integration.accessories;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class AccessoriesClientIntegration {

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;

        try {
            // Note: Model initialization removed because the renderer doesn't actually render anything
            // The render() method just returns immediately, so no model is needed
            BackpackAccessoryRenderer renderer = new BackpackAccessoryRenderer();

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
