package com.badlogic.drop.screens;

import com.badlogic.drop.BitItGame;
import com.badlogic.drop.entities.Circuit;
import com.badlogic.drop.entities.gates.InputBits;
import com.badlogic.drop.levels.Level;
import com.badlogic.drop.levels.LevelManager;
import com.badlogic.drop.ui.WireRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Tela principal de jogo onde o jogador interage com o circuito logico
 * TEMPORARIO - geracao do circuito esta na branch "samuel"
 */
public class GameScreen implements Screen {
    private final BitItGame game;

    private Texture backgroundTexture;
    private Image backgroundImage;
    private Stage stage;

    private final int levelId;
    private Level currentLevel;
    private Circuit circuit;
    private WireRenderer wireRenderer;
    private Viewport viewport;
    private Vector2 touchPos;
    private BitmapFont debugFont;
    private LevelManager levelManager;

    public GameScreen(final BitItGame game, int levelId) {
        game.batch = new SpriteBatch();
        this.game = game;
        this.levelId = levelId;

        Gdx.app.log("GameScreen", "Iniciando nivel " + (levelId + 1));

        // Carrega texturas
        try {
            backgroundTexture = new Texture(Gdx.files.internal("textures/UI/gamescreen.png"));
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setScale(0.5f);
        } catch (Exception e) {
            Gdx.app.error("gameScreen", "Erro ao carregar texturas", e);
            throw e;
        }

        // Limpa tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Cria viewport
        stage = new Stage(new FitViewport(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        
        touchPos = new Vector2();

        // Cria fonte para debug
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(1.5f);

        // Carrega o nivel
        loadLevel(levelId);
    }

    /**
     * Carrega o nivel especificado
     */
    private void loadLevel(int levelId) {
        try {
            levelManager = LevelManager.getInstance();
            levelManager.setCurrLevelIdx(levelId);
            currentLevel = levelManager.getLevel(levelId);

            if (currentLevel != null) {
                Gdx.app.log("GameScreen", "Carregando nivel " + (levelId + 1));
                circuit = currentLevel.getCircuit();

                // Atualiza posições dos elementos do circuito
                circuit.updateAllPos((int)BitItGame.VIRTUAL_WIDTH, (int)BitItGame.VIRTUAL_HEIGHT);
                
                //game.batch.setProjectionMatrix(viewport.getCamera().combined);
                // Cria renderizador de fios
                if (wireRenderer == null) {
                    wireRenderer = new WireRenderer();
                }
                //wireRenderer.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
                wireRenderer.renderAll(circuit.getWires());
                //carrega sprites das portas
                game.batch.begin();           
                for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                    if (gate != null) gate.render(game.batch);
                }
                game.batch.end();

                Gdx.app.log("GameScreen", "Nivel " + (levelId + 1) + " carregado com sucesso");
            } else {
                Gdx.app.error("GameScreen", "Nivel " + levelId + " não encontrado!");
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Erro ao carregar nivel " + levelId, e);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpa tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        viewport = stage.getViewport();
        handleInput();
        
        // Desenha o stage (background)
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
        
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
        // game.batch.begin();
        // debugFont.draw(game.batch, "Nivel: " + (levelId + 1), 10, BitItGame.VIRTUAL_HEIGHT - 10);
        // debugFont.draw(game.batch, "Toque nos inputs para mudar valores", 10, BitItGame.VIRTUAL_HEIGHT - 40);
        // debugFont.draw(game.batch, "ESC para voltar", 10, 30);
        // game.batch.end();
    }

    /**
     * Gerencia entrada do usuario
     */
    private void handleInput() {
        // Volta para tela de niveis ao pressionar ESC
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            int levelPage = levelId / 6; // LEVELS_PER_PAGE = 6
            game.setScreen(new LevelsScreen(game, levelPage));
            dispose();
            return;
        }

        // Detecta clique/toque nos inputs
        if (Gdx.input.justTouched() && circuit != null) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            //Gdx.app.log("GameScreen", "Clique em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ")");

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
            
            // verificacao do circuito depois de clique
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
                game.batch.begin();
                for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                    if (gate != null) gate.render(game.batch);
                }
                game.batch.end();
                //checagem de vitoria
                if (circuit.isCorrect()) {
                    Gdx.app.log("Main.draw", "Circuito correto! Nivel " + (levelId + 1) + " concluido.");
                    // voltar para a tela inicial e atualizar estado de jogos
                    circuit.resetInputs();
                    levelManager.getLevel(levelId).setCompleted(true);
                    levelManager.getLevel(levelId).setStars(3);
                    if (levelId + 1 < levelManager.getTotalLevels()) {
                        levelManager.getLevel(levelId + 1).setUnlocked(true);
                    }
                    //dispose();
                    
                    int levelPage = (levelId < levelManager.getTotalLevels() - 1 ? levelId + 1 : levelId) / 6; // LEVELS_PER_PAGE = 6
                    game.setScreen(new LevelsScreen(game, levelPage));
                }


            }
        }
    }

    @Override
    public void resize(int width, int height) {
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
        if (stage != null) stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (wireRenderer != null) wireRenderer.dispose();
        if (debugFont != null) debugFont.dispose();
        if (game.batch != null) game.batch.dispose();

        // Dispose textures owned by gates
        if (circuit != null) {
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.dispose();
            }
        }
    }
}
