package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class MenuScreen implements Screen {

    private final BookwormGame game;
    private Stage stage;
    private Skin skin;

    // Aset yang dikelola oleh screen ini
    private Texture backgroundTexture;
    private Texture titleTexture;

    // Variabel untuk animasi karakter 1 (Player)
    private Animation<TextureRegion> playerAnimation;
    private Texture playerSheet;
    private Image playerImage;
    private float playerStateTime;

    // Variabel untuk animasi karakter 2 (Wizard)
    private Animation<TextureRegion> wizardAnimation;
    private Texture wizardSheet;
    private Image wizardImage;
    private float wizardStateTime;

    public MenuScreen(final BookwormGame game) {
        this.game = game;

        stage = new Stage(game.viewport, game.batch);
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        // PEMUATAN ASET
        try {
            backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
            titleTexture = new Texture(Gdx.files.internal("menu/Title2.png"));

            playerSheet = new Texture(Gdx.files.internal("characters/player/Idle.png"));
            playerAnimation = createAnimation(playerSheet, 10, 1, 0.08f);
            playerStateTime = 0f;

            wizardSheet = new Texture(Gdx.files.internal("characters/wizard/Idle.png"));
            wizardAnimation = createAnimation(wizardSheet, 8, 1, 0.15f);
            wizardStateTime = 0f;

            skin.add("play_button_tex", new Texture(Gdx.files.internal("menu/play_button.png")));
            skin.add("play_button_shadow_tex", new Texture(Gdx.files.internal("menu/play_button_shadow.png")));
            skin.add("quit_button_tex", new Texture(Gdx.files.internal("menu/quit_button.png")));
            skin.add("quit_button_shadow_tex", new Texture(Gdx.files.internal("menu/quit_button_shadow.png")));
        } catch (Exception e) {
            System.err.println("ERROR: Gagal memuat satu atau lebih aset. Periksa nama file dan lokasi.");
            e.printStackTrace();
        }

        // STYLE TOMBOL
        TextButton.TextButtonStyle mainButtonStyle = new TextButton.TextButtonStyle();
        mainButtonStyle.up = skin.newDrawable("play_button_tex");
        mainButtonStyle.over = skin.newDrawable("play_button_shadow_tex");
        mainButtonStyle.down = skin.newDrawable("play_button_tex", Color.GRAY);
        mainButtonStyle.font = game.font;
        mainButtonStyle.fontColor = Color.WHITE;
        skin.add("default", mainButtonStyle);

        TextButton.TextButtonStyle quitButtonStyle = new TextButton.TextButtonStyle();
        quitButtonStyle.up = skin.newDrawable("quit_button_tex");
        quitButtonStyle.over = skin.newDrawable("quit_button_shadow_tex");
        quitButtonStyle.down = skin.newDrawable("quit_button_tex", Color.GRAY);
        quitButtonStyle.font = game.font;
        quitButtonStyle.fontColor = Color.WHITE;
        skin.add("quit_style", quitButtonStyle);

        // INISIALISASI ELEMEN UI
        Image titleImage = new Image(titleTexture);
        playerImage = new Image(playerAnimation.getKeyFrame(0));
        wizardImage = new Image(wizardAnimation.getKeyFrame(0));

        final TextButton playButton = new TextButton("PLAY", skin, "default");
        final TextButton howToPlayButton = new TextButton("HOW TO PLAY", skin, "default");
        final TextButton exitButton = new TextButton("EXIT", skin, "quit_style");

        playButton.getLabel().setFontScale(1.5f);
        howToPlayButton.getLabel().setFontScale(1.5f);
        exitButton.getLabel().setFontScale(1.5f);

        float verticalTextOffset = 10f;
        float horizontalTextOffset = 15f;
        playButton.getLabelCell().padBottom(verticalTextOffset).padLeft(horizontalTextOffset);
        howToPlayButton.getLabelCell().padBottom(verticalTextOffset).padLeft(horizontalTextOffset);
        exitButton.getLabelCell().padBottom(verticalTextOffset).padLeft(horizontalTextOffset);

        // Sub-Table tengah khusus untuk tombol-tombol menu secara vertikal
        Table buttonSubTable = new Table();
        buttonSubTable.add(playButton).width(280).height(70).pad(10);
        buttonSubTable.row();
        buttonSubTable.add(howToPlayButton).width(280).height(70).pad(10);
        buttonSubTable.row();
        buttonSubTable.add(exitButton).width(280).height(70).pad(10);

        // STRUKTUR LAYOUT GRID UTAMA
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        mainTable.top();

        stage.addActor(mainTable);

        mainTable.add(titleImage).width(950).height(550).colspan(3).padTop(0).center();
        mainTable.row();

        float contentRowPadTop = -200f;

        mainTable.add(playerImage).size(400).bottom().left().expandX().padTop(contentRowPadTop);

        mainTable.add(buttonSubTable).center().expandX().padTop(contentRowPadTop);

        mainTable.add(wizardImage).size(420).bottom().right().expandX().padTop(contentRowPadTop);

        // AKSI TOMBOL
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.clickSound != null) game.clickSound.play();
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(0.4f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new GameScreen(game));
                            dispose();
                        }
                    })
                ));
            }
        });

        howToPlayButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.clickSound != null) game.clickSound.play();
                stage.addAction(Actions.sequence(
                    Actions.fadeOut(0.4f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new HowToPlayScreen(game));
                            dispose();
                        }
                    })
                ));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.clickSound != null) game.clickSound.play();
                stage.addAction(Actions.sequence(Actions.delay(0.2f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                })));
            }
        });
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int frameCols, int frameRows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frameCols, sheet.getHeight() / frameRows);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames.add(tmp[i][j]);
            }
        }
        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        playerStateTime += delta;
        wizardStateTime += delta;

        if (playerAnimation != null) {
            ((TextureRegionDrawable)playerImage.getDrawable()).setRegion(playerAnimation.getKeyFrame(playerStateTime, true));
        }
        if (wizardAnimation != null) {
            ((TextureRegionDrawable)wizardImage.getDrawable()).setRegion(wizardAnimation.getKeyFrame(wizardStateTime, true));
        }

        game.viewport.getCamera().update();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, 1000, 800);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (titleTexture != null) titleTexture.dispose();
        if (playerSheet != null) playerSheet.dispose();
        if (wizardSheet != null) wizardSheet.dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        if (game.backgroundMusic != null && !game.backgroundMusic.isPlaying()) {
            game.backgroundMusic.play();
        }
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
