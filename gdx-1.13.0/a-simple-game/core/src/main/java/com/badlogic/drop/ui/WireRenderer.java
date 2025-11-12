package com.badlogic.drop.ui;

import com.badlogic.drop.entities.Wire;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

// Renderizador dedicado para desenhar fios conectando portas logicas
// Usa ShapeRenderer do libGDX para desenhar linhas conectando os pontos do caminho
public class WireRenderer {

    private final ShapeRenderer shapeRenderer;
    private final boolean ownsRenderer; // se este renderer criou o ShapeRenderer

    // Cria um WireRenderer com seu proprio ShapeRenderer
    public WireRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.ownsRenderer = true;
    }

    // Cria um WireRenderer usando um ShapeRenderer existente (compartilhado)
    public WireRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.ownsRenderer = false;
    }

    // Renderiza um unico fio
    // - wire: fio a ser desenhado
    // - autoBeginEnd: se true, chama begin() e end() automaticamente
    public void render(Wire wire, boolean autoBeginEnd) {
        if (wire == null) return;

        Array<Vector2> points = wire.getPathPoints();
        if (points.size < 2) return;

        if (autoBeginEnd) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        }

        Color color = wire.getCurrentColor();
        shapeRenderer.setColor(color);

        float lineWidth = wire.getLineWidth();

        // Desenha segmentos conectando os pontos
        for (int i = 0; i < points.size - 1; i++) {
            Vector2 start = points.get(i);
            Vector2 end = points.get(i + 1);

            // Desenha linha grossa usando retangulo
            drawThickLine(start.x, start.y, end.x, end.y, lineWidth);
        }

        if (autoBeginEnd) {
            shapeRenderer.end();
        }
    }

    //Renderiza multiplos fios
    // - wires: array de fios a serem desenhados
    public void renderAll(Array<Wire> wires) {
        if (wires == null || wires.size == 0) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (Wire wire : wires) {
            render(wire, false); // false porque ja chamamos begin()
        }

        shapeRenderer.end();
    }

    // Desenha uma linha grossa entre dois pontos com um retangulo
    private void drawThickLine(float x1, float y1, float x2, float y2, float thickness) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) return;

        // Desenha retangulo representando a linha
        shapeRenderer.rect(x1, y1, 0, 0, length, thickness, 1, 1,
                          (float) Math.toDegrees(Math.atan2(dy, dx)));
    }


    // Versao alternativa usando linha simples do ShapeRenderer
    // Mais performatico mas menos controle sobre aparencia
    public void renderSimple(Wire wire, boolean autoBeginEnd) {
        if (wire == null) return;

        Array<Vector2> points = wire.getPathPoints();
        if (points.size < 2) return;

        if (autoBeginEnd) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }

        Color color = wire.getCurrentColor();
        shapeRenderer.setColor(color);

        // Configura espessura (disponível no libGDX mas depende do driver)
        // shapeRenderer.set...() // LibGDX nao tem controle direto de espessura

        // Desenha segmentos conectando os pontos
        for (int i = 0; i < points.size - 1; i++) {
            Vector2 start = points.get(i);
            Vector2 end = points.get(i + 1);
            shapeRenderer.line(start.x, start.y, end.x, end.y);
        }

        if (autoBeginEnd) {
            shapeRenderer.end();
        }
    }

    // Renderiza fio com pontos de debug
    public void renderDebug(Wire wire) {
        Array<Vector2> points = wire.getPathPoints();
        if (points.size == 0) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Desenha os segmentos do fio
        render(wire, false);

        // Desenha pontos de controle como círculos
        shapeRenderer.setColor(Color.RED);
        for (Vector2 point : points) {
            shapeRenderer.circle(point.x, point.y, 3);
        }

        shapeRenderer.end();
    }


    // Libera recursos (so se este renderer criou o ShapeRenderer)
    public void dispose() {
        if (ownsRenderer && shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }
}
