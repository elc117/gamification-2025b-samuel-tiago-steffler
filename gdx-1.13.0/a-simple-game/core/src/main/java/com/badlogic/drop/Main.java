package com.badlogic.drop;

import com.badlogic.drop.entities.InputBits;
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

    Vector2 touchPos;

    // Circuit demo
    private com.badlogic.drop.entities.Circuit circuit;
    private com.badlogic.drop.ui.WireRenderer wireRenderer;

    public Preloader preloader;

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
            Array<Level> levels;
            levels = new JSONtoCircuit().convert(Gdx.files.internal("levels/levels.json"));
            Gdx.app.log("Main.create", "Niveis carregados: " + levels.size);
            if (levels.size > 0) {
                Level level1 = levels.get(5);
                circuit = level1.getCircuit();
            }
            Gdx.app.log("Main.create", "Circuito carregado.");
            /*Gdx.app.log("Main.create", "Starting circuit creation...");
            com.badlogic.drop.entities.CircuitBuilder builder = new com.badlogic.drop.entities.CircuitBuilder();
            Gdx.app.log("Main.create", "CircuitBuilder created successfully");

            //builder.addInput("A"); // input A
            //builder.addInput("B"); // input B
            //builder.addInput("C"); // input C
            Gdx.app.log("Main.create", "Adding gates...");

            builder.addXOR("XOR1"); // XOR gate
            builder.addAND("AND1"); // AND gate
            builder.addOR("OR1"); // OR gate
            builder.addNOT("NOT1"); // NOT gate
            builder.addNAND("NAND1"); // NAND gate
            builder.addNOR("NOR1"); // NOR gate
            builder.addXNOR("XNOR1"); // XNOR gate
            Gdx.app.log("Main.create", "All gates added successfully");

            // conecta inputs nas gates
            Gdx.app.log("Main.create", "Connecting inputs to gates...");
            builder.connectInput("A", "XOR1", 0); // A -> XOR input 0
            builder.connectInput("B", "XOR1", 1); // B -> XOR input 1
            builder.connectInput("A", "AND1", 0); // A -> AND input 0
            builder.connectInput("B", "AND1", 1); // B -> AND input 1
            builder.connectInput("A", "OR1", 0); // A -> OR input 0
            builder.connectInput("B", "OR1", 1); // B -> OR input 1
            builder.connectInput("C", "NOT1", 0); // C -> NOT input 0
            builder.connectInput("A", "NAND1", 0); // A -> NAND input 0
            builder.connectInput("B", "NAND1", 1); // B -> NAND input 1
            builder.connectInput("A", "NOR1", 0); // A -> NOR input 0
            builder.connectInput("B", "NOR1", 1); // B -> NOR input 1
            builder.connectInput("A", "XNOR1", 0); // A -> XNOR input 0
            builder.connectInput("B", "XNOR1", 1); // B -> XNOR input 1
            Gdx.app.log("Main.create", "All inputs connected successfully");

            // conecta gates nas outputs
            Gdx.app.log("Main.create", "Adding outputs...");
            builder.addOutput("X0", "XOR1"); // output X0 na gate XOR
            builder.addOutput("X1", "AND1"); // output X1 na gate AND
            builder.addOutput("X2", "OR1"); // output X2 na gate OR
            builder.addOutput("X3", "NOT1"); // output X3 na gate NOT
            builder.addOutput("X4", "NAND1"); // output X4 na gate NAND
            builder.addOutput("X5", "NOR1"); // output X5 na gate NOR
            builder.addOutput("X6", "XNOR1"); // output X6 na gate XNOR
            Gdx.app.log("Main.create", "All outputs added successfully");

            // build do circuito
            Gdx.app.log("Main.create", "Building circuit...");
            circuit = builder.build(true);
            Gdx.app.log("Main.create", "Circuit built successfully!");
            */
            // Ensure positions and wire endpoints are computed before first frame
            Gdx.app.log("Main.create", "Computing positions for screen: " + Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight());
            circuit.updateAllPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.app.log("Main.create", "Positions updated");

            // Create a WireRenderer to draw wires
            Gdx.app.log("Main.create", "Creating WireRenderer...");
            wireRenderer = new com.badlogic.drop.ui.WireRenderer();
            wireRenderer.renderAll(circuit.getWires());
            Gdx.app.log("Main.create", "Circuit creation complete!");
            
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

            System.out.println("Clique detectado em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ") world pixels");

            // verifica se clicou em algum input bit
            if (circuit != null) {
                Array<InputBits> inputs = circuit.getInputs();
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
