package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;

/**
 * Representa um nó de entrada do circuito (fonte de sinal).
 * Herda de LogicGate para facilitar o gerenciamento de conexões.
 * Funciona como uma "porta" sem entradas que apenas emite um valor controlado pelo usuário.
 */
public class InputBits extends LogicGate {
    
    private String label; // Ex: "A", "B", "Input 1"
    private boolean value; // Valor atual do input
    
    /**
     * Construtor padrão - cria um input com valor falso
     */
    public InputBits(String label, float x, float y) {
        super(0, x, y); // 0 inputs (fonte de sinal)
        this.label = label;
        this.value = false;
        this.gateType = "INPUT";
    }

    /**
     * Construtor de input com valor predefinido
     */
    public InputBits(String label, boolean initialValue, float x, float y) {
        super(0, x, y); // 0 inputs
        this.label = label;
        this.value = initialValue;
        this.gateType = "INPUT";
    }
    
    /**
     * InputBits não tem lógica - apenas retorna o valor definido pelo usuário
     */
    @Override
    public void compute() {
        this.output = value;
    }
    
    /**
     * Define o valor do input
     */
    public void setValue(boolean value) {
        this.value = value;
        this.output = value; // Atualiza output também
    }
    
    /**
     * Obtém o valor atual
     */
    public boolean getValue() {
        return value;
    }
    
    /**
     * Toggle do valor de entrada por meio de interação do usuário
     */
    public void toggle() {
        setValue(!value);
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
