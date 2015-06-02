package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.util.ArrayList;
import java.time.LocalDate;

abstract class AbstractUser implements User {
	protected int maxFiles;
	protected int maxRentTime;
	protected String name;
	protected String id;
	protected String nationality;
	protected boolean permission;
	protected LocalDate ban;
	protected LocalDate creationDate;
	protected ArrayList<Rentable> ownedFiles;

	public AbstractUser(String name, String id, String nationality, LocalDate date){
		this.name = name;
		this.id = id;
		this.nationality = nationality;
		ownedFiles = new ArrayList<Rentable>();
		ban = null;
		creationDate = date;
	}

	public int getMaxFiles(){
		return maxFiles;
	}

	public int getMaxRentTime(){
		return maxRentTime;
	}

	public boolean hasPermission(){
		return permission;
	}

	public boolean hasFile(Rentable r){
		return ownedFiles.contains(r);
	}

	public void setBan(LocalDate date){
		ban = date;
	}

	public LocalDate getBanTime(){
		return ban;
	}

	public boolean isBanned(){
		return ban == null ? false : true;
	}

	//não precisa verificar se a pessoa tem o max de livros, pq a biblioteca sabe quantos livros ele tem
	//e so vai deixar ele alugar um livro se el não tiver o número máximo de livros
	public void rentFile(Rentable r){
		ownedFiles.add(r);
	}

	public void refundFile(Rentable r){
		ownedFiles.remove(r);
	}

	public int getFilesQuantity(){
		return ownedFiles.size();
	}

	public String getName(){
		return name;
	}
	
	public String getNationality(){
		return nationality;
	}

	public String getId(){
		return id;
	}
	
	public LocalDate getCreationDate(){
		return creationDate;
	}
}