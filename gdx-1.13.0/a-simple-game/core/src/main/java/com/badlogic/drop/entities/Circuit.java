package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Representa o circuito completo com avaliação baseada em ordenação topológica.
 * Constrói uma árvore de dependências e avalia as portas na ordem correta (folhas -> raiz).
 * Como InputBits e OutputBits herdam de LogicGate, são uniformemente tratados como um grafo de LogicGates.
 */
public class Circuit {

    // Todos os nós do circuito (inputs, gates, outputs são todos LogicGates)
    private LogicGate[] allGates;
    
    // Referências específicas para acesso conveniente
    private InputBits[] inputs;
    private OutputBits[] outputs;
    
    // Conexões
    private Wire[] wires;
    
    // Saídas esperadas
    private boolean[] expectedOutput;
    
    // Ordem de avaliacao e atualzacao das portas logicas
    private Array<LogicGate> evaluationOrder;
    
    // par de porta e array de portas das quais depende (geralmente 1 ou 2)
    private ObjectMap<LogicGate, Array<LogicGate>> dependencies; 
    
    // organizacao espacial dos gates
    private Array<Array<LogicGate>> levels; // levels[i] = array dos gates no nivel i
    private int maxLevel;
    
    /**
     * @param inputs Nós de entrada (herdam de LogicGate)
     * @param gates Portas lógicas intermediárias
     * @param wires Conexões entre portas
     * @param outputs Nós de saída (herdam de LogicGate)
     * @param expectedOutput Valores esperados para as saídas
     */
    public Circuit(InputBits[] inputs, LogicGate[] gates, Wire[] wires, 
                   OutputBits[] outputs, boolean[] expectedOutput) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.wires = wires;
        this.expectedOutput = expectedOutput;
        
        // Combina tudo em um único array de LogicGates
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
        this.levels = new Array<>();
        
        buildDependencyGraph();
        computeEvaluationOrder();
        computeLevels();
    }
    
    /**
     * Constrói o grafo de dependências baseado nos fios.
     */
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
    
    /**
     * Calcula a ordem de avaliação usando ordenação topológica (Kahn's algorithm).
     */
    private void computeEvaluationOrder() {
        evaluationOrder.clear();
        
        // Calcula grau de entrada para cada porta (quantas dependências)
        ObjectMap<LogicGate, Integer> inDegree = new ObjectMap<>();
        for (LogicGate gate : allGates) {
            inDegree.put(gate, dependencies.get(gate).size);
        }
        
        // Fila com portas sem dependências (grau de entrada = 0)
        // InputBits naturalmente terão grau 0
        Array<LogicGate> queue = new Array<>();
        for (LogicGate gate : allGates) {
            if (inDegree.get(gate) == 0) {
                queue.add(gate);
            }
        }
        
        // Processa portas em ordem topológica
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
        
        // Verifica se há ciclos
        if (evaluationOrder.size != allGates.length) {
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
            levels.add(new Array<LogicGate>());
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
            
            for (LogicGate gate : gatesInLevel) {
                gate.updatePos(screenWidth, screenHeight, totalLevels, gatesCount);
            }
        }
        
        // Atualiza os caminhos dos fios
        for (Wire wire : wires) {
            wire.calculatePath();
        }
    }
    
    /**
     * Define os valores das entradas do circuito
     */
    public void setInputValues(boolean[] inputValues) {
        if (inputValues.length != inputs.length) {
            throw new IllegalArgumentException("Contagem de valores de entrada não corresponde ao número de entradas.");
        }
        
        for (int i = 0; i < inputs.length; i++) {
            inputs[i].setValue(inputValues[i]);
        }
    }
    
    /**
     * Avalia todo o circuito na ordem correta.
     * Execução: apenas itera pela ordem topológica e chama update().
     */
    public void evaluate() {
        // Atualiza inputs das portas baseado nos fios
        for (Wire wire : wires) {
            LogicGate from = wire.getFromGate();
            LogicGate to = wire.getToGate();
            int inputIndex = wire.getToInputIndex();
            
            // Conecta output de 'from' ao input de 'to'
            to.setInput(inputIndex, from.getOutput());
        }
        
        // Avalia todas as portas na ordem topológica
        for (LogicGate gate : evaluationOrder) {
            gate.update();
        }
        
        // Atualiza estado dos fios para renderização
        for (Wire wire : wires) {
            wire.updateState();
        }
    }
    
    /**
     * Verifica se o circuito está correto (outputs == expectedOutputs)
     */
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
    
    /**
     * Obtém os valores atuais das saídas
     */
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
    
    /**
     * Debug: imprime a ordem de avaliação
     */
    public void printEvaluationOrder() {
        System.out.println("Ordem de avaliação do circuito:");
        for (int i = 0; i < evaluationOrder.size; i++) {
            LogicGate gate = evaluationOrder.get(i);
            System.out.println((i + 1) + ". " + gate.getGateType() + " gate");
        }
    }
}
