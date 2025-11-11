package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta NOR - a saida é verdadeira somente se todas as entradas forem falsas (NOT OR)
// pode ser construida com mais de duas entradas

public class NORGate extends LogicGate {

    // construtor para porta NOR padrao com 2 entradas
    public NORGate(float x, float y) {
        super(2, x, y); // default 2
        this.gateType = "NOR";

        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/NOR_off.png"));
    }

    // construtor para porta NOR com varias entradas
    public NORGate(int numInputs, float x, float y) {
        super(numInputs, x, y);
        this.gateType = "NOR";
        this.width = 131f;
        this.height = 154f;
        this.setTexture(new Texture("gates/NOR_off.png"));
    }

    @Override
    public void compute() {
        // retorna verdadeiro somente se todas as entradas forem falsas (NOT OR)
        for (boolean input : inputs) {
            if (input) {
                this.output = false;
                this.setTexture(new Texture("gates/NOR_off.png"));
                return;
            }
        }
        this.setTexture(new Texture("gates/NOR_on.png"));
        this.output = true;
    }
}
