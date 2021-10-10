package com.projet.carteTresors.service.impl;

import com.projet.carteTresors.model.Coordonnees;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.EMouvement;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.enumeration.EOrientation;
import com.projet.carteTresors.model.exception.CarteTresorException;
import com.projet.carteTresors.service.LigneService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LigneServiceImpl implements LigneService {

    @Override
    public Ligne creerCarte(List<String> ligneString) {
        Ligne carte = new Ligne();

        carte.setTypeLigne(ETypeElement.C);

        carte.setLargeur(Integer.parseInt(ligneString.get(1)));
        carte.setHauteur(Integer.parseInt(ligneString.get(2)));

        return carte;
    }

    @Override
    public Ligne creerMontagne(List<String> ligneString) {
        Ligne montagne = new Ligne();

        montagne.setTypeLigne(ETypeElement.M);

        montagne.setCoordonnees(
                new Coordonnees(Integer.parseInt(ligneString.get(1)), Integer.parseInt(ligneString.get(2)))
        );

        return montagne;
    }

    @Override
    public Ligne creerTresor(List<String> ligneString) {
        Ligne tresor = new Ligne();

        tresor.setTypeLigne(ETypeElement.T);

        tresor.setCoordonnees(
                new Coordonnees(Integer.parseInt(ligneString.get(1)), Integer.parseInt(ligneString.get(2)))
        );

        tresor.setNbTresors(Integer.parseInt(ligneString.get(3)));

        return tresor;
    }

    @Override
    public Ligne creerAventurier(List<String> ligneString) {
        Ligne aventurier = new Ligne();

        aventurier.setTypeLigne(ETypeElement.A);
        aventurier.setNom(ligneString.get(1));

        aventurier.setCoordonnees(
                new Coordonnees(Integer.parseInt(ligneString.get(2)), Integer.parseInt(ligneString.get(3)))
        );

        aventurier.setOrientation(EOrientation.valueOf(ligneString.get(4)));
        aventurier.setMouvements(getSequence(ligneString.get(5)));

        aventurier.setNbTresors(0);

        return aventurier;
    }

    @Override
    public List<Ligne> getLignesByTypeElement(List<Ligne> lignes, ETypeElement typeElement) {
        return lignes.stream().filter(l -> l.getTypeLigne().equals(typeElement)).collect(Collectors.toList());
    }

    @Override
    public void verifLigne(Ligne ligne) throws CarteTresorException {

        boolean isOk = true;

        if(ligne.getTypeLigne().equals(ETypeElement.C) && (ligne.getLargeur() <= 0 || ligne.getHauteur() <= 0)){
            isOk = false;
        }

        if(ligne.getTypeLigne().equals(ETypeElement.T) && ligne.getNbTresors() <= 0){
            isOk = false;
        }

        if(null != ligne.getCoordonnees() && (ligne.getCoordonnees().getVerticale() < 0 || ligne.getCoordonnees().getHorizontale() < 0)){
            isOk = false;
        }

        if(!isOk)
            throw new CarteTresorException(null);
    }

    @Override
    public String affichageLigne(String i) {
        return i + " ".repeat(12 - i.length() > 0 ? 10 - i.length() : 1);
    }

    private List<EMouvement> getSequence(String sequences) {

        return Arrays.stream(sequences.split(""))
                .map(EMouvement::valueOf)
                .collect(Collectors.toList());
    }


}
