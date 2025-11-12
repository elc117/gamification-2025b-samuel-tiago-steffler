package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta lógica AND  - A saída é verdadeira somente se todas as entradas forem verdadeiras
// suporta mais do que duas entradas

public class ANDGate extends LogicGate {

    // constroi a porta com 2 entradas por default
    public ANDGate(String label) {
        super(label, 2); // default 2
        this.gateType = "AND";
        this.setTextureOff(new Texture("gates/AND_off.png"));
        this.setTextureOn(new Texture("gates/AND_on.png"));
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTexture(this.textureOff);
    }

    // constroi a porta com número customizado de entradas
    public ANDGate(String label, int numInputs) {
        super(label, numInputs);
        this.gateType = "AND";

        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/AND_off.png"));
        this.setTextureOn(new Texture("gates/AND_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // retorna verdadeiro se nenhuma entrada for falsa e atualiza textura
        for (boolean input : inputs) {
            if (!input) {
                this.output = false;
                this.setTexture(this.textureOff);
                return;
            }
        }
        this.setTexture(this.textureOn);
        this.output = true;
    }
}
