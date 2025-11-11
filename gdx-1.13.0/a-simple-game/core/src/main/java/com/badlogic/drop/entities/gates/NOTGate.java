package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta NOT - a saida é o inverso da entrada

public class NOTGate extends LogicGate {

    // construtor para porta NOT com 1 entrada
    public NOTGate(float x, float y) {
        super(1, x, y); // maximo 1 entrada
        this.gateType = "NOT";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/NOT_off.png"));
    }

    @Override
    public void compute() {
        // Retorna o inverso da unica entrada
        this.output = !inputs[0];
        if (this.output) {
            this.setTexture(new Texture("gates/NOT_on.png"));
        } else {
            this.setTexture(new Texture("gates/NOT_off.png"));
        }
    }
}
