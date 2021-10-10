package com.projet.carteTresors;

import com.projet.carteTresors.component.Runner;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.EMouvement;
import com.projet.carteTresors.model.enumeration.EOrientation;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.exception.CarteTresorException;
import com.projet.carteTresors.service.impl.CarteServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class CarteTests {

    @Autowired
    private CarteServiceImpl carteService;

    @Autowired
    private Runner runner;

    @Value(StringUtils.SPACE + "${carte.lignes.delimiteur}" + StringUtils.SPACE)
    String delimiteur;


    private List<String> sampleLignesOriginale(){
        List<String> lignesOriginal = new ArrayList<>();

        lignesOriginal.add("C - 3 - 4");
        lignesOriginal.add("M - 1 - 0");
        lignesOriginal.add("M - 2 - 1");
        lignesOriginal.add("T - 0 - 3 - 2");
        lignesOriginal.add("T - 1 - 3 - 3");
        lignesOriginal.add("A - Lara - 1 - 1 - S - AADADAGGA");

        return lignesOriginal;
    }

    private void isLignesOk(List<Ligne> lignes) {

        assertEquals(lignes.get(0).getTypeLigne(), ETypeElement.C);
        assertEquals(lignes.get(0).getLargeur(), 3);
        assertEquals(lignes.get(0).getHauteur(),4);

        assertEquals(lignes.get(1).getTypeLigne(), ETypeElement.M);
        assertEquals(lignes.get(1).getCoordonnees().getHorizontale(), 1);
        assertEquals(lignes.get(1).getCoordonnees().getVerticale(),0);

        assertEquals(lignes.get(2).getTypeLigne(), ETypeElement.M);
        assertEquals(lignes.get(2).getCoordonnees().getHorizontale(), 2);
        assertEquals(lignes.get(2).getCoordonnees().getVerticale(),1);

        assertEquals(lignes.get(3).getTypeLigne(), ETypeElement.T);
        assertEquals(lignes.get(3).getCoordonnees().getHorizontale(), 0);
        assertEquals(lignes.get(3).getCoordonnees().getVerticale(),3);
        assertEquals(lignes.get(3).getNbTresors(),2);

        assertEquals(lignes.get(4).getTypeLigne(), ETypeElement.T);
        assertEquals(lignes.get(4).getCoordonnees().getHorizontale(), 1);
        assertEquals(lignes.get(4).getCoordonnees().getVerticale(),3);
        assertEquals(lignes.get(4).getNbTresors(),3);

        assertEquals(lignes.get(5).getTypeLigne(), ETypeElement.A);
        assertEquals(lignes.get(5).getCoordonnees().getHorizontale(), 1);
        assertEquals(lignes.get(5).getCoordonnees().getVerticale(),1);
        assertEquals(lignes.get(5).getNom(),"Lara");
        assertEquals(lignes.get(5).getOrientation(), EOrientation.S);
        assertEquals(lignes.get(5).getMouvements(), Arrays.asList(EMouvement.A, EMouvement.A, EMouvement.D, EMouvement.A,EMouvement.D, EMouvement.A, EMouvement.G, EMouvement.G, EMouvement.A));
    }

    @Test()
    void lignesEmpty() {

        assertThrows(CarteTresorException.class, () -> {
            carteService.recuperationLigne(new ArrayList<>(), "");
        });
    }

    @SneakyThrows
    @Test()
    void lignesCommentaire() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.add(0, "# test commentaire");
        lignesOriginal.add(3, "# test commentaire");

        List<Ligne> lignes = carteService.recuperationLigne(lignesOriginal, delimiteur);

        isLignesOk(lignes);
    }

    @SneakyThrows
    @Test()
    void lignesIconnu() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.add(0, "TEST LIGNE ICONNU");
        lignesOriginal.add(4, "TEST LIGNE ICONNU");

        List<Ligne> lignes = carteService.recuperationLigne(lignesOriginal, delimiteur);

        isLignesOk(lignes);
    }

    @Test()
    void ligneErreur() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(0,  "C - --3 - 4");

        assertThrows(CarteTresorException.class, () -> {
            carteService.recuperationLigne(lignesOriginal, delimiteur);
        });
    }

    @Test()
    void ligneNegative() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(0,  "C - -3 - 4");

        assertThrows(CarteTresorException.class, () -> {
            carteService.recuperationLigne(lignesOriginal, delimiteur);
        });
    }

    @Test()
    void limiteInteger() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(0,  "C - 2147483648 - 4");

        assertThrows(CarteTresorException.class, () -> {
            carteService.recuperationLigne(lignesOriginal, delimiteur);
        });
    }

    @Test()
    void montagneHorsLimite() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(1,  "M - 50 - 4");

        try{
            Runner.creationCarteTresors(lignesOriginal);
        }
        catch (CarteTresorException e){
            System.out.println(e.getMessage());
        }

    }

    @Test()
    void tresorHorsLimite() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(3,  "T - 50 - 56 - 2");

        try{
            Runner.creationCarteTresors(lignesOriginal);
        }
        catch (CarteTresorException e){
            System.out.println(e.getMessage());
        }

    }

    @Test()
    void nbTresorIncoherent() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(3,  "T - 3 - 2 - 56");

        try{
            Runner.creationCarteTresors(lignesOriginal);
        }
        catch (CarteTresorException e){
            System.out.println(e.getMessage());
        }
    }

    @Test()
    void aventurierHorsLimite() {

        List<String> lignesOriginal = sampleLignesOriginale();

        lignesOriginal.set(5,  "A - Lara - 56 - 56 - S - AADADAGGA");

        try{
            Runner.creationCarteTresors(lignesOriginal);
        }
        catch (CarteTresorException e){
            System.out.println(e.getMessage());
        }
    }
}
