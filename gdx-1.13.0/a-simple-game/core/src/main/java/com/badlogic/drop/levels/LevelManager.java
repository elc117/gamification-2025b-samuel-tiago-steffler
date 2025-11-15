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
        } catch (Exception e) {
            Gdx.app.error("LevelManager", "Erro ao carregar níveis", e);
            levels = new Array<>();
        }
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
