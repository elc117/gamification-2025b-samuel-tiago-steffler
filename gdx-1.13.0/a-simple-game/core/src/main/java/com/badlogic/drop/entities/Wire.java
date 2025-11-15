package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

//classe que armazena as conexões entre portas lógicas
public final class Wire {

    protected boolean state;        // estado do fio (true/false)
    protected LogicGate fromGate;   // porta de origem
    protected LogicGate toGate;     // porta de destino
    protected int fromOutputIndex;  // saída da porta de origem (dificilmente será diferente de 0)
    protected int toInputIndex;     // entrada da porta de destino
    
    // Coordenadas dos pontos de conexão (output da origem, input do destino)
    protected float fromX, fromY;   // coordenadas de origem
    protected float toX, toY;       // coordenadas de destino
    
    // Pontos de controle para renderizar (para criar o caminho do fio)
    protected Array<Vector2> pathPoints;
    
    // Comprimento do segmento reto ao sair/entrar nas portas
    // 30 pixels serao usados para ficar "dentro" das portas
    protected float seglen = 60f;

    // Espessura da linha do fio
    protected float lineWidth = 5f;
    
    // Cores para diferentes estados
    protected Color activeColor;    // cor quando true
    protected Color inactiveColor;  // cor quando false

    /**
     * Construtor de Wire padrao conectando a saída 0 de uma porta à entrada 0 de outra porta
     */
    public Wire(LogicGate fromGate, LogicGate toGate) {
        this(fromGate, 0, toGate, 0);
    }
    
    /**
     * Cria um fio com índices específicos de entrada/saída
     * @param fromGate Porta de origem
     * @param fromOutputIndex Índice da saída da porta de origem (geralmente 0)
     * @param toGate Porta de destino
     * @param toInputIndex Índice da entrada da porta de destino
     */
    public Wire(LogicGate fromGate, int fromOutputIndex, LogicGate toGate, int toInputIndex) {
        this.state = false;
        this.fromGate = fromGate;
        this.toGate = toGate;
        this.fromOutputIndex = fromOutputIndex;
        this.toInputIndex = toInputIndex;
        
        // Cores padrão
        this.inactiveColor = new Color(1f, 1f, 1f, 1f);    // Branco quando inativo
        this.activeColor = new Color(0x00d4ffff);           // #00d4ffff quando ativo
        
        // Inicializa array de pontos
        this.pathPoints = new Array<>();
        
        // Calcula posições de conexão
        updateConnectionPoints();
    }
    
    /**
     * Atualiza as coordenadas de conexão baseado nas posições das portas.
     * Será chamado para a geração inicial do circuito, mas também pode ser chamado ao mover as portas.
     */
    public void updateConnectionPoints() {
        // Por enquanto, usa o centro das portas
        // saida: coordenada X no meio da porta de origem (gate.X + width / 2)
        //        coordenada Y baseada da porta de origem - altura (gate.Y + height) - 30 para ficar "dentro"
        this.fromX = fromGate.getX() + fromGate.getWidth() / 2;
        this.fromY = fromGate.getY() + fromGate.getHeight() - 30;

        // entrada: coordenada X baseada na entrada da porta de destino (gate.X + (width / numInputs) * (inputIndex + 0.5)) para centralizar
        //          coordenada Y na base da porta de destino (gate.Y) + 30 para ficar "dentro"
        this.toX = toGate.getX() + (toGate.getWidth() / toGate.getNumInputs()) * (this.toInputIndex + 0.5f); // entrada por baixo (circuito vertical)
        this.toY = toGate.getY() + 30;
        
        // Recalcula o caminho
        calculatePath(false);
    }
    
