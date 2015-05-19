package br.usp.icmc.poo.TurmaA015.Person;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.util.*;

abstract class AbstractPerson implements Person {
	protected ArrayList<Rentable> filesOwned;
	protected String name;

	public AbstractPerson(String str){
		name = str;
		filesOwned = new ArrayList<Rentable>();
	}

	public AbstractPerson(){

	}

	public String getName(){
		return name;
	}

	public boolean rentFile(Rentable r){
		filesOwned.add(r);
		return true;
	}

	public boolean refundFile(Rentable r){
		filesOwned.remove(r);
		return true;
	}
}