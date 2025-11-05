package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.ANDGate;
import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.NOTGate;
import com.badlogic.drop.entities.gates.ORGate;
import com.badlogic.drop.ui.WireRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

/**
 * Exemplo de como usar Wire e WireRenderer para criar circuitos.
 */
public class WireExample {
    
    /**
     * Exemplo básico: criando e conectando portas com fios
     */
    public static void basicExample() {
        // Cria portas em diferentes posições
        ANDGate gate1 = new ANDGate(100f, 300f);
        ORGate gate2 = new ORGate(300f, 250f);
        NOTGate gate3 = new NOTGate(300f, 350f);
        
        // Conecta gate1 para gate2
        Wire wire1 = new Wire(gate1, gate2);
        
        // Conecta gate1 para gate3
        Wire wire2 = new Wire(gate1, gate3);
        
        // Customiza aparência do fio
        wire1.setLineWidth(4f);
        wire1.setActiveColor(new Color(0f, 1f, 0f, 1f));    // Verde brilhante
        wire1.setInactiveColor(new Color(0.2f, 0.2f, 0.2f, 1f)); // Cinza escuro
        
        // Ajusta comprimento dos segmentos retos
        wire1.setSeglen(40f);
        
        // Atualiza estado baseado na porta de origem
        gate1.setInput(0, true);
        gate1.setInput(1, true);
        gate1.update();
        
        wire1.updateState(); // Pega o output de gate1
        
        // Propaga o sinal para a próxima porta
        gate2.setInput(0, wire1.getState());
    }
    
    /**
     * Exemplo de renderização
     * Este código seria chamado no método render() da sua tela
     */
    public static void renderExample(Array<Wire> wires, WireRenderer renderer) {
        // Renderiza todos os fios de uma vez
        renderer.renderAll(wires);
        
        // OU renderiza individualmente com mais controle
        /*
        for (Wire wire : wires) {
            renderer.render(wire, true);
        }
        */
    }
    
    /**
     * Exemplo de um circuito completo: Half Adder
     * Dois inputs (A, B) produzem Sum e Carry
     */
    public static class HalfAdderCircuit {
        private LogicGate xorGate;  // Para Sum
        private LogicGate andGate;  // Para Carry
        private Array<Wire> wires;
        private WireRenderer renderer;
        
        // Posições dos inputs "virtuais" (onde o jogador interage)
        private float inputAX = 50f, inputAY = 350f;
        private float inputBX = 50f, inputBY = 250f;
        
        public HalfAdderCircuit() {
            // Cria as portas
            xorGate = new com.badlogic.drop.entities.gates.XORGate(300f, 300f);
            andGate = new ANDGate(300f, 200f);
            
            wires = new Array<>();
            
            // Nota: Em um circuito real, você precisaria de "pseudo-gates" 
            // ou objetos InputNode para representar os inputs A e B
            // Por simplicidade, vamos apenas demonstrar a estrutura
            
            renderer = new WireRenderer();
        }
        
        /**
         * Atualiza o circuito com novos valores de entrada
         */
        public void setInputs(boolean a, boolean b) {
            // Define inputs para XOR
            xorGate.setInput(0, a);
            xorGate.setInput(1, b);
            xorGate.update();
            
            // Define inputs para AND
            andGate.setInput(0, a);
            andGate.setInput(1, b);
            andGate.update();
            
            // Atualiza estado dos fios
            for (Wire wire : wires) {
                wire.updateState();
            }
        }
        
        /**
         * Obtém o resultado Sum
         */
        public boolean getSum() {
            return xorGate.getOutput();
        }
        
        /**
         * Obtém o resultado Carry
         */
        public boolean getCarry() {
            return andGate.getOutput();
        }
        
        /**
         * Renderiza o circuito
         */
        public void render() {
            // Renderiza os fios primeiro (abaixo das portas)
            renderer.renderAll(wires);
            
            // Depois renderiza as portas
            // (seu código de renderização de portas aqui)
        }
        
        public void dispose() {
            renderer.dispose();
        }
    }
    
    /**
     * Exemplo mostrando como recalcular posições quando portas são movidas
     */
    public static void dynamicPositionExample() {
        ANDGate gate1 = new ANDGate(100f, 200f);
        ORGate gate2 = new ORGate(300f, 200f);
        
        Wire wire = new Wire(gate1, gate2);
        
        // Mais tarde, se você mover as portas:
        gate1.setPosition(150f, 250f);
        gate2.setPosition(350f, 250f);
        
        // Atualiza o caminho do fio
        wire.updateConnectionPoints();
        
        // Agora o fio está conectado nas novas posições
    }
}
