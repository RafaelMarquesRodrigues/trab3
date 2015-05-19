
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

	//adiciona um novo arquivo na biblioteca
	public boolean addFile(Rentable r){
		Optional<Rentable> or = _hasFile(r.getName());

		if(!or.isPresent())
			files.add(r);
		else
			r.addCopy();

		return true;
	}

	//adiciona um novo usuario na biblioteca
	public boolean addUser(Person p){
		Optional<Person> newPerson = _hasUser(p.getName());

		if(!newPerson.isPresent()){
			users.add(p);
			return true;
		}

			return false;
	}

	//retorna, se existir, um arquivo com nome "name"
	public Rentable getFile(String name){
		Optional<Rentable> f = _hasFile(name);

		return f.orElse(null);
	}

	//retorna, se existir, um usuario com nome "name"
	public Person getUser(String name){
		Optional<Person> p = _hasUser(name);

		return p.orElse(null);
	}

	//ambas as funções _has<E> retornam o primeiro elemento compatível que encontrarem, porque nao sao aceitos duas pessoas com mesmo nome na biblioteca
	//e os livros com nomes repetidos sao adicionados como cópias de um mesmo livro
	//retorna o primeiro arquivo com nome == str que encontrar
	private Optional<Rentable> _hasFile(String str){
		return files
			.stream()
			.filter(f -> f.getName().equals(str))
			.findFirst();
	}

	//retorna a primeira pessoa com nome == str que encontrar
	private Optional<Person> _hasUser(String str){
		return users
			.stream()
			.filter(u -> u.getName().equals(str))
			.findFirst();	
	}

	public int getUsersSize(){
		return users.size();
	}

	public int getFilesSize(){
		return users.size();
	}
}