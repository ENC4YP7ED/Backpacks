package com.spydnel.backpacks.mixins;

import com.spydnel.backpacks.BackpackWearer;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPDataAttatchments;
import com.spydnel.backpacks.registry.BPSounds;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_COUNT;
import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_TICKS;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BackpackWearer {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "baseTick", at = @At("HEAD"))
    public void baseTick(CallbackInfo ci) {
        if (getData(OPEN_COUNT) > 0 && getData(OPEN_TICKS) < 10) { setData(OPEN_TICKS, getData(OPEN_TICKS) + 1); }
        if (getData(OPEN_COUNT) == 0 && getData(OPEN_TICKS) > 0) { setData(OPEN_TICKS, getData(OPEN_TICKS) - 1); }
    }

    public void onBackpackOpen() {
        this.setData(OPEN_COUNT, getData(OPEN_COUNT) + 1);
    }

    public void onBackpackClose() {
        this.setData(OPEN_COUNT, getData(OPEN_COUNT) - 1);
    }
}
