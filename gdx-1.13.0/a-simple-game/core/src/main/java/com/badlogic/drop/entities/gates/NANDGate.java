package com.badlogic.drop.entities.gates;

// porta NAND - a saida é falsa somente se todas as entradas forem verdadeiras, e verdadeiro caso contrário (NOT AND)
// pode ser construida com mais de duas entradas

public class NANDGate extends LogicGate {
    
    // construtor para porta NAND padrao com 2 entradas
    public NANDGate(float x, float y) {
        super(2, x, y); // default 2 portas
        this.gateType = "NAND";
    }
    
    // construtor para porta NAND com varias portas
    public NANDGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "NAND";
    }
    
    @Override
    public boolean compute() {
        // retorna falso somente se todas as entradas forem verdadeiras (NOT AND)
        for (boolean input : inputs) {
            if (!input) {
                return true;
            }
        }
        return false;
    }
}
