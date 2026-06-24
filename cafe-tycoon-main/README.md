# Cafe Tycoon (Merged)

Proyek **resmi gabungan** proto1 (Alan — GUI) + protojaden (Jaden — logika game).

## Struktur folder

```
cafe-tycoon/
├── README.md
├── .gitignore
├── compile_and_run.bat      → memanggil scripts/
├── scripts/
│   └── compile_and_run.bat
├── .vscode/
│   └── settings.json
└── src/
    ├── assets/              ← sprite (opsional, dari proto1)
    ├── main/Main.java       ← entry point
    ├── engine/              ← GamePanel, GameLoop, AssetManager (proto1)
    ├── entity/              ← Player, Customer, TableEntity (gabungan)
    ├── logic/               ← GameEngine, InventoryManager, DrinkRecipe (protojaden)
    ├── station/             ← Coffee/Milk/Topping (protojaden)
    └── ui/                  ← HUD, MenuUI (proto1)
```

Folder `proto1/` dan `protojaden/` = arsip tim; **kerjakan & push dari `cafe-tycoon/` saja**.

## Asset (dari proto1 + UI custom)

| File | Dipakai untuk |
|------|----------------|
| `Basic Charakter Spritesheet.png` | Barista (animasi 4 arah) |
| `Free Chicken Sprites.png` | **Pelanggan/NPC** (ayam) |
| `dialog box medium.png` | Dialog pesanan & menu overlay |
| `Inventory_Light_example_with_slots_2.png` | **HUD atas** (panel slot) |
| `Special Icons.png` | Bintang level, hati, koin |
| `pixelFont-7-8x14-sproutLands.ttf` | Font pixel UI |
| `Interiors_free_16x16.png` | Stasiun dapur |
| `Emoji_Spritesheet_Free.png` | Meja |

Ekspor sprite hasil crop:

```bat
scripts\export_sprites.bat
```

Output: `src/assets/sprites/` dan `src/assets/sprites/ui/`

## Menjalankan

```bat
compile_and_run.bat
```

## Kontrol

| Tombol | Fungsi |
|--------|--------|
| WASD | Gerak |
| E / Space | Interaksi stasiun / meja |
| 1–4 | Pilih topping (di Topping Bar) |
| 1–3 | Beli stok di Supply Shelf |
| Enter | Mulai / lanjut level |
| Esc | Pause |
| R | Ulangi level |

## Keselarasan kode

| Fitur | Sumber |
|-------|--------|
| Level 1–5, target poin, kapasitas meja | `logic/GameEngine.java` ← protojaden |
| Resep kopi & menu | `logic/DrinkRecipe.java` ← `Table.terjemahkanRacikan` |
| Stok & toko | `logic/InventoryManager.java` ← protojaden |
| Stasiun Coffee/Milk/Topping | `station/*.java` ← protojaden |
| Render & loop 60 FPS | `engine/*` ← proto1 |

## Setup GitHub (untuk tim)

```bash
cd cafe-tycoon
git init
git add .
git commit -m "Cafe Tycoon: merge proto1 GUI + protojaden logic"
git remote add origin <url-repo-baru>
git push -u origin main
```

Jangan push folder `proto1/.git` atau struktur acak `Project Cafe Tycoon/` ke repo utama.
