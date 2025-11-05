package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    FitViewport viewport;

    // Texturas do botão
    Texture bitOnTexture;
    Texture bitOffTexture;
    Texture oneTexture;
    Texture zeroTexture;

    // Estado do bit (true = 1, false = 0)
    boolean bitState;

    // Posição e tamanho do botão
    Rectangle buttonBounds;
    Vector2 buttonPosition;
    float buttonSize;

    // Para detectar cliques
    Vector2 touchPos;

    public Preloader preloader;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);

        // Inicializa estado como 0 (false)
        bitState = false;

        // Carrega as texturas
        try {
            bitOnTexture = new Texture("textures/bits/in_on.png");
            bitOffTexture = new Texture("textures/bits/in_off.png");
            oneTexture = new Texture("textures/bits/one.png");
            zeroTexture = new Texture("textures/bits/zero.png");
            System.out.println("Texturas carregadas com sucesso!");
        } catch (Exception e) {
            System.out.println("ERRO ao carregar texturas: " + e.getMessage());
            System.out.println("Verifique se os arquivos estao em: assets/textures/bits/");
        }

        // Posiciona o bit de entrada no centro da tela
        buttonSize = 1.5f;
        buttonPosition = new Vector2(
            viewport.getWorldWidth() / 2 - buttonSize / 2,
            viewport.getWorldHeight() / 2 - buttonSize / 2
        );

        buttonBounds = new Rectangle(
            buttonPosition.x - buttonSize, // Ajuste para considerar o número ao lado
            buttonPosition.y,
            buttonSize,
            buttonSize
        );

        touchPos = new Vector2();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        handleInput();
        draw();
    }

    private void handleInput() {
        // Detecta clique/toque
        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos); // Converte coordenadas da tela para o mundo

            // Verifica se clicou no botão
            if (buttonBounds.contains(touchPos.x, touchPos.y)) {
                // Alterna o estado do bit
                bitState = !bitState;
                System.out.println("Bit alternado! Novo estado: " + (bitState ? "1" : "0"));
            }
        }
    }

    private void draw() {
        // Limpa a tela com cor de fundo
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1.0f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Desenha o botao (fundo)
        Texture currentButtonTexture = bitState ? bitOnTexture : bitOffTexture;
        if (currentButtonTexture != null) {
            spriteBatch.draw(
                currentButtonTexture,
                buttonPosition.x,
                buttonPosition.y,
                buttonSize,
                buttonSize
            );
        }

        // Desenha o número (1 ou 0) ao lado do botao
        Texture currentNumberTexture = bitState ? oneTexture : zeroTexture;
        if (currentNumberTexture != null) {
            // Centraliza o botao numero
            float numberSize = buttonSize * 0.6f; // numero menor que o botao
            float numberX = buttonPosition.x + (buttonSize - numberSize) / 2 - buttonSize; // Desenha a esquerda do bit de entrada
            float numberY = buttonPosition.y + (buttonSize - numberSize) / 2;

            spriteBatch.draw(
                currentNumberTexture,
                numberX,
                numberY,
                numberSize,
                numberSize
            );
        }

        spriteBatch.end();
    }

    @Override
    public void pause() {
        // Não precisa fazer nada
    }

    @Override
    public void resume() {
        // Não precisa fazer nada
    }

    @Override
    public void dispose() {
        // Libera recursos
        if (spriteBatch != null) spriteBatch.dispose();
        if (bitOnTexture != null) bitOnTexture.dispose();
        if (bitOffTexture != null) bitOffTexture.dispose();
        if (oneTexture != null) oneTexture.dispose();
        if (zeroTexture != null) zeroTexture.dispose();
    }
}
