package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta XNOR - saida verdadeira se as entradas forem iguais (2 entradas)
// saida verdadeira se o numero de entradas verdadeiras for par (varias entradas)

public class XNORGate extends LogicGate {

    // construtor padrao para a porta XNOR 2 entradas
    public XNORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "XNOR";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/XNOR_off.png"));
    }

    // construtor para porta XNOR com varias entradas
    public XNORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "XNOR";
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/XNOR_off.png"));
    }

    @Override
    public void compute() {
        // Conta o numero de entradas verdadeiras, retorna verdadeiro se for par
        int trueCount = 0;
        for (boolean input : inputs) {
            if (input) {
                trueCount++;
            }
        }
        this.output = trueCount % 2 == 0;
        if(this.output) {
            this.setTexture(new Texture("gates/XNOR_on.png"));
        } else {
            this.setTexture(new Texture("gates/XNOR_off.png"));
        }
    }
}
