package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.ANDGate;
import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.NOTGate;
import com.badlogic.drop.entities.gates.ORGate;
import com.badlogic.drop.entities.gates.XORGate;

/**
 * Exemplos de criação e uso de circuitos completos.
 */
public class CircuitExample {
    
    /**
     * Exemplo 1: Circuito simples AND
     * Inputs: A, B
     * Output: A AND B
     */
    public static Circuit createSimpleAndCircuit() {
        // Criar inputs
        InputBits[] inputs = new InputBits[2];
        inputs[0] = new InputBits("A", 50f, 300f);
        inputs[1] = new InputBits("B", 50f, 200f);
        
        // Criar porta AND
        LogicGate[] gates = new LogicGate[1];
        gates[0] = new ANDGate(200f, 250f);
        
        // Criar outputs
        OutputBits[] outputs = new OutputBits[1];
        outputs[0] = new OutputBits("Result", 350f, 250f);
        
        // Criar wires conectando tudo
        Wire[] wires = new Wire[3];
        wires[0] = new Wire(inputs[0], 0, gates[0], 0); // Input A -> AND input 0
        wires[1] = new Wire(inputs[1], 0, gates[0], 1); // Input B -> AND input 1
        wires[2] = new Wire(gates[0], 0, outputs[0], 0); // AND output -> Output
        
        // Definir output esperado
        boolean[] expectedOutput = new boolean[]{false}; // Esperamos false por padrão
        
        // Criar circuito (tudo é conectado via wires agora!)
        return new Circuit(inputs, gates, wires, outputs, expectedOutput);
    }
    
    /**
     * Exemplo 2: Half Adder
     * Inputs: A, B
     * Outputs: Sum (A XOR B), Carry (A AND B)
     */
    public static Circuit createHalfAdder() {
        // Criar inputs
        InputBits[] inputs = new InputBits[2];
        inputs[0] = new InputBits("A", 50f, 300f);
        inputs[1] = new InputBits("B", 50f, 200f);
        
        // Criar portas
        LogicGate[] gates = new LogicGate[2];
        gates[0] = new XORGate(200f, 280f);  // Para Sum
        gates[1] = new ANDGate(200f, 180f);  // Para Carry
        
        // Criar outputs
        OutputBits[] outputs = new OutputBits[2];
        outputs[0] = new OutputBits("Sum", 350f, 280f);
        outputs[1] = new OutputBits("Carry", 350f, 180f);
        
        // Criar wires conectando tudo
        Wire[] wires = new Wire[6];
        wires[0] = new Wire(inputs[0], 0, gates[0], 0); // A -> XOR input 0
        wires[1] = new Wire(inputs[1], 0, gates[0], 1); // B -> XOR input 1
        wires[2] = new Wire(inputs[0], 0, gates[1], 0); // A -> AND input 0
        wires[3] = new Wire(inputs[1], 0, gates[1], 1); // B -> AND input 1
        wires[4] = new Wire(gates[0], 0, outputs[0], 0); // XOR -> Sum output
        wires[5] = new Wire(gates[1], 0, outputs[1], 0); // AND -> Carry output
        
        // Output esperado (por exemplo, para A=1, B=1: Sum=0, Carry=1)
        boolean[] expectedOutput = new boolean[]{false, true};
        
        return new Circuit(inputs, gates, wires, outputs, expectedOutput);
    }
    
    /**
     * Exemplo 3: Circuito com múltiplas camadas (NAND usando AND + NOT)
     * Demonstra a avaliação em ordem correta
     */
    public static Circuit createNandFromAndNot() {
        // Criar inputs
        InputBits[] inputs = new InputBits[2];
        inputs[0] = new InputBits("A", 50f, 300f);
        inputs[1] = new InputBits("B", 50f, 200f);
        
        // Criar portas
        LogicGate[] gates = new LogicGate[2];
        gates[0] = new ANDGate(200f, 250f);   // Primeira camada
        gates[1] = new NOTGate(350f, 250f);   // Segunda camada
        
        // Criar outputs
        OutputBits[] outputs = new OutputBits[1];
        outputs[0] = new OutputBits("Result", 500f, 250f);
        
        // Criar wires
        Wire[] wires = new Wire[4];
        wires[0] = new Wire(inputs[0], 0, gates[0], 0); // Input A -> AND
        wires[1] = new Wire(inputs[1], 0, gates[0], 1); // Input B -> AND
        wires[2] = new Wire(gates[0], 0, gates[1], 0);  // AND -> NOT
        wires[3] = new Wire(gates[1], 0, outputs[0], 0); // NOT -> Output
        
        // Output esperado
        boolean[] expectedOutput = new boolean[]{true}; // NOT(false) = true
        
        return new Circuit(inputs, gates, wires, outputs, expectedOutput);
    }
    
