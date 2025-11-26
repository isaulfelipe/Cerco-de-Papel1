package br.edu.ufersa.ed1.cercoDePapel.enums;

import java.awt.*;

public enum Rarity {
    COMMON (Color.GREEN, "Comum"),
    RARE (Color.BLUE, "Rara"),
    EPIC (Color.MAGENTA, "Épica"),
    LEGENDARY (Color.YELLOW, "Lendária");

    private Color color;
    private String description;

    private Rarity(Color color, String description){
        this.color = color;
        this.description = description;
    }

    public Color getColor(){
        return color;
    }
    public String getDescription(){
        return description;
    }
}
