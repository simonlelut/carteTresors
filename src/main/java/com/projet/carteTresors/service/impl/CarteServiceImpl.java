package com.projet.carteTresors.service.impl;


import com.projet.carteTresors.model.Coordonnees;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.EMouvement;
import com.projet.carteTresors.model.enumeration.EOrientation;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.exception.CarteTresorException;
import com.projet.carteTresors.service.LigneService;
import com.projet.carteTresors.service.MapperService;
import com.projet.carteTresors.service.CarteService;
import org.springframework.stereotype.Service;



import java.util.*;
import java.util.stream.IntStream;

@Service
public class CarteServiceImpl implements CarteService {


    private final MapperService mapperService;
    private final LigneService ligneService;

    public CarteServiceImpl(MapperService mapperService, LigneService ligneService) {
        this.mapperService = mapperService;
        this.ligneService = ligneService;
    }

    @Override
    public List<Ligne> recuperationLigne(List<String> lignesOriginal, String delimiteur) throws CarteTresorException {
        List<Ligne> lignes = new ArrayList<>();

        for (String ligneOriginal : lignesOriginal) {
            Ligne ligne = mapperService.ligneStringToLigneObject(List.of(ligneOriginal.split(delimiteur)));
            if (ligne != null) {
                lignes.add(ligne);
            }
        }

        if(lignes.isEmpty())
            throw new CarteTresorException("Aucune lignes identifiée dans le fichier !");

        return lignes;
    }


    @Override
    public String[][] creerCarte(List<Ligne> lignes) throws CarteTresorException {

        List<Ligne> carte = ligneService.getLignesByTypeElement(lignes, ETypeElement.C);

        return new String[carte.get(0).getHauteur()][carte.get(0).getLargeur()];
    }

    @Override
    public String[][] calculerMouvementAventurier(String[][] carte, List<Ligne> lignes) throws CarteTresorException {

        Ligne ligne = ligneService.getLignesByTypeElement(lignes, ETypeElement.A).stream()
                .max(Comparator.comparing(a -> a.getMouvements().size()))
                .orElseThrow(NoSuchElementException::new);

        for(int mouvement = 0; mouvement < ligne.getMouvements().size(); mouvement++) {
            for (Ligne aventurier : ligneService.getLignesByTypeElement(lignes, ETypeElement.A)) {

                if (aventurier.getMouvements().size() > mouvement) {
                    EMouvement typeMouvement = aventurier.getMouvements().get(mouvement);

                    if(typeMouvement.equals(EMouvement.A))
                        avancer(carte, aventurier, lignes);
                    else
                        tourner(carte, aventurier, typeMouvement);
                }
            }
        };

        return carte;
    }

    private void tourner(String[][] carte, Ligne aventurier, EMouvement typeMouvement) throws CarteTresorException {

        if (typeMouvement.equals(EMouvement.G)) {

            switch (aventurier.getOrientation()) {
                case N -> aventurier.setOrientation(EOrientation.O);
                case S -> aventurier.setOrientation(EOrientation.E);
                case E -> aventurier.setOrientation(EOrientation.N);
                case O -> aventurier.setOrientation(EOrientation.S);
            }
        } else {
            switch (aventurier.getOrientation()) {
                case N -> aventurier.setOrientation(EOrientation.E);
                case S -> aventurier.setOrientation(EOrientation.O);
                case E -> aventurier.setOrientation(EOrientation.S);
                case O -> aventurier.setOrientation(EOrientation.N);
            }
        }

        //pour update le mouvement
        genererLigne(carte, aventurier);
    }

