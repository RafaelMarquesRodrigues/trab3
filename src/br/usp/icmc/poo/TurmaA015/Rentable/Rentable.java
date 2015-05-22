package br.usp.icmc.poo.TurmaA015.Rentable;

public interface Rentable {
	String getName();
	String getType();
	
	boolean needsPermission();
	
	void setRentExpirationDate(String date);
	String getRentExpirationDate();
	
	void setDelay(int n);
	void removeDelay();
	int getDelay();
}