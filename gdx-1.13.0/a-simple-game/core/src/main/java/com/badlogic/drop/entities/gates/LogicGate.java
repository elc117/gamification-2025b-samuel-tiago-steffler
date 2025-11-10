package com.badlogic.drop.entities.gates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    // Construtor da porta logica com um numero de entradas especificas
    // numInputs - numero de entradas da porta logica
    // x - posição X na tela
    // y - posição Y na tela
    public LogicGate(int numInputs, float x, float y) {
        this.numInputs = numInputs;
        this.inputs = new boolean[numInputs];
        this.position = new Vector2(x, y);
        this.output = false;

        // dimensoes padrao
        this.width = 64f;
        this.height = 64f;
    }

   // computa a saida da porta logica (abstrato)
   // retorno: valor de saida computado
    public abstract void compute();


    //Atualiza a saida da porta com base nas entradas atuais
    public void update() {
        compute();
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


   // Set do valor de uma porta especifica
   // index - indice da entrada
   // value - valor booleano para a entrada
    public void setInput(int index, boolean value) {
        if (index >= 0 && index < numInputs) {
            inputs[index] = value;
        }
    }

    // Set de todas as entradas simultaneamente
    // inputs - array de valores booleanos
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


    // Renderiza a porta logica na tela
    // - batch: SpriteBatch usado para desenhar a textura
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
