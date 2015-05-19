
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class Library implements Organizer {
	private Map<String, Integer> rentTime;	//guardar o tempo de aluguel máximo que cada pessoa pode ter
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();
		
		rentTime = new HashMap<String, Integer>();

		//nome de cada classe para guardar o tempo reservado para o alugle de cada um
		rentTime.put((new Student()).toString(), new Integer(4));
		rentTime.put((new Teacher()).toString(), new Integer(6));
		rentTime.put((new Community()).toString(), new Integer(2));
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
			users.add(new User(p));
			return true;
		}

			return false;
	}

	/*public int makeRent(Person person, String str){
		if(_hasUser(person.getName()) == null)
			return -1;
		if(_hasFile(person.getName()) == null)
			return -2;

		rentTime.get(person.toString())
	}
*/
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
			.map(User::getPerson)
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