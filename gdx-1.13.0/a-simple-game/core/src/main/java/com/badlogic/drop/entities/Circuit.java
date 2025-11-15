package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.InputBits;
import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.OutputBits;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

// Representa o circuito completo - avaliacao baseada em ordenacao topologica
public class Circuit {

    private final Array<LogicGate> allGates; // nos do circuito (inputs, gates, outputs)
    private final Array<InputBits> inputs;
    private final Array<OutputBits> outputs;

    private final Array<Wire> wires;

    // Saídas esperadas
    private Array<Boolean> expectedOutput;

    private boolean debugMode = false;

    // Ordem de avaliacao e atualzacao das portas logicas
    private final Array<LogicGate> evaluationOrder;

    // par de porta e array de portas das quais depende (geralmente 1 ou 2)
    private final ObjectMap<LogicGate, Array<LogicGate>> dependencies;

    // organizacao espacial dos gates
    private final Array<Array<LogicGate>> levels; // levels[i] = array dos gates no nivel i
    private int maxLevel;

    /**
     * @param inputs Nós de entrada (herdam de LogicGate)
     * @param gates Portas lógicas intermediárias
     * @param wires Conexões entre portas
     * @param outputs Nós de saída (herdam de LogicGate)
     * @param expectedOutput Valores esperados para as saídas
     */
    public Circuit(Array<InputBits> inputs, Array<LogicGate> gates, Array<Wire> wires,
                   Array<OutputBits> outputs, Array<Boolean> expectedOutput, boolean debugMode) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.wires = wires;
        this.expectedOutput = expectedOutput;
        this.debugMode = debugMode;
        // Combina tudo em um unico array de LogicGates
        this.allGates = new Array<>(inputs.size + gates.size + outputs.size);
        for (InputBits input : inputs) {
            allGates.add(input);
        }
        for (LogicGate gate : gates) {
            allGates.add(gate);
        }
        for (OutputBits output : outputs) {
            allGates.add(output);
        }

        this.dependencies = new ObjectMap<>();
        this.evaluationOrder = new Array<>();
        this.levels = new Array<>();

