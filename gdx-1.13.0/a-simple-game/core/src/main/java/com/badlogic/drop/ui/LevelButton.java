package com.badlogic.drop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Botao do nivel na tela de selecao
 */
public class LevelButton extends Stack {
    private int levelId;
    private boolean unlocked;
    private boolean completed;
    private int stars;
    private ImageButton button;
    private Label levelLabel;
    private Image starsImage;
    private Texture buttonTexture;
    private static Texture baseTexture = null;
    private static Texture[] starTextures = null;

    public LevelButton(int levelId, boolean unlocked, boolean completed, int stars, BitmapFont font) {
        this.levelId = levelId;
        this.unlocked = unlocked;
        this.completed = completed;
        this.stars = stars;

        // Carrega a textura base uma única vez
        if (baseTexture == null) {
            baseTexture = new Texture(Gdx.files.internal("textures/UI/levelbutton.png"));
        }

        // Carrega texturas das estrelas uma única vez
        if (starTextures == null) {
            starTextures = new Texture[4];
            starTextures[0] = new Texture(Gdx.files.internal("textures/UI/0star.png"));
            starTextures[1] = new Texture(Gdx.files.internal("textures/UI/1star.png"));
            starTextures[2] = new Texture(Gdx.files.internal("textures/UI/2star.png"));
            starTextures[3] = new Texture(Gdx.files.internal("textures/UI/3star.png"));
        }

        // Usa a textura base
        buttonTexture = baseTexture;

        // Cria o botao com escala maior
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(buttonTexture);
        button = new ImageButton(style);
        button.setSize(300, 300); // Aumenta o tamanho do botão

        // Aplica cor baseada no estado
        if (!unlocked) {
            // Nivel bloqueado
            button.setColor(0.2f, 0.2f, 0.2f, 1f);
        } else if (completed) {
            // Nivel completado
            button.setColor(0.4f, 1f, 0.5f, 1f);
        } else {
            // Nivel desbloqueado
            button.setColor(1f, 1f, 1f, 1f);
        }

        // Cria label com número do nivel (menor)
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = unlocked ? Color.WHITE : Color.DARK_GRAY;

        String labelText = unlocked ? String.valueOf(levelId + 1) : "?";
        levelLabel = new Label(labelText, labelStyle);
        levelLabel.setFontScale(2.75f); // Reduz o tamanho do número

        // Cria imagem de estrelas se completado
        if (completed && stars > 0) {
            int starsIndex = Math.min(Math.max(stars, 0), 3);
            starsImage = new Image(starTextures[starsIndex]);
        }

        // cria o conteudo do botao
        com.badlogic.gdx.scenes.scene2d.ui.Table contentTable = new com.badlogic.gdx.scenes.scene2d.ui.Table();
        contentTable.setFillParent(true);
        contentTable.add(levelLabel).padTop(30).row();
        if (completed && stars > 0 && starsImage != null) {
            contentTable.add(starsImage).size(721*0.18f, 281*0.18f).padTop(0);
        }
        this.add(button);
        this.add(contentTable);
    }

    /**
     * Atualiza o estado visual do botao
     */
    public void updateState(boolean unlocked, boolean completed, int stars) {
        this.unlocked = unlocked;
        this.completed = completed;
        this.stars = stars;

        // cor do botao baseado no estado
        if (!unlocked) {
            button.setColor(0.2f, 0.2f, 0.2f, 1f);
        } else if (completed) {
            button.setColor(0.4f, 1f, 0.5f, 1f);
        } else {
            button.setColor(1f, 1f, 1f, 1f);
        }

        // label
        levelLabel.setColor(unlocked ? Color.WHITE : Color.DARK_GRAY);
        levelLabel.setText(unlocked ? String.valueOf(levelId + 1) : "?");

        // estrelas
        if (starsImage != null) {
            starsImage.remove();
            starsImage = null;
        }

        if (completed && stars > 0) {
            int starsIndex = Math.min(Math.max(stars, 0), 3);
            starsImage = new Image(starTextures[starsIndex]);
            // Recria a tabela das estrelas
            this.clear();

            com.badlogic.gdx.scenes.scene2d.ui.Table contentTable = new com.badlogic.gdx.scenes.scene2d.ui.Table();
            contentTable.setFillParent(true);
            contentTable.add(levelLabel).padTop(10).row();
            contentTable.add(starsImage).size(80, 20).padTop(5);

            this.add(button);
            this.add(contentTable);
        }
    }

    public void dispose() {
    }
}
