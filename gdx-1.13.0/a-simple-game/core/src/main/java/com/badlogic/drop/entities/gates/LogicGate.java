package com.badlogic.drop.entities.gates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

// superclasse abstrata para todas as portas logicas
public abstract class LogicGate {
    
    // Display
    protected Texture texture;
    protected Vector2 position;
    protected float width;
    protected float height;
    
    // Logica
    protected boolean[] inputs;
    protected boolean output;
    protected int numInputs;
    
    // tipo de porta logica
    protected String gateType;
    
    /**
     * Construtor de uma porta lógica com um número de entradas específicas
     * @param numInputs Número de entradas da porta lógica
     * @param x Posição X na tela
     * @param y Posição Y na tela
     */
    public LogicGate(int numInputs, float x, float y) {
        this.numInputs = numInputs;
        this.inputs = new boolean[numInputs];
        this.position = new Vector2(x, y);
        this.output = false;
        
        // dimensoes padrao
        this.width = 64f;
        this.height = 64f;
    }
    
    /**
     * Método abstrato para computar a saída da porta lógica.
     * Implementação cabe a cada tipo de porta.
     * @return O valor de saída computado
     */
    public abstract boolean compute();
    
    /**
     * Atualiza a saída da porta com base nas entradas atuais
     */
    public void update() {
        this.output = compute();
    }
    
    // Getters e Setters

    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
    
    public float getX() {
        return position.x;
    }
    
    public float getY() {
        return position.y;
    }
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public boolean[] getInputs() {
        return inputs;
    }
    
    /**
     * Set do valor de uma porta específica
     * @param index Índice da entrada (0 - numInputs-1)
     * @param value Valor booleano para a entrada
     */
    public void setInput(int index, boolean value) {
        if (index >= 0 && index < numInputs) {
            inputs[index] = value;
        }
    }
    
    /**
     * Set de todas as entradas simultaneamente
     * @param inputs Array de valores booleanos
     */
    public void setInputs(boolean[] inputs) {
        if (inputs.length == this.numInputs) {
            System.arraycopy(inputs, 0, this.inputs, 0, numInputs);
        }
    }
    
    public boolean getOutput() {
        return output;
    }
    
    public int getNumInputs() {
        return numInputs;
    }
    
    public String getGateType() {
        return gateType;
    }
    
    /**
     * Descarta recursos quando a porta não for mais necessária
     */
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
