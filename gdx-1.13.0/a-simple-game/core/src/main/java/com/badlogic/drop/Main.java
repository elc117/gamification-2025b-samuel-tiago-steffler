package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;

/**
 * Ponto de entrada da aplicação.
 * Delega o gerenciamento do jogo para DropGame.
 */
public class Main implements ApplicationListener {
    private DropGame game;
    public Preloader preloader; // Mantido para compatibilidade com launchers

    @Override
    public void create() {
        game = new DropGame();
        game.create();
    }

    @Override
    public void resize(int width, int height) {
        // Não redimensiona - mantém tamanho fixo da tela
    }

    @Override
    public void render() {
        if (game != null && game.getScreen() != null) {
            game.getScreen().render(com.badlogic.gdx.Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void pause() {
        if (game != null && game.getScreen() != null) {
            game.getScreen().pause();
        }
    }

    @Override
    public void resume() {
        if (game != null && game.getScreen() != null) {
            game.getScreen().resume();
        }
    }

    @Override
    public void dispose() {
        if (game != null) {
            game.dispose();
        }
    }
}
