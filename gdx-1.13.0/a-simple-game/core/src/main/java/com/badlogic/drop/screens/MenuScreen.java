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
 * Tela inicial do jogo
 */
public class MenuScreen implements Screen {
    private final BitItGame game;
    private Stage stage;
    private Texture backgroundTexture;
    private Texture playButtonTexture;
    private Texture creditsButtonTexture;
    private Image backgroundImage;

    public MenuScreen(final BitItGame game) {
        this.game = game;

        // Carrega texturas
        try {
            backgroundTexture = new Texture(Gdx.files.internal("textures/UI/homescreen.png"));
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setScale(0.5f);
            playButtonTexture = new Texture(Gdx.files.internal("textures/UI/playbutton.png"));
            creditsButtonTexture = new Texture(Gdx.files.internal("textures/UI/creditsbutton.png"));
        } catch (Exception e) {
            Gdx.app.error("MenuScreen", "Erro ao carregar texturas", e);
            throw e;
        }

        stage = new Stage(new FitViewport(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Configura botao de play
        ImageButton.ImageButtonStyle playButtonStyle = new ImageButton.ImageButtonStyle();
        playButtonStyle.imageUp = new TextureRegionDrawable(playButtonTexture);
        final ImageButton playButton = new ImageButton(playButtonStyle);
        float playBtnWidth = playButtonTexture.getWidth();
        float playBtnHeight = playButtonTexture.getHeight();
        float scale = 0.5f;

        // Configura botao de credits
        ImageButton.ImageButtonStyle creditsButtonStyle = new ImageButton.ImageButtonStyle();
        creditsButtonStyle.imageUp = new TextureRegionDrawable(creditsButtonTexture);
        final ImageButton creditsButton = new ImageButton(creditsButtonStyle);
        float creditBtnWidth = creditsButtonTexture.getWidth();
        float creditBtnHeight = creditsButtonTexture.getHeight();

        // Listener para clique do botao de Play
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Botao play clicado em: " + x + ", " + y);
                game.setScreen(new LevelsScreen(game));
                dispose();
            }
        });

        // Listener para clique do botao de Credits
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Botao creditos clicado em: " + x + ", " + y);
                //game.setScreen(new LevelsScreen(game));
                //dispose();
            }
        });

        // Define tamanho e posicao dos botoes
        playButton.setSize(playBtnWidth * scale, playBtnHeight * scale);
        playButton.setPosition((BitItGame.VIRTUAL_WIDTH - playBtnWidth * scale) / 2,  252);
        stage.addActor(playButton);
        creditsButton.setSize(creditBtnWidth * scale, creditBtnHeight * scale);
        creditsButton.setPosition((BitItGame.VIRTUAL_WIDTH - creditBtnWidth * scale) / 2 + 70, 78);
        stage.addActor(creditsButton);
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
        if (playButtonTexture != null) playButtonTexture.dispose();
    }
}
