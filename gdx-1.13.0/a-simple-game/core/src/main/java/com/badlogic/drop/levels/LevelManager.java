package com.badlogic.drop.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

/**
 * Gerencia os níveis disponíveis no jogo
 */
public class LevelManager {
    private Array<Level> levels;
    private static LevelManager instance;
    private int currLevel;

    private LevelManager() {
        levels = new Array<>();
        loadLevels();
    }

    public static LevelManager getInstance() {
        if (instance == null) {
            instance = new LevelManager();
        }
        return instance;
    }



    /**
     * Carrega todos os níveis do jogo
     */
    private void loadLevels() {
        try {
            JSONtoCircuit converter = new JSONtoCircuit();
            levels = converter.convert(Gdx.files.internal("levels/levels.json"), true);
            Gdx.app.log("LevelManager", "Carregados " + levels.size + " níveis");

            // Sincroniza o estado dos níveis com o progresso salvo
            syncLevelsWithProgress();
        } catch (Exception e) {
            Gdx.app.error("LevelManager", "Erro ao carregar níveis", e);
            levels = new Array<>();
        }
    }

    /**
     * Sincroniza o estado dos níveis (estrelas, completed, unlocked) com o LevelProgress
     */
    private void syncLevelsWithProgress() {
        LevelProgress progress = LevelProgress.getInstance();

        for (int i = 0; i < levels.size; i++) {
            Level level = levels.get(i);
            if (level != null) {
                // Carrega estrelas salvas
                int savedStars = progress.getLevelStars(i);
                level.setStars(savedStars);

                // Atualiza estado de completado
                boolean completed = progress.isLevelCompleted(i);
                level.setCompleted(completed);

                // Atualiza estado de desbloqueado baseado no LevelProgress
                // O primeiro nível sempre está desbloqueado
                boolean unlocked = progress.isLevelUnlocked(i);
                level.setUnlocked(unlocked);

                Gdx.app.log("LevelManager", "Nivel " + i + ": unlocked=" + unlocked + ", completed=" + completed + ", stars=" + savedStars);
            }
        }
        Gdx.app.log("LevelManager", "Progresso sincronizado. Maior nível desbloqueado: " + progress.getHighestLevelUnlocked());
    }

    /**
     * Retorna todos os níveis
     */
    public Array<Level> getAllLevels() {
        return levels;
    }

    /**
     * Retorna um nível específico pelo ID
     */
    public Level getLevel(int id) {
        if (id >= 0 && id < levels.size) {
            return levels.get(id);
        }
        return null;
    }

    /**
     * Retorna o número total de níveis
     */
    public int getTotalLevels() {
        return levels.size;
    }

    public int getCurrLevelIdx() {
        return currLevel;
    }

    public int setCurrLevelIdx(int idx) {
        this.currLevel = idx;
        return this.currLevel;
    }
}
