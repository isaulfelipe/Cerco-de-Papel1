package br.edu.ufersa.ed1.cercoDePapel.enums;

public enum CardType {
    UNIT ("Unidade"),
    SPELL ("Magia");

    private String description;

    CardType(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
