package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

// Representa o circuito completo - avaliacao baseada em ordenacao topologica
public class Circuit {

    private LogicGate[] allGates; // nos do circuito (inputs, gates, outputs)
    private InputBits[] inputs;
    private OutputBits[] outputs;

    private Wire[] wires;

    private boolean[] expectedOutput;

    // ordem de avaliacao (resultado da ordenacao topologica)
    private Array<LogicGate> evaluationOrder;

    private ObjectMap<LogicGate, Array<LogicGate>> dependencies; // dependencias


    // inputs - nos de entrada (herdam de LogicGate)
    // gates - portas logicas intermediarias
    // wires - conexoes entre portas
    // outputs - nos de saida (herdam de LogicGate)
    // expectedOutput - valores esperados para as saidas
    public Circuit(InputBits[] inputs, LogicGate[] gates, Wire[] wires,
                   OutputBits[] outputs, boolean[] expectedOutput) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.wires = wires;
        this.expectedOutput = expectedOutput;

        // Combina tudo em um unico array de LogicGates
        this.allGates = new LogicGate[inputs.length + gates.length + outputs.length];
        int index = 0;
        for (InputBits input : inputs) {
            allGates[index++] = input;
        }
        for (LogicGate gate : gates) {
            allGates[index++] = gate;
        }
        for (OutputBits output : outputs) {
            allGates[index++] = output;
        }

        this.dependencies = new ObjectMap<>();
        this.evaluationOrder = new Array<>();

        buildDependencyGraph();
        computeEvaluationOrder();
    }

    // controi grafo de dependencias baseado nos fios
    private void buildDependencyGraph() {
        // Inicializa listas vazias para todas as portas
        for (LogicGate gate : allGates) {
            dependencies.put(gate, new Array<>());
        }

        // Para cada fio, adiciona dependência
        for (Wire wire : wires) {
            LogicGate from = wire.getFromGate();
            LogicGate to = wire.getToGate();

            // 'to' depende de 'from'
            if (!dependencies.get(to).contains(from, true)) {
                dependencies.get(to).add(from);
            }
        }
    }


    // Calcula a ordem de avaliacao usando ordenacao topologica - algoritmo de Kahn's
    private void computeEvaluationOrder() {
        evaluationOrder.clear();

        // Calcula grau de entrada para cada porta (quantas dependências)
        ObjectMap<LogicGate, Integer> inDegree = new ObjectMap<>();
        for (LogicGate gate : allGates) {
            inDegree.put(gate, dependencies.get(gate).size);
        }

        // Fila com portas sem dependências (grau de entrada = 0)
        // InputBits naturalmente terao grau 0
        Array<LogicGate> queue = new Array<>();
        for (LogicGate gate : allGates) {
            if (inDegree.get(gate) == 0) {
                queue.add(gate);
            }
        }

        // Processa portas em ordem topologica
        while (queue.size > 0) {
            LogicGate current = queue.removeIndex(0);
            evaluationOrder.add(current);

            // Para cada porta que depende da atual
            for (Wire wire : wires) {
                if (wire.getFromGate() == current) {
                    LogicGate dependent = wire.getToGate();
                    int newDegree = inDegree.get(dependent) - 1;
                    inDegree.put(dependent, newDegree);

                    if (newDegree == 0) {
                        queue.add(dependent);
                    }
                }
            }
        }

        // Verifica se ha ciclos
        if (evaluationOrder.size != allGates.length) {
            throw new IllegalStateException("Circuito contem lacos (estilo latches)! Reevaluar expressao.");
        }
    }


   // Define os valores das entradas do circuito

    public void setInputValues(boolean[] inputValues) {
        if (inputValues.length != inputs.length) {
            throw new IllegalArgumentException("Contagem de valores de entrada nao corresponde ao numero de entradas.");
        }

        for (int i = 0; i < inputs.length; i++) {
            inputs[i].setValue(inputValues[i]);
        }
    }

    // Avalia todo o circuito na ordem correta
    // Execucao: itera pela ordem topologica, atualiza inputs e chama update()
    public void evaluate() {
        // Avalia todas as portas na ordem topologica
        for (LogicGate gate : evaluationOrder) {
            // atualiza os inputs desta porta baseado nos fios
            for (Wire wire : wires) {
                if (wire.getToGate() == gate) {
                    LogicGate from = wire.getFromGate();
                    int inputIndex = wire.getToInputIndex();
                    gate.setInput(inputIndex, from.getOutput());
                }
            }

            // Agora calcula o output desta porta
            gate.update();
        }

        // Atualiza estado dos fios para renderizacao
        for (Wire wire : wires) {
            wire.updateState();
        }
    }


    // verifica se o circuito esta correto (outputs == expectedOutputs)

    public boolean isCorrect() {
        if (outputs.length != expectedOutput.length) {
            return false;
        }

        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i].getValue() != expectedOutput[i]) {
                return false;
            }
        }
        return true;
    }


    // obtem os valores atuais das saidas
    public boolean[] getActualOutputs() {
        boolean[] result = new boolean[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            result[i] = outputs[i].getValue();
        }
        return result;
    }

    // Getters
    public InputBits[] getInputs() {
        return inputs;
    }

    public LogicGate[] getAllGates() {
        return allGates;
    }

    public Wire[] getWires() {
        return wires;
    }

    public OutputBits[] getOutputs() {
        return outputs;
    }

    public boolean[] getExpectedOutput() {
        return expectedOutput;
    }

    public Array<LogicGate> getEvaluationOrder() {
        return evaluationOrder;
    }

    // Imprime a ordem de avaliacao
    public void printEvaluationOrder() {
        System.out.println("Ordem de avaliacao do circuito:");
        for (int i = 0; i < evaluationOrder.size; i++) {
            LogicGate gate = evaluationOrder.get(i);
            System.out.println((i + 1) + ". " + gate.getGateType() + " gate");
        }
    }
}
