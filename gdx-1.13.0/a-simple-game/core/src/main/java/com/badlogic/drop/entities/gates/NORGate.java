package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta NOR - a saida é verdadeira somente se todas as entradas forem falsas (NOT OR)
// pode ser construida com mais de duas entradas

public class NORGate extends LogicGate {

    // construtor para porta NOR padrao com 2 entradas
    public NORGate(String label) {
        super(label, 2); // default 2
        this.gateType = "NOR";

        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/NOR_off.png"));
        this.setTextureOn(new Texture("gates/NOR_on.png"));
        this.setTexture(this.textureOff);
    }

    // construtor para porta NOR com varias entradas
    public NORGate(String label, int numInputs) {
        super(label, numInputs);
        this.gateType = "NOR";
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/NOR_off.png"));
        this.setTextureOn(new Texture("gates/NOR_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // retorna verdadeiro somente se todas as entradas forem falsas (NOT OR)
        for (boolean input : inputs) {
            if (input) {
                this.output = false;
                this.setTexture(this.textureOff);
                return;
            }
        }
        this.setTexture(this.textureOn);
        this.output = true;
    }
}
