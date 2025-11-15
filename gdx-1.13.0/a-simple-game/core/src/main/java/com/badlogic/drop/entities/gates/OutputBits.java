package com.badlogic.drop.entities.gates;

import com.badlogic.gdx.graphics.Texture;

/**
 * Representa um nó de saída do circuito (terminal de leitura).
 * Herda de LogicGate para facilitar o gerenciamento de conexões.
 * Funciona como uma "porta" passthrough que apenas propaga seu input único.
 */
public class OutputBits extends LogicGate {
    /**
     * Construtor - cria um output com 1 entrada
     */
    public OutputBits(String label) {
        super(label, 1); // 1 input (recebe sinal de outra porta)
        this.gateType = "OUTPUT";

        // Tamanho do icone PNG
        this.width = 83f;
        this.height = 83f;
        this.setTextureOff(new Texture("textures/bits/out_off.png"));
        this.setTextureOn(new Texture("textures/bits/out_on.png"));
        this.setTexture(this.textureOff);
    }

    /**
     * OutputBits não tem lógica - apenas propaga o input
     */
    @Override
    public void compute() {
        // Propaga o primeiro (e único) input para o output
        this.output = inputs.length > 0 ? inputs[0] : false;
        if (this.output) {
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
    }

    /**
     * Obtém o valor atual da saída
     */
    public boolean getValue() {
        return getOutput();
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
