package com.badlogic.drop;

import com.badlogic.drop.levels.LevelManager;
import com.badlogic.drop.screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Classe principal que gerencia o jogo e as telas
 */
public class BitItGame extends Game {
    // Resolucao virtual fixa 16:9 vertical
    public static final float VIRTUAL_WIDTH = 540f;
    public static final float VIRTUAL_HEIGHT = 960f;

    public SpriteBatch batch;
    public BitmapFont font;

    private LevelManager levelManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Fonte padrão da libGDX
        font.getData().setScale(1.5f);

        Gdx.app.log("BitItGame", "Game initialized");

        initializeLevels();
        levelManager.setCurrLevelIdx(0);
        levelManager.getLevel(0).setUnlocked(true);

        // Inicia na tela de menu
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();

        // Dispose da tela atual
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }

    private void initializeLevels() {
        levelManager = LevelManager.getInstance();
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }
}
