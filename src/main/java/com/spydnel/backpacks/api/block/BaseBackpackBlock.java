package com.spydnel.backpacks.api.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

/**
 * Base class for custom backpack blocks.
 * Addon mods should extend this class to create their own backpack block variants.
 *
 * Features:
 * - Waterlogging support
 * - Floating animation when placed on water
 * - Directional placement
 * - Custom interaction handling
 *
 * Override methods to customize behavior:
 * - {@link #useWithoutItem} - Custom open behavior
 * - {@link #getBlockEntityType} - Specify block entity type
 * - {@link #getTicker} - Custom tick logic
 * - {@link #getEquipSound} - Custom equip sound
 */
public abstract class BaseBackpackBlock extends BaseEntityBlock implements Equipable, EntityBlock, SimpleWaterloggedBlock {

    protected static final VoxelShape SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 11.0, 12.0);
    protected static final VoxelShape SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 11.0, 13.0);
    protected static final VoxelShape FLOATING_SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 8.0, 12.0);
    protected static final VoxelShape FLOATING_SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 8.0, 13.0);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FLOATING = BooleanProperty.create("floating");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BaseBackpackBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLOATING, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FLOATING, level.getFluidState(pos.below()).isSource() && !level.getFluidState(pos).isSource())
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(FLOATING, level.getFluidState(currentPos.below()).isSource());
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    /**
     * Override this to provide custom equip sound.
     * Default uses armor equip generic sound.
     */
    @Override
    public Holder<SoundEvent> getEquipSound() {
        return Holder.direct(SoundEvents.ARMOR_EQUIP_GENERIC.value());
    }

    /**
     * Override this to customize block interaction behavior.
     * Default implementation opens the block entity menu.
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && canOpenMenu(blockEntity, player)) {
                openMenu(level, pos, player, blockEntity);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.PASS;
        }
    }

    /**
     * Override to customize menu opening logic.
     * Default checks if block entity implements MenuProvider.
     */
    protected boolean canOpenMenu(BlockEntity blockEntity, Player player) {
        return blockEntity instanceof net.minecraft.world.MenuProvider;
    }

    /**
     * Override to customize how the menu is opened.
     */
    protected void openMenu(Level level, BlockPos pos, Player player, BlockEntity blockEntity) {
        if (blockEntity instanceof net.minecraft.world.MenuProvider menuProvider) {
            player.openMenu(menuProvider);
        }
        if (blockEntity instanceof BaseBackpackBlockEntity backpackEntity) {
            backpackEntity.onOpen(player);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        if (state.getValue(FLOATING)) {
            return direction.getAxis() == Direction.Axis.X ? FLOATING_SHAPE_Z : FLOATING_SHAPE_X;
        } else {
            return direction.getAxis() == Direction.Axis.X ? SHAPE_Z : SHAPE_X;
        }
    }

    /**
     * Override to specify the block entity type for ticker registration.
     */
    protected abstract BlockEntityType<?> getBlockEntityType();

    /**
     * Override to provide custom ticker logic.
     * Default implementation returns null - subclasses should override to provide ticker.
     *
     * Example implementation:
     * <pre>
     * @Override
     * public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
     *     return createTickerHelper(type, getBlockEntityType(), YourBlockEntity::tick);
     * }
     * </pre>
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Subclasses should override this to provide their specific ticker
        return null;
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FLOATING, WATERLOGGED);
    }
}
