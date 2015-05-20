package br.usp.icmc.poo.TurmaA015.Rentable;

public interface Rentable {
	int getCopies();
	String getName();
	String getType();
	void addCopy();
	void removeCopy();
	boolean needsPermission();
}