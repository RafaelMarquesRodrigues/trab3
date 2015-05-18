
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

import java.util.ArrayList;
import java.util.Optional;

public class Library implements Organizer {
	private ArrayList<Person> users;
	private ArrayList<Rentable> files;

	public Library() {
		users = new ArrayList<Person>();
		files = new ArrayList<Rentable>();
	}

	public void add(Rentable r){
		files.add(r);
	}

	public void newUser(Person p){
		users.add(p);
	}

	public Person getUser(String name){
		Optional<Person> p = users
			.stream()
			.filter(u -> u.getName().equals(name))
			.findFirst();	

		return p.orElse(null);
	}

	public int getUserSize(){
		return users.size();
	}

	public int getArchives(){
		return users.size();
	}

	public boolean hasArchive(String str){
		Optional<Rentable> r = files
			.stream()
			.filter(f -> f.getName().equals(str))
			.findFirst();

		if(r.isPresent())
			return true;
		return false;
	}

}