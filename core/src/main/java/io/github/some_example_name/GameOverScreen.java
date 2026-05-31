package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// UI Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen extends ScreenAdapter {
    final BookwormGame game;
    SpriteBatch batch;
    private int finalScore;
    private GlyphLayout gameOverLayout;
    private GlyphLayout scoreLayout;

    private Stage stage;
    private Skin skin;
    private TextButton restartButton;
    private TextButton exitButton;

    public GameOverScreen(final BookwormGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
        this.batch = game.batch;

        gameOverLayout = new GlyphLayout();
        scoreLayout = new GlyphLayout();

        // Menggunakan game.viewport (FitViewport 1000x800) agar sinkron dengan game utama
        stage = new Stage(game.viewport, batch);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add("default-font", game.font);

        // KANVAS VIRTUAL KITA ADALAH WIDTH = 1000, HEIGHT = 800
        float virtualWidth = 1000f;
        float virtualHeight = 800f;

        // Restart Button (Diposisikan pas di tengah vertikal)
        restartButton = new TextButton("Restart Game", skin);
        restartButton.setSize(250, 70); // Diperbesar sedikit agar lebih proporsional
        restartButton.setPosition(virtualWidth / 2f - restartButton.getWidth() / 2f, virtualHeight / 2f - 20f);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game)); // Mulai permainan baru
                dispose();
            }
        });
        stage.addActor(restartButton);

        // Exit Button (Diposisikan di bawah tombol restart)
        exitButton = new TextButton("Exit Game", skin);
        exitButton.setSize(250, 70);
        exitButton.setPosition(virtualWidth / 2f - exitButton.getWidth() / 2f, virtualHeight / 2f - 110f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit(); // Tutup aplikasi
            }
        });
        stage.addActor(exitButton);
    }

    @Override
    public void render(float delta) {
        // Bersihkan layar dengan warna abu-abu gelap estetik
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        // SINKRONISASI UTAMA: Paksa batch mengikuti kamera milik viewport (1000x800)
        game.viewport.getCamera().update();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);

        batch.begin();

        // 1. Gambar Teks "GAME OVER!" di atas tombol
        game.font.setColor(Color.RED);
        gameOverLayout.setText(game.font, "GAME OVER!");
        // Posisi X: Tengah kanvas (500), Posisi Y: Melayang tinggi di atas tombol (Y = 560f)
        float gameOverX = 1000f / 2f - gameOverLayout.width / 2f;
        game.font.draw(batch, gameOverLayout, gameOverX, 560f);

        // 2. Gambar Teks "Final Score" tepat di bawah GAME OVER!
        game.font.setColor(Color.WHITE);
        scoreLayout.setText(game.font, "Final Score: " + finalScore);
        // Posisi X: Tengah kanvas (500), Posisi Y: Di bawah judul (Y = 500f)
        float scoreX = 1000f / 2f - scoreLayout.width / 2f;
        game.font.draw(batch, scoreLayout, scoreX, 500f);

        batch.end();

        // Update dan gambar tombol UI
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Sinkronisasi pembaruan ukuran window agar rasio 1000x800 tetap terjaga di tengah layar
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        // Kunci input processor ke stage ini saat screen tampil
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
