package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;

public class Note extends AbstractRentable {

	public Note(String filename, String language, String publishingHouse, LocalDate date){
		super(filename, language, publishingHouse, date);							//chama o construtor da classe que está estendendo
		permission = true;
	}

	public String getType(){
		return "Note";
	}
}