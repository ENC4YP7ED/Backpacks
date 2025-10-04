package com.spydnel.backpacks.integration.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.registry.BPDataAttatchments;
import com.spydnel.backpacks.registry.BPLayers;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_COUNT;
import static com.spydnel.backpacks.registry.BPDataAttatchments.OPEN_TICKS;

@OnlyIn(Dist.CLIENT)
public class BackpackAccessoryRenderer implements AccessoryRenderer {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "textures/model/backpack.png");
    private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "textures/model/backpack_overlay.png");

    private ModelPart model;

    public BackpackAccessoryRenderer() {
    }

    public void initModel(EntityModelSet modelSet) {
        if (this.model == null) {
            this.model = modelSet.bakeLayer(BPLayers.BACKPACK);
        }
    }

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        // Rendering disabled - backpack is invisible when worn in accessories slot
        return;
    }

    private void renderColoredLayer(PoseStack matrices, MultiBufferSource buffer, int light, ItemStack stack) {
        int color = DyedItemColor.getOrDefault(stack, 0);
        if (FastColor.ARGB32.alpha(color) == 0) {
            return;
        }

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.armorCutoutNoCull(OVERLAY_TEXTURE), stack.hasFoil());
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.opaque(color));
    }
}
