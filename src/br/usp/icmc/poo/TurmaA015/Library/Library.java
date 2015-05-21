
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.util.ArrayList;
import java.util.Optional;
import java.io.PrintWriter;
import java.io.*;
import java.util.*;

public class Library implements Organizer {
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca
	private int day;
	private int month;
	private int year;
	private String usersLog;
	private String filesLog;

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();
		usersLog = "users.log";
		filesLog = "files.log";
	}

	public void setDate(int day, int month, int year){
		this.day = day;
		this.month = month;
		this.year = year;
		System.out.println(this.day + " " + this.month + " " + this.year);
	}

	//adiciona um novo arquivo na biblioteca
	public boolean addFile(Rentable r){
		Rentable file = getFile(r.getName());
		
		if(file == null){
			files.add(r);
			writeFilesLog(null, r, "new");
		}
		else{
			writeFilesLog(null, r, "copy");
			file.addCopy();
		}

		return true;
	}

	//adiciona um novo usuario na biblioteca
	public boolean addUser(User u){
		User newUser = getUser(u.getName());
	
		if(newUser == null){
			users.add(u);
			writeUsersLog(u, null, "new");
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
		
		writeUsersLog(user, rentedFile, "rent");			
		writeFilesLog(user, rentedFile, "rent");			
		
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

		writeUsersLog(user, rentedFile, "refund");			
		writeFilesLog(user, rentedFile, "refund");

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

	private String getDate(){
		return day + "/" + month + "/" + year;
	}

	private void writeUsersLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added " + u.getType().toLowerCase() + " \"" + u.getName() + "\" at " + getDate() + ".", usersLog);
		else if(str.equals("rent")){
			writeLog("Rented " + r.getType().toLowerCase() + " \"" + r.getName() + "\" for " + u.getType().toLowerCase() + " " + u.getName() + " at " + 
				getDate() + ". User has " + u.getFilesQuantity() + " files now.", usersLog);	
		}
		else{
			writeLog(u.getType() + " " + u.getName() + " refunded " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + 
														". User has " + u.getFilesQuantity() + " files now.", usersLog);	
		}
	}

	private void writeFilesLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added new " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + ".", filesLog);
		else if(str.equals("copy"))
			writeLog("Added copy of " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + ".", filesLog);
		else if(str.equals("rent")){
			writeLog(r.getType() + " \"" + r.getName() + "\" was rented by " + u.getType().toLowerCase() + " " + u.getName() + " at " + getDate() + "." +
																" Copies left: " + r.getCopies() + ".", filesLog);
		}
		else{
			writeLog(r.getType() + " \"" + r.getName() + "\" was refunded by " + u.getType().toLowerCase() + " " + u.getName() + " at " + getDate()
			 									+ ". Copies available: " + r.getCopies() + ".", filesLog);	
		}
	}

	private void writeLog(String str, String filename){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
			pw.println(str);
		}
		catch(IOException e){
			System.out.println("Error trying to open file");
		}
		finally{
			pw.close();
		}
	}
}