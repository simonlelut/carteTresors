package com.projet.carteTresors.service;

import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.exception.CarteTresorException;

import java.util.List;

public interface LigneService {

    Ligne creerCarte(List<String> ligneString);

    Ligne creerMontagne(List<String> ligneString);

    Ligne creerTresor(List<String> ligneString);

    Ligne creerAventurier(List<String> ligneString);

    List<Ligne> getLignesByTypeElement(List<Ligne> lignes, ETypeElement typeElement) ;

    void verifLigne(Ligne ligne) throws CarteTresorException;

    String affichageLigne(String item);
}
