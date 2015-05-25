package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;
import java.util.Optional;

public interface Rentable {
	String getName();
	String getType();
	
	boolean needsPermission();
	
	void setRentExpirationDate(LocalDate date);
	Optional<LocalDate> getRentExpirationDate();
	
	void setDelay(int n);
	void removeDelay();
	int getDelay();

	boolean isAvailable();
	void rent();
	void refund();
}