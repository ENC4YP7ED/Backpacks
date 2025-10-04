package com.spydnel.backpacks.items;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages shared backpack containers to ensure proper synchronization
 * when multiple players access the same backpack simultaneously.
 */
public class BackpackContainerManager {
    private static final Map<String, BackpackItemContainer> openContainers = new ConcurrentHashMap<>();
    private static final Map<String, Long> containerCloseTime = new ConcurrentHashMap<>();

    // Keep containers around for 5 seconds after last viewer closes to prevent race conditions
    private static final long CONTAINER_CACHE_TIME = 5000; // 5 seconds in milliseconds

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
            containerCloseTime.remove(key); // Clear any scheduled removal
        } else {
            // Container exists - refresh its itemStack reference to ensure it's current
            container.refreshItemStack();
            containerCloseTime.remove(key); // Cancel any pending removal
        }

        return container;
    }

    /**
     * Called when a container is closed. Schedules removal after a delay to prevent race conditions.
     */
    public static void onContainerClosed(LivingEntity target, BackpackItemContainer container) {
        if (container.getViewerCount() == 0) {
            String key = getContainerKey(target);
            // Mark the time when container became empty
            containerCloseTime.put(key, System.currentTimeMillis());
        }
    }

    private static String getContainerKey(LivingEntity entity) {
        return entity.getUUID().toString();
    }

    /**
     * Cleanup method to remove stale containers after the cache timeout
     */
    public static void cleanup() {
        long currentTime = System.currentTimeMillis();

        containerCloseTime.entrySet().removeIf(entry -> {
            String key = entry.getKey();
            long closeTime = entry.getValue();

            // If enough time has passed since container became empty, remove it
            if (currentTime - closeTime > CONTAINER_CACHE_TIME) {
                BackpackItemContainer container = openContainers.get(key);
                if (container != null && container.getViewerCount() == 0) {
                    openContainers.remove(key);
                }
                return true; // Remove from closeTime map
            }
            return false;
        });
    }

    /**
     * Validates all open containers and kicks viewers if backpack was removed.
     * Called periodically from server tick.
     */
    public static void validateAllContainers() {
        for (BackpackItemContainer container : openContainers.values()) {
            if (container.getViewerCount() > 0) {
                container.validateAndKickIfNeeded();
            }
        }
    }

    /**
     * Validates container for a specific entity and kicks viewers if backpack is gone.
     * Called immediately when equipment changes are detected.
     */
    public static void validateContainerForEntity(LivingEntity entity) {
        String key = getContainerKey(entity);
        BackpackItemContainer container = openContainers.get(key);

        if (container != null && container.getViewerCount() > 0) {
            container.validateAndKickIfNeeded();
        }
    }
}
