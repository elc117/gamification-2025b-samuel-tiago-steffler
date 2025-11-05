package com.badlogic.drop.entities;

// Representa o circuito completo na tela

import com.badlogic.drop.entities.gates.LogicGate;

// armazena expressão lógica, portas, conexões, etc.
public class Circuit {

    // Lista de portas lógicas no circuito
    LogicGate[] gates;
    boolean[] expectedOutput;
    boolean[] actualOutput;
    boolean[] inputs;

    // Lista de ligações entre portas
    Wire[] wires;

    // construtor do circuito
    public Circuit(LogicGate[] gates, Wire[] wires, boolean[] expectedOutput, boolean[] inputs) {
        this.gates = gates;
        this.wires = wires;
        this.expectedOutput = expectedOutput;
        this.inputs = inputs;
        this.actualOutput = new boolean[inputs.length];
    }

}
