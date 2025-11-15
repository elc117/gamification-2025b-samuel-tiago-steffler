package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta NOT - a saida é o inverso da entrada

public class NOTGate extends LogicGate {

    // construtor para porta NOT com 1 entrada
    public NOTGate(String label, float scale) {
        super(label, 1, scale); // maximo 1 entrada
        this.gateType = "NOT";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/NOT_off.png"));
        this.setTextureOn(new Texture("gates/NOT_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // Retorna o inverso da unica entrada
        this.output = !inputs[0];
        if (this.output) {
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
    }
}
