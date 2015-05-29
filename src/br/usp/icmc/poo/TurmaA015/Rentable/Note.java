package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;

public class Note extends AbstractRentable {

	public Note(String str, LocalDate date){
		super(str, date);							//chama o construtor da classe que está estendendo
		permission = true;
	}

	public String getType(){
		return "Note";
	}
}