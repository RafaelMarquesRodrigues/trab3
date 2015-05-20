package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public interface User {
	boolean rentFile(Rentable r);
	boolean refundFile(Rentable r);
	String getName();
	int getFilesQuantity();
	int getMaxFiles();
}