    private void avancer(String[][] carte, Ligne aventurier, List<Ligne> lignes) throws CarteTresorException {

        Coordonnees coordonneesOld = aventurier.getCoordonnees();
        Coordonnees coordonneesNew = calculNouvelleCoordonnees(
                aventurier.getCoordonnees().getVerticale(),
                aventurier.getCoordonnees().getHorizontale(),
                aventurier.getOrientation()
        );

        //vérifier si on peut bien se déplacer dans les nouvelles coordonnées
        if (canMove(coordonneesNew, carte)) {

            Ligne tresor = getTresor(lignes, coordonneesNew);

            if (null != tresor && tresor.getNbTresors() > 0) {

                tresor.setNbTresors(tresor.getNbTresors() - 1);
                aventurier.setNbTresorsObtenu(aventurier.getNbTresorsObtenu() + 1);
            } else {
                genererplaine(carte, aventurier.getCoordonnees());
            }

            aventurier.setCoordonnees(coordonneesNew);
            genererLigne(carte, aventurier);

            //old
            Ligne tresorOld = getTresor(lignes, coordonneesOld);

            if (null != tresorOld && tresorOld.getNbTresors() > 0) {
                genererLigne(carte, tresorOld);
            } else {
                genererplaine(carte, coordonneesOld);
            }

        }
    }

    private Ligne getTresor(List<Ligne> lignes, Coordonnees coordonnees) {

        return lignes.stream()
                .filter(ligne -> ligne.getTypeLigne().equals(ETypeElement.T) &&
                        ligne.getCoordonnees().getVerticale() == coordonnees.getVerticale() &&
                        ligne.getCoordonnees().getHorizontale() == coordonnees.getHorizontale()
                )
                .findFirst().orElse(null);
    }

    private boolean canMove(Coordonnees coordonnees, String[][] carte) {

        //cas ou les coordonnées sont négative
        if (coordonnees.getVerticale() < 0 || coordonnees.getHorizontale() < 0)
            return false;

        //si ça ne dépasse pas la carte
        if (null == getValueFromCoordonnees(coordonnees, carte))
            return false;

        //si il y a une montagne dans cet emplacement;
        if (Objects.equals(getValueFromCoordonnees(coordonnees, carte), ETypeElement.M.name()))
            return false;

        //ou s'il y a un aventurier dans cet emplacement;
        return !Objects.requireNonNull(getValueFromCoordonnees(coordonnees, carte)).contains(ETypeElement.A.name());
    }

    private String getValueFromCoordonnees(Coordonnees coordonnees, String[][] carte) {

        //vérifier que l'on ne dépasse pas la limite du tableau
        if (carte[0].length >= coordonnees.getVerticale() && carte.length >= coordonnees.getHorizontale()) {
            return carte[coordonnees.getVerticale()][coordonnees.getHorizontale()];
        }
        return null;
    }

    private Coordonnees calculNouvelleCoordonnees(int verticale, int horizontale, EOrientation orientation) {
        switch (orientation) {
            case N -> verticale -= 1;
            case S -> verticale += 1;
            case E -> horizontale += 1;
            case O -> horizontale -= 1;
        }

        return new Coordonnees(horizontale, verticale);
    }

    @Override
    public String[][] generer(String[][] carte, List<Ligne> lignesByTypeElement) throws CarteTresorException {

        for (Ligne ligne : lignesByTypeElement) {
            genererLigne(carte, ligne);
        }
        return carte;
    }

    private void genererLigne(String[][] carte, Ligne ligne) throws CarteTresorException {
        try{
            carte[ligne.getCoordonnees().getVerticale()][ligne.getCoordonnees().getHorizontale()] = getValueByElement(ligne);
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new CarteTresorException("Ligne hors limite");
        }
    }

    private void genererplaine(String[][] carte, Coordonnees coordonnees) {
        carte[coordonnees.getVerticale()][coordonnees.getHorizontale()] = ".";
    }

    private String getValueByElement(Ligne ligne) {

        return switch (ligne.getTypeLigne()) {
            case M -> ETypeElement.M.name();
            case T -> ETypeElement.T.name() + "(" + ligne.getNbTresors() + ")";
            case A -> ETypeElement.A.name() + "(" + ligne.getNom() + ")";
            case C -> null;
        };
    }

    @Override
    public String[][] genererPlaines(String[][] carte) {

        for (String[] strings : carte) {
            Arrays.fill(strings, ".");
        }

        return carte;
    }
}
