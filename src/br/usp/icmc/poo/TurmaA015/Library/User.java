package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

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
	private Arraylist<Rentable> ownedFiles;

	public User(Person p){
		person = p;
		delays = new HashMap<Rentable, Integer>();
		rentTime = new HashMap<Rentable, Integer>();
		ownedFiles = new Arraylist<Rentable>();
	}

	public void addFile(Rentable r){
		ownedFiles.add(r);
	}

	public void refundFile(Rentable r){
		ownedFiles.remove(r);
	}

	public boolean increaseRentTime(Rentable r, int time){
		if(rentTime.containsKey(r)){
			rentTime.put(r, new Integer((rentTime.get(r)).intValue() + time));
			return true;
		}

		return false;
	}

	public Person getPerson(){
		return person;
	}

}