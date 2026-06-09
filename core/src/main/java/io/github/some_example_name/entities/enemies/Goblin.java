package io.github.some_example_name.entities.enemies;

import io.github.some_example_name.entities.GameEntity;

public class Goblin extends Enemy {
    public Goblin(String basePath) {
        super(30, 5, 10,
            basePath + "Idle.png", 4, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f,
            basePath + "Hit.png", 4, 1, 0.15f,
            basePath + "Death.png", 4, 1, 0.15f,
            475, 475);
        System.out.println("Goblin spawned!");
    }
}
