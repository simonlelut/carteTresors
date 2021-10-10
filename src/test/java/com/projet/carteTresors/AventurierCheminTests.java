package com.projet.carteTresors;

import com.projet.carteTresors.component.Runner;
import com.projet.carteTresors.model.CarteTresors;
import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.service.impl.CarteServiceImpl;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest

public class AventurierCheminTests {


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


        @SneakyThrows
        @Test()
        void multiplesAventurier() {

                List<String> lignesOriginal = sampleLignesOriginale();

                lignesOriginal.add("A - Clement - 0 - 0 - S - AAGADAA");

                CarteTresors carteTresors = Runner.creationCarteTresors(lignesOriginal);

                Optional<Ligne> clement = carteTresors.getLignes().stream().filter(ligne -> ligne.getTypeLigne().equals(ETypeElement.A) && ligne.getNom().equals("Clement")).findFirst();

                clement.ifPresent(ligne -> assertEquals(ligne.getNbTresorsObtenu(), 1));

        }

        @SneakyThrows
        @Test()
        void laraBloquer() {

                List<String> lignesOriginal = sampleLignesOriginale();

                lignesOriginal.add("A - Clement - 0 - 0 - S - AAA");

                CarteTresors carteTresors = Runner.creationCarteTresors(lignesOriginal);

                Optional<Ligne> clement = carteTresors.getLignes().stream().filter(ligne -> ligne.getTypeLigne().equals(ETypeElement.A) && ligne.getNom().equals("Clement")).findFirst();
                Optional<Ligne> lara = carteTresors.getLignes().stream().filter(ligne -> ligne.getTypeLigne().equals(ETypeElement.A) && ligne.getNom().equals("Lara")).findFirst();

                clement.ifPresent(ligne -> {
                        assertEquals(ligne.getNbTresorsObtenu(), 1);
                        assertEquals(ligne.getCoordonnees().getVerticale(), 3);
                        assertEquals(ligne.getCoordonnees().getHorizontale(), 0);
                });

                lara.ifPresent(ligne -> {
                        assertEquals(ligne.getNbTresorsObtenu(), 2);
                        assertEquals(ligne.getCoordonnees().getVerticale(), 3);
                        assertEquals(ligne.getCoordonnees().getHorizontale(), 1);
                });
        }
}
