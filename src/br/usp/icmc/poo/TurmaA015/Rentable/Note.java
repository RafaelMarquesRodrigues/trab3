package br.usp.icmc.poo.TurmaA015.Rentable;

class Note extends AbstractRentable {

	public Note(String str){
		super(str);							//chama o construtor da classe que está estendendo
	}

	public String getType(){
		return "Note";
	}
}