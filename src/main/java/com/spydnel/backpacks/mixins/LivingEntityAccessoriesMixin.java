package com.spydnel.backpacks.mixins;

import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import com.spydnel.backpacks.BackpackAccessoriesHelper;
import com.spydnel.backpacks.registry.BPItems;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin that provides fast, direct access to Accessories API without reflection.
 * Only applied when Accessories mod is loaded.
 * Implements BackpackAccessoriesHelper interface for zero-overhead backpack access.
 */
@IfModLoaded("accessories")
@Mixin(LivingEntity.class)
public abstract class LivingEntityAccessoriesMixin implements BackpackAccessoriesHelper {

    @Override
    public ItemStack backpacks$getAccessoriesBackpack() {
        LivingEntity self = (LivingEntity) (Object) this;

        // Direct API access - no reflection!
        AccessoriesCapability capability = AccessoriesCapability.get(self);
        if (capability == null) {
            return ItemStack.EMPTY;
        }

        var equipped = capability.getEquipped(BPItems.BACKPACK.get());
        if (equipped.isEmpty()) {
            return ItemStack.EMPTY;
        }

        return equipped.getFirst().stack();
    }

    @Override
    public boolean backpacks$hasAccessoriesBackpack() {
        LivingEntity self = (LivingEntity) (Object) this;

        // Direct API access - no reflection!
        AccessoriesCapability capability = AccessoriesCapability.get(self);
        if (capability == null) {
            return false;
        }

        return capability.isEquipped(BPItems.BACKPACK.get());
    }
}
