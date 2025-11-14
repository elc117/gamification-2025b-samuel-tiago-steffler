package com.badlogic.drop.levels;

import com.badlogic.drop.entities.Circuit;

/**
 * Representa um nível do jogo
 * Contém o circuito e a solução esperada
 */
public class Level {
    private final int id;
    private final Circuit circuit;
   
    public Level(int id, Circuit circuit) {
        this.id = id;
        this.circuit = circuit;
    }

    public int getId() {
        return id;
    }

    public Circuit getCircuit() {
        return circuit;
    }
}
