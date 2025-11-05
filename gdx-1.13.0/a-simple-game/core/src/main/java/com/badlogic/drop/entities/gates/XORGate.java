package com.badlogic.drop.entities.gates;

// porta XOR - retorna verdadeiro se as entradas tiverem valores distintos (2 entradas)
// retorna verdadeiro se o numero de entradas verdadeiras for impar (varias entradas)

public class XORGate extends LogicGate {

    // construtor padrao para a porta XOR 2 entradas
    public XORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "XOR";
    }

    // construtor para porta XOR com varias entradas
    public XORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "XOR";
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
    }
}
