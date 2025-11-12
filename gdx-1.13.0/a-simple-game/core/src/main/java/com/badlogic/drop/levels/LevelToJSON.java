package com.badlogic.drop.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LevelToJSON {

    /*
     * Estrutura das gates / portas logicas
     */
    private static class Gate {
        public String label;
        public String type;
        public ObjectMap<String, String> inputs;

        public Gate(String label, String type, ObjectMap<String, String> inputs) {
            this.label = label;
            this.type = type;
            this.inputs = inputs;
        }
    }

    /*
     * Estrutura dos inputs / bits de entrada
     */
    private static class Input {
        public String label;

        public Input(String label) {
            this.label = label;
        }
    }

    /*
     * Estrutura dos outputs / bits de saida
     */
    private static class Output {
        public String label;
        public String input;

        public Output(String label, String input) {
            this.label = label;
            this.input = input;
        }
    }

    /*
     * Estrutura do nivel completo
     */
    private static class Level {
        public int id;
        public List<Input> inputs;
        public List<Gate> gates;
        public List<Output> outputs;

        public Level(int id, List<Input> inputs, List<Gate> gates, List<Output> outputs) {
            this.id = id;
            this.inputs = inputs;
            this.gates = gates;
            this.outputs = outputs;
        }
    }

    /*
     * Estrutura dos niveis completos
     */
    private static class LevelsData {
        public List<Level> levels;

        public LevelsData(List<Level> levels) {
            this.levels = levels;
        }
    }

    // variaveis auxiliares
    private int gateCounter;
    private Map<String, String> expressionToGateLabel;
    private Set<String> allInputs;
    private List<Gate> gates;

    public LevelToJSON() {
        this.gateCounter = 0;
        this.expressionToGateLabel = new HashMap<>();
        this.allInputs = new TreeSet<>();
        this.gates = new ArrayList<>();
    }

    /**
     * Converte o arquivo de entrada (levels.txt) para o formato JSON
     */
    public void convertLevelsToJSON(String inputPath, String outputPath) {
        FileHandle inputFile = Gdx.files.internal(inputPath);
        String content = inputFile.readString();

        List<Level> levels = parseLevels(content);
        LevelsData levelsData = new LevelsData(levels);

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false); // Nao inclui informacoes de classe/tipo
        String jsonString = json.prettyPrint(levelsData);

        FileHandle outputFile = Gdx.files.local(outputPath);
        outputFile.writeString(jsonString, false);

        System.out.println("Conversão concluída: " + outputPath);
    }

    /**
     * Faz o parsing de todas as linhas do arquivo
     */
    private List<Level> parseLevels(String content) {
        List<Level> levels = new ArrayList<>();
        // Normaliza quebras de linha (Windows/Unix/Mac)
        String[] lines = content.split("\\r?\\n");

        int levelId = 1;

        for (String line : lines) {
            line = line.trim();

            // Ignora linhas vazias
            if (line.isEmpty()) {
                continue;
            }

            gateCounter = 0;
            expressionToGateLabel = new HashMap<>();
            allInputs = new TreeSet<>();
            gates = new ArrayList<>();

            // Tokenizacao por ponto e virgula (separa circuitos/saidas)
            String[] circuits = line.split(";");

            List<String> outputGateLabels = new ArrayList<>();

            // Processa cada circuito
            for (String circuit : circuits) {
                circuit = circuit.trim();
                String gateLabel = parseExpression(circuit);
                outputGateLabels.add(gateLabel);
            }

            // Cria os inputs
            List<Input> inputs = new ArrayList<>();
            for (String inputLabel : allInputs) {
                inputs.add(new Input(inputLabel));
            }

            // Cria os outputs
            List<Output> outputs = new ArrayList<>();
            for (int i = 0; i < outputGateLabels.size(); i++) {
                outputs.add(new Output("X" + i, outputGateLabels.get(i)));
            }

            levels.add(new Level(levelId++, inputs, new ArrayList<>(gates), outputs));
        }

        return levels;
    }

    /**
     * Faz o parsing recursivo de uma expressao
     * Retorna o label da gate que representa essa expressao
     */
    private String parseExpression(String expr) {
        expr = expr.trim();

        // Retorna o label existente se ja processada
        if (expressionToGateLabel.containsKey(expr)) {
            return expressionToGateLabel.get(expr);
        }

        // Verifica se eh um input direto (I0, I1, I2, ...)
        if (expr.matches("I\\d+")) {
            allInputs.add(expr);
            return expr;
        }

        // Extrai o tipo da gate e seus argumentos
        Pattern pattern = Pattern.compile("^(\\w+)\\((.+)\\)$");
        Matcher matcher = pattern.matcher(expr);

        if (!matcher.matches()) {
            throw new RuntimeException("Expressao invalida: " + expr);
        }

        String gateType = matcher.group(1);
        String argsString = matcher.group(2);

        // Separa os argumentos
        List<String> args = splitArguments(argsString);

        // Processa recursivamente cada argumento
        ObjectMap<String, String> gateInputs = new ObjectMap<>();
        for (int i = 0; i < args.size(); i++) {
            String argLabel = parseExpression(args.get(i));
            gateInputs.put(String.valueOf(i), argLabel);
        }

        // Cria a gate
        String gateLabel = String.valueOf(gateCounter++);
        Gate gate = new Gate(gateLabel, gateType, gateInputs);
        gates.add(gate);

        // Guarda o mapeamento
        expressionToGateLabel.put(expr, gateLabel);

        return gateLabel;
    }

    /**
     * Separa os argumentos de uma expressao respeitando procedencia
     */
    private List<String> splitArguments(String argsString) {
        List<String> args = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (char c : argsString.toCharArray()) {
            if (c == '(') {
                depth++;
                current.append(c);
            } else if (c == ')') {
                depth--;
                current.append(c);
            } else if (c == ',' && depth == 0) {
                args.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            args.add(current.toString().trim());
        }

        return args;
    }

    /**
     * Versao standalone que nao depende do contexto libGDX
     * Usa Java IO padrao para ler e escrever arquivos
     */
    public void convertLevelsToJSONStandalone(String inputPath, String outputPath) throws IOException {
        // Le o arquivo de entrada
        String content = new String(Files.readAllBytes(Paths.get(inputPath)));

        List<Level> levels = parseLevels(content);
        LevelsData levelsData = new LevelsData(levels);

        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false); // Nao inclui informacoes de classe/tipo
        String jsonString = json.prettyPrint(levelsData);

        // Escreve o arquivo de saida
        Files.write(Paths.get(outputPath), jsonString.getBytes());

        System.out.println("Conversao concluida!");
        System.out.println("Arquivo de entrada: " + inputPath);
        System.out.println("Arquivo de saida: " + outputPath);
        System.out.println("Total de niveis: " + levels.size());
    }

    /**
     * Metodo main para teste standalone
     */
    public static void main(String[] args) {
        try {
            LevelToJSON converter = new LevelToJSON();

            // Define os caminhos dos arquivos
            String projectRoot = System.getProperty("user.dir");

            // Se estamos em core/, sobe um nivel para a raiz do projeto
            if (projectRoot.endsWith("core")) {
                projectRoot = projectRoot.substring(0, projectRoot.length() - 5);
            }

            String inputPath = projectRoot + "/assets/levels/levels.txt";
            String outputPath = projectRoot + "/assets/levels/levels.json";

            System.out.println("=== Conversor de Niveis TXT para JSON ===");
            System.out.println("Diretorio de trabalho: " + projectRoot);
            System.out.println();

            converter.convertLevelsToJSONStandalone(inputPath, outputPath);

        } catch (Exception e) {
            System.err.println("Erro durante a conversao:");
            e.printStackTrace();
        }
    }
}
