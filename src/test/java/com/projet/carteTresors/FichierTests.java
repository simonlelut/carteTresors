package com.projet.carteTresors;

import com.projet.carteTresors.model.CarteTresors;
import com.projet.carteTresors.service.impl.FileServiceImpl;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FichierTests {

	public static final String TEST_ERROR_FILEPATH = "test/test.txt";

	@Autowired
	private FileServiceImpl fileService;

	@Test()
	void fichierInputEmpty() {
		assertThrows(IOException.class, () -> {
			this.fileService.readFile(TEST_ERROR_FILEPATH);
		});
	}

	@Test()
	void fichierOutputEmpty() {
		assertThrows(IOException.class, () -> {
			this.fileService.createFile(new CarteTresors(new ArrayList<>(), new String[0][0]), TEST_ERROR_FILEPATH, "");
		});
	}

}
