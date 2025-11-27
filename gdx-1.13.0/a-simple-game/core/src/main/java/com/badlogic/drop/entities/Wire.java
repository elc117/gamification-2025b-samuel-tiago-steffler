package com.badlogic.drop.entities;

import com.badlogic.drop.entities.gates.LogicGate;
import com.badlogic.drop.entities.gates.ORGate;
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

    protected Array<LogicGate> gates; // referencia para todas as portas do circuito (usado para calcular o caminho)

    /**
     * Construtor de Wire padrao conectando a saída 0 de uma porta à entrada 0 de outra porta
     */
    public Wire(LogicGate fromGate, LogicGate toGate, Array<LogicGate> gates) {
        this(fromGate, 0, toGate, 0, gates);
    }

    /**
     * Cria um fio com índices específicos de entrada/saída
     * @param fromGate Porta de origem
     * @param fromOutputIndex Índice da saída da porta de origem (geralmente 0)
     * @param toGate Porta de destino
     * @param toInputIndex Índice da entrada da porta de destino
     * @param gatesArray de todas as portas lógicas do circuito
     */
    public Wire(LogicGate fromGate, int fromOutputIndex, LogicGate toGate, int toInputIndex, Array<LogicGate> gates) {
        this.state = false;
        this.fromGate = fromGate;
        this.toGate = toGate;
        this.fromOutputIndex = fromOutputIndex;
        this.toInputIndex = toInputIndex;
        this.gates = gates;

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

        // Determina qual elemento tem o Y maior e menor (mais superior na tela)
        float UpperReferenceY, LowerReferenceY;
        if (fromLevel > toLevel) {
            UpperReferenceY = fromY;
            LowerReferenceY = toY;
        } else {
            UpperReferenceY = toY;
            LowerReferenceY = fromY;
        }

        // Altura do fio horizontal: parte do elemento mais superior
        // e soma proporcionalmente ao indice da linha onde o fio sai
        float baseDistance = 38f;
        float xFactor = 13f; // separacao vertical baseada na posicao do gate na linha
        float levelDiffFactor = 15f; // Separação vertical adicional baseada em levelDiff
        float horizontalY = UpperReferenceY - baseDistance - (fromLevelIdx * xFactor) - (levelDiff * levelDiffFactor);

        // Ponto inicial (saída da porta de origem)
        pathPoints.add(new Vector2(fromX, fromY));
        //pathPoints.add(new Vector2(fromX, fromY + seglen * 0.7f));
        if(debug) Gdx.app.log("Wire.calculatePath", "ponto 1: (" + fromX + ", " + fromY + ")");


        // verifica a posicao do elemento imediato superior para caso abaixo
        Array<LogicGate> superiorGates = new Array<>();
        LogicGate lowerGate = (toGate.getY() < fromGate.getY()) ? toGate : fromGate;        // gate inferior
        LogicGate upperGate = (toGate.getY() < fromGate.getY()) ? fromGate : toGate;        // gate superior
        float lowerX = lowerGate.getX() + lowerGate.getWidth() / 2;
        Gdx.app.log("Wire.calculatePath", "Analisando gates superiores entre " + lowerGate.getLabel() + " e " + upperGate.getLabel());
        for (LogicGate gate : gates) {

            // analisa apenas gates entre os dois
            if (gate.getLevel() > ((fromLevel < toLevel) ? fromLevel : toLevel) && gate.getLevel() < ((fromLevel > toLevel) ? fromLevel : toLevel)) {
                float Xposgate = gate.getX() + gate.getWidth() / 2;

                // intervalo para evitar: [ Xposgate - gate.getWidth()/2 - 20 .......... Xposgate + gate.getWidth()/2 + 20 ] margem de 20px
                if ((lowerX < Xposgate + gate.getWidth()/2 + 10) && (lowerX > Xposgate - gate.getWidth()/2 - 10)) {
                    // gate esta no caminho
                    Gdx.app.log("Wire.calculatePath", " - gate superior no caminho: " + gate.getLabel());
                    superiorGates.add(gate);
                    continue;
                }
            } else continue;
        }



        // caso haja gates imediatamente superiores no caminho, calcula desvio
        if (levelDiff > 1 && superiorGates.size > 0) {

            // calcula ponto de dobra vertical mais cedo
            float iniY = fromY + seglen * 0.7f;//S + (baseDistance * 0.5f);

            // adiciona ponto vertical curto
            pathPoints.add(new Vector2(fromX, iniY));
            if(debug) Gdx.app.log("Wire.calculatePath", "ponto 2 (dobra cedo): (" + fromX + ", " + iniY + ")");

            // analisa as gates superiores no caminho para definir o segmento horizontal
            //boolean canBreakOnce = true;
            //for (LogicGate gate : superiorGates){
            //    // verifica se vai pra direita ou esquerda
            //    if (toX < fromX){
            //        // esquerda
            //        if (toX > gate.getX() - gate.getWidth()/2 - 20){
            //            // deve quebrar mais de uma vez
            //            canBreakOnce = false;
            //        } else continue;
            //    } else {
            //        // direita
            //        if (toX < gate.getX() + gate.getWidth()/2 + 20){
            //            // deve quebrar mais de uma vez
            //            canBreakOnce = false;
            //        }
            //    }
            //}

            // fingir que estaria ligando em uma porta diretamente acima (para imitar altura de quebra)
            // altura sera definida na gambiarra ja que nao temos acesso ao circuito como um todo
            LogicGate fakeUpperGate = null;
            for (LogicGate gate : gates){
                if (gate.getLevel() == fromGate.getLevel() + 1){
                    fakeUpperGate = gate;
                    Gdx.app.log("Wire.calculatePath", " gate achado para usar altura: " + gate.getLabel());
                    break;
                }
            }
            if (fakeUpperGate == null){
                // se nao achar, usar a porta superior direta
                fakeUpperGate = upperGate;
                Gdx.app.log("Wire.calculatePath", " nenhum gate de nivel 0 achado, usando o superior direto: " + fakeUpperGate.getLabel());
            }
            UpperReferenceY = fakeUpperGate.getY();
            horizontalY = UpperReferenceY - baseDistance - (fromLevelIdx * xFactor) - (1 * levelDiffFactor);


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
        }
        // padrao
        else {

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
