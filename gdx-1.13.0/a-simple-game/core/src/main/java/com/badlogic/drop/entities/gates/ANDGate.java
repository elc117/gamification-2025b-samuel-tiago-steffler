package com.badlogic.drop.entities.gates;

// porta lógica AND  - A saída é verdadeira somente se todas as entradas forem verdadeiras
// suporta mais do que duas entradas

public class ANDGate extends LogicGate {

    // constroi a porta com 2 entradas por default
    public ANDGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "AND";
    }

    // constroi a porta com número customizado de entradas
    public ANDGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "AND";
    }

    @Override
    public void compute() {
        // retorna verdadeiro se nenhuma entrada for falsa
        for (boolean input : inputs) {
            if (!input) {
                this.output = false;
                return;
            }
        }
        this.output = true;
    }
}
