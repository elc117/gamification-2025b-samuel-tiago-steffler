package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;

/**
 * Representa um nó de saída do circuito (terminal de leitura).
 * Herda de LogicGate para facilitar o gerenciamento de conexões.
 * Funciona como uma "porta" passthrough que apenas propaga seu input único.
 */
public class OutputBits extends LogicGate {
    
    private String label; // Ex: "Output", "Result", "Sum"
    
    /**
     * Construtor - cria um output com 1 entrada
     */
    public OutputBits(String label, float x, float y) {
        super(1, x, y); // 1 input (recebe sinal de outra porta)
        this.label = label;
        this.gateType = "OUTPUT";
    }
    
    /**
     * OutputBits não tem lógica - apenas propaga o input
     */
    @Override
    public void compute() {
        // Propaga o primeiro (e único) input para o output
        this.output = inputs.length > 0 ? inputs[0] : false;
    }
    
    /**
     * Obtém o valor atual da saída
     */
    public boolean getValue() {
        return getOutput();
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
