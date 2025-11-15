package com.badlogic.drop.screens;

import com.badlogic.drop.DropGame;
import com.badlogic.drop.entities.Circuit;
import com.badlogic.drop.entities.InputBits;
import com.badlogic.drop.levels.Level;
import com.badlogic.drop.levels.LevelManager;
import com.badlogic.drop.ui.WireRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Tela principal de jogo onde o jogador interage com o circuito lógico
 */
public class GameScreen implements Screen {
    private final DropGame game;
    private final int levelId;
    private Level currentLevel;
    private Circuit circuit;
    private WireRenderer wireRenderer;
    private FitViewport viewport;
    private Vector2 touchPos;
    private BitmapFont debugFont;

    public GameScreen(final DropGame game, int levelId) {
        this.game = game;
        this.levelId = levelId;

        Gdx.app.log("GameScreen", "Iniciando nível " + (levelId + 1));

        // Cria viewport
        viewport = new FitViewport(DropGame.VIRTUAL_WIDTH, DropGame.VIRTUAL_HEIGHT);
        touchPos = new Vector2();

        // Cria fonte para debug
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(1.5f);

        // Carrega o nível
        loadLevel(levelId);
    }

    /**
     * Carrega o nível especificado
     */
    private void loadLevel(int levelId) {
        try {
            LevelManager levelManager = LevelManager.getInstance();
            currentLevel = levelManager.getLevel(levelId);

            if (currentLevel != null) {
                circuit = currentLevel.getCircuit();

                // Atualiza posições dos elementos do circuito
                circuit.updateAllPos((int)DropGame.VIRTUAL_WIDTH, (int)DropGame.VIRTUAL_HEIGHT);

                // Cria renderizador de fios
                wireRenderer = new WireRenderer();
                wireRenderer.renderAll(circuit.getWires());

                Gdx.app.log("GameScreen", "Nível " + (levelId + 1) + " carregado com sucesso");
            } else {
                Gdx.app.error("GameScreen", "Nível " + levelId + " não encontrado!");
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Erro ao carregar nível " + levelId, e);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        handleInput();

        // Limpa tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        if (circuit != null) {
            // Avalia o circuito
            circuit.evaluate();

            // Desenha fios
            if (wireRenderer != null) {
                wireRenderer.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
                wireRenderer.renderAll(circuit.getWires());
            }

            // Desenha gates
            game.batch.begin();
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.render(game.batch);
            }
            game.batch.end();
        }

        // Desenha informações de debug
        game.batch.begin();
        debugFont.draw(game.batch, "Nível: " + (levelId + 1), 10, DropGame.VIRTUAL_HEIGHT - 10);
        debugFont.draw(game.batch, "Toque nos inputs para mudar valores", 10, DropGame.VIRTUAL_HEIGHT - 40);
        debugFont.draw(game.batch, "ESC para voltar", 10, 30);
        game.batch.end();
    }

    /**
     * Gerencia entrada do usuário
     */
    private void handleInput() {
        // Volta para tela de níveis ao pressionar ESC
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            game.setScreen(new LevelsScreen(game));
            dispose();
            return;
        }

        // Detecta clique/toque nos inputs
        if (Gdx.input.justTouched() && circuit != null) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            Gdx.app.log("GameScreen", "Clique em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ")");

            // Verifica se clicou em algum input bit
            Array<InputBits> inputs = circuit.getInputs();
            for (InputBits input : inputs) {
                Rectangle bounds = new Rectangle(input.getX(), input.getY(),
                                                 input.getWidth(), input.getHeight());
                if (bounds.contains(touchPos.x, touchPos.y)) {
                    input.toggle();
                    Gdx.app.log("GameScreen", "Input " + input.getLabel() + " -> " + input.getValue());
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (circuit != null) {
            circuit.updateAllPos((int)DropGame.VIRTUAL_WIDTH, (int)DropGame.VIRTUAL_HEIGHT);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (wireRenderer != null) wireRenderer.dispose();
        if (debugFont != null) debugFont.dispose();

        // Dispose textures owned by gates
        if (circuit != null) {
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.dispose();
            }
        }
    }
}
