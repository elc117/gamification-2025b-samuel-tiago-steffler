package com.badlogic.drop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/*
 * Classe para abrir menu para voltar para a tela principal ou reiniciar o nivel
 */
public class MenuPopup {
    
    private final Stage stage;
    private final Image menuImage;
    private final Texture menuTexture;
    private final ImageButton restartButton;
    private final ImageButton homeButton;
    private boolean visible;
    private final Image bkgclick; // so pra detectar clique fora do menu
    
    public MenuPopup(Stage stage, float width, float height) {
        this.stage = stage;
        this.visible = false;
        
       // bkgclick vai carregar imagem vazia pq o dim ja tem no popupmenu
        bkgclick = new Image();
        bkgclick.setSize(width, height);
        bkgclick.setPosition(0, 0);
        bkgclick.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // pos x e y e tamanho do menu
                float menuX = 60f;
                float menuY = 260f;
                float menuWidth = 420f;
                float menuHeight = 440f;
                // esconder se clicar fora do menu
                if (x < menuX || x > menuX + menuWidth || 
                    y < menuY || y > menuY + menuHeight) {
                    hide();
                }
            }
        });
        
        // mudar pra depois ficar com textura sem botoes
        // original do canva = 1080x1920
        // escala de FHD pra tela atual (540x960) = 0.5
        menuTexture = new Texture(Gdx.files.internal("textures/UI/pausemenu.png"));
        Image scaleImg = new Image(menuTexture);
        scaleImg.setSize(scaleImg.getWidth() * 0.5f, scaleImg.getHeight() * 0.5f);
        menuImage = scaleImg;
        
        
        float menuWidth = menuImage.getWidth();
        float menuHeight = menuImage.getHeight();
        Gdx.app.log("MenuPopup", "tamanho: " + menuWidth + "x" + menuHeight);
        menuImage.setSize(menuWidth, menuHeight);
        menuImage.setPosition((width - menuWidth) / 2, (height - menuHeight) / 2);
        
        // restart na esquerda (x = 230, y = 860 250x250 no original, escala 0.5)
        // hardcode pq preguica de calcular
        restartButton = criabotao(230/2, 860/2, 125, 125);
        
        // niveis na direira (x = 575, y = 860 250x250 no original, escala 0.5)
        homeButton = criabotao(575/2, 860/2, 125, 125);
        
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
    // botao para levelscreen
    public void setHomeListener(Runnable onHome) {
        homeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onHome.run();
                hide();
            }
        });
    }
    
    public void show() {
        if (!visible) {
            visible = true;
            stage.addActor(menuImage);
            stage.addActor(bkgclick);
            stage.addActor(restartButton);
            stage.addActor(homeButton);
        }
    }
    
    public void hide() {
        if (visible) {
            visible = false;
            bkgclick.remove();
            menuImage.remove();
            restartButton.remove();
            homeButton.remove();
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
    
    // jeitinho de conseguir renderizar o menu sobre o circuito sobre o bkg
    public void draw() {
        if (!visible) return;
        
        stage.getBatch().begin();
        menuImage.draw(stage.getBatch(), 1);
        restartButton.draw(stage.getBatch(), 1);
        homeButton.draw(stage.getBatch(), 1);
        stage.getBatch().end();
    }
    
    public void dispose() {
        if (menuTexture != null) {
            menuTexture.dispose();
        }
    }
}
