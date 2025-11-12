package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

// porta NAND - a saida é falsa somente se todas as entradas forem verdadeiras, e verdadeiro caso contrário (NOT AND)
// pode ser construida com mais de duas entradas

public class NANDGate extends LogicGate {

    // construtor para porta NAND padrao com 2 entradas
    public NANDGate(String label) {
        super(label, 2); // default 2 portas
        this.gateType = "NAND";

        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/NAND_off.png"));
        this.setTextureOn(new Texture("gates/NAND_on.png"));
        this.setTexture(this.textureOff);
    }

    // construtor para porta NAND com varias portas
    public NANDGate(String label, int numInputs) {
        super(label, numInputs);
        this.gateType = "NAND";
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/NAND_off.png"));
        this.setTextureOn(new Texture("gates/NAND_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // retorna falso somente se todas as entradas forem verdadeiras (NOT AND)
        for (boolean input : inputs) {
            if (!input) {
                this.output = true;
                this.setTexture(this.textureOn);
                return;
            }
        }
        this.setTexture(this.textureOff);
        this.output = false;
    }
}
