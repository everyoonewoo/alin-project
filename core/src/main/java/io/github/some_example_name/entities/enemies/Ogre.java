package io.github.some_example_name.entities.enemies;

public class Ogre extends Enemy {
    public Ogre(String basePath) {
        super(80, 15, 30,
            basePath + "Idle.png", 4, 1, 0.15f,
            basePath + "Attack.png", 8, 1, 0.1f,
            basePath + "Hit.png", 4, 1, 0.15f,
            basePath + "Death.png", 4, 1, 0.15f,
            475, 475);
        System.out.println("Ogre spawned!");
    }
}
