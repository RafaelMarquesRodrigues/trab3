package br.usp.icmc.poo.TurmaA015.Rentable;

//classe criada para implementar métodos que são comuns para todos os tipos de objeto "alugáveis"
abstract class AbstractRentable implements Rentable {
	protected int quantity;
	protected String name;
	protected boolean permission;

	public AbstractRentable(String str){
		name = str;
		quantity = 1;
	}
	
	public boolean needsPermission(){
		return permission;
	}

	//pode haver mais de um livro igual
	public int getCopies(){
		return quantity;
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

	public String toString(){
		return name + " - Copies available: " + quantity;
	}
}