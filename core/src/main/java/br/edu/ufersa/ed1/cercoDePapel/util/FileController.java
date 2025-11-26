package br.edu.ufersa.ed1.cercoDePapel.util;

import br.edu.ufersa.ed1.cercoDePapel.entities.SpellCard;
import br.edu.ufersa.ed1.cercoDePapel.entities.UnitCard;
import br.edu.ufersa.ed1.cercoDePapel.enums.Rarity;
import br.edu.ufersa.ed1.cercoDePapel.enums.SpellType;
import br.edu.ufersa.ed1.cercoDePapel.enums.UnitClass;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileController {
    private static final String pathUnits = "files/unidades.csv";
    private static final String pathSpells = "files/magias.csv";
    private static final String pathMap = "assets/maps/testMap.tmx";

    public static Set<UnitCard> readAllUnits() throws IOException{
        Set<UnitCard> unitSet = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathUnits))) {
            reader.readLine(); // Ignora cabeçalho

            String text;
            while ((text = reader.readLine()) != null) {
                if (text.trim().isEmpty()) continue;

                UnitCard newCard = readUnit(text);
                if (newCard != null) {
                    unitSet.add(newCard);
                }
            }
        }

        return unitSet;
    }
    public static UnitCard readUnit(String text){
        try {
            List<String> components = parseCSVLine(text);
            if (components.size() < 11) {
                System.err.println("Linha inválida: " + text);
                return null;
            }

            UnitCard unit = new UnitCard();

            unit.setId(components.get(0));
            unit.setRarity(Rarity.valueOf(components.get(1)));
            unit.setName(components.get(2));
            unit.setCost(Integer.parseInt(components.get(3)));
            unit.setDescription(components.get(4));
            unit.setUnitClass(UnitClass.valueOf(components.get(5)));
            unit.setDmgMin(Integer.parseInt(components.get(6)));
            unit.setDmgMax(Integer.parseInt(components.get(7)));
            unit.setHP(Integer.parseInt(components.get(8)));
            unit.setMov(Integer.parseInt(components.get(9)));
            unit.setRange(Integer.parseInt(components.get(10)));

            return unit;
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + text);
            System.err.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public static Set<SpellCard> readAllSpells() throws IOException{
        Set<SpellCard> spellSet = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(pathSpells))) {
            reader.readLine(); // Ignora cabeçalho

            String text;
            while ((text = reader.readLine()) != null) {
                if (text.trim().isEmpty()) continue;

                SpellCard newCard = readSpell(text);
                if (newCard != null) {
                    spellSet.add(newCard);
                }
            }
        }

        return spellSet;
    }
    public static SpellCard readSpell(String text){
        try {
            List<String> components = parseCSVLine(text);
            if (components.size() < 6) {
                System.err.println("Linha inválida: " + text);
                return null;
            }

            SpellCard spell = new SpellCard();

            spell.setId(components.get(0));
            spell.setRarity(Rarity.valueOf(components.get(1)));
            spell.setName(components.get(2));
            spell.setCost(Integer.parseInt(components.get(3)));
            spell.setDescription(components.get(4));
            spell.setSpellType(SpellType.valueOf(components.get(5)));

            return spell;
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + text);
            System.err.println("Erro: " + e.getMessage());
            return null;
        }
    }

    public static int[][] readMap() throws IOException {
        final int rows = 7;
        final int columns = 10;
        final int[][] map = new int[rows][columns];

        try (BufferedReader reader = new BufferedReader(new FileReader(pathMap))) {
            for (int i = 0; i < 5; i++) reader.readLine(); //pula as linhas de xml

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                if(line.endsWith(",")){
                    line = line.substring(0, line.length() - 1);
                }

                List<String> tiles = parseCSVLine(line);
                for (int j = 0; j < columns; j++){
                    map[i][j] = Integer.parseInt(tiles.get(j));
                }
            }
        }

        return map;
    }

    public static List<String> readAllLines(String path) throws IOException{
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))){
            String line;
            while ((line = reader.readLine()) != null){
                lines.add(line);
            }
        }
        return lines;
    }

    public static void writeMap(int[][] newMap) throws IOException{
        // Verifica se o mapa tem tamanho 7x10
        if (newMap.length != 7) {
            return;
        }
        for (int[] row : newMap) {
            if (row == null || row.length != 10) {
                return;
            }
        }

        String tempMapPath = pathMap + ".tmp";
        List<String> allLines = readAllLines(pathMap);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempMapPath))){
            for (int i = 0; i < 15; i++) {
                if (i < 5 || i >= 12) {
                    writer.write(allLines.get(i));
                }
                else {
                    writer.write(Arrays.toString(newMap[i - 5])
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "")
                        + ((i - 5) < 6 ? "," : ""));
                }
                writer.write("\n");
            }
        }

        Files.move(Path.of(tempMapPath), Path.of(pathMap), StandardCopyOption.REPLACE_EXISTING);
    }

    private static List<String> parseCSVLine(String text){
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString().trim());
                field.setLength(0); // limpa o StringBuilder
            } else {
                field.append(c);
            }
        }

        // Adiciona o último campo
        result.add(field.toString().trim());

        return result;
    }
}
