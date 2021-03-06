package br.usp.icmc.poo.TurmaA015.Rentable;

import java.time.LocalDate;

//classe criada para implementar métodos que são comuns para todos os tipos de objeto "alugáveis"
abstract class AbstractRentable implements Rentable {
	protected String name;
	protected String language;
	protected String publishingHouse;
	protected boolean permission;
	protected LocalDate rentExpirationDate;
	protected LocalDate creationDate;
	protected boolean available;
	protected int delay;

	public AbstractRentable(String filename, String language, String publishingHouse, LocalDate date){
		name = filename;
		this.language = language;
		this.publishingHouse = publishingHouse;
		rentExpirationDate = null;
		delay = 0;
		available = true;
		creationDate = date;
	}
	
	public boolean needsPermission(){
		return permission;
	}

	public void setRentExpirationDate(LocalDate date){
		rentExpirationDate = date;
	}

	public LocalDate getRentExpirationDate(){
		return rentExpirationDate;
	}

	public LocalDate getCreationDate(){
		return creationDate;
	}

	public void setDelay(int n){
		delay = n;
	}

	public int getDelay(){
		return delay;
	}

	public void removeDelay(){
		delay = 0;
	}

	public void rent(){
		available = false;
	}

	public void refund(){
		rentExpirationDate = null;
		available = true;
	}

	public boolean isAvailable(){
		return available;
	}

	//nome do livro/anotação
	public String getName(){
		return name;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public String getPublishingHouse(){
		return publishingHouse;
	}
}