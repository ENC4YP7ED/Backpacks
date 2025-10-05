package com.spydnel.backpacks.api.client;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry for addon backpack keybindings.
 * Allows addons to register their own keybindings under the main mod's category.
 *
 * Usage in addon mod (client-side only):
 * <pre>
 * @OnlyIn(Dist.CLIENT)
 * public class MyBackpackKeybindings {
 *     public static KeyMapping OPEN_MY_BACKPACK;
 *
 *     public static void register() {
 *         OPEN_MY_BACKPACK = BackpackKeybindingRegistry.registerKeybinding(
 *             "key.mymod.open_my_backpack",
 *             GLFW.GLFW_KEY_B,
 *             "My Custom Backpack"
 *         );
 *     }
 * }
 * </pre>
 *
 * Then in your mod constructor (CLIENT SIDE ONLY):
 * <pre>
 * if (FMLEnvironment.dist == Dist.CLIENT) {
 *     MyBackpackKeybindings.register();
 * }
 * </pre>
 */
@OnlyIn(Dist.CLIENT)
public class BackpackKeybindingRegistry {

    private static final String CATEGORY = "key.categories.backpacks";
    private static final List<KeyMapping> REGISTERED_KEYBINDINGS = new ArrayList<>();

    /**
     * Register a keybinding for an addon backpack.
     * The keybinding will appear under "Backpack for Dummies" category in controls.
     *
     * @param translationKey The translation key for the keybinding (e.g., "key.mymod.open_my_backpack")
     * @param defaultKey The default key code (from GLFW, e.g., GLFW.GLFW_KEY_B)
     * @param backpackName The display name of the backpack (for better organization)
     * @return The registered KeyMapping
     */
    public static KeyMapping registerKeybinding(String translationKey, int defaultKey, String backpackName) {
        KeyMapping keyMapping = new KeyMapping(
            translationKey,
            defaultKey,
            CATEGORY
        );

        REGISTERED_KEYBINDINGS.add(keyMapping);
        return keyMapping;
    }

    /**
     * Get all registered addon keybindings.
     * Used internally by the main mod to register all keybindings.
     */
    public static List<KeyMapping> getRegisteredKeybindings() {
        return new ArrayList<>(REGISTERED_KEYBINDINGS);
    }

    /**
     * Get the category name for backpack keybindings.
     */
    public static String getCategory() {
        return CATEGORY;
    }
}
