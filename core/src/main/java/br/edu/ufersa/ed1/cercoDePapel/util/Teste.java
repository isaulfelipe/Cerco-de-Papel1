package br.edu.ufersa.ed1.cercoDePapel.util;

import br.edu.ufersa.ed1.cercoDePapel.entities.Card;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Teste {
    public static final Set<Card> cards = new LinkedHashSet<>();

    public static void main(String[] args) {
        try {
            int[][] currentMap = FileController.readMap();
            System.out.println(Arrays.deepToString(currentMap));
            FileController.writeMap(currentMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
