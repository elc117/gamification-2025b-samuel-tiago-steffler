package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta lógica AND  - A saída é verdadeira somente se todas as entradas forem verdadeiras
// suporta mais do que duas entradas

public class ANDGate extends LogicGate {

    // constroi a porta com 2 entradas por default
    public ANDGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "AND";

        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/AND_off.png"));
    }

    // constroi a porta com número customizado de entradas
    public ANDGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "AND";

        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/AND_off.png"));
    }

    @Override
    public void compute() {
        // retorna verdadeiro se nenhuma entrada for falsa e atualiza textura
        for (boolean input : inputs) {
            if (!input) {
                this.output = false;
                this.setTexture(new Texture("gates/AND_off.png"));
                return;
            }
        }
        this.setTexture(new Texture("gates/AND_on.png"));
        this.output = true;
    }
}
