package io.github.some_example_name.entities.enemies;

public class Wizard extends Enemy {
    public Wizard(String basePath) {
        super(500, 20, 1000,
            basePath + "Idle.png", 8, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f,
            basePath + "Hit.png", 4, 1, 0.15f,
            basePath + "Death.png", 5, 1, 0.15f,
           475, 375);
        System.out.println("Wizard (Final Boss) spawned!");
    }
}
