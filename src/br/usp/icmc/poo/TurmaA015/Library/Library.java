
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.util.*;

public class Library implements Organizer {
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();
	}

	//adiciona um novo arquivo na biblioteca
	public boolean addFile(Rentable r){
		Rentable file = getFile(r.getName());
		
		if(file == null)
			files.add(r);
		else
			file.addCopy();
		return true;
	}

	//adiciona um novo usuario na biblioteca
	public boolean addUser(User u){
		User newUser = getUser(u.getName());
	
		if(newUser == null){
			users.add(u);
			return true;
		}

		return false;
	}

	public int makeRent(String userName, String fileName){
		User user = getUser(userName);
		Rentable rentedFile = getFile(fileName);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;
		if(rentedFile.getCopies() == 0)			//o livro existe mas está alugado
			return -3;
		if(user.getFilesQuantity() >= user.getMaxFiles())
			return -4;
		if(rentedFile.needsPermission() && !user.hasPermission())
			return -5;

		user.rentFile(rentedFile);
		rentedFile.removeCopy();			
		return 1;	//ok
	}

	public int refundFile(String userName, String fileName){
		User user = getUser(userName);
		Rentable rentedFile = getFile(fileName);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;
		if(!user.hasFile(rentedFile))
			return -3;
		if(user.hasDelay(rentedFile))
			user.removeDelay(rentedFile);
		
		user.refundFile(rentedFile);
		rentedFile.addCopy();
		return 1;
	}

	public void showUsers(){
		users
			.stream()
			.forEach(System.out::println);
	}

	public void showFiles(){
		files
			.stream()
			.forEach(System.out::println);
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
			.findAny();
	}

	//retorna a primeira pessoa com nome == str que encontrar
	private Optional<User> _hasUser(String str){
		return users
			.stream()
			.filter(u -> u.getName().equals(str))
			.findAny();	
	}

	public int getUsersSize(){
		return users.size();
	}

	public int getFilesSize(){
		return users.size();
	}
}