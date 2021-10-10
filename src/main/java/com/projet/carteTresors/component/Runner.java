package com.projet.carteTresors.component;

import com.projet.carteTresors.model.CarteTresors;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.exception.CarteTresorException;
import com.projet.carteTresors.service.CarteService;
import com.projet.carteTresors.service.FileService;
import com.projet.carteTresors.service.LigneService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    private static FileService fileService;
    private static CarteService carteService;
    private static LigneService ligneService;
    private static String delimiteur;
    private static String fichierEntree;
    private static String fichierSorti;


    Logger logger = LoggerFactory.getLogger(Runner.class);


    public Runner(FileService fileService, CarteService carteService,
                  LigneService ligneService,
                  @Value(StringUtils.SPACE + "${carte.lignes.delimiteur}" + StringUtils.SPACE) String delimiteur,
                  @Value("${carte.fichier.input}") String fichierEntree,
                  @Value("${carte.fichier.output}") String fichierSorti) {

        Runner.fileService = fileService;
        Runner.carteService = carteService;
        Runner.ligneService = ligneService;
        Runner.delimiteur = delimiteur;
        Runner.fichierEntree = fichierEntree;
        Runner.fichierSorti = fichierSorti;
    }

    @Override
    public void run(String... args) {
        chercherTresor();
    }

    public void chercherTresor() {

        try {
            //lire le fichier en entree
            List<String> lignesOriginal = fileService.readFile(fichierEntree);

            System.out.println("Fichier en input : " + StringUtils.LF);
            lignesOriginal.forEach(System.out::println);

            //création de la carte au trésor
            CarteTresors carteTresors = creationCarteTresors(lignesOriginal);

            //générer le fichier de sortie
            fileService.createFile(carteTresors, fichierSorti, delimiteur);

        } catch (NoSuchFileException e) {
            logger.error("Fichier d'entrée impossible à localiser");
        } catch (IOException e) {
            logger.error("Erreur niveau fichier");
        }
        catch (CarteTresorException e) {
            logger.error(e.getMessage());
        }
    }

    public static CarteTresors creationCarteTresors(List<String> lignesOriginal) throws CarteTresorException {

        //récupèration de la liste des objets lignes
        List<Ligne> lignes = carteService.recuperationLigne(lignesOriginal, delimiteur);

        //creation de la carte
        String[][] carte = carteService.creerCarte(lignes);

        carte = carteService.genererPlaines(carte);
        System.out.println(StringUtils.LF + "Génération des plaines : " + StringUtils.LF);
        afficherCarte(carte);

        //génération des montagnes
        carte = carteService.generer(carte, ligneService.getLignesByTypeElement(lignes, ETypeElement.M));

        System.out.println(StringUtils.LF + "Génération des montagnes: " + StringUtils.LF);
        afficherCarte(carte);

        carte = carteService.generer(carte, ligneService.getLignesByTypeElement(lignes, ETypeElement.T));
        System.out.println(StringUtils.LF + "Génération des trésors : " + StringUtils.LF);
        afficherCarte(carte);

        carte = carteService.generer(carte, ligneService.getLignesByTypeElement(lignes, ETypeElement.A));
        System.out.println(StringUtils.LF + "Génération de l'aventurier : " + StringUtils.LF);
        afficherCarte(carte);

        carte = carteService.calculerMouvementAventurier(carte, lignes);

        System.out.println(StringUtils.LF + "Calcul de l'itinérarie de l'aventurier : " + StringUtils.LF);
        afficherCarte(carte);

        return new CarteTresors(lignes, carte);
    }

    public static void afficherCarte(String[][] carte) {
        for (String[] a : carte) {
            for (String i : a) {
                System.out.print(ligneService.affichageLigne(i));
            }
            System.out.print(StringUtils.LF);
        }
    }
}
