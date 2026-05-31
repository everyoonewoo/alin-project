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

public class HowToPlayScreen implements Screen {

    private final BookwormGame game;
    private Stage stage;
    private Skin skin;

    // Aset yang dikelola oleh screen ini
    private Texture backgroundTexture;
    private Texture howToPlayTexture;

    public HowToPlayScreen(final BookwormGame game) {
        this.game = game;

        // DIUBAH: Menggunakan game.viewport (FitViewport 1000x800) agar terkunci rapi di frame tengah
        stage = new Stage(game.viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        // --- PEMUATAN ASET ---
        try {
            // Muat gambar utama untuk layar ini
            backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
            howToPlayTexture = new Texture(Gdx.files.internal("menu/how_to_play.png"));

            // Aset untuk tombol kembali
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
        backButtonStyle.font = game.font;
        backButtonStyle.fontColor = Color.WHITE;
        skin.add("back_style", backButtonStyle);

        // --- LAYOUT TAMPILAN (MENGGUNAKAN SKALA VIRTUAL 1000x800) ---
        Table table = new Table();
        table.setFillParent(true);
        table.top();
        stage.addActor(table);

        // --- BUAT ELEMEN ---
        Image howToPlayImage = new Image(howToPlayTexture);
        final TextButton backButton = new TextButton("BACK", skin, "back_style");

        // Atur posisi tulisan di dalam tombol "BACK" agar pas di tengah tombol
        backButton.getLabel().setFontScale(1.5f);
        float verticalTextOffset = 10f;
        float horizontalTextOffset = 15f;
        backButton.getLabelCell().padBottom(verticalTextOffset).padLeft(horizontalTextOffset);

        // --- AKSI TOMBOL ---
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.clickSound != null) game.clickSound.play();

                // Kembali ke MenuScreen dengan efek fadeOut yang mulus
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(0.4f),
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

        // DIUBAH: Mengunci ukuran gambar panduan dan tombol secara statis di dalam kanvas virtual 1000x800
        // Lebar gambar dibuat 750px dan tinggi 450px agar proporsional dan menyisakan ruang untuk tombol BACK di bawahnya
        table.add(howToPlayImage).width(1050).height(650).padTop(10).center();
        table.row();
        table.add(backButton).width(280).height(70).padTop(15);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        // DIUBAH: Menggunakan projection matrix FitViewport agar background tergambar presisi di frame tengah
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        if (backgroundTexture != null) {
            // Menggambar background pas dengan ukuran kanvas virtual 1000x800
            game.batch.draw(backgroundTexture, 0, 0, 1000, 800);
        }
        game.batch.end();

        // Menggambar stage (UI) di atas latar belakang
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // DIUBAH: Sinkronisasi pembaruan ukuran layar menggunakan game.viewport
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (howToPlayTexture != null) howToPlayTexture.dispose();
    }

    @Override
    public void show() {
        if (game.backgroundMusic != null && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