    /**
     * Calcula os pontos do caminho do fio usando apenas linhas horizontais e verticais:
     * 1. Sai verticalmente da porta de origem
     * 2. Dobra horizontalmente (altura determinada pelo elemento com Y menor, 
     *    distanciando-se proporcionalmente à sua posição X)
     * 3. Dobra verticalmente em direção à porta de destino
     * 4. Entra verticalmente na porta de destino
     * Fios terão dobras mais próximas das portas de destino e vão descendo de acordo com a posição da porta de origem.
     * Isso permite criar "grids" de linhas sem haver overlapping de fios.
     */
    protected void calculatePath(boolean debug) {
        pathPoints.clear();

        int fromLevelIdx = fromGate.getLevelIdx();
        int toLevelIdx = toGate.getLevelIdx();
        int fromLevel = fromGate.getLevel();
        int toLevel = toGate.getLevel();

        if(debug) Gdx.app.log("Wire.calculatePath", "Calculando pontos entre " + fromGate.getLabel() + " e " + toGate.getLabel());
        
        // Calcula a diferença de níveis para ajustar o comportamento do fio
        int levelDiff = Math.abs(toLevel - fromLevel);
           
        // Determina qual elemento tem o Y maior (mais superior na tela)
        float referenceY;
        if (fromLevel > toLevel) {
            referenceY = fromY;
        } else {
            referenceY = toY;
        }

        // Altura do fio horizontal: parte do elemento mais superior
        // e soma proporcionalmente ao indice da linha onde o fio sai
        float baseDistance = 50f;
        float xFactor = 15f; // separacao vertical baseada na posicao do gate na linha
        float levelDiffFactor = 40f; // Separação vertical adicional baseada em levelDiff
        float horizontalY = referenceY - baseDistance - (fromLevelIdx * xFactor) - (levelDiff * levelDiffFactor);

        // Ponto inicial (saída da porta de origem)
        pathPoints.add(new Vector2(fromX, fromY));
        //pathPoints.add(new Vector2(fromX, fromY + seglen * 0.7f));
        if(debug) Gdx.app.log("Wire.calculatePath", "ponto 1: (" + fromX + ", " + fromY + ")");

        
               
        // evitar pulos de mais de 1 nivel dos fios para evitar overlap com gates
        if (levelDiff > 1) {
            // calcula ponto de dobra vertical mais cedo
            float iniY = fromY + seglen * 0.7f;//S + (baseDistance * 0.5f);
            
            // adiciona ponto vertical curto
            pathPoints.add(new Vector2(fromX, iniY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 2 (dobra cedo): (" + fromX + ", " + iniY + ")");
            
            // ponto X intermediario
            float xDiff = toX - fromX;
            float iniX = fromX + (xDiff * 0.6f); // 60% da diferenca X entre inicio e fim
            //float iniX = toX;
            pathPoints.add(new Vector2(iniX, iniY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 3 (horizontal intermed): (" + iniX + ", " + iniY + ")");
            
            // desce até a altura do fio horizontal principal
            pathPoints.add(new Vector2(iniX, horizontalY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 4 (desce): (" + iniX + ", " + horizontalY + ")");
            
            // segue horizontalmente até o X de destino
            pathPoints.add(new Vector2(toX, horizontalY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 5 (horizontal final): (" + toX + ", " + horizontalY + ")");
        } else {
            // padrao
            pathPoints.add(new Vector2(fromX, horizontalY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 2: (" + fromX + ", " + horizontalY + ")");
            
            pathPoints.add(new Vector2(toX, horizontalY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 3: (" + toX + ", " + horizontalY + ")");
        }
        
        float entryY = /*(horizontalY < toY) ?*/ Math.max(horizontalY, toY - seglen); // : Math.min(horizontalY, toY + seglen);
        pathPoints.add(new Vector2(toX, entryY));
        if(debug) Gdx.app.log("Wire.calculatePath", "ponto N-1 (entrada): (" + toX + ", " + entryY + ")");

        
        // ponto final (entrada da porta de destino)
        pathPoints.add(new Vector2(toX, toY));
        // workaround para evitar pequenos segmentos que bugam o render
        // segmentos com comprimento muito pequeno serao apagados
        for (int i = 0; i < pathPoints.size - 1; ) {
            Vector2 p1 = pathPoints.get(i);
            Vector2 p2 = pathPoints.get(i + 1);
            float dx = p2.x - p1.x;
            float dy = p2.y - p1.y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);
            if (length < lineWidth) {
                pathPoints.removeIndex(i + 1); // remove o ponto seguinte
            } else {
                i++; // tamanho razoavel
            }
        }
        if(debug) Gdx.app.log("Wire.calculatePath", "ponto final: (" + toX + ", " + toY + ")");
        if(debug) Gdx.app.log("Wire.calculatePath", "==============================");
    }
    
    /**
     * Atualiza o estado do fio baseado na saída da porta de origem
     */
    public void updateState() {
        if (fromGate != null) {
            this.state = fromGate.getOutput();
        }
    }

    // Getters e Setters
    
    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
    
    public LogicGate getFromGate() {
        return fromGate;
    }
    
    public LogicGate getToGate() {
        return toGate;
    }
    
    public int getFromOutputIndex() {
        return fromOutputIndex;
    }
    
    public int getToInputIndex() {
        return toInputIndex;
    }
    
    public Array<Vector2> getPathPoints() {
        return pathPoints;
    }
    
    public float getLineWidth() {
       return lineWidth;
    }
    
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }
    
    public float getSeglen() {
        return seglen;
    }
    
    public void setSeglen(float length) {
        this.seglen = length;
        calculatePath(false); // Recalcula quando muda
    }
    
    /**
     * Retorna a cor atual baseada no estado do fio
     */
    public Color getCurrentColor() {
        return state ? activeColor : inactiveColor;
    }
    
    public Color getActiveColor() {
        return activeColor;
    }
    
    public void setActiveColor(Color color) {
        this.activeColor = color;
    }
    
    public Color getInactiveColor() {
        return inactiveColor;
    }
    
    public void setInactiveColor(Color color) {
        this.inactiveColor = color;
    }
}


/*


   O0   01     
    |___|____
        |   |
        |   |
       ... ...

 ...    ...  ...
  |      |    |
(AND)  (NOT) (OR)
 |  |    |___|__|    
 |  |_____   |  |
 |_______|___|  |
 |       |      |
I0      I1     I2

*/