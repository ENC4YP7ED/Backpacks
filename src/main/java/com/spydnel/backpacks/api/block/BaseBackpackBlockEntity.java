package com.spydnel.backpacks.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Base class for backpack block entities.
 * Addon mods should extend this to create custom backpack block entity behavior.
 *
 * Features:
 * - Item storage (27 slots by default)
 * - Color support via dyed item color
 * - Animation handling (open, float, place)
 * - Sound effects
 * - Client-server synchronization
 *
 * Override methods to customize:
 * - {@link #getContainerSize()} - Change inventory size
 * - {@link #createMenu(int, Inventory)} - Custom menu
 * - {@link #getDefaultName()} - Custom display name
 * - {@link #getOpenSound()} - Custom open sound
 * - {@link #getCloseSound()} - Custom close sound
 * - {@link #shouldStoreItems()} - Whether to store items (false for ender chest sync)
 */
public abstract class BaseBackpackBlockEntity extends RandomizableContainerBlockEntity {

    private NonNullList<ItemStack> itemStacks;
    public int openTicks;
    public boolean newlyPlaced;
    public int placeTicks;
    public int floatTicks;
    public boolean open;
    private int openCount;
    private int color;

    public BaseBackpackBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        this.itemStacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        this.newlyPlaced = true;
    }

    /**
     * Override to change inventory size.
     * Default is 27 slots (3 rows).
     */
    @Override
    public int getContainerSize() {
        return 27;
    }

    /**
     * Override to prevent item storage (useful for ender chest sync).
     * Default is true (stores items).
     */
    protected boolean shouldStoreItems() {
        return true;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        setChanged();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openCount = type;
            if (openCount == 0) { openTicks = 10; }
            if (openCount == 1) { openTicks = 0; }
            open = openCount > 0;
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    /**
     * Tick method for animations.
     * Called every game tick.
     */
    public static <T extends BaseBackpackBlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        // Open animation
        if (blockEntity.open && blockEntity.openTicks < 10) {
            ++blockEntity.openTicks;
        }
        if (!blockEntity.open && blockEntity.openTicks > 0) {
            --blockEntity.openTicks;
        }

        // Place animation
        if (blockEntity.newlyPlaced && blockEntity.placeTicks < 20) {
            ++blockEntity.placeTicks;
        }
        if (blockEntity.placeTicks == 20) {
            blockEntity.newlyPlaced = false;
        }

        // Float animation
        if (blockEntity.floatTicks < 90) {
            ++blockEntity.floatTicks;
        }
        if (blockEntity.floatTicks == 90) {
            blockEntity.floatTicks = 0;
        }
    }

    /**
     * Called when a player opens the backpack.
     */
    public void onOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++openCount;
            if (this.level != null) {
                this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, openCount);
                if (this.openCount == 1) {
                    this.level.gameEvent(player, GameEvent.CONTAINER_OPEN, this.worldPosition);
                    SoundEvent openSound = getOpenSound();
                    if (openSound != null) {
                        this.level.playSound(null, this.getBlockPos(), openSound, SoundSource.BLOCKS);
                    }
                }
            }
        }
    }

    /**
     * Called when a player closes the backpack.
     */
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            --openCount;
            if (this.level != null) {
                this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, openCount);
                if (this.openCount <= 0) {
                    this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                    SoundEvent closeSound = getCloseSound();
                    if (closeSound != null) {
                        this.level.playSound(null, this.getBlockPos(), closeSound, SoundSource.BLOCKS);
                    }
                }
            }
        }
    }

    /**
     * Override to customize open sound.
     * Return null for no sound.
     */
    protected abstract SoundEvent getOpenSound();

    /**
     * Override to customize close sound.
     * Return null for no sound.
     */
    protected abstract SoundEvent getCloseSound();

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.backpack");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return shouldStoreItems() ? this.itemStacks : NonNullList.create();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        if (shouldStoreItems()) {
            this.itemStacks = items;
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return new ShulkerBoxMenu(id, player, this);
    }

    // Client-server synchronization

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadFromTag(tag, lookupProvider);
            if (level != null && level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        loadFromTag(tag, lookupProvider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadFromTag(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (shouldStoreItems() && !this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.itemStacks, false, registries);
        }
        tag.putInt("FloatTicks", this.floatTicks);
        tag.putBoolean("NewlyPlaced", this.newlyPlaced);
        tag.putInt("Color", this.color);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        DyedItemColor dyedItemColor = componentInput.get(DataComponents.DYED_COLOR);
        this.color = dyedItemColor != null ? dyedItemColor.rgb() : 0;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (color != 0) {
            components.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
        }
    }

    /**
     * Load block entity data from NBT.
     * Override to add custom data loading.
     */
    public void loadFromTag(CompoundTag tag, HolderLookup.Provider levelRegistry) {
        if (shouldStoreItems()) {
            this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            if (!this.tryLoadLootTable(tag) && tag.contains("Items", 9)) {
                ContainerHelper.loadAllItems(tag, this.itemStacks, levelRegistry);
            }
        }
        this.floatTicks = tag.getInt("FloatTicks");
        this.newlyPlaced = tag.getBoolean("NewlyPlaced");
        this.color = tag.getInt("Color");
    }
}
