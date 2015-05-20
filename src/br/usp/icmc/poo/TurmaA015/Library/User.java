package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

//classe para facilitar o trabalho da biblioteca para guardar os dados
//cada pessoa que aluga um livro na biblioteca tem seu respectivo usuario na biblioteca
//o usuário deve guardar:
//1 - o atraso de cada pessoa em relação à um respectivo livro (hashmap)
//2 - os livros em posse de cada pessoa (arraylist)
//3 - o tempo que a pessoa está com o livro
public class User {
	private Person person;
	private Map<Rentable, Integer> delays;
	private Map<Rentable, Integer> rentTime;
	private ArrayList<Rentable> ownedFiles;

	public User(Person p){
		person = p;
		delays = new HashMap<Rentable, Integer>();
		rentTime = new HashMap<Rentable, Integer>();
		ownedFiles = new ArrayList<Rentable>();
	}

	//não precisa verificar se a pessoa tem o max de livros, pq a biblioteca sabe quantos livros ele tem
	//e so vai deixar ele alugar um livro se el não tiver o número máximo de livros
	public void addFile(Rentable r){
		ownedFiles.add(r);
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

	//retorna a pessoa que esse usuário guarda
	public Person getPerson(){
		return person;
	}
}


