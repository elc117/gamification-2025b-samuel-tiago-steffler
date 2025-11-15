package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta XOR - retorna verdadeiro se as entradas tiverem valores distintos (2 entradas)
// retorna verdadeiro se o numero de entradas verdadeiras for impar (varias entradas)

public class XORGate extends LogicGate {

    // construtor padrao para a porta XOR 2 entradas
    public XORGate(String label, float scale) {
        super(label, 2, scale); // default 2
        this.gateType = "XOR";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/XOR_off.png"));
        this.setTextureOn(new Texture("gates/XOR_on.png"));
        this.setTexture(this.textureOff);
    }

    // construtor para porta XOR com varias entradas
    public XORGate(String label, int numInputs, float scale) {
        super(label, numInputs, scale);
        this.gateType = "XOR";
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/XOR_off.png"));
        this.setTextureOn(new Texture("gates/XOR_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // conta o numero de portas verdadeiras, retorna verdadeiro se for impar
        int trueCount = 0;
        for (boolean input : inputs) {
            if (input) {
                trueCount++;
            }
        }
        this.output = trueCount % 2 == 1;
        if(this.output) {
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
    }
}
