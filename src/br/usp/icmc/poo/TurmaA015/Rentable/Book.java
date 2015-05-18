package br.usp.icmc.poo.TurmaA015.Rentable;

public class Book extends AbstractRentable {

	public Book(String str){
		super(str);						//chama o construtor da classe que está estendendo
	}

	public String getType(){
		return "Book";
	}
}