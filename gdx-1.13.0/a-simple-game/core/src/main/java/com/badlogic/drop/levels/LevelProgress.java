package com.badlogic.drop.levels;

import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia o progresso do jogador nos níveis
 * Nao ha salvamento persistente, apenas durante execucao
 */
public class LevelProgress {
    private static LevelProgress instance;

    private Map<Integer, Boolean> completedLevels;
    private Map<Integer, Integer> levelStars;
    private int highestLevelUnlocked;

    private LevelProgress() {
        completedLevels = new HashMap<>();
        levelStars = new HashMap<>();
        highestLevelUnlocked = 0;
        Gdx.app.log("LevelProgress", "Progresso iniciado em memória (não persistente)");
    }

    public static LevelProgress getInstance() {
        if (instance == null) {
            instance = new LevelProgress();
        }
        return instance;
    }

    /**
     * Marca nivel como completado
     */
    public void setLevelCompleted(int levelId, boolean completed) {
        completedLevels.put(levelId, completed);

        // Atualiza o maior nivel desbloqueado
        if (completed) {
            if (levelId >= highestLevelUnlocked) {
                highestLevelUnlocked = levelId + 1;
                Gdx.app.log("LevelProgress", "Nível " + (levelId + 1) + " desbloqueado!");
            }
        }
    }

    /**
     * Verifica se um nivel foi completado
     */
    public boolean isLevelCompleted(int levelId) {
        return completedLevels.getOrDefault(levelId, false);
    }

    /**
     * Verifica se um nivel está desbloqueado
     */
    public boolean isLevelUnlocked(int levelId) {
        if (levelId == 0) return true;
        return levelId <= highestLevelUnlocked;
    }

    /**
     * Retorna o maior nivel desbloqueado
     */
    public int getHighestLevelUnlocked() {
        return highestLevelUnlocked;
    }

    /**
     * Desbloqueia todos os niveis (debug)
     */
    public void unlockAllLevels(int totalLevels) {
        highestLevelUnlocked = totalLevels - 1;
        Gdx.app.log("LevelProgress", "Todos os " + totalLevels + " níveis desbloqueados");
    }

    /**
     * Salva o número de estrelas de um nível apenas se for maior que o valor anterior
     * @param levelId ID do nível
     * @param stars Número de estrelas obtidas (1-3)
     * @return true se a pontuação foi atualizada (nova pontuação é melhor)
     */
    public boolean setLevelStars(int levelId, int stars) {
        int currentStars = getLevelStars(levelId);
        if (stars > currentStars) {
            levelStars.put(levelId, stars);
            return true;
        }
        return false;
    }

    /**
     * Retorna o número de estrelas de um nível
     * @param levelId ID do nível
     * @return Número de estrelas (0-3)
     */
    public int getLevelStars(int levelId) {
        return levelStars.getOrDefault(levelId, 0);
    }
}
