package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta XOR - retorna verdadeiro se as entradas tiverem valores distintos (2 entradas)
// retorna verdadeiro se o numero de entradas verdadeiras for impar (varias entradas)

public class XORGate extends LogicGate {

    // construtor padrao para a porta XOR 2 entradas
    public XORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "XOR";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/XOR_off.png"));
    }

    // construtor para porta XOR com varias entradas
    public XORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "XOR";
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/XOR_off.png"));
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
            this.setTexture(new Texture("gates/XOR_on.png"));
        } else {
            this.setTexture(new Texture("gates/XOR_off.png"));
        }
    }
}
