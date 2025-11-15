package com.badlogic.drop.levels;

import com.badlogic.drop.entities.Circuit;
import com.badlogic.drop.entities.CircuitBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JSONtoCircuit {
    /* 
     * Retorna um Objeto Circuit a partir de um arquivo Json
     */
    public Array<Level> convert(FileHandle file, boolean debug) {
        // array de niveis
        Array<Level> levelArr = new Array<>();
        //leitura do arquivo JSON
        //futuramente podemos fazert ele dar um fetch no repositorio online depois de upado para o itch.io
        String jsonStr = file.readString("UTF-8");
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(jsonStr);
        JsonValue levels = root.get("levels");
        Gdx.app.log("JSONtoCircuit.convert", "Niveis encontrados: " + levels.size);

        for (JsonValue level : levels){
            // novo circuito
            CircuitBuilder cir = new CircuitBuilder();
            int id = level.getInt("id");
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Circuito nivel: " + id);

            JsonValue inputsJson = level.get("inputs");
            for (JsonValue inputJson : inputsJson){
                String label = inputJson.getString("label");
                //boolean value = inputJson.getBoolean("value");
                //Gdx.app.log("JSONtoCircuit.convert", "Adicionando input: " + label + " com valor " + false);
                cir.addInput(label, false);
            }
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Inputs adicionados: " + inputsJson.size);

            JsonValue outputsJson = level.get("outputs");
            for (JsonValue outputJson : outputsJson){
                String label = outputJson.getString("label");
                cir.addOutput(label);
            }
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Outputs adicionados: " + outputsJson.size);
            
            JsonValue gatesJson = level.get("gates");
            for (JsonValue gateJson : gatesJson){
                String type = gateJson.getString("type");
                String label = gateJson.getString("label");
                switch (type) {
                    case "AND":
                        cir.addAND(label);
                        break;
                    case "OR":
                        cir.addOR(label);
                        break;
                    case "NOT":
                        cir.addNOT(label);
                        break;
                    case "NAND":
                        cir.addNAND(label);
                        break;
                    case "NOR":
                        cir.addNOR(label);
                        break;
                    case "XOR":
                        cir.addXOR(label);
                        break;
                    case "XNOR":
                        cir.addXNOR(label);
                        break;
                    default:
                        throw new IllegalArgumentException("Tipo de porta desconhecido: " + type);
                }

                JsonValue inputsObj = gateJson.get("inputs");
                if (inputsObj != null) {
                    for (JsonValue inputEntry : inputsObj) {
                        String indexName = inputEntry.name(); 
                        int inputIndex = Integer.parseInt(indexName);
                        String inputLabel = inputEntry.asString();
                        cir.connect(inputLabel, label, inputIndex);
                    }
                }
            }
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Gates adicionadas: " + gatesJson.size);
            
            for (JsonValue outs : outputsJson){
                String inputLabel = outs.getString("input");
                String outputLabel = outs.getString("label");
                cir.addOutput(outputLabel, inputLabel);
            }
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Outputs conectados: " + outputsJson.size);

            Circuit levelCir = cir.build(debug);
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Circuito do nivel " + id + " construido.");
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Numero de outputs esperados no circuito: " + levelCir.getExpectedOutput().size);

            // Carrega a solucao esperada (refatorar)
            JsonValue solutionJson = level.get("solution");
            for (JsonValue outputValue : solutionJson){
                String outputLabel = outputValue.name();
                if(debug) Gdx.app.log("JSONtoCircuit.convert", "Processando saida esperada: " + outputLabel);
                boolean value = outputValue.asBoolean();
                if(debug) Gdx.app.log("JSONtoCircuit.convert", "Definindo saida esperada: " + outputLabel + " = " + value);
                levelCir.setExpectedOutput(outputLabel, value);
                if(debug) Gdx.app.log("JSONtoCircuit.convert", "Saida esperada definida: " + outputLabel + " = " + value);
            }
            
            levelArr.add(new Level(id, levelCir));
            if(debug) Gdx.app.log("JSONtoCircuit.convert", "Circuito do nivel " + id + " adicionado à lista.");
        }
        return levelArr;
    }
}
