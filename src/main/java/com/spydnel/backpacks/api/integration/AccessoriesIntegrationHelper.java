package com.spydnel.backpacks.api.integration;

import io.wispforest.accessories.api.AccessoriesAPI;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

/**
 * Helper class for initializing Accessories API integration.
 * Simplifies the registration process for addon mods.
 *
 * Example usage in your main mod class:
 * <pre>
 * private void commonSetup(final FMLCommonSetupEvent event) {
 *     event.enqueueWork(() -> {
 *         AccessoriesIntegrationHelper.registerAccessory(
 *             MyItems.CUSTOM_BACKPACK.get(),
 *             new MyCustomBackpackAccessory(),
 *             LOGGER,
 *             "Custom Backpack"
 *         );
 *     });
 * }
 * </pre>
 */
public class AccessoriesIntegrationHelper {

    /**
     * Register an accessory with the Accessories API.
     * Handles mod loading check and error logging automatically.
     *
     * @param item The item to register
     * @param accessory The accessory implementation
     * @param logger The logger for logging success/errors
     * @param name Display name for logging
     * @return true if successfully registered
     */
    public static boolean registerAccessory(Item item, BaseBackpackAccessory accessory, Logger logger, String name) {
        if (!ModList.get().isLoaded("accessories")) {
            logger.debug("Accessories mod not loaded, skipping {} registration", name);
            return false;
        }

        try {
            AccessoriesAPI.registerAccessory(item, accessory);
            logger.info("Accessories integration enabled for {}", name);
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize Accessories integration for {}", name, e);
            return false;
        }
    }

    /**
     * Initialize accessories integration using reflection.
     * Useful for optional loading when Accessories API is not a compile dependency.
     *
     * @param integrationClassName The fully qualified class name of your integration class
     * @param logger The logger for logging
     * @param name Display name for logging
     * @return true if successfully initialized
     */
    public static boolean initializeWithReflection(String integrationClassName, Logger logger, String name) {
        if (!ModList.get().isLoaded("accessories")) {
            logger.debug("Accessories mod not loaded, skipping {} integration", name);
            return false;
        }

        try {
            Class.forName(integrationClassName)
                    .getMethod("init")
                    .invoke(null);
            logger.info("{} Accessories integration initialized", name);
            return true;
        } catch (Exception e) {
            logger.error("Failed to load {} Accessories integration", name, e);
            return false;
        }
    }

    /**
     * Check if Accessories mod is loaded.
     */
    public static boolean isAccessoriesLoaded() {
        return ModList.get().isLoaded("accessories");
    }
}
