package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BookwormGame extends Game {

    // Aset bersama untuk seluruh game
    public SpriteBatch batch;
    public BitmapFont font;
    public Viewport viewport;

    // Aset audio bersama
    public Music backgroundMusic;
    public Sound clickSound;

    // Aset visual game
    private Texture tileTexture;
    private TextureRegion tileTextureRegion;
    private Texture tileHighlightTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(1000,800);

        // Inisialisasi font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        parameter.color = Color.WHITE;
        font = generator.generateFont(parameter);
        generator.dispose();

        // Inisialisasi aset visual untuk tile
        tileTexture = new Texture(Gdx.files.internal("tiles/basic_tile.png"));
        tileTextureRegion = new TextureRegion(tileTexture);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        tileHighlightTexture = new Texture(pixmap);
        pixmap.dispose();

        // Memuat musik latar
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("menu_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("ERROR: Gagal memuat 'audio/menu_music.mp3'.");
        }

        // Memuat efek suara klik
        try {
            clickSound = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
        } catch (Exception e) {
            System.err.println("ERROR: Gagal memuat 'audio/click.mp3'.");
        }

        // Tampilkan MenuScreen saat game dimulai
        this.setScreen(new MenuScreen(this));
    }

    // Getter untuk aset visual
    public TextureRegion getTileTextureRegion() {
        return tileTextureRegion;
    }

    public Texture getTileHighlightTexture() {
        return tileHighlightTexture;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Hapus semua aset bersama saat game ditutup
        super.dispose();
        batch.dispose();
        font.dispose();

        // Dispose aset visual
        if (tileTexture != null) tileTexture.dispose();
        if (tileHighlightTexture != null) tileHighlightTexture.dispose();

        // Dispose aset audio
        if (backgroundMusic != null) backgroundMusic.dispose();
        if (clickSound != null) clickSound.dispose();
    }
}
