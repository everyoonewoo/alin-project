package io.github.some_example_name.entities.enemies;

public class Dragon extends Enemy {
    public Dragon(String basePath) {
        super(200, 25, 100,
            basePath + "Idle.png", 8, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f,
            basePath + "Hit.png", 4, 1, 0.15f,
            basePath + "Death.png", 4, 1, 0.15f,
            475, 475);
        System.out.println("Dragon (Mini Boss) spawned!");
    }
}
