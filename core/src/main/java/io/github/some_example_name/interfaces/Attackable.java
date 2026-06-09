package io.github.some_example_name.interfaces;

public interface Attackable {
    void takeDamage(int amount);
    boolean isAlive();
    int getHealth();
    int getMaxHealth();
}
