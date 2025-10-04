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

#### Full Feature Compatibility
All original features work in both slots:
- ✅ Opening/closing animations
- ✅ Behind-the-back access from other players
- ✅ Dyeable colors
- ✅ Safety lock (can't remove non-empty backpacks)
- ✅ Visual rendering with animations
- ✅ Sound effects

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
   - Rendering system updated to support accessories slot
   - Behind-the-back access checks both slots

#### Technical Improvements
- **New Classes Added**:
  - `BackpackAccessory` - Implements Accessory interface
  - `AccessoriesIntegration` - Server-side integration handler
  - `BackpackAccessoryRenderer` - Custom renderer for accessories
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

#### Multilingual Support
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

## Support

For bug reports and feature requests, please use the [Issues](../../issues) page.

---

**Enjoy your adventures with Backpack for Dummies!** 🎒
