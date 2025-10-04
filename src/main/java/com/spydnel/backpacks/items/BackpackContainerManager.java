package com.spydnel.backpacks.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages shared backpack containers to ensure proper synchronization
 * when multiple players access the same backpack simultaneously.
 */
public class BackpackContainerManager {
    private static final Map<String, BackpackItemContainer> openContainers = new HashMap<>();

    /**
     * Gets or creates a container for the given entity's backpack.
     * If multiple players access the same backpack, they share the same container instance.
     */
    public static BackpackItemContainer getOrCreateContainer(LivingEntity target, Player viewer) {
        String key = getContainerKey(target);

        BackpackItemContainer container = openContainers.get(key);

        if (container == null) {
            // Create new container for this backpack
            container = new BackpackItemContainer(target, viewer);
            openContainers.put(key, container);
        }

        return container;
    }

    /**
     * Called when a container is closed. If no more viewers, removes the container from tracking.
     */
    public static void onContainerClosed(LivingEntity target, BackpackItemContainer container) {
        if (container.getViewerCount() == 0) {
            String key = getContainerKey(target);
            openContainers.remove(key);
        }
    }

    private static String getContainerKey(LivingEntity entity) {
        return entity.getUUID().toString();
    }

    /**
     * Cleanup method to remove stale containers (called periodically or on server shutdown)
     */
    public static void cleanup() {
        openContainers.entrySet().removeIf(entry -> entry.getValue().getViewerCount() == 0);
    }
}
