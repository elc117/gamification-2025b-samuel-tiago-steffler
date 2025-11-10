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
    
    // nivel no circuito (0 = inputs, n = outputs) para desenho (circuito sera feito de baixo para cima)
    protected int level;
    
    // disposicao horizontal para o nivel de portas (usado pelo renderizador)
    protected int levelIdx;

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
        
        // Valores padrão para posicionamento dinâmico
        this.level = 0;
        this.levelIdx = 0;
    }
    
    /**
     * Atualiza a posição do gate baseado no tamanho da tela.
     * Chamado quando a tela é redimensionada.
     * 
     * @param screenWidth Largura da tela em pixels
     * @param screenHeight Altura da tela em pixels
     * @param levelTot Número total de níveis no circuito
     * @param gateLevel Número de gates neste nível
     */
    public void updatePos(float screenWidth, float screenHeight, 
                               int levelTot, int gateLevel) {



        // ------------------ Margens - modificar se preciso ------------------
        float bottom = 100f;    // Espaço na parte inferior
        float top = 100f;       // Espaço no topo
        float sides = 100f;     // Espaço nas laterais
        


        // Altura util do circuito
        float aUtil = screenHeight - bottom - top;
        
        // Calcula Y baseado no nivel (0 = bottom, totalLevels - 1 = top)
        float espac = levelTot > 1 ? aUtil / (levelTot - 1) : 0;
        float y = bottom + (level * espac);
        
        // Largura util para distribuir gates horizontalmente
        float lUtil = screenWidth - (2 * sides);
        
        // Calcula X baseado no indice dentro do nível
        float x;
        if (gateLevel == 1) {
            // se for apenas um gate (tipo um output), centraliza
            x = screenWidth / 2 - width / 2;
        } else {
            // Distribui uniforme
            float spacing = lUtil / (gateLevel - 1);
            x = sides + (levelIdx * spacing) - width / 2;
        }
        
        this.position.set(x, y);
    }

    /**
     * Método abstrato para computar a saída da porta lógica.
     * Implementação cabe a cada tipo de porta.
     * @return O valor de saída computado
     */
    public abstract void compute();

    /**
     * Atualiza a saída da porta com base nas entradas atuais
     */
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
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public int getLevelIdx() {
        return levelIdx;
    }
    
    public void setLevelIdx(int levelIdx) {
        this.levelIdx = levelIdx;
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
