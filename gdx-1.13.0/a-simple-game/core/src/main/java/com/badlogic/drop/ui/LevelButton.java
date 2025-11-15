package com.badlogic.drop.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Botão/Card visual para representar um nível na tela de seleção
 */
public class LevelButton extends Stack {
    private int levelId;
    private boolean unlocked;
    private boolean completed;
    private ImageButton button;
    private Label levelLabel;
    private Texture buttonTexture;
    
    public LevelButton(int levelId, boolean unlocked, boolean completed, BitmapFont font) {
        this.levelId = levelId;
        this.unlocked = unlocked;
        this.completed = completed;
        
        // Cria textura do botão baseado no estado
        buttonTexture = createButtonTexture();
        
        // Cria o botão
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(buttonTexture);
        button = new ImageButton(style);
        
        // Cria label com número do nível
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = unlocked ? Color.WHITE : Color.DARK_GRAY;
        
        String labelText = unlocked ? String.valueOf(levelId + 1) : "?";
        levelLabel = new Label(labelText, labelStyle);
        levelLabel.setFontScale(2f);
        
        // Adiciona ao Stack (botão atrás, label na frente)
        this.add(button);
        this.add(levelLabel);
    }
    
    /**
     * Cria a textura do botão baseado no estado (desbloqueado/bloqueado/completo)
     */
    private Texture createButtonTexture() {
        int size = 100;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        
        if (!unlocked) {
            // Nível bloqueado - preto
            pixmap.setColor(0.1f, 0.1f, 0.1f, 1f);
        } else if (completed) {
            // Nível completado - verde escuro
            pixmap.setColor(0.2f, 0.6f, 0.3f, 1f);
        } else {
            // Nível desbloqueado mas não completado - azul
            pixmap.setColor(0.2f, 0.4f, 0.8f, 1f);
        }
        
        pixmap.fill();
        
        // Borda branca
        pixmap.setColor(Color.WHITE);
        pixmap.drawRectangle(0, 0, size, size);
        pixmap.drawRectangle(1, 1, size-2, size-2);
        
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
    
    public int getLevelId() {
        return levelId;
    }
    
    public boolean isUnlocked() {
        return unlocked;
    }
    
    public ImageButton getButton() {
        return button;
    }
    
    /**
     * Atualiza o estado visual do botão
     */
    public void updateState(boolean unlocked, boolean completed) {
        this.unlocked = unlocked;
        this.completed = completed;
        
        // Recria a textura com o novo estado
        if (buttonTexture != null) {
            buttonTexture.dispose();
        }
        buttonTexture = createButtonTexture();
        button.getStyle().imageUp = new TextureRegionDrawable(buttonTexture);
        
        // Atualiza a label
        levelLabel.setColor(unlocked ? Color.WHITE : Color.DARK_GRAY);
        levelLabel.setText(unlocked ? String.valueOf(levelId + 1) : "?");
    }
    
    public void dispose() {
        if (buttonTexture != null) {
            buttonTexture.dispose();
        }
    }
}
