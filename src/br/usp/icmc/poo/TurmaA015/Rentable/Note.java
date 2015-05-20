package br.usp.icmc.poo.TurmaA015.Rentable;

public class Note extends AbstractRentable {

	public Note(String str){
		super(str);							//chama o construtor da classe que está estendendo
		permission = true;
	}

	public String getType(){
		return "Note";
	}
}