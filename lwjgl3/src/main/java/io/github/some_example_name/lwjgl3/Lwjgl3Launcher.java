package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.some_example_name.BookwormGame;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new BookwormGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        // Mengatur judul pada window bar atas
        configuration.setTitle("BookwormGame");

        // 1. Memenuhi layar monitor saat pertama dibuka
        configuration.setMaximized(true);

        // 2. Mengatur ukuran window dasar/default sebelum di-maximize (1000x800 sesuai kanvas virtual)
        configuration.setWindowedMode(1000, 800);

        // 3. Mengizinkan resize agar FitViewport di GameScreen
        configuration.setResizable(true);

        // Mengatur batas FPS dan sinkronisasi vertikal (Vsync) agar game berjalan mulus tanpa patah-patah
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        return configuration;
    }
}
