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
        if (!(model instanceof HumanoidModel<?> humanoidModel)) return;
        if (this.model == null) return;

        matrices.pushPose();

        // Align to body
        AccessoryRenderer.transformToModelPart(matrices, humanoidModel.body, 0, 0, -1);

        // Calculate lid rotation based on open state
        float lidRot = 0;
        LivingEntity entity = reference.entity();
        boolean isOpen = entity.getData(OPEN_COUNT) > 0;
        int openTicks = entity.getData(OPEN_TICKS);

        if (isOpen && openTicks < 10) {
            float t = ((float) openTicks + partialTicks);
            lidRot = (float) Math.pow(2, -1 * t) * Mth.sin((t - 0.75F) * 0.5F) + 1;
        } else if (openTicks == 10) {
            lidRot = 1;
        } else if (openTicks > 0) {
            float t = ((float) openTicks - partialTicks);
            lidRot = (float) -Math.pow(2, t - 10) * Mth.sin((t - 10.75F) * 0.5F);
        }

        this.model.getChild("base").getChild("lid").xRot = lidRot;

        // Render base layer
        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(TEXTURE), stack.hasFoil());
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        // Render colored overlay
        renderColoredLayer(matrices, multiBufferSource, light, stack);

        matrices.popPose();
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
