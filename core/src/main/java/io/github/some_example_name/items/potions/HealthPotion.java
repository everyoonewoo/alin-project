package io.github.some_example_name.items.potions;

import io.github.some_example_name.items.Item;
import io.github.some_example_name.entities.Player; // Penting: Pastikan ini diimpor

public class HealthPotion extends Item {
    private int healAmount;

    public HealthPotion() {
        super("Health Potion", "items/health_potion.png");
        this.healAmount = 50;
        System.out.println("Health Potion created.");
    }

    @Override
    public void interact(Player player) {
        System.out.println("Player used Health Potion. Healed " + healAmount + " HP.");
        player.heal(healAmount);
    }

    public int getHealAmount() {
        return healAmount;
    }
}
