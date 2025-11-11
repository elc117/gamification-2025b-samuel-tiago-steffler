package com.badlogic.drop;

import com.badlogic.drop.entities.Wire;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    // Circuit demo
    private com.badlogic.drop.entities.Circuit circuit;
    private com.badlogic.drop.ui.WireRenderer wireRenderer;

    public Preloader preloader;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new ScreenViewport(); // Usa pixels 1:1 da tela
        bitState = false;

        // ================ criacao do circuito logico ================
        try {
            com.badlogic.drop.entities.CircuitBuilder builder = new com.badlogic.drop.entities.CircuitBuilder();

            builder.addInput("A", 0, 0); // input A
            builder.addInput("B", 0, 0); // input B

            builder.addXOR(0, 0); // XOR gate
            builder.addAND(0, 0); // AND gate

            // conecta inputs nas gates
            builder.connectInput("A", 0, 0); // A -> XOR input 0
            builder.connectInput("B", 0, 1); // B -> XOR input 1

            builder.connectInput("A", 1, 0); // A -> AND input 0
            builder.connectInput("B", 1, 1); // B -> AND input 1

            // conecta gates nas outputs
            builder.addOutput("X", 0, 0, 0); // output X na gate XOR
            builder.addOutput("Y", 1, 0, 0); // output Y na gate AND

            // criacao dos fios (manual, eh pra criar automaticamente ja)
            Wire wire1 = new Wire(builder.getInput("A"), builder.getGate(0));
            Wire wire2 = new Wire(builder.getInput("B"), builder.getGate(0));
            Wire wire3 = new Wire(builder.getInput("A"), builder.getGate(1));
            Wire wire4 = new Wire(builder.getInput("B"), builder.getGate(1));
            Wire wire5 = new Wire(builder.getGate(0), builder.getOutput("X"));
            Wire wire6 = new Wire(builder.getGate(1), builder.getOutput("Y"));
            builder.addWire(wire1);
            builder.addWire(wire2);
            builder.addWire(wire3);
            builder.addWire(wire4);
            builder.addWire(wire5);
            builder.addWire(wire6);

            // build do circuito
            circuit = builder.build();

            // Ensure positions and wire endpoints are computed before first frame
            circuit.updateAllPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Create a WireRenderer to draw wires
            wireRenderer = new com.badlogic.drop.ui.WireRenderer();

        } catch (Exception e) {
            System.out.println("Erro ao criar circuito: " + e.getMessage());
            e.printStackTrace();
        }

        buttonSize = 150f;
        //updateButtonPosition();
        touchPos = new Vector2();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (circuit != null) {
            circuit.updateAllPos(width, height);
        }
    }

    @Override
    public void render() {
        handleInput();
        draw();
    }


    /*
     * Gerencia entrada do usuario (cliques/toques)
     * Implementacao atual apenas analisa toggle dos bits de entrada do circuito
     */
    private void handleInput() {
        // Detecta clique/toque

        if (Gdx.input.justTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            System.out.println("Clique detectado em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ") world pixels");

            // verifica se clicou em algum input bit
            if (circuit != null) {
                com.badlogic.drop.entities.InputBits[] inputs = circuit.getInputs();
                for (com.badlogic.drop.entities.InputBits input : inputs) {
                    float ix = input.getX();
                    float iy = input.getY();
                    float iw = input.getWidth();
                    float ih = input.getHeight();

                    Rectangle r = new Rectangle(ix, iy, iw, ih);
                    if (r.contains(touchPos.x, touchPos.y)) {
                        input.toggle();
                        System.out.println("Input " + input.getLabel() + " -> " + input.getValue());
                    }
                }
            }

            // verifica se clicou no menu
            // (a implementar)
        }
    }

    private void draw() {
        // Limpa a tela com cor de fundo
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1.0f);

        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        // Evaluate and draw circuit if present
        if (circuit != null) {
            // Evaluate circuit logic
            circuit.evaluate();

            // Draw wires first (so gates are on top)
            if (wireRenderer != null) {
                // Ensure shapeRenderer uses same projection
                wireRenderer.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
                wireRenderer.renderAll(com.badlogic.gdx.utils.Array.with(circuit.getWires()));
            }

            // Draw gates/textures
            spriteBatch.begin();
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.render(spriteBatch);
            }
            spriteBatch.end();
        } else {
            // fallback: draw the simple button + bit as before
            spriteBatch.begin();

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

        if (wireRenderer != null) wireRenderer.dispose();

        // Dispose textures owned by gates
        if (circuit != null) {
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.dispose();
            }
        }
    }
}
