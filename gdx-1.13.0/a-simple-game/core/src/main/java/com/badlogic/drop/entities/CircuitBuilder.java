package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.ANDGate;
import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.NANDGate;
import com.badlogic.drop.entities.gates.NORGate;
import com.badlogic.drop.entities.gates.NOTGate;
import com.badlogic.drop.entities.gates.ORGate;
import com.badlogic.drop.entities.gates.XNORGate;
import com.badlogic.drop.entities.gates.XORGate;
import com.badlogic.gdx.utils.Array;

/**
 * Builder pattern para facilitar a criação de circuitos complexos.
 * Permite construir circuitos passo a passo.
 */
public class CircuitBuilder {

    private final Array<InputBits> inputs;
    private final Array<LogicGate> gates;
    private final Array<Wire> wires;
    private final Array<OutputBits> outputs;
    private boolean[] expectedOutput;

    // construtor default
    public CircuitBuilder() {
        this.inputs = new Array<>();
        this.gates = new Array<>();
        this.wires = new Array<>();
        this.outputs = new Array<>();
    }

    /**
     * Adiciona um input ao circuito
     */
    public CircuitBuilder addInput(String label, float x, float y) {
        inputs.add(new InputBits(label, x, y));
        return this;
    }

    /**
     * Adiciona um input com valor inicial
     */
    public CircuitBuilder addInput(String label, boolean initialValue, float x, float y) {
        inputs.add(new InputBits(label, initialValue, x, y));
        return this;
    }

    /**
     * Adiciona uma porta AND
     */
    public CircuitBuilder addAND(float x, float y) {
        gates.add(new ANDGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta OR
     */
    public CircuitBuilder addOR(float x, float y) {
        gates.add(new ORGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta NOT
     */
    public CircuitBuilder addNOT(float x, float y) {
        gates.add(new NOTGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta XOR
     */
    public CircuitBuilder addXOR(float x, float y) {
        gates.add(new XORGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta NAND
     */
    public CircuitBuilder addNAND(float x, float y) {
        gates.add(new NANDGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta NOR
     */
    public CircuitBuilder addNOR(float x, float y) {
        gates.add(new NORGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta XNOR
     */
    public CircuitBuilder addXNOR(float x, float y) {
        gates.add(new XNORGate(x, y));
        return this;
    }

    /**
     * Adiciona uma porta genérica
     */
    public CircuitBuilder addGate(LogicGate gate) {
        gates.add(gate);
        return this;
    }

    public CircuitBuilder addWire(Wire wire) {
        wires.add(wire);
        return this;
    }

    /**
     * Conecta duas portas com um fio
     * @param fromIndex Índice da porta de origem
     * @param toIndex Índice da porta de destino
     */
    public CircuitBuilder connect(int fromIndex, int toIndex) {
        if (fromIndex >= gates.size || toIndex >= gates.size) {
            throw new IllegalArgumentException("Indice fora dos limites.");
        }
        wires.add(new Wire(gates.get(fromIndex), gates.get(toIndex)));
        return this;
    }

    /**
     * Conecta duas portas especificando os índices de entrada/saída
     * @param fromGateIndex Índice da porta de origem
     * @param fromOutputIndex Índice da saída da porta de origem
     * @param toGateIndex Índice da porta de destino
     * @param toInputIndex Índice da entrada da porta de destino
     */
    public CircuitBuilder connect(int fromGateIndex, int fromOutputIndex, int toGateIndex, int toInputIndex) {
        if (fromGateIndex >= gates.size || toGateIndex >= gates.size) {
            throw new IllegalArgumentException("GIndice fora dos limites.");
        }
        wires.add(new Wire(gates.get(fromGateIndex), fromOutputIndex,
                          gates.get(toGateIndex), toInputIndex));
        return this;
    }

    /**
     * Conecta um input a uma porta
     * @param inputIndex Índice do input
     * @param gateIndex Índice da porta de destino
     * @param gateInputIndex Índice da entrada da porta de destino
     */
    public CircuitBuilder connectInput(String label, int gateIndex, int gateInputIndex) {

        if (gateIndex >= gates.size) {
            throw new IllegalArgumentException("Gate index fora dos limites.");
        }
        wires.add(new Wire(this.getInput(label), 0, gates.get(gateIndex), gateInputIndex));
        return this;
    }

    /**
     * Adiciona um output ao circuito
     * @param label Rótulo do output
     * @param gateIndex Índice da porta de onde o output é retirado
     */
    public CircuitBuilder addOutput(String label, int gateIndex, float x, float y) {
        if (gateIndex >= gates.size) {
            throw new IllegalArgumentException("Indice fora dos limites.");
        }

        // Criação de uma saída
        OutputBits output = new OutputBits(label, x, y);
        outputs.add(output);

        // Criação de um fio conectando a porta à saída
        Wire wire = new Wire(gates.get(gateIndex), 0, output, 0);
        wires.add(wire);

        return this;
    }

    /**
     * Define os valores esperados para as saídas
     * @param values Valores esperados (pode ser um valor para cada output ou um array boolean[])
     */
    public CircuitBuilder expectOutputs(boolean... values) {
        this.expectedOutput = values;
        return this;
    }

    /**
     * Constrói o circuito final com base nos componentes adicionados.
     * Esse método precisa ser chamado após todas as adições/conexões.
     * Para conectar inputs a portas, use o método connectInput().
     * Portas a portas usam connect(), e conexões de portas a saídas são criadas automaticamente em addOutput().
     */
    public Circuit build() {
        if (inputs.size == 0) {
            throw new IllegalStateException("Circuito precisa ter uma entrada no minimo.");
        }
        if (outputs.size == 0) {
            throw new IllegalStateException("Circuito precisa ter pelo menos uma saida.");
        }
        if (expectedOutput == null) {
            // Default: todos false
            expectedOutput = new boolean[outputs.size];
        }

        Circuit circuit = new Circuit(
            inputs.toArray(InputBits.class),
            gates.toArray(LogicGate.class),
            wires.toArray(Wire.class),
            outputs.toArray(OutputBits.class),
            expectedOutput
        );

        return circuit;
    }

    /**
     * Obtém a última porta adicionada (útil para encadear conexões)
     */
    public int getLastGateIndex() {
        return gates.size - 1;
    }

    /**
     * Obtém o número de portas atualmente no circuito
     */
    public int getGateCount() {
        return gates.size;
    }

    /**
     * Obtém o número de inputs
     */
    public int getInputCount() {
        return inputs.size;
    }

    // Métodos de acesso para configuração manual

    public Array<InputBits> getInputs() {
        return inputs;
    }

    public Array<LogicGate> getGates() {
        return gates;
    }

    public InputBits getInput(String label) {
        for (InputBits input : inputs) {
            if (input == null) continue;
            // Assumes InputBits has a getLabel() method
            if (label == null) {
                if (input.getLabel() == null) return input;
            } else if (label.equals(input.getLabel())) {
                return input;
            }
        }
        throw new IllegalArgumentException("Entrada não encontrada: " + label);
    }

    public LogicGate getGate(int index) {
        return gates.get(index);
    }

    public OutputBits getOutput(String label) {
        for(OutputBits output : outputs) {
            if(output == null) continue;
            if(label == null) {
                if(output.getLabel() == null) return output;
            } else if(label.equals(output.getLabel())) {
                return output;
            }
        }
        throw new IllegalArgumentException("Output não encontrado: " + label);
    }
}
