package br.usp.icmc.poo.TurmaA015.Person;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

import java.util.*;

abstract class AbstractPerson implements Person {
	protected Map<Rentable, Integer> filesOwned;
	protected int maxFiles;
	protected String name;

	public AbstractPerson(String str){
		name = str;
	}

	public String getName(){
		return name;
	}

	public boolean rentFile(Rentable r, int timeOfPossession){
		
		if(maxFiles < filesOwned.size()){
			filesOwned.put(r, new Integer(timeOfPossession));
			return true;

		}

		return false;
	}

	public boolean refundFile(Rentable r){
		filesOwned.remove(r);
		return true;
	}
}