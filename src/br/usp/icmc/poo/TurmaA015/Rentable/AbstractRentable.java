package br.usp.icmc.poo.TurmaA015.Rentable;

abstract class AbstractRentable implements Rentable {
	protected int copies;
	protected String name;

	public AbstractRentable(String str){
		name = str;
	}
	
	//podem haver mais de um livro igual
	public int getCopies(){
		return copies;
	}

	//nome do livro/anotação
	public String getName(){
		return name;
	}
}