package com.spydnel.backpacks.events;

import com.spydnel.backpacks.Backpacks;
import com.spydnel.backpacks.blocks.BackpackBlockEntity;
import com.spydnel.backpacks.registry.BPBlocks;
import com.spydnel.backpacks.registry.BPItems;
import com.spydnel.backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

import static com.spydnel.backpacks.blocks.BackpackBlock.FACING;
import static com.spydnel.backpacks.blocks.BackpackBlock.WATERLOGGED;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Backpacks.MODID)
public class BackpackPickupEvents {

    // Helper method to get backpack from either chest or accessories slot
    private static ItemStack getEquippedBackpack(Player player) {
        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItem.is(BPItems.BACKPACK)) {
            return chestItem;
        }

        // Check accessories slot if mod is loaded
        if (ModList.get().isLoaded("accessories")) {
            try {
                Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
                ItemStack accessoriesItem = (ItemStack) integrationClass.getMethod("getBackpackFromAccessories", net.minecraft.world.entity.LivingEntity.class)
                    .invoke(null, player);
                if (!accessoriesItem.isEmpty()) {
                    return accessoriesItem;
                }
            } catch (Exception e) {
                // Accessories not loaded or error
            }
        }

        return ItemStack.EMPTY;
    }

    // Helper method to check if player has free backpack slot
    private static boolean hasEmptyBackpackSlot(Player player) {
        // Check accessories slot first if available
        if (ModList.get().isLoaded("accessories")) {
            try {
                Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
                boolean hasBackpack = (boolean) integrationClass.getMethod("isWearingBackpackInAccessories", net.minecraft.world.entity.LivingEntity.class)
                    .invoke(null, player);
                if (!hasBackpack) {
                    // Accessories slot is available
                    return true;
                }
            } catch (Exception e) {
                // Accessories not loaded or error
            }
        }

        // Check chest slot
        return player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    // Helper method to equip backpack to best available slot
    private static boolean equipBackpack(Player player, ItemStack backpack) {
        // Try accessories slot first if mod is loaded
        if (ModList.get().isLoaded("accessories")) {
            try {
                Class<?> integrationClass = Class.forName("com.spydnel.backpacks.integration.accessories.AccessoriesIntegration");
                boolean hasBackpack = (boolean) integrationClass.getMethod("isWearingBackpackInAccessories", net.minecraft.world.entity.LivingEntity.class)
                    .invoke(null, player);

                if (!hasBackpack) {
                    // Try to equip in accessories slot using Accessories API
                    Class<?> capabilityClass = Class.forName("io.wispforest.accessories.api.AccessoriesCapability");
                    Object capability = capabilityClass.getMethod("get", net.minecraft.world.entity.LivingEntity.class)
                        .invoke(null, player);

                    if (capability != null) {
                        // Try to equip in accessories slot
                        boolean equipped = (boolean) capability.getClass().getMethod("equipAccessory", ItemStack.class, Boolean.TYPE)
                            .invoke(capability, backpack, false);
                        if (equipped) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                // Accessories not loaded or couldn't equip
            }
        }

        // Fall back to chest slot
        if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            player.setItemSlot(EquipmentSlot.CHEST, backpack);
            return true;
        }

        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock (PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack heldItem = event.getItemStack();
        ItemStack equippedBackpack = getEquippedBackpack(player);

        boolean hasBackpack = !equippedBackpack.isEmpty();
        boolean hasEmptySlot = hasEmptyBackpackSlot(player);
        boolean isAbove = (pos.above().getY() > player.getY());
        boolean isUnobstructed = level.isUnobstructed(BPBlocks.BACKPACK.get().defaultBlockState(), pos.above(),
                CollisionContext.of(player)) && level.getBlockState(pos.above()).canBeReplaced();

        //PICKUP - Now works with both chest and accessories slots
        if (player.isShiftKeyDown() && hasEmptySlot && block == BPBlocks.BACKPACK.get() && blockEntity != null) {

            player.swing(hand);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            itemstack.applyComponents(blockEntity.collectComponents());

            // Equip to accessories slot first, then chest slot
            if (equipBackpack(player, itemstack)) {
                addParticles(level, pos);

                if (!level.isClientSide) {
                    level.removeBlockEntity(pos);
                    level.removeBlock(pos, false);
                }
                event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
                event.setCanceled(true);
            }
        }

        //PLACEMENT - Now works from both chest and accessories slots
        if (player.isShiftKeyDown() && heldItem.isEmpty() && hasBackpack && event.getFace() == Direction.UP && !isAbove && isUnobstructed) {

            player.swing(hand);
            player.swingingArm = InteractionHand.MAIN_HAND;


            BlockState state = BPBlocks.BACKPACK.get().defaultBlockState()
                    .setValue(FACING, player.getDirection())
                    .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

            blockEntity = new BackpackBlockEntity(pos.above(), state);
            blockEntity.applyComponentsFromItemStack(equippedBackpack);



            if (!level.isClientSide) {
                Backpacks.LOGGER.debug(String.valueOf(level.getBlockState(pos.above()).isEmpty()));
                level.setBlockAndUpdate(pos.above(), state);
                level.setBlockEntity(blockEntity);
                //((BackpackBlockEntity)blockEntity).updateColor();
                //blockEntity.getUpdateTag(level.registryAccess());

                equippedBackpack.shrink(1);
                level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
            }
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
            event.setCanceled(true);
        }
    }

    //ARMOR SWAPPING
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();
        EquipmentSlot slot = null;

        if (item instanceof ArmorItem) { slot = ((ArmorItem)item).getEquipmentSlot(); }
        if (item instanceof Equipable) { slot = ((Equipable)item).getEquipmentSlot(); }

        if (slot == EquipmentSlot.CHEST && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    //ITEM PICKUP - Now prioritizes accessories slot
    @SubscribeEvent
    public static void  onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            Player player = event.getPlayer();
            if (hasEmptyBackpackSlot(player) && !itemEntity.hasPickUpDelay()) {
                // Try to equip (will prefer accessories slot if available)
                if (equipBackpack(player, itemStack.copy())) {
                    player.take(itemEntity, 1);
                    itemEntity.discard();
                    player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                    player.onItemPickup(itemEntity);
                }
            }
            event.setCanPickup(TriState.FALSE);
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0,0);
        }
    }
}
