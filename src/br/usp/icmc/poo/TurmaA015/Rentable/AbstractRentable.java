package br.usp.icmc.poo.TurmaA015.Rentable;

abstract class AbstractRentable implements Rentable {
	private int copies;
	
	public int getCopies(){
		return 1;
	}
	public String getName(){
		return "str";
	}
}