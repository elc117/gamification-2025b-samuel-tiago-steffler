package com.badlogic.drop.entities.gates;

//porta OR - retorna verdadeiro quando qualquer uma das entradas for verdadeira
// pode ser construida com mais de duas entradas

public class ORGate extends LogicGate {
    
    // construtor padrão da porta OR
    public ORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "OR";
    }

    // construtor para porta OR com varias entradas
    public ORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "OR";
    }
    
    @Override
    public boolean compute() {
        // retorna verdadeiro se pelo menos uma entrada for verdadeira
        for (boolean input : inputs) {
            if (input) {
                return true;
            }
        }
        return false;
    }
}
