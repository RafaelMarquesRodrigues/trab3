package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;

public interface Rentable {
	String getName();
	String getType();
	
	boolean needsPermission();
	
	void setRentExpirationDate(LocalDate date);
	LocalDate getRentExpirationDate();
	
	void setDelay(int n);
	void removeDelay();
	int getDelay();

	LocalDate getCreationDate();

	boolean isAvailable();
	void rent();
	void refund();
}