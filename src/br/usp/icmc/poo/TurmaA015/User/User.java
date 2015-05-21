package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public interface User {
	void rentFile(Rentable r);
	void refundFile(Rentable r);
	
	boolean hasDelay(Rentable r);
	boolean hasFile(Rentable r);
	boolean hasPermission();

	void removeDelay(Rentable r);

	int getFilesQuantity();
	int getMaxFiles();
	String getType();
	String getName();
	String getFilesName();
}