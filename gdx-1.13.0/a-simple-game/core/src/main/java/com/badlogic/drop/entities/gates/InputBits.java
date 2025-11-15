package com.badlogic.drop.entities.gates;

import com.badlogic.gdx.graphics.Texture;

/**
 * Representa um nó de entrada do circuito (fonte de sinal).
 * Herda de LogicGate para facilitar o gerenciamento de conexões.
 * Funciona como uma "porta" sem entradas que apenas emite um valor controlado pelo usuário.
 */
public class InputBits extends LogicGate {

    private boolean value; // Valor atual do input

    /**
     * Construtor padrão - cria um input com valor falso
     */
    public InputBits(String label, float scale) {
        super(label, 0, scale); // 0 inputs (fonte de sinal)
        this.label = label;
        this.value = false;
        this.gateType = "INPUT";

        // Tamanho do icone PNG
        this.width = 108f;
        this.height = 108f;
        this.setTextureOff(new Texture("textures/bits/in_off.png"));
        this.setTextureOn(new Texture("textures/bits/in_on.png"));
        this.setTexture(this.textureOff);
    }

    /**
     * Construtor de input com valor predefinido
     */
    public InputBits(String label, boolean initialValue, float scale) {
        super(label, 0, scale); // 0 inputs
        this.value = initialValue;
        this.gateType = "INPUT";
        this.width = 108f;
        this.height = 108f;
        this.setTextureOff(new Texture("textures/bits/in_off.png"));
        this.setTextureOn(new Texture("textures/bits/in_on.png"));
        if (initialValue) {
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
    }

    /**
     * InputBits não tem lógica - apenas retorna o valor definido pelo usuário
     */
    @Override
    public void compute() {
        this.output = value;
        if (value) {
            this.setTexture(this.textureOn);
        } else {
            this.setTexture(this.textureOff);
        }
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

    public void setLabel(String label) {
        this.label = label;
    }
}
