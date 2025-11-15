package com.badlogic.drop;

import com.badlogic.drop.entities.Circuit;
import com.badlogic.drop.entities.gates.InputBits;
import com.badlogic.drop.levels.JSONtoCircuit;
import com.badlogic.drop.levels.Level;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main implements ApplicationListener {
    SpriteBatch spriteBatch;
    ScreenViewport viewport;
    int levelIndex = 0;
    Vector2 touchPos;

    // Circuit demo
    private Circuit circuit;
    private com.badlogic.drop.ui.WireRenderer wireRenderer;

    public Preloader preloader;

    private Array<Level> levels;

    private Level currentLevel;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        viewport = new ScreenViewport(); // Usa pixels 1:1 da tela

        // Set log level to DEBUG to ensure all messages appear in browser console
        Gdx.app.setLogLevel(com.badlogic.gdx.Application.LOG_DEBUG);
        Gdx.app.log("Main", "=== APPLICATION STARTED ===");
        Gdx.app.log("Main", "App type: " + Gdx.app.getType());

        // ================ criacao do circuito logico ================
        try {
            Gdx.app.log("Main.create", "Buscando niveis em assets/levels/levels.json...");
            levels = new JSONtoCircuit().convert(Gdx.files.internal("levels/levels.json"), true);
            Gdx.app.log("Main.create", "Niveis carregados: " + levels.size);
            if (levels.size > 0) {
                currentLevel = levels.get(levelIndex);
                circuit = currentLevel.getCircuit();
            }
            Gdx.app.log("Main.create", "Circuito carregado.");
            wireRenderer = new com.badlogic.drop.ui.WireRenderer();

        } catch (Exception e) {
            Gdx.app.error("Main.create", "========================================");
            Gdx.app.error("Main.create", "ERROR: Failed to create circuit!");
            Gdx.app.error("Main.create", "Exception type: " + e.getClass().getName());
            Gdx.app.error("Main.create", "Message: " + e.getMessage());
            Gdx.app.error("Main.create", "========================================");
            Gdx.app.error("Main.create", "Stack trace:", e);
            // Set circuit to null so debug screen appears
            circuit = null;
            wireRenderer = null;
        }

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

            //Gdx.app.log("Main.handleInput", "Clique detectado em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ") world pixels");

            // verifica se clicou em algum input bit
            if (circuit != null) {
                Array<InputBits> inputs = circuit.getInputs();
                for (com.badlogic.drop.entities.gates.InputBits input : inputs) {
                    float ix = input.getX();
                    float iy = input.getY();
                    float iw = input.getWidth();
                    float ih = input.getHeight();

                    Rectangle r = new Rectangle(ix, iy, iw, ih);
                    if (r.contains(touchPos.x, touchPos.y)) {
                        input.toggle();
                        //Gdx.app.log("Main.handleInput", "Input " + input.getLabel() + " -> " + input.getValue());
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
        // se circuito estiver presente
        if (circuit != null) {
            // primeiramente calcula todos os valores do circuito
            circuit.evaluate();

            // primeiro fios
            if (wireRenderer != null) {
                // Ensure shapeRenderer uses same projection
                wireRenderer.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
                wireRenderer.renderAll(circuit.getWires());
            }

            // dai os gates
            spriteBatch.begin();
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.render(spriteBatch);
            }
            spriteBatch.end();

            //checagem de vitoria
            if (circuit.isCorrect()) {
                Gdx.app.log("Main.draw", "Circuito correto! Nivel concluido.");
                // trocar para o proximo nivel
                levelIndex++;
                if (levelIndex >= levels.size) {
                    levelIndex = 0; // reinicia do primeiro nivel
                }
                currentLevel = levels.get(levelIndex);
                circuit = currentLevel.getCircuit();
                circuit.resetInputs();
                wireRenderer = new com.badlogic.drop.ui.WireRenderer();
                circuit.updateAllPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }


        } else {
            // fallback: nao desenha nada de circuito
            spriteBatch.begin();

            // quadrado dummy
            spriteBatch.setColor(1, 1, 1, 1);
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            pixmap.fill();
            com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
            pixmap.dispose();

            //texto dummy
            com.badlogic.gdx.graphics.g2d.BitmapFont font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
            font.setColor(com.badlogic.gdx.graphics.Color.RED);
            font.getData().setScale(1.5f);
            
            String errorMsg = "Circuit creation failed!";
            String checkMsg = "Check browser console (F12) for errors";
            String appTypeMsg = "App type: " + Gdx.app.getType();
            
            font.draw(spriteBatch, errorMsg, 
                Gdx.graphics.getWidth() / 2f - 100, 
                Gdx.graphics.getHeight() / 2f);
            font.draw(spriteBatch, checkMsg, 
                Gdx.graphics.getWidth() / 2f - 150, 
                Gdx.graphics.getHeight() / 2f - 30);
            font.draw(spriteBatch, appTypeMsg, 
                Gdx.graphics.getWidth() / 2f - 100, 
                Gdx.graphics.getHeight() / 2f - 60);

            spriteBatch.draw(texture, 
                Gdx.graphics.getWidth() / 2f - 50, 
                Gdx.graphics.getHeight() / 2f - 50, 
                100, 100);
            
            texture.dispose();
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
        if (wireRenderer != null) wireRenderer.dispose();

        // Dispose textures owned by gates
        if (circuit != null) {
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.dispose();
            }
        }
    }
}
