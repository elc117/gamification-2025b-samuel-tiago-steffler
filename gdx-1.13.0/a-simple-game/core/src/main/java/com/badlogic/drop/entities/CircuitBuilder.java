package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.ANDGate;
import com.badlogic.drop.entities.gates.InputBits;
import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.NANDGate;
import com.badlogic.drop.entities.gates.NORGate;
import com.badlogic.drop.entities.gates.NOTGate;
import com.badlogic.drop.entities.gates.ORGate;
import com.badlogic.drop.entities.gates.OutputBits;
import com.badlogic.drop.entities.gates.XNORGate;
import com.badlogic.drop.entities.gates.XORGate;
import com.badlogic.gdx.Gdx;
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
    private Array<Boolean> expectedOutput;
    private final float scale = 0.6f;
    // construtor default
    public CircuitBuilder() {
        this.inputs = new Array<>();
        this.gates = new Array<>();
        this.wires = new Array<>();
        this.outputs = new Array<>();
        this.expectedOutput = new Array<>();
    }

    /**
     * Adiciona um input ao circuito
     */
    public CircuitBuilder addInput(String label) {
        inputs.add(new InputBits(label, scale));
        return this;
    }

    /**
     * Adiciona um input com valor inicial
     */
    public CircuitBuilder addInput(String label, boolean initialValue) {
        inputs.add(new InputBits(label, initialValue, scale));
        return this;
    }

    /**
     * Adiciona um output ao circuito
     */
    public CircuitBuilder addOutput(String label) {
        outputs.add(new OutputBits(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta AND
     */
    public CircuitBuilder addAND(String label) {
        gates.add(new ANDGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta OR
     */
    public CircuitBuilder addOR(String label) {
        gates.add(new ORGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta NOT
     */
    public CircuitBuilder addNOT(String label) {
        gates.add(new NOTGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta XOR
     */
    public CircuitBuilder addXOR(String label) {
        gates.add(new XORGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta NAND
     */
    public CircuitBuilder addNAND(String label) {
        gates.add(new NANDGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta NOR
     */
    public CircuitBuilder addNOR(String label) {
        gates.add(new NORGate(label, scale));
        return this;
    }

    /**
     * Adiciona uma porta XNOR
     */
    public CircuitBuilder addXNOR(String label) {
        gates.add(new XNORGate(label, scale));
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
    public CircuitBuilder connectInput(String from, String to, int gateInputIndex) {
        if (getGate(to) == null) {
            throw new IllegalArgumentException("Gate nao encontrado.");
        }

        // Att. agora n precisa mais criar inputs, eles sao criados automaticamente com outputs se nao existem
        InputBits input = getInput(from);
        if (input == null) {
            input = new InputBits(from, scale);
            inputs.add(input);
        }

        wires.add(new Wire(input, 0, getGate(to), gateInputIndex));
        return this;
    }

    /**
     * Conecta dois componentes (InputBits ou LogicGate) pelo label
     * Detecta automaticamente se "from" é um InputBits ou LogicGate
     * @param from Label do componente de origem (InputBits ou LogicGate)
     * @param to Label da porta de destino (LogicGate)
     * @param toInputIndex Índice da entrada da porta de destino
     */
    public CircuitBuilder connect(String from, String to, int toInputIndex) {
        return connect(from, 0, to, toInputIndex);
    }

    /**
     * Conecta dois componentes (InputBits ou LogicGate) pelo label com índices customizados
     * Detecta automaticamente se "from" é um InputBits ou LogicGate
     * @param from Label do componente de origem (InputBits ou LogicGate)
     * @param fromOutputIndex Índice da saída do componente de origem
     * @param to Label da porta de destino (LogicGate)
     * @param toInputIndex Índice da entrada da porta de destino
     */
    public CircuitBuilder connect(String from, int fromOutputIndex, String to, int toInputIndex) {
        LogicGate toGate = getGate(to);
        if (toGate == null) {
            throw new IllegalArgumentException("Gate de destino nao encontrado: " + to);
        }

        // Primeiro tenta encontrar como LogicGate
        LogicGate fromGate = getGate(from);
        if (fromGate != null) {
            wires.add(new Wire(fromGate, fromOutputIndex, toGate, toInputIndex));
            return this;
        }

        // Se não encontrou como gate, tenta como InputBits
        InputBits fromInput = getInput(from);
        if (fromInput != null) {
            wires.add(new Wire(fromInput, fromOutputIndex, toGate, toInputIndex));
            return this;
        }

        // Se não encontrou nem como gate nem como input, cria um input automaticamente
        fromInput = new InputBits(from, scale);
        inputs.add(fromInput);
        wires.add(new Wire(fromInput, fromOutputIndex, toGate, toInputIndex));
        return this;
    }

    /**
     * Adiciona um output ao circuito
     * @param label Rótulo do output
     * @param gateIndex Índice da porta de onde o output é retirado
     */
    public CircuitBuilder addOutput(String label, String gateLabel) {
        if (getGate(gateLabel) == null) {
            throw new IllegalArgumentException("Gate nao encontrado.");
        }

        // Criação de uma saída se nao existir
        // pode acontecer de mais umm gat ligar no mesmo output
        // (estado sera atualizado pelo ultimo gate modificado ou ultimo wire processado)
        OutputBits output = getOutput(label);
        if (output == null) {
            output = new OutputBits(label, scale);
            outputs.add(output);
        }

        // Criação de um fio conectando a porta à saída
        Wire wire = new Wire(getGate(gateLabel), 0, output, 0);
        wires.add(wire);
        return this;
    }

    /**
     * Constrói o circuito final com base nos componentes adicionados.
     * Esse método precisa ser chamado após todas as adições/conexões.
     * Para conectar inputs a portas, use o método connectInput().
     * Portas a portas usam connect(), e conexões de portas a saídas são criadas automaticamente em addOutput().
     * Incluir true como argumento para emitir mensagens de debug (posicionamento)
     */
    public Circuit build() {
        return build(false);
    }

    public Circuit build(boolean debug) {
        if (inputs.size == 0) {
            throw new IllegalStateException("Circuito precisa ter uma entrada no minimo.");
        }
        if (outputs.size == 0) {
            throw new IllegalStateException("Circuito precisa ter pelo menos uma saida.");
        }
        if (expectedOutput == null || expectedOutput.size == 0) {
            // Default: todos false
            expectedOutput = new Array<>(outputs.size);
            for (int i = 0; i < outputs.size; i++) {
                expectedOutput.add(false);
            }
            if (debug) Gdx.app.log("CircuitBuilder.build", "Criando array de resultados com tamanho " + outputs.size);
        }

        Circuit circuit = new Circuit(inputs, gates, wires, outputs, expectedOutput, debug);

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
        //throw new IllegalArgumentException("Entrada não encontrada: " + label);
        return null;
    }

    public LogicGate getGate(String label) {
        for (LogicGate gate : gates) {
            if (gate == null) continue;
            if (label == null) {
                if (gate.getLabel() == null) return gate;
            } else if (label.equals(gate.getLabel())) {
                return gate;
            }
        }
        //throw new IllegalArgumentException("Gate não encontrado: " + label);
        return null;
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
        //throw new IllegalArgumentException("Output não encontrado: " + label);
        return null;
    }

    public Array<Wire> getWires() {
        return this.wires;
    }
}
