package com.spydnel.backpacks.api.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * Base class for custom backpack items.
 * Addon mods should extend this class to create their own backpack variants.
 *
 * Example usage:
 * <pre>
 * public class EnderBackpackItem extends BaseBackpackItem {
 *     public EnderBackpackItem(Block block, Properties properties) {
 *         super(block, properties);
 *     }
 *
 *     @Override
 *     public void onCraftedBy(ItemStack stack, Level level, Player player) {
 *         super.onCraftedBy(stack, level, player);
 *         // Add custom color or NBT data
 *     }
 * }
 * </pre>
 */
public class BaseBackpackItem extends BlockItem {

    public BaseBackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    /**
     * Override this to customize whether backpacks can be nested.
     * Default implementation prevents nesting.
     */
    protected boolean allowNesting() {
        return false;
    }
}
