package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

import java.util.Map;
import java.util.HashMap;

public class User {
	private Person person;
	private Map<Rentable, Integer> delays;

	public User(Person p){
		person = p;
		delays = new HashMap<Rentable, Integer>();
	}

	public Person getPerson(){
		return person;
	}

}