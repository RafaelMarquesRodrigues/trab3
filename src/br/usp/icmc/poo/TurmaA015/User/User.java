package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.time.LocalDate;

public interface User {
	void rentFile(Rentable r);
	void refundFile(Rentable r);
	
	boolean hasFile(Rentable r);
	boolean hasPermission();

	int getFilesQuantity();
	int getMaxFiles();
	int getMaxRentTime();
	
	String getType();
	String getName();
	String getNationality();
	String getId();
	
	LocalDate getCreationDate();

	void setBan(LocalDate date);
	LocalDate getBanTime();
	boolean isBanned();
}