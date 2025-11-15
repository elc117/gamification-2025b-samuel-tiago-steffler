package com.badlogic.drop.levels;

import com.badlogic.drop.entities.Circuit;

/**
 * Representa um nível do jogo
 * Contém o circuito e a solução esperada
 */
public class Level {
    private final int id;
    private final Circuit circuit;
    private boolean completed;
    private boolean unlocked;
    private int stars = 0;
   
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
