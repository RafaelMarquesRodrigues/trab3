package br.usp.icmc.poo.TurmaA015.Rentable;

public interface Rentable {
	String getName();
	String getType();
	
	boolean needsPermission();
	
	int getCopies();
	void addCopy();
	void removeCopy();
	
	void setRentExpirationDate(String date);
	String getRentExpirationDate();
	//void setDelay();
	//void removeDelay();
}