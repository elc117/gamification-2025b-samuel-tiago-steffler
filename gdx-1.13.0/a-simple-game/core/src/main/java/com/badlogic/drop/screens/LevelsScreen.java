package com.badlogic.drop.screens;

import com.badlogic.drop.DropGame;
import com.badlogic.drop.levels.LevelManager;
import com.badlogic.drop.levels.LevelProgress;
import com.badlogic.drop.ui.LevelButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Tela de seleção de níveis com matriz 3x2 e paginação
 */
public class LevelsScreen implements Screen {
    private final DropGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private Texture buttonBackTexture;

    // Gerenciamento de níveis
    private LevelManager levelManager;
    private LevelProgress levelProgress;
    private Array<LevelButton> levelButtons;
    private Table levelsTable;

    // Paginação
    private static final int LEVELS_PER_PAGE = 6; // 3x2
    private static final int ROWS = 3;
    private static final int COLS = 2;
    private int currentPage = 0;
    private int totalPages;

    private ImageButton upButton;
    private ImageButton downButton;

    public LevelsScreen(final DropGame game) {
        this.game = game;

        // Inicializa gerenciadores
        levelManager = LevelManager.getInstance();
        levelProgress = LevelProgress.getInstance();
        levelButtons = new Array<>();

        // Calcula total de páginas
        int totalLevels = levelManager.getTotalLevels();
        totalPages = (int) Math.ceil((float) totalLevels / LEVELS_PER_PAGE);

        Gdx.app.log("LevelsScreen", "Total de níveis: " + totalLevels + ", Páginas: " + totalPages);

        // Carrega texturas
        try {
            backgroundTexture = new Texture(Gdx.files.internal("textures/UI/levelscreen.png"));
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setScale(0.5f);

            // botoes de navegacao entre niveis e home
            buttonUpTexture = new Texture(Gdx.files.internal("textures/UI/pageup.png"));
            buttonDownTexture = new Texture(Gdx.files.internal("textures/UI/pagedown.png"));
            buttonBackTexture = new Texture(Gdx.files.internal("textures/UI/pageup.png"));

        } catch (Exception e) {
            Gdx.app.error("LevelScreen", "Erro ao carregar texturas", e);
            throw e;
        }

        stage = new Stage(new FitViewport(DropGame.VIRTUAL_WIDTH, DropGame.VIRTUAL_HEIGHT));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Cria tabela para os botões de níveis
        levelsTable = new Table();
        levelsTable.setPosition(DropGame.VIRTUAL_WIDTH / 2, DropGame.VIRTUAL_HEIGHT / 2);
        stage.addActor(levelsTable);

        float scale = 0.5f;

        // Configura botao de up (página anterior)
        ImageButton.ImageButtonStyle upButtonStyle = new ImageButton.ImageButtonStyle();
        upButtonStyle.imageUp = new TextureRegionDrawable(buttonUpTexture);
        upButton = new ImageButton(upButtonStyle);
        float upBtnWidth = buttonUpTexture.getWidth();
        float upBtnHeight = buttonUpTexture.getHeight();

        // Configura botao de down (próxima página)
        ImageButton.ImageButtonStyle downButtonStyle = new ImageButton.ImageButtonStyle();
        downButtonStyle.imageUp = new TextureRegionDrawable(buttonDownTexture);
        downButton = new ImageButton(downButtonStyle);
        float downBtnWidth = buttonDownTexture.getWidth();
        float downBtnHeight = buttonDownTexture.getHeight();

        // Configura botao de back
        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = new TextureRegionDrawable(buttonBackTexture);
        final ImageButton backButton = new ImageButton(backButtonStyle);
        backButton.getImage().setRotation(90f);
        float backBtnWidth = buttonBackTexture.getWidth();
        float backBtnHeight = buttonBackTexture.getHeight();

        // Define tamanho e posicao dos botoes de navegacao
        upButton.setSize(upBtnWidth * scale, upBtnHeight * scale);
        upButton.setPosition((DropGame.VIRTUAL_WIDTH - upBtnWidth * scale) / 2, 725);
        stage.addActor(upButton);

        downButton.setSize(downBtnWidth * scale, downBtnHeight * scale);
        downButton.setPosition((DropGame.VIRTUAL_WIDTH - downBtnWidth * scale) / 2, 30);
        stage.addActor(downButton);

        backButton.setSize(backBtnWidth * scale, backBtnHeight * scale);
        backButton.setPosition(100, DropGame.VIRTUAL_HEIGHT - backBtnHeight * scale - 40);
        stage.addActor(backButton);

        // Listener para navegação de páginas
        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                previousPage();
            }
        });

        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nextPage();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "Botao back clicado");
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });

        // Carrega a primeira página de níveis
        loadLevelsPage(currentPage);
        updateNavigationButtons();
    }

    /**
     * Carrega e exibe os níveis da página especificada
     */
    private void loadLevelsPage(int page) {
        // Limpa botões anteriores
        levelsTable.clear();
        for (LevelButton btn : levelButtons) {
            btn.dispose();
        }
        levelButtons.clear();

        int startIndex = page * LEVELS_PER_PAGE;
        int totalLevels = levelManager.getTotalLevels();

        Gdx.app.log("LevelsScreen", "Carregando página " + page + ", níveis " + startIndex + " a " + (startIndex + LEVELS_PER_PAGE - 1));

        // Cria matriz 3x2
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int levelIndex = startIndex + (row * COLS) + col;

                if (levelIndex < totalLevels) {
                    boolean unlocked = levelProgress.isLevelUnlocked(levelIndex);
                    boolean completed = levelProgress.isLevelCompleted(levelIndex);

                    final LevelButton levelBtn = new LevelButton(levelIndex, unlocked, completed, game.font);
                    levelButtons.add(levelBtn);

                    // Adiciona listener se estiver desbloqueado
                    if (unlocked) {
                        final int finalLevelIndex = levelIndex;
                        levelBtn.getButton().addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                Gdx.app.log("LevelsScreen", "Nível " + (finalLevelIndex + 1) + " clicado");
                                game.setScreen(new GameScreen(game, finalLevelIndex));
                                dispose();
                            }
                        });
                    }

                    levelsTable.add(levelBtn).size(110, 110).pad(10);
                } else {
                    // Célula vazia
                    levelsTable.add().size(110, 110).pad(10);
                }
            }
            levelsTable.row();
        }
    }

    /**
     * Vai para a próxima página
     */
    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadLevelsPage(currentPage);
            updateNavigationButtons();
            Gdx.app.log("LevelsScreen", "Próxima página: " + currentPage);
        }
    }

    /**
     * Volta para a página anterior
     */
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadLevelsPage(currentPage);
            updateNavigationButtons();
            Gdx.app.log("LevelsScreen", "Página anterior: " + currentPage);
        }
    }

    /**
     * Atualiza visibilidade dos botões de navegação
     */
    private void updateNavigationButtons() {
        upButton.setVisible(currentPage > 0);
        downButton.setVisible(currentPage < totalPages - 1);
    }

    @Override
    public void show() {
        // Define stage como input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Limpa tela
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza e desenha stage
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (buttonUpTexture != null) buttonUpTexture.dispose();
        if (buttonDownTexture != null) buttonDownTexture.dispose();
        if (buttonBackTexture != null) buttonBackTexture.dispose();

        // Limpa botões de níveis
        for (LevelButton btn : levelButtons) {
            btn.dispose();
        }
        levelButtons.clear();
    }
}
