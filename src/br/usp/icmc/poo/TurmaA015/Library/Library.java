
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

	public boolean add(Rentable r){
		files.add(r);
		return true;
	}

	public boolean newUser(Person p){
		Optional<Person> newPerson = _hasUser(p.getName());

		if(!newPerson.isPresent()){
			users.add(p);
			return true;
		}

			return false;
	}

	public Person getUser(String name){
		Optional<Person> p = _hasUser(name);

		return p.orElse(null);
	}


	public boolean hasArchive(String str){
		Optional<Rentable> r = _hasArchive(str);

		if(r.isPresent())
			return true;

		return false;
	}

	private Optional<Rentable> _hasArchive(String str){
		return files
			.stream()
			.filter(f -> f.getName().equals(str))
			.findFirst();
	}

	private Optional<Person> _hasUser(String str){
		return users
			.stream()
			.filter(u -> u.getName().equals(str))
			.findFirst();	
	}

	public int getUserSize(){
		return users.size();
	}

	public int getArchivesSize(){
		return users.size();
	}
	
}