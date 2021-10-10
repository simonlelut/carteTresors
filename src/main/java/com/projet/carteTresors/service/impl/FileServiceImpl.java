package com.projet.carteTresors.service.impl;

import com.projet.carteTresors.model.CarteTresors;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.service.FileService;

import com.projet.carteTresors.service.LigneService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {


    @Autowired
    private LigneService ligneService;

    @Override
    public List<String> readFile(String fichier) throws IOException {
        List<String> lignes;

        try (Stream<String> stream = Files.lines(Paths.get(fichier))) {
            lignes = stream.collect(Collectors.toList());
        }

        return lignes;
    }

    @Override
    public void createFile(CarteTresors carteTresor, String fichierSorti, String delimiteur) throws IOException {

        FileWriter myWriter = new FileWriter(fichierSorti);

        for (Ligne ligne : carteTresor.getLignes()) {
            if (!ligne.getTypeLigne().equals(ETypeElement.T) || (ligne.getTypeLigne().equals(ETypeElement.T) && ligne.getNbTresors() > 0)) {
                myWriter.write(getLigne(ligne, delimiteur));
                myWriter.write(StringUtils.LF);
            }
        }

        myWriter.write(StringUtils.LF);
        myWriter.write("Que l’on peut représenter sous la forme suivante :");
        myWriter.write(StringUtils.LF + StringUtils.LF);

        for (String[] a : carteTresor.getCarte()) {
            for (String i : a) {
                myWriter.write(ligneService.affichageLigne(i));
            }
            myWriter.write(StringUtils.LF);
        }

        myWriter.close();

    }


    private String getLigne(Ligne ligne, String delimiteur) {

        return switch (ligne.getTypeLigne()) {
            case C -> ETypeElement.C.name() + delimiteur + ligne.getLargeur() + delimiteur + ligne.getHauteur();

            case M -> ETypeElement.M.name() + delimiteur + ligne.getCoordonnees().getHorizontale() + delimiteur +
                    ligne.getCoordonnees().getVerticale();

            case T -> ETypeElement.T.name() + delimiteur + ligne.getCoordonnees().getHorizontale() + delimiteur +
                    ligne.getCoordonnees().getVerticale() + delimiteur + ligne.getNbTresors();

            case A -> ETypeElement.A.name() + delimiteur + ligne.getNom() + delimiteur + ligne.getCoordonnees().getHorizontale() +
                    delimiteur + ligne.getCoordonnees().getVerticale() + delimiteur + ligne.getOrientation() + delimiteur +
                    ligne.getNbTresorsObtenu();
        };
    }

}
