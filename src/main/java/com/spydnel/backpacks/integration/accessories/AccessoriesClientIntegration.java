package com.spydnel.backpacks.integration.accessories;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import net.minecraft.client.model.geom.EntityModelSet;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class AccessoriesClientIntegration {

    private static boolean initialized = false;
    private static BackpackAccessoryRenderer renderer;

    /**
     * @deprecated Use initWithModelSet instead - called from AddLayers event after model layers are registered
     */
    @Deprecated
    public static void init() {
        // This method is kept for backward compatibility but does nothing
        // The actual initialization happens in initWithModelSet()
    }

    public static void initWithModelSet(EntityModelSet modelSet) {
        if (initialized) return;

        try {
            renderer = new BackpackAccessoryRenderer();

            // Initialize the model with the provided model set
            renderer.initModel(modelSet);

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
