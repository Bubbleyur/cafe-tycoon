# Asset Management Guide for Indie Games

Your AssetManager now supports **3 flexible methods** for cropping assets from any spritesheet!

## Method 1: Uniform Grid (16x16 or 48x48)
**Best for:** Emoji sheets, character sheets, any uniform grid
```java
cropTile(sheet, col, row, size);
// Example: Get emoji at grid position (3, 15) from 16x16 grid
BufferedImage emoji = cropTile(emojiSheet, 3, 15, 16);
```

## Method 2: Variable Grid (Mixed tile sizes)
**Best for:** Interiors_free_16x16.png with varied asset sizes
```java
cropTileVariable(sheet, col, row, width, height);
// Example: Coffee machine at grid (12, 18) but actual size is 32x48
BufferedImage machine = cropTileVariable(interiorSheet, 12, 18, 32, 48);
```

## Method 3: Absolute Pixel Coordinates
**Best for:** Complex irregular layouts where grid math breaks down
```java
cropTileAbsolute(sheet, pixelX, pixelY, width, height);
// Example: Custom asset at exact pixel position
BufferedImage custom = cropTileAbsolute(sheet, 120, 245, 64, 64);
```

---

## Using AssetDefinition for Organization

Instead of hardcoding crop coordinates, use AssetDefinition to document your assets:

```java
// Define assets in a config or array
AssetDefinition[] assets = {
    // Emoji sheet (uniform 16x16)
    new AssetDefinition("roundTable", "Emoji_Spritesheet_Free.png", 3, 15, 16, 16),
    new AssetDefinition("greenCheckmark", "Emoji_Spritesheet_Free.png", 0, 8, 16, 16),
    
    // Interiors sheet (variable sizes)
    new AssetDefinition("coffeeMachine", "Interiors_free_16x16.png", 12, 18, 32, 48),
    new AssetDefinition("milkDispenser", "Interiors_free_16x16.png", 14, 16, 24, 40),
    new AssetDefinition("shelfUnit", "Interiors_free_16x16.png", 8, 12, 48, 64),
};

// Then load them dynamically:
for (AssetDefinition def : assets) {
    BufferedImage asset;
    if (def.isAbsoluteCoords) {
        asset = cropTileAbsolute(sheet, def.x, def.y, def.width, def.height);
    } else if (def.width == def.height) {
        asset = cropTile(sheet, def.x, def.y, def.width);  // Assumes square
    } else {
        asset = cropTileVariable(sheet, def.x, def.y, def.width, def.height);
    }
    // Store asset by name
}
```

---

## Why 3 Methods?

| Method | Use Case | Performance | Flexibility |
|--------|----------|-------------|-------------|
| `cropTile()` | Uniform grids | ⚡ Best | Limited (square only) |
| `cropTileVariable()` | Mixed grids | ⚡ Good | Good (any size) |
| `cropTileAbsolute()` | Irregular | ⚡ Good | Best (pixel-perfect) |

---

## Quick Examples

### Example 1: Load emoji sheet (all 16x16)
```java
BufferedImage emojiSheet = ImageIO.read(new File("assets/Emoji_Spritesheet_Free.png"));
BufferedImage apple = cropTile(emojiSheet, 0, 8, 16);  // Apple emoji
BufferedImage heart = cropTile(emojiSheet, 4, 8, 16);  // Heart emoji
```

### Example 2: Load interiors (mixed sizes)
```java
BufferedImage interiorSheet = ImageIO.read(new File("assets/Interiors_free_16x16.png"));

// Small items (16x16)
BufferedImage smallPlant = cropTileVariable(interiorSheet, 0, 0, 16, 16);

// Medium items (32x32)
BufferedImage coffeeTable = cropTileVariable(interiorSheet, 5, 3, 32, 32);

// Large items (48x64)
BufferedImage refrigerator = cropTileVariable(interiorSheet, 10, 12, 48, 64);
```

### Example 3: Absolute pixel coords for complex sheet
```java
// When the grid method doesn't work, use absolute pixels
BufferedImage specialAsset = cropTileAbsolute(sheet, 245, 180, 48, 48);
```

---

## Tips for Your Indie Game

1. **Document asset positions** - Create a spreadsheet or JSON file mapping asset names to their positions
2. **Test bounds checking** - The functions log warnings if you exceed sheet bounds
3. **Standardize sizes** - Group similar assets by their dimensions for consistency
4. **Use AssetDefinition** - Build a registry of all your game assets for reusability
5. **Fallback handling** - Invalid crops return transparent images (won't crash your game)

---

## Migration from Old Code

**Old way:**
```java
tableSprite = cropTile(emojiSheet, 3, 15, 16);
coffeeMachineSprite = cropTile(interiorSheet, 12, 18, 16);  // WRONG SIZE!
```

**New way:**
```java
// Emoji is 16x16
tableSprite = cropTile(emojiSheet, 3, 15, 16);

// Interior coffee machine is 32x48 at grid (12,18)
coffeeMachineSprite = cropTileVariable(interiorSheet, 12, 18, 32, 48);
```

Enjoy flexible asset management! 🎮
