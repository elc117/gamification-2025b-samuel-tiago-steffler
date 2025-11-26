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
 * Tela de creditos
 */
public class CreditsScreen implements Screen {
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;
    private Texture buttonBackTexture;


    public CreditsScreen(final BitItGame game) {
        Gdx.app.log("CreditsScreen", "Tela de creditos criada");

         // Carrega texturas
        try {
            backgroundTexture = new Texture(Gdx.files.internal("textures/guides/creditsscreen.png"));
            backgroundImage = new Image(backgroundTexture);
            backgroundImage.setScale(0.5f);

            buttonBackTexture = new Texture(Gdx.files.internal("textures/UI/pageup.png"));

        } catch (Exception e) {
            Gdx.app.error("CreditsScreen", "Erro ao carregar texturas", e);
            throw e;
        }

        stage = new Stage(new FitViewport(BitItGame.VIRTUAL_WIDTH, BitItGame.VIRTUAL_HEIGHT));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        float scale = 0.5f;

        // botao de back
        ImageButton.ImageButtonStyle backButtonStyle = new ImageButton.ImageButtonStyle();
        backButtonStyle.imageUp = new TextureRegionDrawable(buttonBackTexture);
        final ImageButton backButton = new ImageButton(backButtonStyle);
        backButton.getImage().setRotation(90f);
        float backBtnWidth = buttonBackTexture.getWidth();
        float backBtnHeight = buttonBackTexture.getHeight();

        backButton.setSize(backBtnWidth * scale, backBtnHeight * scale);
        backButton.setPosition(100, BitItGame.VIRTUAL_HEIGHT - backBtnHeight * scale - 40);
        stage.addActor(backButton);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("LevelScreen", "Botao back clicado");
                game.setScreen(new MenuScreen(game));
                //dispose();
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
        if (buttonBackTexture != null) buttonBackTexture.dispose();
    }
}
