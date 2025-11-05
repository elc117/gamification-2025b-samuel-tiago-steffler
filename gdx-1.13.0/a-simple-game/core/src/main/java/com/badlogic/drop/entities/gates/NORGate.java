package com.badlogic.drop.entities.gates;

// porta NOR - a saida é verdadeira somente se todas as entradas forem falsas (NOT OR)
// pode ser construida com mais de duas entradas

public class NORGate extends LogicGate {

    // construtor para porta NOR padrao com 2 entradas
    public NORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "NOR";
    }

    // construtor para porta NOR com varias entradas
    public NORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "NOR";
    }

    @Override
    public void compute() {
        // retorna verdadeiro somente se todas as entradas forem falsas (NOT OR)
        for (boolean input : inputs) {
            if (input) {
                this.output = false;
                return;
            }
        }
        this.output = true;
    }
}
