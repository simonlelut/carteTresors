package com.projet.carteTresors.service;

import com.projet.carteTresors.model.Ligne;
import com.projet.carteTresors.model.exception.CarteTresorException;

import java.util.List;

public interface MapperService {

    Ligne ligneStringToLigneObject(List<String> lignesString) throws CarteTresorException;
}
