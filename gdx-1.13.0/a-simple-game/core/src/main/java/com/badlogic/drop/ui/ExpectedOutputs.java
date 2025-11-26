package com.badlogic.drop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/**
 * Painel que mostra as saídas esperadas para o nível atual
 * Exibe os bits de saída (on/off) no topo da tela
 */
public class ExpectedOutputs {
    private static final float MAX_WIDTH = 400f;
    private static final float MAX_BIT_SIZE = 50f;

    private Texture bitOnTexture;
    private Texture bitOffTexture;
    private Array<Boolean> expectedOutputs;

    private float bitSize;
    private float totalWidth;
    private float startX;
    private float startY;
    private float spacing = 10f;

    public ExpectedOutputs(Array<Boolean> expectedOutputs, float screenWidth, float yPosition) {
        this.expectedOutputs = expectedOutputs;

        // Carrega texturas
        bitOnTexture = new Texture(Gdx.files.internal("textures/bits/out_on.png"));
        bitOffTexture = new Texture(Gdx.files.internal("textures/bits/out_off.png"));

        // Calcula tamanho dos bits baseado na quantidade
        int numBits = expectedOutputs.size;
        if (numBits > 0) {
            // Calcula os tamanhos e espacamentos
            spacing = 50f;
            float sizeWithSpacing = (MAX_WIDTH - (spacing * (numBits - 1))) / numBits;
            bitSize = Math.min(sizeWithSpacing, MAX_BIT_SIZE);

            // Calcula largura total ocupada
            totalWidth = (bitSize * numBits) + (spacing * (numBits - 1));

            // Centraliza horizontalmente
            startX = (screenWidth - totalWidth) / 2 - 50;
            startY = yPosition;
        }

        Gdx.app.log("ExpectedOutputs", "Criado painel com " + numBits + " bits, tamanho: " + bitSize + "px");
    }

    /**
     * Desenha os bits de saída esperados
     */
    public void render(SpriteBatch batch) {
        if (expectedOutputs == null || expectedOutputs.size == 0) {
            return;
        }

        float currentX = startX;

        for (int i = 0; i < expectedOutputs.size; i++) {
            boolean value = expectedOutputs.get(i);
            Texture texture = value ? bitOnTexture : bitOffTexture;

            batch.draw(texture, currentX, startY, bitSize, bitSize);
            currentX += bitSize + spacing;
        }
    }

    public void updateExpectedOutputs(Array<Boolean> newOutputs) {
        this.expectedOutputs = newOutputs;
    }

    /**
     * Libera recursos
     */
    public void dispose() {
        if (bitOnTexture != null) bitOnTexture.dispose();
        if (bitOffTexture != null) bitOffTexture.dispose();
    }
}
