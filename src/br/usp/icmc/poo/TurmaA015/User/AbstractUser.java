package br.usp.icmc.poo.TurmaA015.User;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.util.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


abstract class AbstractUser implements User {
	protected static int maxFiles;
	protected static int maxRentTime;
	protected static String name;
	private Map<Rentable, Integer> delays;
	private Map<Rentable, Integer> rentTime;
	private ArrayList<Rentable> ownedFiles;

	public AbstractUser(String str){
		name = str;
		delays = new HashMap<Rentable, Integer>();
		rentTime = new HashMap<Rentable, Integer>();
		ownedFiles = new ArrayList<Rentable>();
	}

	public int getMaxFiles(){
		return maxFiles;
	}

	//não precisa verificar se a pessoa tem o max de livros, pq a biblioteca sabe quantos livros ele tem
	//e so vai deixar ele alugar um livro se el não tiver o número máximo de livros
	public boolean rentFile(Rentable r){
		ownedFiles.add(r);
		return true;
	}

	public boolean refundFile(Rentable r){

		if(ownedFiles.contains(r)){
			ownedFiles.remove(r);
			rentTime.remove(r);	//retira o tempo de aluguel do livro
			if(delays.containsKey(r))	//retira o atraso do livro
				delays.remove(r);
			return true;
		}
		return false;
	}

	//faz o tempo "passar"
	public boolean increaseRentTime(Rentable r, int time){
		if(rentTime.containsKey(r)){
			rentTime.put(r, new Integer((rentTime.get(r)).intValue() + time));
			return true;
		}
		return false;
	}

	public int getFilesQuantity(){
		return ownedFiles.size();
	}

	public String getName(){
		return name;
	}
}