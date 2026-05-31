package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

// UI Imports
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

// Import kelas-kelas OOP
import io.github.some_example_name.entities.GameEntity;
import io.github.some_example_name.entities.Player;
import io.github.some_example_name.entities.enemies.*;
import io.github.some_example_name.items.weapons.*;
import io.github.some_example_name.tiles.Tile;
import io.github.some_example_name.effects.tile.TileEffect;
import io.github.some_example_name.utils.WordCalculator;
import io.github.some_example_name.utils.WordDictionary;
import io.github.some_example_name.utils.GameBoard;
import io.github.some_example_name.items.potions.HealthPotion;
import io.github.some_example_name.items.Item;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class GameScreen extends ScreenAdapter {
    // Kunci ukuran virtual kanvas game (Membasmi masalah tumpang tindih)
    private static final float V_WIDTH = 1000f;
    private static final float V_HEIGHT = 800f;

    final BookwormGame game;
    OrthographicCamera camera;
    SpriteBatch batch;

    Player player;
    Enemy currentEnemy;
    public GameBoard gameBoard;

    private Array<Tile> selectedTiles;
    private String currentWord;

    private float tileSize = 64;
    private int gridRows = 8;
    private int gridCols = 8;
    private float gridStartX;
    private float gridStartY;

    private GlyphLayout glyphLayout;
    private TextureRegion defaultTileTextureRegion;

    // UI elements
    private Stage stage;
    private Skin skin;
    private TextButton submitButton;
    private TextButton clearButton;
    private TextButton usePotionButton;
    private GlyphLayout potionCountLayout;
    private TextButton backpackButton;
    private boolean isBackpackOpen = false;
    private com.badlogic.gdx.scenes.scene2d.ui.Window backpackWindow;
    private com.badlogic.gdx.scenes.scene2d.ui.Table inventoryTable;
    private ScrollPane inventoryScrollPane;
    private BitmapFont tileFont;

    private float enemyX;
    private float enemyY;

    private Texture backgroundImage;
    private Music backgroundMusic;
    private Sound playerHitSound;
    private Sound playerDeathSound;
    private Sound playerAttackSound;

    private Sound enemyHitSound;
    private Sound enemyDeathSound;
    private Sound enemyAttackSound;

    private float progress = 0f;

    private enum BattleState {
        PLAYER_INPUT,
        PLAYER_ATTACK_ANIMATION,
        ENEMY_HIT_ANIMATION,
        ENEMY_TURN_ATTACK_ANIMATION,
        PLAYER_HIT_ANIMATION,
        ENEMY_DYING_ANIMATION,
        PLAYER_DYING_ANIMATION,
        CHECK_ROUND_END,
        GAME_OVER_SCREEN
    }
    private BattleState currentBattleState;

    public GameScreen(final BookwormGame game) {
        this.game = game;
        batch = game.batch;

        WordDictionary.loadDictionary();

        // Diperbaiki: Kamera disamakan dengan ukuran dasar kanvas game (1000x800)
        camera = new OrthographicCamera();
        camera.setToOrtho(false, V_WIDTH, V_HEIGHT);

        gridStartX = (V_WIDTH - (gridCols * tileSize)) / 2;
        gridStartY = (V_HEIGHT - (gridRows * tileSize)) / 2;

        player = new Player("characters/player/");
        player.equipWeapon(new BasicSword());

        currentEnemy = new Goblin("characters/goblin/");
        enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_hit.mp3"));
        enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_attack.mp3"));
        enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));

        defaultTileTextureRegion = game.getTileTextureRegion();

        // Diperbaiki: Posisi musuh digeser agar pas di dalam frame kanan bawah
        enemyX = 650f;
        enemyY = 415f;

        backgroundImage = new Texture(Gdx.files.internal("background.jpg"));
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background_music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.25f);
        backgroundMusic.play();

        playerHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/player_hit.wav"));
        playerDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/death.wav"));
        playerAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/player/player_attack.mp3"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/tile_font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.BLACK;
        tileFont = generator.generateFont(parameter);
        generator.dispose();

        initializeNewBoard();

        selectedTiles = new Array<>();
        currentWord = "";

        glyphLayout = new GlyphLayout();
        potionCountLayout = new GlyphLayout();

        // Diperbaiki: Menggunakan viewport utama game agar ikut membesar ke tengah
        stage = new Stage(game.viewport, batch);
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
        skin.add("default-font", game.font);

        // --- TOMBOL SUBMIT (Diatur presisi di bawah grid) ---
        submitButton = new TextButton("SUBMIT", skin);
        submitButton.setSize(120, 45);
        submitButton.setPosition(V_WIDTH / 2f - 130f, 50f);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                processWord();
            }
        });
        stage.addActor(submitButton);

        // --- TOMBOL CLEAR ---
        clearButton = new TextButton("CLEAR", skin);
        clearButton.setSize(120, 45);
        clearButton.setPosition(V_WIDTH / 2f + 10f, 50f);
        clearButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetWordSelection();
            }
        });
        stage.addActor(clearButton);

        // --- TOMBOL USE POTION (Pojok Kiri Atas) ---
        usePotionButton = new TextButton("Use Potion", skin);
        usePotionButton.setSize(120, 45);
        usePotionButton.setPosition(30f, V_HEIGHT - 75f);
        usePotionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.useHealthPotion();
                if (isBackpackOpen) {
                    updateBackpackContent();
                }
            }
        });
        stage.addActor(usePotionButton);

        // --- TOMBOL BACKPACK (Di bawah tombol potion) ---
        backpackButton = new TextButton("Backpack", skin);
        backpackButton.setSize(120, 45);
        backpackButton.setPosition(30f, V_HEIGHT - 130f);
        backpackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isBackpackOpen = !isBackpackOpen;
                if (isBackpackOpen) {
                    openBackpack();
                } else {
                    closeBackpack();
                }
            }
        });
        stage.addActor(backpackButton);

        // --- WINDOW BACKPACK ---
        backpackWindow = new com.badlogic.gdx.scenes.scene2d.ui.Window("Backpack", skin);
        backpackWindow.setSize(320, 420);
        backpackWindow.setPosition(V_WIDTH / 2f - 160f, V_HEIGHT / 2f - 210f);
        backpackWindow.setVisible(false);
        backpackWindow.setModal(true);
        backpackWindow.setMovable(true);
        stage.addActor(backpackWindow);

        inventoryTable = new com.badlogic.gdx.scenes.scene2d.ui.Table(skin);
        inventoryScrollPane = new ScrollPane(inventoryTable, skin);
        inventoryScrollPane.setFadeScrollBars(false);
        inventoryScrollPane.setScrollingDisabled(true, false);

        backpackWindow.add(inventoryScrollPane).expand().fill().row();

        TextButton closeBackpackButton = new TextButton("Close", skin);
        closeBackpackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeBackpack();
            }
        });
        backpackWindow.add(closeBackpackButton).width(80).height(30).padBottom(10).center();

        currentBattleState = BattleState.PLAYER_INPUT;
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this)));
    }

    private void openBackpack() {
        isBackpackOpen = true;
        backpackWindow.setVisible(true);
        updateBackpackContent();
        Gdx.input.setInputProcessor(stage);
    }

    private void closeBackpack() {
        isBackpackOpen = false;
        backpackWindow.setVisible(false);
        Gdx.input.setInputProcessor(new com.badlogic.gdx.InputMultiplexer(stage, new TileInputProcessor(this)));
    }

    private void updateBackpackContent() {
        inventoryTable.clearChildren();
        inventoryTable.add("Your Items:").colspan(2).padBottom(10).row();

        if (player.getInventory().size == 0) {
            inventoryTable.add("Backpack is empty.").colspan(2).row();
        } else {
            for (final Item item : player.getInventory()) {
                inventoryTable.add(item.getName()).pad(5);

                TextButton actionButton = new TextButton("Use/Equip", skin);
                actionButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (item instanceof HealthPotion) {
                            player.useHealthPotion();
                        } else if (item instanceof Weapon) {
                            item.interact(player);
                        }
                        updateBackpackContent();
                    }
                });
                inventoryTable.add(actionButton).width(80).height(30).pad(5).row();
            }
        }
    }

    private void initializeNewBoard() {
        if (gameBoard != null) {
            gameBoard.dispose();
        }
        gameBoard = new GameBoard(gridRows, gridCols, tileSize, gridStartX, gridStartY, defaultTileTextureRegion);
    }

    public Array<Tile> getSelectedTiles() {
        return selectedTiles;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void handleTileClick(Tile clickedTile) {
        if (currentBattleState != BattleState.PLAYER_INPUT) return;

        if (selectedTiles.size == 0) {
            selectedTiles.add(clickedTile);
            currentWord += clickedTile.getLetter();
        } else {
            Tile lastTile = selectedTiles.peek();
            if (isAdjacent(lastTile, clickedTile) && !selectedTiles.contains(clickedTile, true)) {
                selectedTiles.add(clickedTile);
                currentWord += clickedTile.getLetter();
            } else if (selectedTiles.contains(clickedTile, true)) {
                int index = selectedTiles.indexOf(clickedTile, true);
                if (index != selectedTiles.size - 1) {
                    for (int i = selectedTiles.size - 1; i > index; i--) {
                        selectedTiles.removeIndex(i);
                    }
                    currentWord = "";
                    for (Tile t : selectedTiles) currentWord += t.getLetter();
                } else {
                    selectedTiles.removeIndex(selectedTiles.size - 1);
                    currentWord = "";
                    for (Tile t : selectedTiles) currentWord += t.getLetter();
                }
            } else {
                resetWordSelection();
                selectedTiles.add(clickedTile);
                currentWord += clickedTile.getLetter();
            }
        }
    }

    private boolean isAdjacent(Tile tile1, Tile tile2) {
        int r1 = -1, c1 = -1, r2 = -1, c2 = -1;
        for (int r = 0; r < gameBoard.getGridRows(); r++) {
            for (int c = 0; c < gameBoard.getGridCols(); c++) {
                if (gameBoard.tileGrid[r][c] == tile1) { r1 = r; c1 = c; }
                if (gameBoard.tileGrid[r][c] == tile2) { r2 = r; c2 = c; }
            }
        }
        if (r1 == -1 || c1 == -1 || r2 == -1 || c2 == -1) return false;
        int dr = Math.abs(r1 - r2);
        int dc = Math.abs(c1 - c2);
        return (dr <= 1 && dc <= 1) && (dr != 0 || dc != 0);
    }

    public void processWord() {
        if (currentWord.length() < 3) {
            resetWordSelection();
            return;
        }

        if (WordDictionary.isValidWord(currentWord)) {
            currentBattleState = BattleState.PLAYER_ATTACK_ANIMATION;
            player.setState(GameEntity.CharacterState.ATTACKING);
            currentEnemy.setState(GameEntity.CharacterState.IDLE);
            playerAttackSound.play(0.25f);
        } else {
            resetWordSelection();
        }
    }

    private void resetWordSelection() {
        selectedTiles.clear();
        currentWord = "";
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Sinkronisasi proyeksi kamera kanvas virtual
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // 1. Gambar Background pas 1000x800
        batch.draw(backgroundImage, 0, 0, V_WIDTH, V_HEIGHT);

        // 2. Gambar Grid Board
        for (int r = 0; r < gameBoard.getGridRows(); r++) {
            for (int c = 0; c < gameBoard.getGridCols(); c++) {
                Tile tile = gameBoard.tileGrid[r][c];
                if (tile != null) {
                    tile.render(batch, tile.x, tile.y);

                    String letterStr = String.valueOf(tile.getLetter());
                    glyphLayout.setText(tileFont, letterStr);
                    tileFont.draw(batch, letterStr, tile.x + (tile.width - glyphLayout.width) / 2, tile.y + (tile.height + glyphLayout.height) / 2);

                    if (selectedTiles.contains(tile, true)) {
                        batch.setColor(Color.YELLOW.cpy().mul(0.7f));
                        if (game.getTileHighlightTexture() != null) {
                            batch.draw(game.getTileHighlightTexture(), tile.x, tile.y, tile.width, tile.height);
                        }
                        batch.setColor(Color.WHITE);
                    }
                }
            }
        }

        // 3. Diperbaiki: Gambar Player & Status Teks di Kiri Bawah (Masuk Frame, Ga Minus Lagi)
        player.render(batch, -130f, -30f);
        game.font.draw(batch, "Player HP: " + player.getHealth() + "/" + player.getMaxHealth(), 20f, 100f);
        game.font.draw(batch, "Score: " + player.getScore(), 20f, 75f);
        game.font.draw(batch, "Weapon: " + player.getEquippedWeapon().getName(), 20f, 50f);

        // Potion Text ditaruh rapi di bawah tombol Backpack (Pojok kiri atas)
        String potionText = "Potions: " + player.getHealthPotions();
        game.font.draw(batch, potionText, 30f, V_HEIGHT - 150f);

        // 4. Diperbaiki: Gambar Enemy & Status Teks di Kanan Bawah (Ga Tumpat)
        // ====================================================================
        // 4. GAMBAR ENEMY & TEKS STATUS (Dinamis Mengikuti Lebar Sprite Musuh)
        // ====================================================================
        if (currentEnemy.getCurrentState() != GameEntity.CharacterState.DYING || !currentEnemy.getCurrentPlayingAnimation().isAnimationFinished(currentEnemy.getStateTime())) {
            // 1. Tetap gambar karakter musuh di pojok kanan atas
            currentEnemy.render(batch, enemyX, enemyY);

            // 2. HITUNG TITIK TENGAH SECARA OTOMATIS BERDASARKAN LEBAR SPRITE MUSUH
            // Rumus: Titik X musuh + (Lebar display asli musuh / 2)
            // Cara ini menjamin tipe musuh sekecil Dragon atau seramping Ogre akan memiliki pusat yang pas!
            float enemyCenterX = enemyX + (475f / 2f);

            // 3. Teks Baris 1: Nama Musuh
            String enemyNameText = "Enemy Name: " + currentEnemy.getClass().getSimpleName();
            glyphLayout.setText(game.font, enemyNameText); // Mengukur panjang teks nama musuh
            float nameX = enemyCenterX - (glyphLayout.width / 2f); // Geser ke kiri setengah dari panjang teks
            game.font.draw(batch, enemyNameText, nameX, 755f); // Dinaikkan sedikit ke Y=755f agar ada jarak dari kepala

            // 4. Teks Baris 2: HP Musuh
            String enemyHPText = "HP: " + currentEnemy.getHealth() + "/" + currentEnemy.getMaxHealth();
            glyphLayout.setText(game.font, enemyHPText); // Mengukur panjang teks HP musuh
            float hpX = enemyCenterX - (glyphLayout.width / 2f); // Geser ke kiri setengah dari panjang teks
            game.font.draw(batch, enemyHPText, hpX, 730f); // Ditata tepat di bawah nama musuh (Y=730f)

        } else {
            // Jika musuh mati, teks "Enemy Defeated!" juga otomatis berada di tengah kepala tempat musuh berdiri
            float enemyCenterX = enemyX + (475f / 2f);
            String defeatedText = "Enemy Defeated!";
            glyphLayout.setText(game.font, defeatedText);
            float defeatedX = enemyCenterX - (glyphLayout.width / 2f);
            game.font.draw(batch, defeatedText, defeatedX, 755f);
        }

        // 5. Diperbaiki: Teks Current Word ditaruh pas di atas tombol SUBMIT & CLEAR
        String currentWordText = "Current Word: " + currentWord;
        glyphLayout.setText(game.font, currentWordText);
        game.font.draw(batch, currentWordText, (V_WIDTH - glyphLayout.width) / 2f, 735);

        batch.end();

        // 6. Render UI Tombol Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Logic Update State Game (Tetap asli)
        player.update(Gdx.graphics.getDeltaTime());
        if (currentEnemy != null) {
            currentEnemy.update(Gdx.graphics.getDeltaTime());
        }

        switch (currentBattleState) {
            case PLAYER_INPUT:
                break;
            case PLAYER_ATTACK_ANIMATION:
                if (player.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    int wordValue = WordCalculator.calculateWordValue(selectedTiles);
                    int totalDamageToEnemy = wordValue + player.getAttackPower();
                    currentEnemy.takeDamage(totalDamageToEnemy);
                    resetWordSelection();

                    for (Tile tile : selectedTiles) {
                        for (TileEffect effect : tile.getActiveEffects()) {
                            effect.onWordUse(tile, currentEnemy);
                        }
                    }

                    initializeNewBoard();

                    if (!currentEnemy.isAlive() && currentEnemy.getHealth() <=0) {
                        currentEnemy.setState(GameEntity.CharacterState.DYING);
                        currentBattleState = BattleState.ENEMY_DYING_ANIMATION;
                        enemyDeathSound.play(0.25f);
                    } else {
                        currentEnemy.setState(GameEntity.CharacterState.HIT);
                        currentBattleState = BattleState.ENEMY_HIT_ANIMATION;
                        enemyHitSound.play(0.25f);
                    }
                }
                break;

            case ENEMY_HIT_ANIMATION:
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    currentBattleState = BattleState.CHECK_ROUND_END;
                }
                break;

            case CHECK_ROUND_END:
                if (!currentEnemy.isAlive()) {
                    currentEnemy.setState(GameEntity.CharacterState.DYING);
                    currentBattleState = BattleState.ENEMY_DYING_ANIMATION;
                } else if (!player.isAlive()) {
                    player.setState(GameEntity.CharacterState.DYING);
                    currentBattleState = BattleState.PLAYER_DYING_ANIMATION;
                } else {
                    currentBattleState = BattleState.ENEMY_TURN_ATTACK_ANIMATION;
                    currentEnemy.setState(GameEntity.CharacterState.ATTACKING);
                    if (currentEnemy instanceof Wizard){
                        enemyAttackSound.play(0.5f);
                    } else {
                        enemyAttackSound.play(0.25f);
                    }
                    player.setState(GameEntity.CharacterState.IDLE);
                }
                break;

            case ENEMY_TURN_ATTACK_ANIMATION:
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    int enemyDamage = currentEnemy.getAttackPower();
                    player.takeDamage(enemyDamage);

                    if (!player.isAlive() && player.getHealth() <=0) {
                        player.setState(GameEntity.CharacterState.DYING);
                        playerDeathSound.play(0.25f);
                        currentBattleState = BattleState.PLAYER_DYING_ANIMATION;
                    } else {
                        player.setState(GameEntity.CharacterState.HIT);
                        playerHitSound.play(0.25f);
                        currentBattleState = BattleState.PLAYER_HIT_ANIMATION;
                    }
                }
                break;

            case PLAYER_HIT_ANIMATION:
                if (player.getCurrentState() == GameEntity.CharacterState.IDLE) {
                    currentBattleState = BattleState.PLAYER_INPUT;
                }
                break;

            case ENEMY_DYING_ANIMATION:
                if (currentEnemy.getCurrentState() == GameEntity.CharacterState.DYING &&
                    currentEnemy.getCurrentPlayingAnimation().isAnimationFinished(currentEnemy.getStateTime())) {
                    boolean isWizardDefeated = (currentEnemy instanceof Wizard);
                    player.addScore(currentEnemy.getGoldDrop());
                    progress += 0.1f;

                    if (MathUtils.random.nextFloat() < 0.5f) {
                        HealthPotion newPotion = new HealthPotion();
                        player.addHealthPotion(newPotion);
                    }
                    if (MathUtils.random.nextFloat() < 0.2f) {
                        Weapon droppedWeapon;
                        float weaponchance = MathUtils.random.nextFloat();
                        if (weaponchance < 0.01f) {
                            droppedWeapon = new MagicStaff();
                        } else if(weaponchance < 0.05f){
                            droppedWeapon = new LegendarySword();
                        } else if (weaponchance < 0.20f) {
                            droppedWeapon = new EpicSword();
                        }else if (weaponchance < 0.50f) {
                            droppedWeapon = new RareSword();
                        }else {
                            droppedWeapon = new CommonSword();
                        }
                        player.addItem(droppedWeapon);
                    }

                    currentEnemy.dispose();
                    if (enemyAttackSound != null) enemyAttackSound.dispose();
                    if (enemyDeathSound != null) enemyDeathSound.dispose();
                    if (enemyHitSound != null) enemyHitSound.dispose();

                    if (isWizardDefeated) {
                        if (backgroundMusic != null) backgroundMusic.stop();
                        game.setScreen(new WinScreen(game, player.getScore()));
                        dispose();
                    } else {
                        float enemySpawnChance = MathUtils.random.nextFloat();
                        if (progress >= 1f) {
                            currentEnemy = new Wizard("characters/wizard/");
                            progress = 0f;
                        } else if (enemySpawnChance < 0.5f) {
                            currentEnemy = new Goblin("characters/goblin/");
                        } else if (enemySpawnChance < 0.75f) {
                            currentEnemy = new Ogre("characters/ogre/");
                        } else if (enemySpawnChance < 0.9f) {
                            currentEnemy = new Dragon("characters/dragon/");
                        } else {
                            currentEnemy = new Wizard("characters/wizard/");
                        }
                        currentEnemy.setState(GameEntity.CharacterState.IDLE);

                        if (currentEnemy instanceof Goblin) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Ogre) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/ogre/ogre_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/ogre/ogre_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Dragon) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/dragon/dragon_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/dragon/dragon_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/goblin/goblin_death.mp3"));
                        } else if (currentEnemy instanceof Wizard) {
                            enemyHitSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_hit.mp3"));
                            enemyAttackSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_attack.mp3"));
                            enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("characters/wizard/wizard_death.mp3"));
                        }

                        currentBattleState = BattleState.PLAYER_INPUT;
                        player.setState(GameEntity.CharacterState.IDLE);
                    }
                }
                break;

            case PLAYER_DYING_ANIMATION:
                if (player.getCurrentState() == GameEntity.CharacterState.DYING &&
                    player.getCurrentPlayingAnimation().isAnimationFinished(player.getStateTime())) {
                    currentBattleState = BattleState.GAME_OVER_SCREEN;
                    game.setScreen(new GameOverScreen(game, player.getScore()));
                    dispose();
                }
                break;
            case GAME_OVER_SCREEN:
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        // Diperbaiki: Memaksa FitViewport memperbarui skala ke tengah monitor secara presisi
        game.viewport.update(width, height, true);
        camera.viewportWidth = V_WIDTH;
        camera.viewportHeight = V_HEIGHT;
        camera.position.set(V_WIDTH / 2f, V_HEIGHT / 2f, 0f);
        camera.update();

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (player != null) player.dispose();
        if (currentEnemy != null) currentEnemy.dispose();
        if (gameBoard != null) gameBoard.dispose();
        if (stage != null) stage.dispose();
        if (tileFont != null) tileFont.dispose();
        if (backgroundMusic != null) backgroundMusic.dispose();
    }
}
