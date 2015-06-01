package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;

public class Book extends AbstractRentable {

	public Book(String filename, String language, String publishingHouse, LocalDate date){
		super(filename, language, publishingHouse, date);	//chama o construtor da classe que está estendendo
		permission = false;
	}

	public String getType(){
		return "Book";
	}
}