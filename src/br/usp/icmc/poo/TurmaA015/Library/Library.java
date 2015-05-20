
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class Library implements Organizer {
	private Map<String, Integer> maxRentTime;	//guardar o tempo de aluguel máximo que cada pessoa pode ter
	private Map<String, Integer> maxFilesRented;	//guardar o numero de arquivos máximo que cada pessoa pode ter
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();
		maxRentTime = new HashMap<String, Integer>();
		maxFilesRented = new HashMap<String, Integer>();

		//nome de cada classe para guardar o tempo reservado para o aluguel de cada um
		maxRentTime.put((new Student()).toString(), new Integer(15));
		maxRentTime.put((new Teacher()).toString(), new Integer(60));
		maxRentTime.put((new Community()).toString(), new Integer(15));

		maxFilesRented.put((new Student()).toString(), new Integer(4));
		maxFilesRented.put((new Teacher()).toString(), new Integer(6));
		maxFilesRented.put((new Community()).toString(), new Integer(2));
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
		User newUser = getUser(p.getName());

		if(newUser == null){
			users.add(new User(p));
			return true;
		}

			return false;
	}

	public int makeRent(String name, String str){
		if(getUser(name) == null)			//não existe a pessoa requisitada
			return -1;
		if(getFile(str) == null)			//não existe o livro requisitado
			return -2;

		User user = getUser(name);

		//se o usuário não tiver o maior número de livros permitido pela biblioteca
		System.out.println(maxFilesRented.get(user.getPerson().toString()).intValue());
		System.out.println(user.getFilesQuantity());
		
		if(user.getFilesQuantity() < maxFilesRented.get(user.getPerson().toString()).intValue()){
			user.addFile(getFile(str));
			return 1;	//ok
		}

		return -3;//número de livros no máximo
	}

	//retorna, se existir, um arquivo com nome "name"
	public Rentable getFile(String name){
		Optional<Rentable> f = _hasFile(name);

		return f.orElse(null);
	}

	//retorna, se existir, um usuario com nome "name"
	public User getUser(String name){
		Optional<User> p = _hasUser(name);

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
	private Optional<User> _hasUser(String str){
		return users
			.stream()
			.filter(u -> u.getPerson().getName().equals(str))
			.findFirst();	
	}

	public int getUsersSize(){
		return users.size();
	}

	public int getFilesSize(){
		return users.size();
	}
}