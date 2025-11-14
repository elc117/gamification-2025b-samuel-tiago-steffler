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
    public Array<Level> convert(FileHandle file) {
        Array<Level> levelArr = new Array<>();
        String jsonStr = file.readString("UTF-8");
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(jsonStr);
        JsonValue levels = root.get("levels");
        Gdx.app.log("JSONtoCircuit.convert", "Niveis encontrados: " + levels.size);
        for (JsonValue level : levels){
            CircuitBuilder cir = new CircuitBuilder();
            int id = level.getInt("id");
            Gdx.app.log("JSONtoCircuit.convert", "Nivel: " + id);
            JsonValue inputsJson = level.get("inputs");
            for (JsonValue inputJson : inputsJson){
                String label = inputJson.getString("label");
                //boolean value = inputJson.getBoolean("value");
                Gdx.app.log("JSONtoCircuit.convert", "Adicionando input: " + label + " com valor " + false);
                cir.addInput(label, false);
            }
            Gdx.app.log("JSONtoCircuit.convert", "Inputs adicionados: " + inputsJson.size);

            JsonValue outputsJson = level.get("outputs");
            for (JsonValue outputJson : outputsJson){
                String label = outputJson.getString("label");
                cir.addOutput(label);
            }
            Gdx.app.log("JSONtoCircuit.convert", "Outputs adicionados: " + outputsJson.size);
            
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
            Gdx.app.log("JSONtoCircuit.convert", "Gates adicionadas: " + gatesJson.size);
            
            for (JsonValue outs : outputsJson){
                String inputLabel = outs.getString("input");
                String outputLabel = outs.getString("label");
                cir.addOutput(outputLabel, inputLabel);
            }
            Gdx.app.log("JSONtoCircuit.convert", "Outputs conectados: " + outputsJson.size);

            Circuit levelCir = cir.build();
            Gdx.app.log("JSONtoCircuit.convert", "Circuito do nivel " + id + " construido.");
            /*
            // Carrega a solucao esperada
            JsonValue solutionJson = level.get("solution");
            JsonValue valuesJson = solutionJson.get("values");
            for (JsonValue outputValue : valuesJson){
                String outputLabel = outputValue.name();
                boolean value = outputValue.asBoolean();
                levelCir.setExpectedOutput(outputLabel, value);
                Gdx.app.log("JSONtoCircuit.convert", "Saida esperada definida: " + outputLabel + " = " + value);
            }*/
            
            levelArr.add(new Level(id, levelCir));
            Gdx.app.log("JSONtoCircuit.convert", "Circuito do nivel " + id + " adicionado à lista.");
        }
        return levelArr;
    }
}
