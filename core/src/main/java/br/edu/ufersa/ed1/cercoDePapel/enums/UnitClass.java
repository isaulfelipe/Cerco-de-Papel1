package br.edu.ufersa.ed1.cercoDePapel.enums;

public enum UnitClass {
    MELEE ("Corpo-a-corpo"),
    TANK ("Tanque"),
    ARCHER ("Arqueiro"),
    MAGE ("Mago"),
    SUPPORT ("Suporte"),
    SPECIAL ("Especial");

    private String description;

    UnitClass(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
