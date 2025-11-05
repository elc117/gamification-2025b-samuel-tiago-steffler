package com.badlogic.drop.entities.gates;


// porta NOT - a saida é o inverso da entrada

public class NOTGate extends LogicGate {

    // construtor para porta NOT com 1 entrada
    public NOTGate(float x, float y) {
        super(1, x, y); // maximo 1 entrada
        this.gateType = "NOT";
    }

    @Override
    public void compute() {
        // Retorna o inverso da unica entrada
        this.output = !inputs[0];
    }
}
