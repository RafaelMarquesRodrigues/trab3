package br.usp.icmc.poo.TurmaA015.Rentable;

//classe criada para implementar métodos que são comuns para todos os tipos de objeto "alugáveis"
abstract class AbstractRentable implements Rentable {
	protected int quantity;
	protected String name;
	protected boolean permission;
	protected String rentExpirationDate;

	public AbstractRentable(String str){
		name = str;
		quantity = 1;
		rentExpirationDate = null;
	}
	
	public boolean needsPermission(){
		return permission;
	}

	//pode haver mais de um livro igual
	public int getCopies(){
		return quantity;
	}

	public void setRentExpirationDate(String date, int maxRentTime){
		rentExpirationDate = date;
	}

	//nome do livro/anotação
	public String getName(){
		return name;
	}

	//caso queira se adicionar um livro com mesmo nome, simplesmente acrescentamos a quantidade de cópias que o livro tem
	public void addCopy(){
		quantity++;
	}

	public void removeCopy(){
		quantity--;
	}

	public String getType(){
		return "none";
	}

	public String toString(){
		return name + " - Copies available: " + quantity;
	}
}