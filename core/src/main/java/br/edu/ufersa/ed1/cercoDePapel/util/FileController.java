package br.edu.ufersa.ed1.cercoDePapel.util;

import br.edu.ufersa.ed1.cercoDePapel.entities.SpellCard;
import br.edu.ufersa.ed1.cercoDePapel.entities.UnitCard;
import br.edu.ufersa.ed1.cercoDePapel.enums.Rarity;
import br.edu.ufersa.ed1.cercoDePapel.enums.SpellType;
import br.edu.ufersa.ed1.cercoDePapel.enums.UnitClass;
import com.badlogic.gdx.Gdx; // IMPORTANTE: Importar Gdx

import java.io.*;
import java.util.*;

public class FileController {
    // Caminhos ajustados para usar Gdx.files.internal (dentro de assets)
    private static final String pathUnits = "files/unidades.csv";
    private static final String pathSpells = "files/magias.csv";
    private static final String pathMap = "maps/testMap.tmx";

    public static Set<UnitCard> readAllUnits() throws IOException {
        Set<UnitCard> unitSet = new LinkedHashSet<>();

        // CORREÇÃO: Usando Gdx.files.internal para ler de dentro do assets/jar
        try (BufferedReader reader = new BufferedReader(Gdx.files.internal(pathUnits).reader())) {
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
            // Ajuste de validação se necessário
            if (components.size() < 11) {
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
            System.err.println("Erro ao ler unidade: " + text + " -> " + e.getMessage());
            return null;
        }
    }

    public static Set<SpellCard> readAllSpells() throws IOException {
        Set<SpellCard> spellSet = new LinkedHashSet<>();

        try (BufferedReader reader = new BufferedReader(Gdx.files.internal(pathSpells).reader())) {
            reader.readLine();

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
            if (components.size() < 6) return null;

            SpellCard spell = new SpellCard();
            spell.setId(components.get(0));
            spell.setRarity(Rarity.valueOf(components.get(1)));
            spell.setName(components.get(2));
            spell.setCost(Integer.parseInt(components.get(3)));
            spell.setDescription(components.get(4));
            spell.setSpellType(SpellType.valueOf(components.get(5)));

            return spell;
        } catch (Exception e) {
            System.err.println("Erro ao ler magia: " + text);
            return null;
        }
    }

    public static int[][] readMap() throws IOException {
        final int rows = 7;
        final int columns = 10;
        final int[][] map = new int[rows][columns];

        try (BufferedReader reader = new BufferedReader(Gdx.files.internal(pathMap).reader())) {
            for (int i = 0; i < 5; i++) reader.readLine();

            for (int i = 0; i < rows; i++) {
                String line = reader.readLine();
                if(line != null && line.endsWith(",")){
                    line = line.substring(0, line.length() - 1);
                }
                if (line != null) {
                    List<String> tiles = parseCSVLine(line);
                    for (int j = 0; j < columns && j < tiles.size(); j++){
                        map[i][j] = Integer.parseInt(tiles.get(j));
                    }
                }
            }
        }
        return map;
    }

    // Método para salvar mapa (Atenção: Gdx.files.internal é somente leitura em JARs.
    // Para salvar, use Gdx.files.local, mas requer mudança de lógica se for rodar compilado)
    public static void writeMap(int[][] newMap) {
        System.out.println("A escrita de mapas não é suportada diretamente em assets internos em tempo de execução.");
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
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString().trim());
        return result;
    }
}
