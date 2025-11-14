package com.badlogic.drop;

import com.badlogic.drop.screens.MenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Classe principal que gerencia o jogo e as telas
 */
public class DropGame extends Game {
    // Resolução virtual fixa 16:9 vertical
    public static final float VIRTUAL_WIDTH = 540f;
    public static final float VIRTUAL_HEIGHT = 960f;

    public SpriteBatch batch;
    public BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Fonte padrão da libGDX
        font.getData().setScale(1.5f);

        Gdx.app.log("DropGame", "Game initialized");

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
}
