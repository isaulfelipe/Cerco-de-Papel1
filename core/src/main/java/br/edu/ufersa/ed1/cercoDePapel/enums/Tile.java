package br.edu.ufersa.ed1.cercoDePapel.enums;

public enum Tile{
    BLANK (1, "Espaço em branco"),
    FOREST (2, "Espaço de floresta"),
    CAVE (3, "Espaço de caverna"),
    LAKE (4, "Espaço de lago"),
    FIELD (5, "Espaço de campo"),
    MOUNTAINS (6, "Espaço de montanha"),
    CITY (7, "Espaço de cidade"),
    FARM (8, "Espaço de fazenda"),
    RUINS (9, "Espaço de ruínas"),
    CASTLE (10, "Espaço de castelo"),
    VILLAGE (11, "Espaço de vila");

    int value;
    String description;

    Tile(int value, String description){
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }
    public String getDescription() {
        return description;
    }
}
