package com.badlogic.drop.screens;
import com.badlogic.drop.BitItGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Tela de selecao de niveis com matriz 3x2 e paginacao
 */
public class HowToPlayScreen implements Screen {
    private final BitItGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private Texture buttonBackTexture;

    // Tutorial
    private Texture currentTutorialTexture;
    private Image currentTutorialImage;

    // Paginas
    private int currentPage;
    private int totalPages;

    private final ImageButton upButton;
    private final ImageButton downButton;
    private final ImageButton backButton;

    private String paths[] = {
            "textures/guides/inputs.png",
            "textures/guides/outputs.png",
            "textures/guides/gates.png",
            "textures/guides/not.png",
            "textures/guides/or.png",
            "textures/guides/and.png",
            "textures/guides/nor.png",
            "textures/guides/nand.png",
            "textures/guides/xor.png",
            "textures/guides/xnor.png"
    };

    public HowToPlayScreen(final BitItGame game) {
        this(game, 0);
    }

    public HowToPlayScreen(final BitItGame game, int startPage) {
        this.game = game;
        this.currentPage = startPage;

        totalPages = paths.length;

        Gdx.app.log("HowToPlayScreen", "Total de paginas: " + totalPages);

        // Carrega texturas
        try {
            // botoes de navegacao entre niveis e home
            buttonUpTexture = new Texture(Gdx.files.internal("textures/UI/pageup.png"));
            buttonDownTexture = new Texture(Gdx.files.internal("textures/UI/pagedown.png"));
            buttonBackTexture = new Texture(Gdx.files.internal("textures/UI/pageup.png"));

        } catch (Exception e) {
            Gdx.app.error("HowToPlayScreen", "Erro ao carregar texturas", e);
            throw e;
        }

        stage = new Stage(new FitViewport(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT));

        // Carrega a primeira imagem do tutorial
        loadTutorialImage(currentPage);

        float scale = 0.4f;

        // botao de up
        ImageButton.ImageButtonStyle upButtonStyle = new ImageButton.ImageButtonStyle();
        upButtonStyle.imageUp = new TextureRegionDrawable(buttonUpTexture);
        upButton = new ImageButton(upButtonStyle);
        float upBtnWidth = buttonUpTexture.getWidth();
        float upBtnHeight = buttonUpTexture.getHeight();

        // botao de down
        ImageButton.ImageButtonStyle downButtonStyle = new ImageButton.ImageButtonStyle();
        downButtonStyle.imageUp = new TextureRegionDrawable(buttonDownTexture);
        downButton = new ImageButton(downButtonStyle);
        float downBtnWidth = buttonDownTexture.getWidth();
        float downBtnHeight = buttonDownTexture.getHeight();

        // botao de back
        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = new TextureRegionDrawable(buttonBackTexture);
        backButton = new ImageButton(backButtonStyle);
        backButton.getImage().setRotation(90f);
        float backBtnWidth = buttonBackTexture.getWidth();
        float backBtnHeight = buttonBackTexture.getHeight();

        // tamanho e posicao dos botoes de navegacao
        upButton.setSize(upBtnWidth * scale, upBtnHeight * scale);
        upButton.setPosition((BitItGame.VIRTUAL_WIDTH - upBtnWidth * scale) / 2, 740);
        stage.addActor(upButton);

        downButton.setSize(downBtnWidth * scale, downBtnHeight * scale);
        downButton.setPosition((BitItGame.VIRTUAL_WIDTH - downBtnWidth * scale) / 2, 20);
        stage.addActor(downButton);

        backButton.setSize(backBtnWidth * scale, backBtnHeight * scale);
        backButton.setPosition(75, BitItGame.VIRTUAL_HEIGHT - backBtnHeight * scale - 30);
        stage.addActor(backButton);

        // Listener para navegacao entre paginas
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
                Gdx.app.log("HowToPlayScreen", "Botao back clicado");
                game.setScreen(new MenuScreen(game));
                //dispose();
            }
        });

        // Carrega a primeira pagina de niveis
        loadTutorialImage(currentPage);
        updateNavigationButtons();
    }

    /**
     * Carrega a imagem do tutorial para a pagina especificada
     */
    private void loadTutorialImage(int page) {
        try {
            // Remove a imagem anterior se existir
            if (currentTutorialImage != null) {
                currentTutorialImage.remove();
            }

            // Libera a textura anterior se existir
            if (currentTutorialTexture != null) {
                currentTutorialTexture.dispose();
            }

            // Carrega a nova textura do tutorial
            String path = paths[page];
            currentTutorialTexture = new Texture(Gdx.files.internal(path));
            currentTutorialImage = new Image(currentTutorialTexture);
            float imageWidth = currentTutorialTexture.getWidth();
            float imageHeight = currentTutorialTexture.getHeight();
            currentTutorialImage.setSize(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT);
            float x = (BitItGame.VIRTUAL_WIDTH - currentTutorialImage.getWidth()) / 2;
            float y = (BitItGame.VIRTUAL_HEIGHT - currentTutorialImage.getHeight()) / 2;
            currentTutorialImage.setPosition(x, y);

            stage.addActor(currentTutorialImage);
            upButton.toFront();
            downButton.toFront();
            backButton.toFront();

            Gdx.app.log("HowToPlayScreen", "Imagem do tutorial carregada: " + path);

        } catch (Exception e) {
            Gdx.app.error("HowToPlayScreen", "Erro ao carregar imagem do tutorial: " + paths[page], e);
        }
    }

    /**
     * Vai para a proxima pagina
     */
    private void nextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadTutorialImage(currentPage);
            updateNavigationButtons();
            Gdx.app.log("HowToPlayScreen", "Proxima pagina: " + currentPage);
        }
    }

    /**
     * Volta para a pagina anterior
     */
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            loadTutorialImage(currentPage);
            updateNavigationButtons();
            Gdx.app.log("HowToPlayScreen", "Pagina anterior: " + currentPage);
        }
    }

    /**
     * Atualiza visibilidade dos botoes de navegacao
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
        if (currentTutorialTexture != null) currentTutorialTexture.dispose();
    }
}
