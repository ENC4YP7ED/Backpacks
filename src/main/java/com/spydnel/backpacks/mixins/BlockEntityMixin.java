package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.blocks.BackpackBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Network optimization mixin for block entity synchronization.
 * Prevents excessive network packets for backpack block entities by debouncing rapid updates.
 */
@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Unique
    private static final Map<BlockPos, Long> backpacks$lastBlockSyncTime = new ConcurrentHashMap<>();

    @Unique
    private static final long backpacks$BLOCK_SYNC_COOLDOWN_MS = 100; // 100ms = ~2 ticks debounce

    @Unique
    private CompoundTag backpacks$lastSyncedData = null;

    /**
     * Optimizes setChanged for BackpackBlockEntity instances.
     * Prevents redundant network updates when block data hasn't actually changed.
     */
    @Inject(method = "setChanged()V", at = @At("HEAD"), cancellable = true)
    private void backpacks$optimizeSetChanged(CallbackInfo ci) {
        BlockEntity self = (BlockEntity) (Object) this;

        // Only optimize BackpackBlockEntity instances
        if (!(self instanceof BackpackBlockEntity)) {
            return;
        }

        BlockPos pos = self.getBlockPos();
        long currentTime = System.currentTimeMillis();

        // Debounce rapid setChanged calls
        Long lastSync = backpacks$lastBlockSyncTime.get(pos);
        if (lastSync != null && (currentTime - lastSync) < backpacks$BLOCK_SYNC_COOLDOWN_MS) {
            // Too soon - skip this setChanged call to reduce network traffic
            ci.cancel();
            return;
        }

        // Check if data actually changed by comparing NBT
        try {
            CompoundTag currentData = new CompoundTag();
            self.saveWithoutMetadata(self.getLevel().registryAccess());

            if (backpacks$lastSyncedData != null && backpacks$lastSyncedData.equals(currentData)) {
                // Data hasn't changed - skip sync
                ci.cancel();
                return;
            }

            // Data changed - update cache and allow sync
            backpacks$lastSyncedData = currentData.copy();
            backpacks$lastBlockSyncTime.put(pos, currentTime);

        } catch (Exception e) {
            // If comparison fails, allow sync to proceed
            backpacks$lastBlockSyncTime.put(pos, currentTime);
        }

        // Periodic cleanup
        if (backpacks$lastBlockSyncTime.size() > 1000) {
            backpacks$cleanupBlockSyncMap(currentTime);
        }
    }

    /**
     * Cleanup method to remove stale block position entries from sync tracking map.
     */
    @Unique
    private static void backpacks$cleanupBlockSyncMap(long currentTime) {
        backpacks$lastBlockSyncTime.entrySet().removeIf(entry ->
            (currentTime - entry.getValue()) > 300000 // Remove entries older than 5 minutes
        );
    }
}
