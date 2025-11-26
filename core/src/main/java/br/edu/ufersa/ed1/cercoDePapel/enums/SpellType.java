package br.edu.ufersa.ed1.cercoDePapel.enums;

public enum SpellType {
    DAMAGE ("Dano"),
    HEAL ("Cura"),
    BUFF ("Buff"),
    DEBUFF ("Debuff"),
    UTILITY ("Utilidade"),
    CONTROL ("Controle");

    private String description;

    SpellType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
