package com.badlogic.drop.entities.gates;
import com.badlogic.gdx.graphics.Texture;

//porta OR - retorna verdadeiro quando qualquer uma das entradas for verdadeira
// pode ser construida com mais de duas entradas

public class ORGate extends LogicGate {

    // construtor padrão da porta OR
    public ORGate(String label, float scale) {
        super(label, 2, scale); // default 2
        this.gateType = "OR";
        // Tamanho do icone PNG
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/OR_off.png"));
        this.setTextureOn(new Texture("gates/OR_on.png"));
        this.setTexture(this.textureOff);
    }

    // construtor para porta OR com varias entradas
    public ORGate(String label, int numInputs, float scale) {
        super(label, numInputs, scale);
        this.gateType = "OR";
        this.width = 131f;
        this.height = 154f;
        this.setTextureOff(new Texture("gates/OR_off.png"));
        this.setTextureOn(new Texture("gates/OR_on.png"));
        this.setTexture(this.textureOff);
    }

    @Override
    public void compute() {
        // retorna verdadeiro se pelo menos uma entrada for verdadeira
        for (boolean input : inputs) {
            if (input) {
                this.output = true;
                this.setTexture(this.textureOn);
                return;
            }
        }
        this.setTexture(this.textureOff);
        this.output = false;
    }
}
