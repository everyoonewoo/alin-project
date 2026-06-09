package io.github.some_example_name.entities;

import com.badlogic.gdx.utils.Array;
import io.github.some_example_name.items.Item;
import io.github.some_example_name.items.weapons.Weapon;
import io.github.some_example_name.items.potions.HealthPotion;

public class Player extends GameEntity {
    private int score;
    private Array<Item> inventory;
    private Weapon equippedWeapon;

    public Player(String basePath) { // Menerima base path untuk semua animasi Player
        super(200, 10,
            basePath + "Idle.png", 10, 1, 0.085f,
            400, 400);

        this.score = 0;
        this.inventory = new Array<>();

        setAttackAnimation(basePath + "Attack.png", 7, 1, 0.1f);
        setHitAnimation(basePath + "Hit.png", 3, 1, 0.15f);
        setDyingAnimation(basePath + "Death.png",11,1,0.2f);

        System.out.println("Player created!");
    }

    @Override
    public void attack(GameEntity target) {
        int damageDealt = getAttackPower();
        System.out.println("Player attacks " + target.getClass().getSimpleName() + " for " + damageDealt + " damage.");
        target.takeDamage(damageDealt);
    }

    @Override
    public int getAttackPower() {
        int totalAttack = super.getAttackPower();
        if (equippedWeapon != null) {
            totalAttack += equippedWeapon.getBonusAttack();
        }
        return totalAttack;
    }

    public void addScore(int points) {
        this.score += points;
        System.out.println("Score: " + score);
    }

    public void addItem(Item item) {
        inventory.add(item);
        System.out.println("Player picked up: " + item.getName());
        if (item instanceof HealthPotion) {
            System.out.println("Player now has " + getHealthPotions() + " health potions.");
        }
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void equipWeapon(Weapon newWeapon) {
        // Jika sudah ada senjata yang di-equip DAN senjata baru berbeda dari yang sedang di-equip,
        // kembalikan senjata lama ke inventaris.
        if (this.equippedWeapon != null && this.equippedWeapon != newWeapon) {
            // Hanya tambahkan kembali ke inventaris jika senjata lama belum ada di sana
            // Ini mencegah duplikat jika somehow senjata yang sama di-equip ulang dari inventory
            if (!inventory.contains(this.equippedWeapon, true)) {
                inventory.add(this.equippedWeapon);
                System.out.println("Old weapon '" + this.equippedWeapon.getName() + "' returned to inventory.");
            }
        }

        // Sekarang, equip senjata baru
        this.equippedWeapon = newWeapon;
        System.out.println("Player equipped: " + newWeapon.getName());

        // Hapus senjata yang baru saja di-equip dari inventaris, jika ia memang ada di sana
        // Penting karena senjata baru ini berasal dari inventaris
        if (inventory.contains(newWeapon, true)) {
            inventory.removeValue(newWeapon, true);
            System.out.println("Removed new equipped weapon '" + newWeapon.getName() + "' from inventory.");
        }
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getScore() {
        return score;
    }

    public void addHealthPotion(HealthPotion potion) {
        addItem(potion);
    }

    public int getHealthPotions() {
        int count = 0;
        for (Item item : inventory) {
            if (item instanceof HealthPotion) {
                count++;
            }
        }
        return count;
    }

    public void useHealthPotion() {
        for (int i = 0; i < inventory.size; i++) {
            Item item = inventory.get(i);
            if (item instanceof HealthPotion) {
                HealthPotion potion = (HealthPotion) item;
                potion.interact(this);
                inventory.removeIndex(i);
                System.out.println("Used Health Potion. Remaining: " + getHealthPotions());
                return;
            }
        }
        System.out.println("No Health Potions to use!");
    }
}
