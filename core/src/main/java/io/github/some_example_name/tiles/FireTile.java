package io.github.some_example_name.tiles;


public class FireTile extends Tile {
    private int bonusDamage;

    public FireTile(char letter, float x, float y, float width, float height) {
        super(letter, "tiles/fire_tile.png", calculateBasicValue(letter), x, y, width, height);
        this.bonusDamage = 3;
        System.out.println("Fire Tile '" + letter + "' created.");
    }

    public int getBonusDamage() {
        return bonusDamage;
    }
}
