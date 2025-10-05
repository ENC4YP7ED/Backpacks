# Backpack for Dummies

A balanced backpack mod for Minecraft 1.21.1 (NeoForge) focusing on simplicity and gameplay balance.

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)](https://www.minecraft.net/)
[![Mod Loader](https://img.shields.io/badge/Mod%20Loader-NeoForge-orange.svg)](https://neoforged.net/)
[![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-red.svg)](LICENSE)

## Features

### Core Features
- **Wearable Storage**: Backpacks can be worn in the chest armor slot or placed as decorative blocks
- **27 Inventory Slots**: Same capacity as a shulker box for balanced gameplay
- **Dyeable**: Customize your backpack with any dye color, just like leather armor
- **Interactive Access**: Access other players' or entities' backpacks by approaching from behind
- **Real-Time Multiplayer Sync**: Multiple players can access the same backpack simultaneously with live inventory updates - no duplication possible
- **Visual Animations**: Smooth opening and closing animations with custom sound effects
- **Safety Features**: Cannot remove a backpack while it contains items (prevents accidental item loss)
- **Dual-Mode Item**: Functions as both wearable equipment and a placeable block

### Crafting
- **8 Leather** in a hollow square pattern (unless Farmer's Delight is installed, which adds a rope alternative)

## Accessories Mod Integration

**NEW!** This fork adds full integration with the [Accessories mod](https://modrinth.com/mod/accessories):

### What's New

#### Dedicated Backpack Slot
- When Accessories is installed, backpacks can be equipped in a dedicated **"back" accessories slot**
- No longer occupies your chest armor slot - wear your chestplate and backpack simultaneously!
- Works seamlessly with or without Accessories installed

#### Keybinding Support
- **Press 'B'** (configurable) to quickly open your backpack inventory
- Only active when Accessories mod is installed
- Customizable in Controls → "Backpack for Dummies" category
- Works whether backpack is in chest slot or accessories slot

#### Smart Placement & Pickup
- **Place blocks from accessories slot**: Shift+right-click to place backpack as a block, even when equipped in accessories
- **Smart pickup prioritization**: Picking up backpacks automatically goes to accessories slot first, then chest slot
- **Seamless slot switching**: Works intelligently whether Accessories is installed or not

#### Rendering Behavior
- **Invisible in Accessories Slot**: When equipped in the accessories slot, the backpack is **not rendered** on the player's back
- **Reason**: The backpack uses its own custom model/rendering system from the chest armor slot. Accessories mod's item rendering is disabled to avoid conflicts and unnecessary visual duplication
- **Chest Slot**: Full visual rendering with opening/closing animations when equipped in chest slot

#### Full Feature Compatibility
All original features work in both slots:
- ✅ Opening/closing animations (chest slot only)
- ✅ Behind-the-back access from other players
- ✅ Dyeable colors
- ✅ Safety lock (can't remove non-empty backpacks)
- ✅ Sound effects
- ✅ Block placement from either slot
- ✅ Automatic return to preferred slot on pickup

### Optional Integration
The Accessories integration is **completely optional**:
- Works perfectly fine without Accessories installed (uses chest armor slot)
- Automatically detects and enables features when Accessories is present
- No crashes or errors when Accessories is missing

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1 (version 21.1.137 or higher)
2. Download the mod from the [Releases](../../releases) page
3. Place the `.jar` file in your `mods` folder
4. *Optional:* Install [Accessories](https://modrinth.com/mod/accessories) for enhanced functionality
5. Launch Minecraft and enjoy!

## Optional Dependencies

- **[Accessories](https://modrinth.com/mod/accessories)** (1.1.0-beta.39+1.21.1 or higher) - Adds dedicated backpack slot and keybinding
- **[Farmer's Delight](https://modrinth.com/mod/farmers-delight)** - Adds alternative crafting recipe using rope
- **[JEI](https://modrinth.com/mod/jei)** - Recipe viewer support
- **[Figura](https://modrinth.com/mod/figura)** - Custom avatar compatibility

## Compatibility

### Tested Mods
- ✅ Sodium
- ✅ Iris Shaders
- ✅ Farmer's Delight
- ✅ Accessories
- ✅ Spyglass Improvements
- ✅ Figura

### Technical Details
- **Minecraft Version**: 1.21.1
- **Mod Loader**: NeoForge 21.1.137+
- **Java Version**: 21+
- **Mod Version**: 0.3.2

## Usage

### Equipping a Backpack
1. **Without Accessories**: Place in chest armor slot
2. **With Accessories**: Place in the "back" accessories slot OR chest armor slot
3. Press **B** (with Accessories) or right-click to open

### Accessing Other Players' Backpacks
1. Approach a player/entity from **behind**
2. Right-click on them
3. Their backpack inventory will open (if they're wearing one)

### Placing as Block
1. Right-click on any surface to place
2. Retains inventory contents when placed/broken
3. Displays opening animation when accessed

## Development

### Building from Source
```bash
git clone <your-fork-url>
cd Backpacks
chmod +x gradlew
./gradlew build
```

The compiled `.jar` will be in `build/libs/`

### Development Setup
```bash
./gradlew runClient  # Launch test client
./gradlew runServer  # Launch test server
```

## Credits

### Original Author
- **Spydnel** - Original "Backpack for Dummies" mod

### Contributors
- **Community contributions** - Accessories integration, keybinding system, and enhanced compatibility

### Libraries & Dependencies
- [NeoForge](https://neoforged.net/) - Mod loader
- [Accessories API](https://github.com/wisp-forest/accessories) - Accessory slot system
- [MixinConstraints](https://github.com/Moulberry/MixinConstraints) - Mixin compatibility

## Changes from Original

This fork adds the following enhancements to the original Backpack for Dummies mod:

### Accessories Mod Integration (v0.3.2+)

#### New Features Added
1. **Dedicated Accessories Slot Support**
   - Full integration with the Accessories mod's "back" slot
   - Backpacks can now be equipped without occupying chest armor slot
   - Automatic detection and fallback to chest slot when Accessories isn't installed

2. **Keybinding System**
   - New configurable keybind (default: **B**) to open backpack
   - Found in Controls → "Backpack for Dummies" category
   - Works in both chest and accessories slots
   - Client-server synchronized opening

3. **Enhanced Compatibility**
   - Reflection-based optional loading prevents crashes
   - All features work in both equipment slots
   - Accessories slot rendering disabled (backpack uses custom chest armor model)
   - Behind-the-back access checks both slots

#### Technical Improvements
- **New Classes Added**:
  - `BackpackAccessory` - Implements Accessory interface
  - `AccessoriesIntegration` - Server-side integration handler
  - `BackpackAccessoryRenderer` - Renderer with rendering disabled (backpack already has model)
  - `AccessoriesClientIntegration` - Client-side integration
  - `BackpackKeybindings` - Keybinding registration and handling
  - `OpenBackpackPayload` & Handler - Network packet for keybind

- **Modified Files**:
  - Updated `EntityInteractionEvents` to check accessories slot
  - Updated `BackpackItemContainer` to work with both slots
  - Updated `BackpackLayer` renderer with accessories support
  - Enhanced `Backpacks` main class with optional mod loading

- **Data Files**:
  - Added `data/accessories/tags/item/back.json` for slot registration
  - Updated all language files with keybinding translations
  - Added optional dependency in `neoforge.mods.toml`

- **Build Configuration**:
  - Added Accessories Maven repository
  - Added compileOnly and localRuntime dependencies
  - Version: accessories-neoforge 1.1.0-beta.39+1.21.1

#### Enhanced Gameplay Features
4. **Smart Placement & Pickup System**
   - Backpacks in accessories slot can be placed as blocks (same as chest slot)
   - Picking up backpack blocks prioritizes accessories slot
   - Item pickups automatically go to best available slot
   - Works seamlessly with or without Accessories installed

5. **Real-Time Multiplayer Synchronization**
   - Multiple players can access the same backpack simultaneously
   - Shared container instance ensures all viewers see the same inventory
   - Real-time updates: when one player moves/removes items, all viewers see changes instantly
   - Prevents item duplication in multiplayer scenarios
   - Automatic viewer tracking and container cleanup

### Multilingual Support
Keybinding translations added for:
- 🇺🇸 English
- 🇩🇪 German
- 🇪🇸 Spanish (Mexico)
- 🇷🇺 Russian
- 🇸🇪 Swedish

All changes maintain full backwards compatibility with the original mod behavior when Accessories is not installed.

## License

All Rights Reserved

This mod is the property of the original author. Please respect the author's rights.

## Addon Development API

Backpack for Dummies provides a comprehensive API for creating addon mods that add custom backpack types. The API handles all the complex logic, allowing you to focus on your unique features.

### Quick Start

Create a custom backpack addon in just a few steps:

1. **Add dependency to your build.gradle**:
```gradle
dependencies {
    implementation files('../Backpacks-master/build/libs/backpacks-0.3.2.jar')

    // Optional: Accessories API
    compileOnly "io.wispforest:accessories-neoforge:1.1.0-beta.39+1.21.1"
    localRuntime "io.wispforest:accessories-neoforge:1.1.0-beta.39+1.21.1"
}
```

2. **Extend base classes**:

```java
// Item
public class MyBackpackItem extends BaseBackpackItem {
    public MyBackpackItem(Block block, Properties properties) {
        super(block, properties);
    }
}

// Block
public class MyBackpackBlock extends BaseBackpackBlock {
    @Override
    protected BlockEntityType<?> getBlockEntityType() {
        return MyBlockEntities.MY_BACKPACK.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MyBackpackBlockEntity(pos, state);
    }
}

// Block Entity
public class MyBackpackBlockEntity extends BaseBackpackBlockEntity {
    public MyBackpackBlockEntity(BlockPos pos, BlockState state) {
        super(MyBlockEntities.MY_BACKPACK.get(), pos, state);
    }

    @Override
    protected SoundEvent getOpenSound() {
        return SoundEvents.CHEST_OPEN;
    }

    @Override
    protected SoundEvent getCloseSound() {
        return SoundEvents.CHEST_CLOSE;
    }
}
```

### API Packages

#### `com.spydnel.backpacks.api.item`
- **BaseBackpackItem** - Base class for backpack items
  - Prevents nesting by default
  - Inherits all BlockItem functionality

#### `com.spydnel.backpacks.api.block`
- **BaseBackpackBlock** - Base class for backpack blocks
  - Handles waterlogging, floating, directional placement
  - Provides hooks for custom menu opening
- **BaseBackpackBlockEntity** - Base class for block entities
  - Handles animations (open, float, place)
  - Manages color data and NBT serialization
  - Override `shouldStoreItems()` to disable item storage (for ender chest sync, etc.)

#### `com.spydnel.backpacks.api.integration`
- **BaseBackpackAccessory** - Base for Accessories API integration
  - Automatic dual-equip prevention
  - Override `canUnequip()` for custom logic
- **AccessoriesHelper** - Utility methods for checking accessories
  - `getBackpackFromAccessories()` - Get backpack from accessories slot
  - `isWearingBackpackInAccessories()` - Check if wearing in accessories
  - **Graceful degradation**: All methods return null/false if Accessories not loaded
- **AccessoriesIntegrationHelper** - Simplified registration
  - `registerAccessory()` - One-line registration with automatic error handling
- **DualEquipHelper** - Prevent dual-equipping backpacks
  - `isWearingBackpackInOtherSlot()` - Check opposite slot

#### `com.spydnel.backpacks.api.events`
- **BackpackEventHelper** - Common event operations
  - `isBehind()` - Check if player is behind target
  - `getBackpackFromEntity()` - Get backpack from chest OR accessories
  - `isWearingBackpack()` - Check if entity wears specific backpack

#### `com.spydnel.backpacks.api.networking`
- **BackpackNetworkHelper** - Networking utilities
  - `openBackpackInventory()` - Open backpack menu for player
  - `canOpenBackpack()` - Check if player can open

#### `com.spydnel.backpacks.api.client` (Client-side only)
- **BackpackKeybindingRegistry** - Register custom keybindings
  - `registerKeybinding()` - Register under main mod's category
  - Keybindings automatically appear in Controls menu

### Features

#### ✅ Dual-Equip Prevention (Hardcoded)
The API automatically prevents equipping a backpack in chest slot when already wearing one in accessories (and vice versa). This is **hardcoded** and works for all backpack types.

```java
// In your BackpackAccessory class:
public class MyBackpackAccessory extends BaseBackpackAccessory {
    @Override
    protected Item getBackpackItem() {
        return MyItems.MY_BACKPACK.get();
    }
    // Dual-equip prevention is automatic!
}
```

#### ✅ Graceful Accessories API Degradation
All API methods gracefully handle when Accessories mod is not installed:
- Helper methods return `null` or `false`
- No crashes or errors
- Your addon works with OR without Accessories

```java
// This works whether Accessories is installed or not:
ItemStack backpack = BackpackEventHelper.getBackpackFromEntity(player, MyItems.MY_BACKPACK.get());
if (backpack != null) {
    // Handle backpack (works in chest OR accessories slot)
}
```

#### ✅ Custom Keybindings
Register your own keybinding that appears under "Backpack for Dummies" category:

```java
// Client-side only!
@OnlyIn(Dist.CLIENT)
public class MyBackpackKeybindings {
    public static KeyMapping OPEN_MY_BACKPACK;

    public static void register() {
        OPEN_MY_BACKPACK = BackpackKeybindingRegistry.registerKeybinding(
            "key.mymod.open_my_backpack",  // Translation key
            GLFW.GLFW_KEY_B,                // Default key (B)
            "My Custom Backpack"            // Display name
        );
    }
}

// In your mod constructor (client-side):
if (FMLEnvironment.dist == Dist.CLIENT) {
    MyBackpackKeybindings.register();
}
```

### Example: Ender Backpack Addon

See the complete example at: https://github.com/ENC4YP7ED/EnderBackpacks-addon

Key features demonstrated:
- Extends `BaseBackpackItem`, `BaseBackpackBlock`, `BaseBackpackBlockEntity`
- Custom ender chest synchronization (override `shouldStoreItems()`)
- Accessories integration using `BaseBackpackAccessory`
- Custom keybinding registration
- **171 lines of code saved** by using the API (76% reduction in block entity!)

### Code Reduction Example

**Before API** (161 lines):
```java
public class EnderBackpackBlockEntity extends BlockEntity {
    // Manual animation handling
    // Manual NBT serialization
    // Manual client-server sync
    // Manual sound effects
    // ... 161 lines of boilerplate
}
```

**After API** (39 lines):
```java
public class EnderBackpackBlockEntity extends BaseBackpackBlockEntity {
    public EnderBackpackBlockEntity(BlockPos pos, BlockState state) {
        super(EBBlockEntities.ENDER_BACKPACK.get(), pos, state);
    }

    @Override
    protected boolean shouldStoreItems() { return false; }

    @Override
    protected SoundEvent getOpenSound() { return BPSounds.BACKPACK_OPEN.value(); }

    @Override
    protected SoundEvent getCloseSound() { return BPSounds.BACKPACK_CLOSE.value(); }
}
```

**76% reduction** - everything else is handled by the API!

### Best Practices

1. **Always check for null** when using helper methods
2. **Use graceful degradation** - don't check `isAccessoriesLoaded()` manually
3. **Override only what you need** - base classes handle the rest
4. **Register keybindings client-side only** - use `@OnlyIn(Dist.CLIENT)`
5. **Test with and without Accessories** to ensure compatibility

### Support

For API questions or addon development help, please open an issue with the `api` label.

## Support

For bug reports and feature requests, please use the [Issues](../../issues) page.

---

**Enjoy your adventures with Backpack for Dummies!** 🎒
