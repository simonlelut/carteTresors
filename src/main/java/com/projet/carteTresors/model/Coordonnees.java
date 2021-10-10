package com.projet.carteTresors.model;

import lombok.Data;

@Data
public class Coordonnees {

    private int horizontale;
    private int verticale;

    public Coordonnees(int horizontale, int verticale) {
        this.horizontale = horizontale;

        this.verticale = verticale;
    }

}
