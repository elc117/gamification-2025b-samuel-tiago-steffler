package com.badlogic.drop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/*
 * Classe para abrir menu apos completar nivel
 */
public class LevelCompletePopUp {

    private final Stage stage;
    private final Image menuImage;
    private final Texture menuTexture;

    private final ImageButton restartButton;
    private final ImageButton nextButton;
    private final ImageButton lvlMenuButton;

    private boolean visible;
    private boolean isPeeking = false;
    private final Image bkgclick; // so pra detectar clique fora do menu

    // Sistema de estrelas
    private int stars = 0;
    private Texture star1Texture;
    private Texture star2Texture;
    private Texture star3Texture;
    private Image starImage;

    public LevelCompletePopUp(Stage stage, float width, float height) {
        this.stage = stage;
        this.visible = false;

        bkgclick = new Image();
        bkgclick.setSize(width, height);
        bkgclick.setPosition(0, 0);

        // Listener para peek quando pressionado fora do menu
        bkgclick.addListener(new ClickListener() {

            // habilita peek ao clicar fora do menu
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float menuX = 60f;
                float menuY = 260f;
                float menuWidth = 420f;
                float menuHeight = 440f;

                boolean outsideMenu = x < menuX || x > menuX + menuWidth ||
                                     y < menuY || y > menuY + menuHeight;

                Gdx.app.log("LvlPopup", "touchDown - fora do menu: " + outsideMenu + " x: " + x + " y: " + y);

                if (outsideMenu) {
                    if (!isPeeking) {
                        Gdx.app.log("LvlPopup", "touchDown - entrando em peek > isPeeking: " + isPeeking);
                        isPeeking = true;
                        toggle();
                    }
                }

                return true;
            }

            // desabilita peek ao soltar o clique
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
              Gdx.app.log("LvlPopup", "touchUp - restaurando menu");
              toggle();
              isPeeking = false;

            }
        });


        // mudar pra depois ficar com textura sem botoes
        // original do canva = 1080x1920
        // escala de FHD pra tela atual (540x960) = 0.5
        menuTexture = new Texture(Gdx.files.internal("textures/UI/lvlupmenu.png"));
        Image scaleImg = new Image(menuTexture);
        scaleImg.setSize(scaleImg.getWidth() * 0.5f, scaleImg.getHeight() * 0.5f);
        menuImage = scaleImg;

        float menuWidth = menuImage.getWidth();
        float menuHeight = menuImage.getHeight();
        Gdx.app.log("LvlPopup", "tamanho: " + menuWidth + "x" + menuHeight);
        menuImage.setSize(menuWidth, menuHeight);
        menuImage.setPosition((width - menuWidth) / 2, (height - menuHeight) / 2);

        // restart na esquerda (x = 185, y = 410 200x200 no original, escala 0.5)
        restartButton = criabotao(540/2 - 180, 620/2, 100, 100);

        // proximo nivel ao meio (x = 575, y = 410 200x200 no original, escala 0.5)
        nextButton = criabotao(540/2 - 50, 620/2, 100, 100);

        // menu de niveis na direita (x = 400, y = 410 200x200 no original, escala 0.5)
        lvlMenuButton = criabotao(540/2 + 80, 620/2, 100, 100);

        // Carrega texturas de estrelas (cada pontuação tem sua própria textura)
        star1Texture = new Texture(Gdx.files.internal("textures/UI/1star.png"));
        star2Texture = new Texture(Gdx.files.internal("textures/UI/2star.png"));
        star3Texture = new Texture(Gdx.files.internal("textures/UI/3star.png"));

        // Cria imagem de estrela (textura será alterada dinamicamente baseada na pontuação)
        starImage = new Image(star3Texture);
        float starWidth = 721*0.5f; // Largura para caber 3 estrelas lado a lado
        float starHeight = 281*0.5f;
        starImage.setSize(starWidth, starHeight);
        starImage.setPosition((width - starWidth) / 2 + 1, height / 2 - 40); // Centralizada acima do menu
        starImage.setVisible(false);
    }

    /**
     * Cria um botão invisível (hit box) para detectar cliques
     */
    private ImageButton criabotao(float x, float y, float width, float height) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        ImageButton button = new ImageButton(style);
        button.setPosition(x, y);
        button.setSize(width, height);
        return button;
    }

    // botao reiniciar
    public void setRestartListener(Runnable onRestart) {
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onRestart.run();
                hide();
            }
        });
    }

    // botao para proximo nivel
    public void setNextLevelListener(Runnable onNextLevel) {
        nextButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onNextLevel.run();
                hide();
            }
        });
    }

    // botao menu de niveis
    public void setLevelMenuListener(Runnable onLevelMenu) {
        lvlMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onLevelMenu.run();
                hide();
            }
        });
    }


    /**
     * Define a textura das estrelas conforme numero de estrelas
     */
    public void setStars(int numStars) {
        this.stars = Math.max(1, Math.min(3, numStars)); // Garante entre 1 e 3

        switch (this.stars) {
            case 1:
                starImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(star1Texture));
                break;
            case 2:
                starImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(star2Texture));
                break;
            case 3:
                starImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(star3Texture));
                break;
        }

        Gdx.app.log("LvlPopup", "Configurando " + this.stars + " estrelas");
    }

    public void show() {
        if (!visible) {
            visible = true;
            Gdx.app.log("LvlPopup", "Mostrando popup > visible: " + visible + " isPeeking: " + isPeeking);
            stage.addActor(menuImage);

            // Adiciona a imagem de estrelas
            starImage.setVisible(true);
            stage.addActor(starImage);

            // remove e adiciona de novo para garantir que esteja por cima apos peek
            if (isPeeking == false) stage.addActor(bkgclick);
            else {
                bkgclick.remove();
                stage.addActor(bkgclick);
            }

            stage.addActor(restartButton);
            stage.addActor(nextButton);
            stage.addActor(lvlMenuButton);

        }
    }

    public void hide() {
        if (visible) {
            visible = false;
            Gdx.app.log("LvlPopup", "Escondendo popup > visible: " + visible + " isPeeking: " + isPeeking);

            // nao remove para detectar peek no clickup
            if (isPeeking == false) bkgclick.remove();

            menuImage.remove();
            restartButton.remove();
            nextButton.remove();
            lvlMenuButton.remove();

            // Remove a imagem de estrelas
            starImage.setVisible(false);
            starImage.remove();

        }
    }

    // aparece e some
    public void toggle() {
        if (visible) {
            hide();
        } else {
            show();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isPeeking() {
        return isPeeking;
    }

    // jeitinho de conseguir renderizar o menu sobre o circuito sobre o bkg
    public void draw() {
        if (!visible) return;

        stage.getBatch().begin();
        menuImage.draw(stage.getBatch(), 1);
        restartButton.draw(stage.getBatch(), 1);
        nextButton.draw(stage.getBatch(), 1);
        lvlMenuButton.draw(stage.getBatch(), 1);

        // Desenha a imagem de estrelas
        starImage.draw(stage.getBatch(), 1);

        stage.getBatch().end();
    }

    public void dispose() {
        if (menuTexture != null) {
            menuTexture.dispose();
        }
        if (star1Texture != null) {
            star1Texture.dispose();
        }
        if (star2Texture != null) {
            star2Texture.dispose();
        }
        if (star3Texture != null) {
            star3Texture.dispose();
        }
    }
}
