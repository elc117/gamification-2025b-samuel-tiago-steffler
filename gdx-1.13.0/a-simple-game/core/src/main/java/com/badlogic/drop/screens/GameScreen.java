package com.badlogic.drop.screens;

import com.badlogic.drop.BitItGame;
import com.badlogic.drop.entities.Circuit;
import com.badlogic.drop.entities.gates.InputBits;
import com.badlogic.drop.levels.Level;
import com.badlogic.drop.levels.LevelManager;
import com.badlogic.drop.levels.LevelProgress;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
    private final Stage stage;

    private int moves;
    private final int levelId;
    private Level currentLevel;

    private Circuit circuit;
    private WireRenderer wireRenderer;
    private Viewport viewport;
    private LevelManager levelManager;

    private final Vector2 touchPos;
    private final BitmapFont debugFont;
    private final Label movesLabel;

    private final MenuPopup menuPopup;
    private final LevelCompletePopUp levelupPopup;

    private boolean levelCompleted = false;
    private boolean firstMove = false;

    public GameScreen(final BitItGame game, int levelId) {
        game.batch = new SpriteBatch();
        this.game = game;
        this.levelId = levelId;
        this.moves = 0;
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

        // Viewport
        stage = new Stage(new FitViewport(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        touchPos = new Vector2();

        // fonte para debug
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(1.5f);

        // label de movimentos
        movesLabel = new Label("Moves: 0", new Label.LabelStyle(debugFont, Color.WHITE));
        movesLabel.setPosition(10, BitItGame.VIRTUAL_HEIGHT - 40);
        stage.addActor(movesLabel);

        // menu popup
        menuPopup = new MenuPopup(stage, BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT);

        // levelup popup
        levelupPopup = new LevelCompletePopUp(stage, BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT);

        // Carrega o nivel
        loadLevel(levelId);

        // Configuracao dos listeners
        // Menu de pause
        menuPopup.setRestartListener(() -> {
            if (circuit != null) circuit.resetInputs();
            game.setScreen(new GameScreen(game, levelId));
        });
        menuPopup.setHomeListener(() -> {
            int levelPage = levelId / 6;
            if (circuit != null) circuit.resetInputs();
            game.setScreen(new LevelsScreen(game, levelPage));
        });

        // Menu de level complete
        levelupPopup.setRestartListener(() -> {
            if (circuit != null) circuit.resetInputs();
            game.setScreen(new GameScreen(game, levelId));
        });
        levelupPopup.setNextLevelListener(() -> {
            if (circuit != null) circuit.resetInputs();
            // vai para proximo nivel se houver, senao vai para a pagina de niveis
            if (levelId + 1 < levelManager.getTotalLevels()) {
                game.setScreen(new GameScreen(game, levelId + 1));
            } else {
                int levelPage = levelId / 6;
                game.setScreen(new LevelsScreen(game, levelPage));
            }
        });
        levelupPopup.setLevelMenuListener(() -> {
            int levelPage = levelId / 6;
            if (circuit != null) circuit.resetInputs();
            game.setScreen(new LevelsScreen(game, levelPage));
        });
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

                circuit.resetInputs();

                circuit.updateAllPos((int)BitItGame.VIRTUAL_WIDTH, (int)BitItGame.VIRTUAL_HEIGHT);

                // Cria renderizador de fios
                if (wireRenderer == null) {
                    wireRenderer = new WireRenderer();
                }

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

        // Atualiza o texto
        movesLabel.setText("Moves: " + moves);

        viewport = stage.getViewport();
        handleInput();
        stage.act(Math.min(delta, 1 / 30f));

        // desenha background
        viewport.apply();
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        backgroundImage.draw(game.batch, 1);
        game.batch.end();

        if (circuit != null) {
            // evita evaluacao quando ha popup aberto
            if ((menuPopup == null || !menuPopup.isVisible()) &&
                (levelupPopup == null || !levelupPopup.isVisible())) {
                circuit.evaluate();
            }

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

        // desenhar menu
        if (menuPopup != null && menuPopup.isVisible()) {
            menuPopup.draw();
        }

        // desenhar popup de level up
        if (levelupPopup != null && levelupPopup.isVisible()) {
            levelupPopup.draw();
        }

        // desenhar labels da UI
        game.batch.begin();
        movesLabel.draw(game.batch, 1);
        game.batch.end();

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
        // Bloquear todas as entradas se o popup de level up estiver visível
        if (levelupPopup != null && levelupPopup.isVisible()) {
            return;
        }

        // liga o menu com esc
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            if (menuPopup != null) {
                menuPopup.toggle();
            }
            return;
        }
        // nao interagir com o toque com menu abertp
        if (menuPopup != null && menuPopup.isVisible()) {
            return;
        }


        // Detecta clique/toque nos inputs
        if (Gdx.input.justTouched() && circuit != null) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);

            //Gdx.app.log("GameScreen", "Clique em: (" + (int)touchPos.x + ", " + (int)touchPos.y + ")");

            // topo superior direito
            if (touchPos.x > BitItGame.VIRTUAL_WIDTH - 200 && touchPos.y > BitItGame.VIRTUAL_HEIGHT - 200) {
                if (menuPopup != null) {
                    Gdx.app.log("GameScreen", "Menu clicado");
                    menuPopup.toggle();
                }
                return;
            }

            // Verifica se clicou em algum input bit
            Array<InputBits> inputs = circuit.getInputs();
            for (InputBits input : inputs) {
                Rectangle bounds = new Rectangle(input.getX(), input.getY(),
                                                 input.getWidth(), input.getHeight());
                if (bounds.contains(touchPos.x, touchPos.y)) {
                    input.toggle();
                    moves++;
                    firstMove = true;
                    Gdx.app.log("GameScreen", "Input " + input.getLabel() + " -> " + input.getValue());
                }
            }

            // verificacao do circuito depois de clique
            if (circuit != null) {
                // primeiramente calcula todos os valores do circuito
                circuit.evaluate();

                // primeiro fios
                if (wireRenderer != null) {
                    wireRenderer.getShapeRenderer().setProjectionMatrix(viewport.getCamera().combined);
                    wireRenderer.renderAll(circuit.getWires());
                }

                // dai os gates
                game.batch.begin();
                for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                    if (gate != null) gate.render(game.batch);
                }
                game.batch.end();
            }
        }

        // Verificação de vitoria
        if (circuit != null && !levelCompleted && firstMove && circuit.isCorrect()) {
            levelCompleted = true;
            Gdx.app.log("GameScreen", "Circuito correto! Nivel " + (levelId + 1) + " concluido.");

            // Calcula estrelas baseado no numero de movimentos e minMoves do nivel
            int minMoves = currentLevel.getMinMoves();
            int stars = calculateStars(moves, minMoves);
            Gdx.app.log("GameScreen", "Estrelas: " + stars + " (moves: " + moves + ", minMoves: " + minMoves + ")");

            // Atualiza progresso do nivel usando LevelProgress
            LevelProgress levelProgress = LevelProgress.getInstance();
            levelProgress.setLevelCompleted(levelId, true);

            // atualiza dados do nivel atual no LevelManager
            levelManager.getLevel(levelId).setCompleted(true);
            levelManager.getLevel(levelId).setStars(stars);
            if (levelId + 1 < levelManager.getTotalLevels()) {
                levelManager.getLevel(levelId + 1).setUnlocked(true);
            }

            // Mostra popup de nivel completo com estrelas
            if (levelupPopup != null) {
                levelupPopup.setStars(stars);
                levelupPopup.show();
            }
        }
    }

    /**
     * Calcula o número de estrelas baseado nos movimentos
     * 3 estrelas: moves == minMoves (solução perfeita)
     * 2 estrelas: moves == minMoves + 1
     * 1 estrela: acima disso
     */
    private int calculateStars(int moves, int minMoves) {
        if (moves == minMoves) {
            return 3;
        } else if (moves == minMoves + 2) {
            return 2;
        } else {
            return 1;
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
        if (menuPopup != null) menuPopup.dispose();

        // texturas dos gates
        if (circuit != null) {
            for (com.badlogic.drop.entities.gates.LogicGate gate : circuit.getAllGates()) {
                if (gate != null) gate.dispose();
            }
        }
    }
}
