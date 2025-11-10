package com.badlogic.drop.entities.gates;

//porta OR - retorna verdadeiro quando qualquer uma das entradas for verdadeira
// pode ser construida com mais de duas entradas

public class ORGate extends LogicGate {

    // construtor padrão da porta OR
    public ORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "OR";
        // Tamanho do icone PNG 
        this.width = 131f;
        this.height = 154f;
    }

    // construtor para porta OR com varias entradas
    public ORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "OR";
        this.width = 131f;
        this.height = 154f;
    }

    @Override
    public void compute() {
        // retorna verdadeiro se pelo menos uma entrada for verdadeira
        for (boolean input : inputs) {
            if (input) {
                this.output = true;
                return;
            }
        }
        this.output = false;
    }
}
