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

public class WinScreen extends ScreenAdapter {
    final BookwormGame game;
    SpriteBatch batch;
    private int finalScore;
    private GlyphLayout winLayout;
    private GlyphLayout scoreLayout;

    private Stage stage;
    private Skin skin;
    private TextButton restartButton;
    private TextButton exitButton;

    public WinScreen(final BookwormGame game, int finalScore) {
        this.game = game;
        this.finalScore = finalScore;
        this.batch = game.batch;

        winLayout = new GlyphLayout();
        scoreLayout = new GlyphLayout();

        stage = new Stage(game.viewport, batch);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add("default-font", game.font);

        float virtualWidth = 1000f;
        float virtualHeight = 800f;

        // Play Again Button
        restartButton = new TextButton("Play Again!", skin);
        restartButton.setSize(250, 70); // Ukuran tombol diperlebar agar teks tidak sesak
        restartButton.setPosition(virtualWidth / 2f - restartButton.getWidth() / 2f, virtualHeight / 2f - 20f);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game)); // Mulai permainan baru
                dispose();
            }
        });
        stage.addActor(restartButton);

        // Exit Button
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
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        game.viewport.getCamera().update();
        batch.setProjectionMatrix(game.viewport.getCamera().combined);

        batch.begin();

        // 1. Gambar Pesan Kemenanga
        game.font.setColor(Color.YELLOW);
        winLayout.setText(game.font, "YOU GOT THE RESURRECTION STONE!");
        float winX = 1000f / 2f - winLayout.width / 2f;
        game.font.draw(batch, winLayout, winX, 560f);

        // 2. Gambar Nilai Akhir Score
        game.font.setColor(Color.WHITE);
        scoreLayout.setText(game.font, "Final Score: " + finalScore);
        float scoreX = 1000f / 2f - scoreLayout.width / 2f;
        game.font.draw(batch, scoreLayout, scoreX, 500f);

        batch.end();

        // Jalankan logika logika dan render tombol UI stage di atas teks
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
