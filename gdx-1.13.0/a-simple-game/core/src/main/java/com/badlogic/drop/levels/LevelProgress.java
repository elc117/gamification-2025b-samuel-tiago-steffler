package com.badlogic.drop.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Gerencia o progresso do jogador nos níveis
 * Salva e carrega quais níveis já foram completados
 */
public class LevelProgress {
    private static final String PREFS_NAME = "bitit_progress";
    private static final String LEVEL_COMPLETED_PREFIX = "level_";
    private static final String HIGHEST_LEVEL_UNLOCKED = "highest_unlocked";
    
    private Preferences prefs;
    private static LevelProgress instance;
    
    private LevelProgress() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }
    
    public static LevelProgress getInstance() {
        if (instance == null) {
            instance = new LevelProgress();
        }
        return instance;
    }
    
    /**
     * Marca um nível como completado
     */
    public void setLevelCompleted(int levelId, boolean completed) {
        prefs.putBoolean(LEVEL_COMPLETED_PREFIX + levelId, completed);
        
        // Atualiza o maior nível desbloqueado
        if (completed) {
            int currentHighest = getHighestLevelUnlocked();
            if (levelId >= currentHighest) {
                prefs.putInteger(HIGHEST_LEVEL_UNLOCKED, levelId + 1);
            }
        }
        
        prefs.flush();
    }
    
    /**
     * Verifica se um nível foi completado
     */
    public boolean isLevelCompleted(int levelId) {
        return prefs.getBoolean(LEVEL_COMPLETED_PREFIX + levelId, false);
    }
    
    /**
     * Verifica se um nível está desbloqueado
     * O nível 0 sempre está desbloqueado
     */
    public boolean isLevelUnlocked(int levelId) {
        if (levelId == 0) return true;
        return levelId <= getHighestLevelUnlocked();
    }
    
    /**
     * Retorna o maior nível desbloqueado
     */
    public int getHighestLevelUnlocked() {
        return prefs.getInteger(HIGHEST_LEVEL_UNLOCKED, 0);
    }
    
    /**
     * Reseta todo o progresso (útil para debug)
     */
    public void resetProgress() {
        prefs.clear();
        prefs.flush();
    }
    
    /**
     * Desbloqueia todos os níveis (útil para debug/teste)
     */
    public void unlockAllLevels(int totalLevels) {
        prefs.putInteger(HIGHEST_LEVEL_UNLOCKED, totalLevels - 1);
        prefs.flush();
    }
}
