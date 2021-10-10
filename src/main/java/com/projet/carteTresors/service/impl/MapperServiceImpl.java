package com.projet.carteTresors.service.impl;

import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.enumeration.ETypeElement;
import com.projet.carteTresors.model.exception.CarteTresorException;
import com.projet.carteTresors.service.LigneService;
import com.projet.carteTresors.service.MapperService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class MapperServiceImpl implements MapperService {

    private final LigneService ligneService;


    public MapperServiceImpl(LigneService ligneService) {
        this.ligneService = ligneService;
    }

    @Override
    public Ligne ligneStringToLigneObject(List<String> ligneString) throws CarteTresorException {

        Ligne ligne;
        try {
            if(null != EnumUtils.getEnum(ETypeElement.class, ligneString.get(0))){

                //récupération du type de la ligne
                ligne = switch (ETypeElement.valueOf(ligneString.get(0))) {
                    case C -> ligneService.creerCarte(ligneString);
                    case M -> ligneService.creerMontagne(ligneString);
                    case T -> ligneService.creerTresor(ligneString);
                    case A -> ligneService.creerAventurier(ligneString);
                };
            }
            else{
                return null;
            }

           if(null != ligne){
               //verification de l'objet ligne
               ligneService.verifLigne(ligne);
           }
        }
        catch (Exception e){
            throw new CarteTresorException("Erreur données invalide");
        }

        return ligne;

    }
}
