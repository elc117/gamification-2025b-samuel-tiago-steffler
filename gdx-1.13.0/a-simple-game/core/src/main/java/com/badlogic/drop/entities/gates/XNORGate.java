package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta XNOR - saida verdadeira se as entradas forem iguais (2 entradas)
// saida verdadeira se o numero de entradas verdadeiras for par (varias entradas)

public class XNORGate extends LogicGate {

    // construtor padrao para a porta XNOR 2 entradas
    public XNORGate(String label) {
        super(label, 2); // default 2
        this.gateType = "XNOR";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/XNOR_off.png"));
        this.setTextureOn(new Texture("gates/XNOR_on.png"));
        this.setTexture(this.textureOff);
    }

    // construtor para porta XNOR com varias entradas
    public XNORGate(String label, int numInputs) {
        super(label, numInputs);
        this.gateType = "XNOR";
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/XNOR_off.png"));
        this.setTextureOn(new Texture("gates/XNOR_on.png"));
        this.setTexture(this.textureOff);
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
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
    }
}
