package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.some_example_name.BookwormGame;

/** Meluncurkan aplikasi desktop berbasis LWJGL3. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; // Diperlukan untuk stabilitas macOS/Linux (jika ada)
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new BookwormGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        // Mengatur judul pada window bar atas
        configuration.setTitle("BookwormGame");

        // 1. MENGAKTIFKAN MODE MAKSIMAL (Memenuhi layar monitor saat pertama dibuka)
        configuration.setMaximized(true);

        // 2. Mengatur ukuran window dasar/default sebelum di-maximize (1000x800 sesuai kanvas virtual)
        configuration.setWindowedMode(1000, 800);

        // 3. WAJIB TRUE: Mengizinkan resize agar FitViewport di GameScreen
        // bisa otomatis menghitung ulang skala gambar ke tengah layar monitor.
        configuration.setResizable(true);

        // Mengatur batas FPS dan sinkronisasi vertikal (Vsync) agar game berjalan mulus tanpa patah-patah
        configuration.useVsync(true);
        configuration.setForegroundFPS(60);

        // Mengatur icon aplikasi (Opsional, bawaan libGDX jika kamu menggunakannya nanti)
        // configuration.setWindowIcon("libgdx16.png", "libgdx32.png", "libgdx64.png", "libgdx128.png");

        return configuration;
    }
}
