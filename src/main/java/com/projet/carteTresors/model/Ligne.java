package com.projet.carteTresors.model;

import com.projet.carteTresors.model.enumeration.EMouvement;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.enumeration.EOrientation;
import lombok.Data;

import java.util.List;

@Data
public class Ligne {

    private ETypeElement typeLigne;

    //Carte
    private int largeur;
    private int hauteur;

    // montagne & trésor && aventurier
    private Coordonnees coordonnees;

    //trésor
    private int nbTresors;

    // aventurier
    private String nom;
    private EOrientation orientation;
    private int nbTresorsObtenu;
    private List<EMouvement> mouvements;
}
