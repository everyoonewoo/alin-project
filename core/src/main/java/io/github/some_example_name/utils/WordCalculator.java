package io.github.some_example_name.utils;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.tiles.GemTile;

import io.github.some_example_name.tiles.FireTile;

public class WordCalculator {
    public static int calculateWordValue(Array<Tile> selectedTiles) {
        int totalTileValues = 0; // Penjumlahan nilai dasar setiap tile (termasuk dari FireTile)
        int wordMultiplier = 1;
        int fireTileDirectDamageBonus = 0;

        for (Tile tile : selectedTiles) {
            totalTileValues ++; // Mengambil nilai dasar tile (yang sudah termasuk bonus dari FireTile jika itu FireTile)

            if (tile instanceof GemTile) {
                wordMultiplier *= ((GemTile) tile).getBonusMultiplier();
            } else if (tile instanceof FireTile) {
                // Menambahkan bonus damage spesifik dari FireTile
                fireTileDirectDamageBonus += ((FireTile) tile).getBonusDamage();
            }
        }
        System.out.println("firegemtile:"+fireTileDirectDamageBonus);
        System.out.println("wordmultiplier:"+wordMultiplier);
        System.out.println("totaltilevalue:"+totalTileValues);
        // Total damage adalah (penjumlahan nilai dasar * multiplier) + bonus damage langsung dari FireTile
        return (totalTileValues * wordMultiplier) + fireTileDirectDamageBonus;
    }
}
