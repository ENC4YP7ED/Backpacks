package com.spydnel.backpacks.registry;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.models.BackpackBlockRenderer;
import com.spydnel.backpacks.models.BackpackLayer;
import com.spydnel.backpacks.models.BackpackModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Backpacks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BPLayers {

    public static final ModelLayerLocation BACKPACK = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "backpack"), "main");
    public static final ModelLayerLocation BACKPACK_BLOCK = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "backpack_block"), "main");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    BPItems.BACKPACK.asItem(),
                    ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "dyed"),
                    (stack, level, player, seed) -> isDyed(stack)
            );
        });
    }

    private static float isDyed(ItemStack stack) {
        return stack.has(DataComponents.DYED_COLOR) ? 1 : 0;
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BACKPACK, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(BACKPACK_BLOCK, BackpackBlockRenderer::createBodyLayer);
    }


    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                BPBlockEntities.BACKPACK.get(),
                BackpackBlockRenderer::new
        );
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {

            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {

                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(entityType);

            if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                humanoidMobRenderer.addLayer(new BackpackLayer(humanoidMobRenderer, event.getEntityModels()));

            } else if (renderer instanceof ArmorStandRenderer armorStandRenderer) {
                armorStandRenderer.addLayer(new BackpackLayer<>(armorStandRenderer, event.getEntityModels()));
            }
        }

        // Initialize Accessories integration here - after model layers are registered
        initializeAccessoriesIntegration(event.getEntityModels());
    }

    private static void initializeAccessoriesIntegration(EntityModelSet modelSet) {
        if (net.neoforged.fml.ModList.get().isLoaded("accessories")) {
            try {
                Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesClientIntegration")
                    .getMethod("initWithModelSet", EntityModelSet.class)
                    .invoke(null, modelSet);
            } catch (Exception e) {
                Backpacks.LOGGER.error("Failed to initialize Accessories integration with model set", e);
            }
        }
    }
}
