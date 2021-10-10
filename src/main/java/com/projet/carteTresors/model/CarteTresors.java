package com.projet.carteTresors.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CarteTresors {

    private List<Ligne> lignes;

    private String[][] carte;
}
