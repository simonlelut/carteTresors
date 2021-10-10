package com.projet.carteTresors.service;

import com.projet.carteTresors.model.CarteTresors;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.exception.CarteTresorException;

import java.util.List;

public interface CarteService {

    List<Ligne> recuperationLigne(List<String> lignes, String delimiteur) throws CarteTresorException;

    String[][] creerCarte(List<Ligne> lignes) throws CarteTresorException;

    String[][] calculerMouvementAventurier(String[][] carte, List<Ligne> lignes) throws CarteTresorException;

    String[][] generer(String[][] carte, List<Ligne> lignesByTypeElement) throws CarteTresorException;

    String[][] genererPlaines(String[][] carte);
}
