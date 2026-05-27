# 🎮 Flexible Asset System for Your Indie Game

## What's New ✨

Your `AssetManager` now has **3 smart cropping methods** that work with ANY spritesheet layout!

---

## The 3 Methods

### 1️⃣ `cropTile()` - Simple Uniform Grids
```java
cropTile(sheet, col, row, size)
```
- **Use for:** Emoji sheets, character sprites, any uniform grid
- **Speed:** ⚡ Fastest
- **Example:** 
  ```java
  BufferedImage emoji = cropTile(emojiSheet, 3, 15, 16);  // 16x16 emoji
  ```

### 2️⃣ `cropTileVariable()` - Mixed-Size Grids
```java
cropTileVariable(sheet, col, row, width, height)
```
- **Use for:** Your Interiors_free_16x16.png with varied furniture sizes
- **Speed:** ⚡ Fast + Flexible
- **Example:**
  ```java
  BufferedImage machine = cropTileVariable(interiorSheet, 12, 18, 32, 48);  // Coffee machine at grid (12,18) but 32x48 pixels
  ```

### 3️⃣ `cropTileAbsolute()` - Exact Pixel Coordinates
```java
cropTileAbsolute(sheet, pixelX, pixelY, width, height)
```
- **Use for:** Irregular layouts where grid math breaks down
- **Speed:** ⚡ Fast + Maximum Control
- **Example:**
  ```java
  BufferedImage asset = cropTileAbsolute(sheet, 245, 180, 48, 48);  // At pixel (245,180)
  ```

---

## AssetDefinition Helper Class

Keeps your asset mappings organized and reusable:

```java
// Grid-based (most common)
AssetDefinition coffee = new AssetDefinition(
    "coffeeTable",           // name
    "Emoji_Spritesheet_Free.png",  // file
    3,                       // col
    15,                      // row
    16,                      // width
    16                       // height
);

// Absolute coordinates (for irregular layouts)
AssetDefinition custom = new AssetDefinition(
    "customAsset",
    "complex.png",
    245,                     // pixel X
    180,                     // pixel Y
    48,                      // width
    48,                      // height
    true                     // isAbsoluteCoords = true
);
```

---

## Quick Reference

| Asset Type | Method | Grid Math | Flexibility |
|-----------|--------|-----------|-------------|
| 🎮 Character sprite (48x48) | `cropTile()` | ✅ Grid-based | Limited |
| 🍕 Emoji (16x16) | `cropTile()` | ✅ Grid-based | Limited |
| 🪑 Furniture (mixed sizes) | `cropTileVariable()` | ✅ Grid-based | Good |
| 🏗️ Complex layout | `cropTileAbsolute()` | ❌ Pixel-based | Best |

---

## Your Current Setup

```java
// Emoji sheet - all 16x16
tableSprite = cropTile(emojiSheet, 3, 15, 16);

// Interiors sheet - variable sizes
coffeeMachineSprite = cropTileVariable(interiorSheet, 12, 18, 32, 48);
milkDispenserSprite = cropTileVariable(interiorSheet, 14, 16, 24, 40);
toppingStationSprite = cropTileVariable(interiorSheet, 13, 18, 28, 44);
stockStationSprite = cropTileVariable(interiorSheet, 8, 12, 48, 56);
cashierCounterSprite = cropTileVariable(interiorSheet, 7, 18, 44, 52);
```

---

## Adding New Assets

**Step 1:** Determine the asset's position and size
- Is it in a uniform grid? → Use `cropTile()`
- Is it in a grid but different size? → Use `cropTileVariable()`
- Is it at an exact pixel? → Use `cropTileAbsolute()`

**Step 2:** Add to AssetManager
```java
// Example: Adding a potted plant
plantSprite = cropTileVariable(interiorSheet, 20, 15, 32, 40);
```

**Step 3:** (Optional) Document in AssetDefinition
```java
new AssetDefinition("pottedPlant", "Interiors_free_16x16.png", 20, 15, 32, 40)
```

---

## Error Handling

All methods include bounds checking:
- ✅ Logs warnings if you exceed sheet boundaries
- ✅ Returns transparent image (won't crash your game)
- ✅ Safe for production

```
Warning: Tile at (99,99) with size (48x48) exceeds sheet bounds!
```

---

## Tips for Best Results

1. **Measure your assets** - Use an image editor to find dimensions
2. **Group by size** - Organize assets by their pixel dimensions
3. **Use AssetDefinition** - Create a registry for all game assets
4. **Test bounds** - Make sure coordinates don't exceed sheet size
5. **Comment positions** - Document the grid/pixel location of each asset

---

## File Structure

```
src/engine/
├── AssetManager.java      ← Updated with 3 flexible methods
└── AssetDefinition.java   ← New helper class for organization
```

---

## Example: Creating an Asset Registry

```java
// In AssetManager.java, create a map for all assets
private static Map<String, BufferedImage> assetRegistry = new HashMap<>();

public static void loadAssetRegistry(BufferedImage emojiSheet, BufferedImage interiorSheet) {
    assetRegistry.put("table", cropTile(emojiSheet, 3, 15, 16));
    assetRegistry.put("coffee_machine", cropTileVariable(interiorSheet, 12, 18, 32, 48));
    assetRegistry.put("milk_dispenser", cropTileVariable(interiorSheet, 14, 16, 24, 40));
    // ... add all your assets
}

// Later, access by name
BufferedImage coffeeTable = assetRegistry.get("table");
```

---

## You're All Set! 🚀

Your indie game now has a professional-grade asset management system that scales with your project. Add thousands of assets without changing your code structure!

Questions? Check the methods' JavaDoc comments in AssetManager.java