    /**
     * Exemplo de uso completo de um circuito
     */
    public static void demonstrateCircuitUsage() {
        // Criar circuito
        Circuit circuit = createHalfAdder();
        
        // Definir valores de entrada
        boolean[] inputValues = new boolean[]{true, true}; // A=1, B=1
        circuit.setInputValues(inputValues);
        
        // Avaliar o circuito
        circuit.evaluate();
        
        // Verificar outputs
        boolean[] actualOutputs = circuit.getActualOutputs();
        System.out.println("Sum: " + actualOutputs[0]);    // Should be 0
        System.out.println("Carry: " + actualOutputs[1]);  // Should be 1
        
        // Verificar se está correto
        boolean correct = circuit.isCorrect();
        System.out.println("Circuit correct: " + correct);
        
        // Debug: ver ordem de avaliação
        circuit.printEvaluationOrder();
    }
    
    /**
     * Exemplo 4: Circuito mais complexo (Full Adder simplificado)
     * Mostra múltiplas camadas de dependência
     */
    public static Circuit createComplexCircuit() {
        // 3 inputs
        InputBits[] inputs = new InputBits[3];
        inputs[0] = new InputBits("A", 50f, 350f);
        inputs[1] = new InputBits("B", 50f, 250f);
        inputs[2] = new InputBits("Cin", 50f, 150f);
        
        // Várias portas em camadas
        LogicGate[] gates = new LogicGate[5];
        gates[0] = new XORGate(200f, 300f);   // A XOR B
        gates[1] = new ANDGate(200f, 200f);   // A AND B
        gates[2] = new XORGate(350f, 275f);   // (A XOR B) XOR Cin = Sum
        gates[3] = new ANDGate(350f, 175f);   // (A XOR B) AND Cin
        gates[4] = new ORGate(500f, 187f);    // Carry out
        
        // Outputs
        OutputBits[] outputs = new OutputBits[2];
        outputs[0] = new OutputBits("Sum", 650f, 275f);
        outputs[1] = new OutputBits("Cout", 650f, 187f);
        
        // Wires conectando as camadas e inputs
        Wire[] wires = new Wire[12];
        // Inputs to first layer
        wires[0] = new Wire(inputs[0], 0, gates[0], 0);  // A -> XOR1
        wires[1] = new Wire(inputs[1], 0, gates[0], 1);  // B -> XOR1
        wires[2] = new Wire(inputs[0], 0, gates[1], 0);  // A -> AND1
        wires[3] = new Wire(inputs[1], 0, gates[1], 1);  // B -> AND1
        // First layer to second layer
        wires[4] = new Wire(gates[0], 0, gates[2], 0);   // XOR1 -> XOR2
        wires[5] = new Wire(inputs[2], 0, gates[2], 1);  // Cin -> XOR2
        wires[6] = new Wire(gates[0], 0, gates[3], 0);   // XOR1 -> AND2
        wires[7] = new Wire(inputs[2], 0, gates[3], 1);  // Cin -> AND2
        // Second layer to third layer
        wires[8] = new Wire(gates[1], 0, gates[4], 0);   // AND1 -> OR
        wires[9] = new Wire(gates[3], 0, gates[4], 1);   // AND2 -> OR
        // Final outputs
        wires[10] = new Wire(gates[2], 0, outputs[0], 0); // XOR2 -> Sum
        wires[11] = new Wire(gates[4], 0, outputs[1], 0); // OR -> Cout
        
        boolean[] expectedOutput = new boolean[]{false, false};
        
        return new Circuit(inputs, gates, wires, outputs, expectedOutput);
    }
}
