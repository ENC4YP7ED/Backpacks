package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.items.BackpackItemContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Network optimization mixin for container synchronization.
 * Prevents excessive network traffic by debouncing rapid backpack container updates.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuMixin {

    @Unique
    private static final Map<Integer, Long> backpacks$lastSyncTime = new HashMap<>();

    @Unique
    private static final long backpacks$SYNC_COOLDOWN_MS = 50; // 50ms = ~1 tick debounce

    @Unique
    private long backpacks$lastBroadcastTime = 0;

    /**
     * Optimizes broadcastChanges for BackpackItemContainer instances.
     * Prevents redundant network packets when multiple changes occur in rapid succession.
     */
    @Inject(method = "broadcastChanges", at = @At("HEAD"), cancellable = true)
    private void backpacks$optimizeBroadcast(CallbackInfo ci) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;

        // Only optimize BackpackItemContainer instances
        if (!(self instanceof net.minecraft.world.inventory.ShulkerBoxMenu)) {
            return;
        }

        // Check if this is actually a backpack container by examining the container
        try {
            var field = net.minecraft.world.inventory.ShulkerBoxMenu.class.getDeclaredField("container");
            field.setAccessible(true);
            var container = field.get(self);

            if (!(container instanceof BackpackItemContainer)) {
                return;
            }

            // Debounce rapid updates - only sync if enough time has passed
            long currentTime = System.currentTimeMillis();
            int containerId = self.containerId;

            Long lastSync = backpacks$lastSyncTime.get(containerId);
            if (lastSync != null && (currentTime - lastSync) < backpacks$SYNC_COOLDOWN_MS) {
                // Too soon - skip this broadcast to reduce network traffic
                ci.cancel();
                return;
            }

            // Update last sync time
            backpacks$lastSyncTime.put(containerId, currentTime);
            backpacks$lastBroadcastTime = currentTime;

            // Clean up old entries (containers that are no longer active)
            if (backpacks$lastSyncTime.size() > 100) {
                backpacks$cleanupSyncMap(currentTime);
            }

        } catch (Exception e) {
            // If reflection fails, don't interfere with normal operation
        }
    }

    /**
     * Cleanup method to remove stale container entries from sync tracking map.
     */
    @Unique
    private static void backpacks$cleanupSyncMap(long currentTime) {
        backpacks$lastSyncTime.entrySet().removeIf(entry ->
            (currentTime - entry.getValue()) > 60000 // Remove entries older than 60 seconds
        );
    }

    /**
     * Remove tracking when container is removed.
     */
    @Inject(method = "removed", at = @At("HEAD"))
    private void backpacks$onRemoved(net.minecraft.world.entity.player.Player player, CallbackInfo ci) {
        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;

        try {
            var field = net.minecraft.world.inventory.ShulkerBoxMenu.class.getDeclaredField("container");
            field.setAccessible(true);
            var container = field.get(self);

            if (container instanceof BackpackItemContainer) {
                // Clean up sync tracking for this container
                backpacks$lastSyncTime.remove(self.containerId);
            }
        } catch (Exception e) {
            // If reflection fails, just continue
        }
    }
}
