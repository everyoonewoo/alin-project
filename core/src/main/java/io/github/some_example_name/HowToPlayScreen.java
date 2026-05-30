package io.github.some_example_name; // Pastikan package ini SAMA dengan BookwormGame.java

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToPlayScreen implements Screen {

    private final BookwormGame game;
    private Stage stage;
    private Skin skin;

    // Aset yang dikelola oleh screen ini
    private Texture backgroundTexture;
    private Texture howToPlayTexture;

    public HowToPlayScreen(final BookwormGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        // --- PEMUATAN ASET ---
        try {
            // Muat gambar utama untuk layar ini
            backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
            howToPlayTexture = new Texture(Gdx.files.internal("menu/how_to_play.png"));

            // Aset untuk tombol kembali
            // Efek suara klik sekarang diambil dari 'game.clickSound'
            skin.add("quit_button_tex", new Texture(Gdx.files.internal("menu/quit_button.png")));
            skin.add("quit_button_shadow_tex", new Texture(Gdx.files.internal("menu/quit_button_shadow.png")));

        } catch (Exception e) {
            System.err.println("ERROR: Gagal memuat aset untuk HowToPlayScreen.");
            e.printStackTrace();
        }

        // --- STYLE TOMBOL KEMBALI ---
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.up = skin.newDrawable("quit_button_tex");
        backButtonStyle.over = skin.newDrawable("quit_button_shadow_tex");
        backButtonStyle.down = skin.newDrawable("quit_button_tex", Color.GRAY);
        backButtonStyle.font = game.font; // Mengambil font dari kelas Game utama
        backButtonStyle.fontColor = Color.WHITE;
        skin.add("back_style", backButtonStyle);

        // --- LAYOUT TAMPILAN ---
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        // --- BUAT ELEMEN ---
        Image howToPlayImage = new Image(howToPlayTexture);
        final TextButton backButton = new TextButton("BACK", skin, "back_style");

        // Atur posisi tulisan di dalam tombol "BACK"
        backButton.getLabelCell().padBottom(5f);

        // --- AKSI TOMBOL ---
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Memanggil efek suara dari 'game'
                if (game.clickSound != null) game.clickSound.play();

                // Kembali ke MenuScreen
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(0.5f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new MenuScreen(game));
                            dispose();
                        }
                    })
                ));
            }
        });
        float verticalTextOffset = 10f; // Nilai untuk menggeser tulisan ke atas
        float horizontalTextOffset = 15f; // Nilai untuk menggeser tulisan ke kanan
        backButton.getLabelCell().padBottom(verticalTextOffset).padLeft(horizontalTextOffset);
        table.add(howToPlayImage).width(Gdx.graphics.getWidth() * 1f).height(Gdx.graphics.getHeight() * 0.8f).pad(5);
        table.row();
        table.add(backButton).width(350).height(80).pad(20); // Beri jarak 20px dari gambar
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // Menggambar latar belakang
        game.batch.begin();
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        game.batch.end();

        // Menggambar stage (UI) di atas latar belakang
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (howToPlayTexture != null) howToPlayTexture.dispose();
        // Tidak perlu dispose clickSound di sini lagi karena dikelola oleh BookwormGame
    }

    // Metode lain dari interface Screen
    @Override public void show() {
        // Pastikan musik terus berlanjut jika user kembali ke menu ini
        if (game.backgroundMusic != null && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
