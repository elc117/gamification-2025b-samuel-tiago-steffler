package com.badlogic.drop.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private ImageButton button;
    private Label levelLabel;
    private Texture buttonTexture;
    private static Texture baseTexture = null;

    public LevelButton(int levelId, boolean unlocked, boolean completed, BitmapFont font) {
        this.levelId = levelId;
        this.unlocked = unlocked;
        this.completed = completed;

        // Carrega a textura base uma única vez
        if (baseTexture == null) {
            baseTexture = new Texture(Gdx.files.internal("textures/UI/levelbutton.png"));
        }

        // Usa a textura base
        buttonTexture = baseTexture;

        // Cria o botao
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(buttonTexture);
        button = new ImageButton(style);

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

        // Cria label com número do nivel
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = unlocked ? Color.WHITE : Color.DARK_GRAY;

        String labelText = unlocked ? String.valueOf(levelId + 1) : "?";
        levelLabel = new Label(labelText, labelStyle);
        levelLabel.setFontScale(4f);

       // ajuste em relacao ao botao
        float offsetX = 0f;
        float offsetY = -10f;

        this.add(button);
        com.badlogic.gdx.scenes.scene2d.ui.Table labelContainer = new com.badlogic.gdx.scenes.scene2d.ui.Table();
        labelContainer.add(levelLabel).padLeft(offsetX).padBottom(offsetY);
        this.add(labelContainer);
    }

    /**
     * Atualiza o estado visual do botao
     */
    public void updateState(boolean unlocked, boolean completed) {
        this.unlocked = unlocked;
        this.completed = completed;

        // Atualiza a cor do botao baseado no estado
        if (!unlocked) {
            button.setColor(0.2f, 0.2f, 0.2f, 1f);
        } else if (completed) {
            button.setColor(0.4f, 1f, 0.5f, 1f);
        } else {
            button.setColor(1f, 1f, 1f, 1f);
        }

        // Atualiza a label
        levelLabel.setColor(unlocked ? Color.WHITE : Color.DARK_GRAY);
        levelLabel.setText(unlocked ? String.valueOf(levelId + 1) : "?");
    }

    public void dispose() {
    }
}
