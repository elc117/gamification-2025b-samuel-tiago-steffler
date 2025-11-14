package com.badlogic.drop.screens;

import com.badlogic.drop.DropGame;
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
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Tela de seleção de níveis
 */
public class LevelsScreen implements Screen {
    private final DropGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Texture buttonUpTexture;
    private Texture buttonDownTexture;
    private Texture buttonBackTexture;

    public LevelsScreen(final DropGame game) {
        this.game = game;

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

        // Configura layout para futuros botões de níveis
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Configura botao de up
        ImageButton.ImageButtonStyle upButtonStyle = new ImageButton.ImageButtonStyle();
        upButtonStyle.imageUp = new TextureRegionDrawable(buttonUpTexture);
        final ImageButton upButton = new ImageButton(upButtonStyle);
        float upBtnWidth = buttonUpTexture.getWidth();
        float upBtnHeight = buttonUpTexture.getHeight();
        float scale = 0.5f;
        // Configura botao de down
        ImageButton.ImageButtonStyle downButtonStyle = new ImageButton.ImageButtonStyle();
        downButtonStyle.imageUp = new TextureRegionDrawable(buttonDownTexture);
        final ImageButton downButton = new ImageButton(downButtonStyle);
        float downBtnWidth = buttonDownTexture.getWidth();
        float downBtnHeight = buttonDownTexture.getHeight();
        // Configura botao de back
        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = new TextureRegionDrawable(buttonBackTexture);
        final ImageButton backButton = new ImageButton(backButtonStyle);
        //backButton.getImage().setOrigin(buttonBackTexture.getWidth() / 2f, buttonBackTexture.getHeight() / 2f);
        backButton.getImage().setRotation(90f);
        float backBtnWidth = buttonBackTexture.getWidth();
        float backBtnHeight = buttonBackTexture.getHeight();

        // TODO: Adicionar botões de níveis aqui
        


        // Define tamanho e posicao dos botoes de navegacao
        upButton.setSize(upBtnWidth * scale, upBtnHeight * scale);
        upButton.setPosition((DropGame.VIRTUAL_WIDTH - upBtnWidth * scale) / 2,  725);
        stage.addActor(upButton);
        downButton.setSize(downBtnWidth * scale, downBtnHeight * scale);
        downButton.setPosition((DropGame.VIRTUAL_WIDTH - downBtnWidth * scale) / 2, 30);
        stage.addActor(downButton);
        backButton.setSize(backBtnWidth * scale, backBtnHeight * scale);
        backButton.setPosition(100,  DropGame.VIRTUAL_HEIGHT - backBtnHeight * scale - 40);
        stage.addActor(backButton);


        // Listener para clique dos botoes de navegacao
        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "Botao up clicado em: " + x + ", " + y);
                //game.setScreen(new LevelsScreen(game));
                //dispose();
            }
        });
        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "Botao down clicado em: " + x + ", " + y);
                //game.setScreen(new LevelsScreen(game));
                //dispose();
            }
        });
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "Botao back clicado em: " + x + ", " + y);
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });
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
    }
}
