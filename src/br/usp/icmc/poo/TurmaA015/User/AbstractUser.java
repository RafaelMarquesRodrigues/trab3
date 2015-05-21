package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.util.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;


abstract class AbstractUser implements User {
	protected int maxFiles;
	protected int maxRentTime;
	protected String name;
	protected boolean permission;
	private ArrayList<Rentable> ownedFiles;

	public AbstractUser(String str){
		name = str;
		ownedFiles = new ArrayList<Rentable>();
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

	public String getFilesName(){
		return ownedFiles
			.stream()
			.map(Rentable::getName)
			.collect(Collectors.joining(" "));
	}
}