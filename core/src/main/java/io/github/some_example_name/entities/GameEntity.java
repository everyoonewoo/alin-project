package io.github.some_example_name.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import io.github.some_example_name.interfaces.Attackable;
import io.github.some_example_name.interfaces.Renderable;

public abstract class GameEntity implements Attackable, Renderable {
    public enum CharacterState {
        IDLE,
        ATTACKING,
        HIT,
        DYING
    }

    protected int health;
    protected int maxHealth;
    protected int attackPower;
    protected float x, y; // Posisi di layar

    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> dyingAnimation;
    protected float stateTime; // Waktu yang berlalu sejak animasi dimulai
    protected Texture spriteSheet; // Texture untuk sprite sheet (gambar asli)
    protected float displayWidth; // Lebar karakter saat digambar di layar
    protected float displayHeight; // Tinggi karakter saat digambar di layar

    protected Animation<TextureRegion> currentPlayingAnimation; // Animasi yang sedang diputar
    protected Texture idleSpriteSheet; // Texture untuk sprite sheet idle
    protected Texture attackSpriteSheet; // Texture untuk sprite sheet attack
    protected Texture hitSpriteSheet; // Texture untuk sprite sheet hit
    protected Texture dyingSpriteSheet;
    protected CharacterState currentState;

    public GameEntity(int maxHealth, int attackPower,
                      String idleSpriteSheetPath, int idleFrameCols, int idleFrameRows, float idleFrameDuration,
                      float displayWidth, float displayHeight) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;

        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        idleSpriteSheet = new Texture(idleSpriteSheetPath);
        TextureRegion[][] tmpIdle = TextureRegion.split(idleSpriteSheet,
            idleSpriteSheet.getWidth() / idleFrameCols,
            idleSpriteSheet.getHeight() / idleFrameRows);
        Array<TextureRegion> idleFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < idleFrameRows; i++) {
            for (int j = 0; j < idleFrameCols; j++) {
                idleFrames.add(tmpIdle[i][j]);
            }
        }
        idleAnimation = new Animation<TextureRegion>(idleFrameDuration, idleFrames);

        currentPlayingAnimation = idleAnimation; // Default ke animasi idle
        currentState = CharacterState.IDLE; // Default state
        stateTime = 0f;
    }

    @Override
    public void takeDamage(int amount) {
        this.health -= amount;
        if (this.health < 0) {
            this.health = 0;
        }
        System.out.println(this.getClass().getSimpleName() + " took " + amount + " damage. Health: " + this.health);
    }

    public void heal(int amount) {
        this.health += amount;
        if (this.health > maxHealth) {
            this.health = maxHealth; // Jangan melebihi max health
        }
        System.out.println(this.getClass().getSimpleName() + " healed for " + amount + ". Health: " + this.health);
    }

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return maxHealth;
    }

    public abstract void attack(GameEntity target);

    public void setDyingAnimation(String dyingSpriteSheetPath, int dyingFrameCols, int dyingFrameRows, float dyingFrameDuration) {
        if (this.dyingSpriteSheet != null) this.dyingSpriteSheet.dispose();
        this.dyingSpriteSheet = new Texture(dyingSpriteSheetPath);
        TextureRegion[][] tmpDying = TextureRegion.split(this.dyingSpriteSheet,
            this.dyingSpriteSheet.getWidth() / dyingFrameCols,
            this.dyingSpriteSheet.getHeight() / dyingFrameRows);
        Array<TextureRegion> dyingFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < dyingFrameRows; i++) {
            for (int j = 0; j < dyingFrameCols; j++) {
                dyingFrames.add(tmpDying[i][j]);
            }
        }
        dyingAnimation = new Animation<TextureRegion>(dyingFrameDuration, dyingFrames);
    }

    public void setAttackAnimation(String attackSpriteSheetPath, int attackFrameCols, int attackFrameRows, float attackFrameDuration) {
        if (this.attackSpriteSheet != null) this.attackSpriteSheet.dispose(); // Dispose jika sudah ada
        this.attackSpriteSheet = new Texture(attackSpriteSheetPath);
        TextureRegion[][] tmpAttack = TextureRegion.split(this.attackSpriteSheet,
            this.attackSpriteSheet.getWidth() / attackFrameCols,
            this.attackSpriteSheet.getHeight() / attackFrameRows);
        Array<TextureRegion> attackFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < attackFrameRows; i++) {
            for (int j = 0; j < attackFrameCols; j++) {
                attackFrames.add(tmpAttack[i][j]);
            }
        }
        attackAnimation = new Animation<TextureRegion>(attackFrameDuration, attackFrames);
    }

    public Animation<TextureRegion> getCurrentPlayingAnimation() {
        return currentPlayingAnimation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setHitAnimation(String hitSpriteSheetPath, int hitFrameCols, int hitFrameRows, float hitFrameDuration) {
        if (this.hitSpriteSheet != null) this.hitSpriteSheet.dispose(); // Dispose jika sudah ada
        this.hitSpriteSheet = new Texture(hitSpriteSheetPath);
        TextureRegion[][] tmpHit = TextureRegion.split(this.hitSpriteSheet,
            this.hitSpriteSheet.getWidth() / hitFrameCols,
            this.hitSpriteSheet.getHeight() / hitFrameRows);
        Array<TextureRegion> hitFrames = new Array<TextureRegion>(TextureRegion.class);
        for (int i = 0; i < hitFrameRows; i++) {
            for (int j = 0; j < hitFrameCols; j++) {
                hitFrames.add(tmpHit[i][j]);
            }
        }
        hitAnimation = new Animation<TextureRegion>(hitFrameDuration, hitFrames);
    }

    // Implementasi dari Renderable
    public void setState(CharacterState newState) {
        if (this.currentState == newState) return;

        this.currentState = newState;
        this.stateTime = 0f;

        switch (newState) {
            case IDLE:
                currentPlayingAnimation = idleAnimation;
                break;
            case ATTACKING:
                if (attackAnimation != null) {
                    currentPlayingAnimation = attackAnimation;
                } else {
                    currentPlayingAnimation = idleAnimation;
                }
                break;
            case HIT:
                if (hitAnimation != null) {
                    currentPlayingAnimation = hitAnimation;
                } else {
                    currentPlayingAnimation = idleAnimation;
                }
                break;
            case DYING: // BARU: Handle state DYING
                if (dyingAnimation != null) {
                    currentPlayingAnimation = dyingAnimation;
                } else {
                    currentPlayingAnimation = idleAnimation; // Fallback
                }
                break;
        }
    }

    public CharacterState getCurrentState() {
        return currentState;
    }

    @Override
    public void render(SpriteBatch batch, float x, float y) {
        this.x = x;
        this.y = y;

        if (currentState == CharacterState.DYING && currentPlayingAnimation.isAnimationFinished(stateTime)) {
            return;
        }

        TextureRegion currentFrame = currentPlayingAnimation.getKeyFrame(stateTime, true); // true untuk looping (untuk idle)

        if (currentState == CharacterState.ATTACKING || currentState == CharacterState.HIT) {
            currentFrame = currentPlayingAnimation.getKeyFrame(stateTime, false); // false agar tidak looping
        }

        batch.draw(currentFrame, x, y, displayWidth, displayHeight);
    }

    public void update(float delta) {
        stateTime += delta;

        // Otomatis kembali ke IDLE setelah animasi satu kali selesai, kecuali jika DYING
        if (currentState != CharacterState.IDLE && currentState != CharacterState.DYING &&
            currentPlayingAnimation.isAnimationFinished(stateTime)) {
            setState(CharacterState.IDLE);
        }
    }

    @Override
    public void dispose() {
        if (idleSpriteSheet != null) idleSpriteSheet.dispose();
        if (attackSpriteSheet != null) attackSpriteSheet.dispose();
        if (hitSpriteSheet != null) hitSpriteSheet.dispose();
    }
}