        buildDependencyGraph();
        computeEvaluationOrder();
        computeLevels();
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
        if (evaluationOrder.size != allGates.size) {
            throw new IllegalStateException("Circuito contém laços (estilo latches)! Reevaluar expressão.");
        }
    }

    /**
     * Função para calcular o nível de cada logic gate a partir das entradas com BFS.
     * Inputs ficam no nível 0, outputs no nível mais alto.
     */
    private void computeLevels() {
        levels.clear();

        // par de gate e nível
        ObjectMap<LogicGate, Integer> gateLevels = new ObjectMap<>();

        // Inputs começam no nível 0
        for (InputBits input : inputs) {
            gateLevels.put(input, 0);
        }

        // Processa gates na ordem de avaliação
        for (LogicGate gate : evaluationOrder) {
            if (gateLevels.containsKey(gate)) {
                continue; // Já processado (é um input)
            }

            // Encontra o nível máximo dos predecessores
            int maxLevelAnt = -1;
            for (Wire wire : wires) {
                if (wire.getToGate() == gate) {
                    LogicGate ant = wire.getFromGate();
                    if (gateLevels.containsKey(ant)) {
                        maxLevelAnt = Math.max(maxLevelAnt,
                                                       gateLevels.get(ant));
                    }
                }
            }
            // insere par com nivel maxLevelAnt + 1
            gateLevels.put(gate, maxLevelAnt + 1);
        }

        // Encontra o max nivel
        maxLevel = 0;
        for (Integer level : gateLevels.values()) {
            maxLevel = Math.max(maxLevel, level);
        }

        // Organiza gates por nivel
        for (int i = 0; i <= maxLevel; i++) {
            levels.add(new Array<>());
        }

        for (LogicGate gate : allGates) {
            int level = gateLevels.get(gate);
            levels.get(level).add(gate);

            // Define nivel e indice do gate
            gate.setLevel(level);
            gate.setLevelIdx(levels.get(level).size - 1);
        }
    }

    /**
     * Atualiza as posições de todos os gates baseado no tamanho da tela.
     * Recomendado ser chamado ao redimensionar a tela.
     *
     * @param screenWidth Largura da tela em pixels
     * @param screenHeight Altura da tela em pixels
     */
    public void updateAllPos(float screenWidth, float screenHeight) {
        int totalLevels = maxLevel + 1;

        // Atualiza cada gate individualmente, nivel a nivel
        for (int levelIndex = 0; levelIndex <= maxLevel; levelIndex++) {
            Array<LogicGate> gatesInLevel = levels.get(levelIndex);
            int gatesCount = gatesInLevel.size;

            if (debugMode) {
                System.out.println("Posicoes atualizadas para tela " + screenWidth + "x" + screenHeight);
            }
            for (LogicGate gate : gatesInLevel) {
                /*if (gatesInLevel.size == 1 && levelIndex < maxLevel - 2){
                    // significa que tem um gate nesse nivel e outro acima, deslocar um pouco para os lados e centralizar
                    if (gate.getLevelIdx() < gatesInLevel.size / 2) {
                        gate.updatePos(screenWidth - gate.getWidth() / 2, screenHeight, totalLevels, gatesCount, debugMode);
                    } else {
                        gate.updatePos(screenWidth + gate.getWidth() / 2, screenHeight, totalLevels, gatesCount, debugMode);
                    }

                } else {*/
                    gate.updatePos(screenWidth, screenHeight, totalLevels, gatesCount, debugMode);
                //}
            }
        }
        // Atualiza os caminhos dos fios
        for (Wire wire : wires) {
            wire.updateConnectionPoints();
        }
    }

    /**
     * Define os valores das entradas do circuito
     */
    public void setInputValues(boolean[] inputValues) {
        if (inputValues.length != inputs.size) {
            throw new IllegalArgumentException("Contagem de valores de entrada nao corresponde ao numero de entradas.");
        }

        for (int i = 0; i < inputs.size; i++) {
            inputs.get(i).setValue(inputValues[i]);
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
            // avaliar caso em que duas portas estao ligadas no mesmo output
            // valor que prevalece sera high ou low?
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
        if (outputs.size != expectedOutput.size) {
            return false;
        }

        for (int i = 0; i < outputs.size; i++) {
            if (outputs.get(i).getValue() != expectedOutput.get(i)) {
                return false;
            }
        }
        return true;
    }


    // obtem os valores atuais das saidas
    public Array<Boolean> getActualOutputs() {
        Array<Boolean> result = new Array<>(outputs.size);
        for (int i = 0; i < outputs.size; i++) {
            result.add(outputs.get(i).getValue());
        }
        return result;
    }

    // Getters
    public Array<InputBits> getInputs() {
        return inputs;
    }

    public Array<LogicGate> getAllGates() {
        return allGates;
    }

    public Array<Wire> getWires() {
        return wires;
    }

    public Array<OutputBits> getOutputs() {
        return outputs;
    }

    public Array<Boolean> getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String outputLabel, boolean value) {
        for(OutputBits output : outputs) {
            if (output.getLabel().equals(outputLabel)) {
                if(debugMode){
                    Gdx.app.log("Circuit.setExpectedOutput", "Definindo saida esperada " + outputLabel + " para " + value);
                    Gdx.app.log("Circuit.setExpectedOutput", "Indice da saida: " + outputs.indexOf(output, true));
                    Gdx.app.log("Circuit.setExpectedOutput", "Tamanho de expectedOutput: " + expectedOutput.size);
                }
                expectedOutput.set(outputs.indexOf(output, true), value);
                return;
            }
        }
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


    // Reseta os inputs para false
    public void resetInputs() {
        for (InputBits input : inputs) {
            input.setValue(false);
        }
    }
}
