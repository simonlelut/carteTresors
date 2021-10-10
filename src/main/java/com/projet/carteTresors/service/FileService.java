package com.projet.carteTresors.service;

import com.projet.carteTresors.model.CarteTresors;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    List<String> readFile(String filepath) throws IOException;

    void createFile(CarteTresors carteTresor, String filepath, String delimiteur) throws IOException;

}
