package br.usp.icmc.poo.TurmaA015.Rentable;

public interface Rentable {
	String getName();
	String getType();
	
	boolean needsPermission();
	
	int getCopies();
	void addCopy();
	void removeCopy();
	
	void setRentExpirationDate(String date, int masRentTime);
	//void setDelay();
	//void removeDelay();
}