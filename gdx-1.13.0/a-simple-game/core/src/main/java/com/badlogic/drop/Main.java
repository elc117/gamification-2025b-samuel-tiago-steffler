package com.badlogic.drop;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.drop.logic.CircuitTester;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    ScreenViewport viewport;

    Texture bitOnTexture;
    Texture bitOffTexture;
    Texture oneTexture;
    Texture zeroTexture;

    boolean bitState;

    Rectangle buttonBounds;
    Vector2 buttonPosition;
    float buttonSize;

    Vector2 touchPos;

    public Preloader preloader;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new ScreenViewport(); // Usa pixels 1:1 da tela
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

        // Testa todas as portas lógicas
        CircuitTester.testAllGates();

        buttonSize = 150f;
        updateButtonPosition();
        touchPos = new Vector2();
    }

    // centraliza botao na tela
    private void updateButtonPosition() {

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // botao posicionado a esquerda do centro
        buttonPosition = new Vector2(
            screenWidth / 2 - buttonSize / 2 - 200,
            screenHeight / 2 - buttonSize / 2
        );

        buttonBounds = new Rectangle(
            buttonPosition.x,
            buttonPosition.y,
            buttonSize,
            buttonSize
        );

        System.out.println("Botao posicionado em: (" + buttonPosition.x + ", " + buttonPosition.y + ")");
        System.out.println("Tamanho da tela: " + screenWidth + "x" + screenHeight + " pixels");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        updateButtonPosition(); // Reposiciona o botão quando redimensionar a janela
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

            System.out.println("Clique detectado em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ") pixels");

            // Verifica se clicou no botão
            if (buttonBounds.contains(touchPos.x, touchPos.y)) {
                // Alterna o estado do bit
                bitState = !bitState;
                System.out.println("Bit alternado - estado: " + (bitState ? "1" : "0"));
            }
        }
    }

    private void draw() {
        // Limpa a tela com cor de fundo
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1.0f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // Desenha o botao com numero
        Texture currentButtonTexture = bitState ? oneTexture : zeroTexture;
        if (currentButtonTexture != null) {
            spriteBatch.draw(
                currentButtonTexture,
                buttonPosition.x,
                buttonPosition.y,
                buttonSize,
                buttonSize
            );
        }

        // Desenha o bit de input
        Texture currentInputTexture = bitState ? bitOnTexture : bitOffTexture;
        if (currentInputTexture != null) {

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();

            float numberSize = buttonSize;
            float numberX = screenWidth / 2 - numberSize / 2;
            float numberY = screenHeight / 2 - numberSize / 2;

            spriteBatch.draw(
                currentInputTexture,
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


    }

    @Override
    public void resume() {


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
