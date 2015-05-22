
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;
import br.usp.icmc.poo.TurmaA015.TimeChecker.*;

import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
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
	private String usersData;
	private String filesLog;
	private String filesData;
	private String rentsData;
	private TimeChecker tc;
	private boolean systemLoading;

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();
		tc = new TimeChecker();
		Path p = Paths.get("br/usp/icmc/poo/TurmaA015/Library/logs/users.log");
		usersLog = p.toString();
		p = Paths.get("br/usp/icmc/poo/TurmaA015/Library/logs/files.log");
		filesLog = p.toString();
		p = Paths.get("br/usp/icmc/poo/TurmaA015/Library/data/users.csv");
		usersData = p.toString();;
		p = Paths.get("br/usp/icmc/poo/TurmaA015/Library/data/files.csv");
		filesData = p.toString();
		p = Paths.get("br/usp/icmc/poo/TurmaA015/Library/data/rents.csv");
		rentsData = p.toString();
		systemLoading = false;
	}

	public void setDate(int day, int month, int year){
		this.day = day;
		this.month = month;
		this.year = year;
	}

	//adiciona um novo arquivo na biblioteca
	public boolean addFile(Rentable r){
		files.add(r);
		writeFilesLog(null, r, "new");
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

	public int rentFile(String userName, String fileName){
		User user = getUser(userName);
		Rentable rentedFile = getFile(fileName);

		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;
		
		rentedFile = getAvailableFile(fileName);

		if(rentedFile == null)					//livro indisponível
			return -3;
		if(user.getFilesQuantity() >= user.getMaxFiles())
			return -4;
		if(rentedFile.needsPermission() && !user.hasPermission())
			return -5;
		if(!systemLoading && user.isBanned())		//se o sistema estiver recuperando os dados do arquivo temos que adicionar os livros mesmo que o usuario 
			return -6;								//tenha um delay

		user.rentFile(rentedFile);
		rentedFile.setRentExpirationDate(tc.setDate(getDate(), user.getMaxRentTime()));
		rentedFile.rent();

		System.out.println("Rent date expires in " + tc.setDate(getDate(), user.getMaxRentTime()));
		
		writeUsersLog(user, rentedFile, "rent");			
		writeFilesLog(user, rentedFile, "rent");

		if(!systemLoading)
			writeLog(user.getType() + "," + user.getName() + "," + rentedFile.getType() + "," + rentedFile.getName() + "," + getDate() + "," + 
										tc.setDate(getDate(), user.getMaxRentTime()), rentsData, true);
		
		return 1;	//ok
	}

	public int refundFile(String userName, String fileName){
		User user = getUser(userName);
		Rentable rentedFile = getFile(fileName);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;

		rentedFile = getRentedFile(fileName, userName);

		if(!user.hasFile(rentedFile))
			return -3;
		
		user.refundFile(rentedFile);
		rentedFile.setRentExpirationDate("null");
		rentedFile.refund();
		
		if(rentedFile.getDelay() > 0){
			user.setBan(rentedFile.getDelay());
			rentedFile.removeDelay();
		}

		writeUsersLog(user, rentedFile, "refund");			
		writeFilesLog(user, rentedFile, "refund");

		return 1;
	}

	public void showUsers(){
		for(User user : users){
			System.out.println("\n================================================\n");
			System.out.println(user.getType() + " " + user.getName());

			if(user.getFilesQuantity() > 0){
				System.out.println("Rented books for this user: \n");

				for(Rentable r : files){
					if(user.hasFile(r)){
						System.out.print(r.getType() + " " + r.getName());
						
						if(r.getDelay() != 0)
							System.out.print(" (Please refund this book to the library as soon as possible.)");
					
					System.out.print("\n");
					}
				}
		/*		files
					.stream()
					.filter(f -> {
						System.out.println("Filtering " + f.getName());
						return user.hasFile(f) == true;
					})
					.forEach(f -> System.out.println(f.getType() + " " + f.getName()));
		*/
			}
			else
				System.out.println("This user doens't have any book rented.");
		}
	}

	public void showFiles(){
		Map<String, Long> filesMap = files 
									.stream()
									.collect(Collectors.groupingBy(Rentable::getName, Collectors.mapping(Rentable::getName, Collectors.counting())));

		filesMap
		.forEach((k, v) -> System.out.println(k + " " + v));
	/*	files
			.stream()
			.forEach(r -> System.out.println(r.getType() + ": " + r.getName()));*/
	}

	public void showRents(){
		BufferedReader br;

		try{
			String[] content = null;
			String input;

			br = new BufferedReader(new FileReader(rentsData));

			while((input = br.readLine()) != null){
				content = input.split(",");
				System.out.println(content[0] + " " + content[1] + " " + content[2] + " " + content[3] + " " + content[4] + " " + content[5]);
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no rents file to load.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}
	}
	/*public void showRents(){
		String str;
		List<User> usersList;

		for(Rentable r : files){
			usersList = users
					.stream()
					.filter(u -> u.hasFile(r))
					.collect(Collectors.toList());

			for(User user : usersList){		
				str = "";
				str += user.getType() + " ";
				str += user.getName() + " ";
				str += r.getType() + " ";
				str += r.getName() + " ";
				str += r.getRentExpirationDate();

				System.out.println(str);
			}
		}
	}
*/
	public Rentable getAvailableFile(String name){
		return files
			.stream()
			.filter(f -> f.getName().equals(name) && f.isAvailable())
			.findAny()
			.orElse(null);	
	}

	public Rentable getRentedFile(String fileName, String userName){
		User u = getUser(userName);

		return files
			.stream()
			.filter(f -> f.getName().equals(fileName) && u.hasFile(f))
			.findAny()
			.orElse(null);	
	}

	//retorna, se existir, um arquivo com nome "name"
	public Rentable getFile(String name){
		return _hasFile(name).orElse(null);
	}

	//retorna, se existir, um usuario com nome "name"
	public User getUser(String name){
		return _hasUser(name).orElse(null);
	}

	//ambas as funções _has retornam o primeiro elemento compatível que encontrarem, porque nao sao aceitos duas pessoas com mesmo nome na biblioteca
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

	public void loadContent(){
		String[] content = null;
		String input = null;
		BufferedReader br = null;

		systemLoading = true;		//faz com que os livros adicionados na operação de load não sejam guardados novamente no arquivos de rents
									//e evitar que o arquivo seja refeito desnecessariamente

		try {

			br = new BufferedReader(new FileReader(usersData));

			while((input = br.readLine()) != null){
				content = input.split(",");

				if(content[0].equals("Student"))
					addUser(new Student(content[1]));
				else if(content[0].equals("Teacher"))
					addUser(new Teacher(content[1]));
				else if(content[0].equals("Community"))
					addUser(new Community(content[1]));
			}

			br.close();

			br = new BufferedReader(new FileReader(filesData));

			while((input = br.readLine()) != null){
				content = input.split(",");

				if(content[1].equals("Book")){
					Book book = new Book(content[2]);
					addFile(book);
					
					if(!content[0].equals("none")){
						book.setRentExpirationDate(content[3]);
						rentFile(content[0], content[2]);

						if(tc.dateDifference(getDate(), content[3]) > 0){
							book.setDelay(tc.dateDifference(getDate(), content[3]));
							System.out.println("delay on book " + book.getName() + " " + tc.dateDifference(getDate(), content[3]));
							getUser(content[0]).setBan(tc.dateDifference(getDate(), content[3]));
						}
					}

				}
				else if(content[1].equals("Note")){
					Note note = new Note(content[2]);
					addFile(note);

					//podemos juntar as linhas do set expiration date com add file e new note
					if(!content[0].equals("none")){
						note.setRentExpirationDate(content[3]);
						rentFile(content[0], content[2]);
	
						if(tc.dateDifference(getDate(), content[3]) > 0){
							note.setDelay(tc.dateDifference(getDate(), content[3]));
							System.out.println("delay on note " + note.getName() + " " + tc.dateDifference(getDate(), content[3]));
							getUser(content[0]).setBan(tc.dateDifference(getDate(), content[3]));
						}
					}

				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no content to load.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}

		systemLoading = false;
	}

	public void saveContent(){
		writeUsersData();
		writeFilesData();
	//	writeRentsData();
	}

	private void writeUsersData(){
		String separator = ",";
		String data = "";
		boolean type = false;

		for(User u : users){
			data = "";
			data += u.getType() + separator;
			data += u.getName() + separator;
			data += u.getBanTime();

			writeLog(data, usersData, type);				//escreve o tipo e o nome em um arquivo csv
			if(!type) type = true;
		}
	}

	private void writeFilesData(){
		String separator = ",";
		String data = "";
		boolean type = false;

		for(Rentable r : files){
			data = "";
			data += users
						.stream()
						.filter(u -> u.hasFile(r))
						.findAny()
						.map(User::getName)
						.orElse("none") + separator;
			data += r.getType() + separator;
			data += r.getName() + separator;
			data += r.getRentExpirationDate();

			writeLog(data, filesData, type);				//escreve o tipo e o nome em um arquivo csv
			if(!type) type = true;
		}
	}

/*	//escreve em um arquivo todos os empréstimos feitos
	private void writeRentsData(){
		String separator = ",";
		String data = "";
		List<User> usersList = null;
		//boolean type = false; pode ser true/false a depender se quer todos os rents desde o começo ou só os em andamento

		for(Rentable r : files){
			usersList = users
					.stream()
					.filter(u -> u.hasFile(r))
					//.peek(System.out::println)
					.collect(Collectors.toList());

			for(User user : usersList){		
				data = "";
				data += user.getType() + separator;
				data += user.getName() + separator;
				data += r.getType() + separator;
				data += r.getName() + separator;
				data += "rentDate";

				writeLog(data, rentsData, true);
			}
		}
	}
*/
	private void writeUsersLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added " + u.getType().toLowerCase() + " \"" + u.getName() + "\" at " + getDate() + ".", usersLog, true);
		else if(str.equals("rent")){
			writeLog("Rented " + r.getType().toLowerCase() + " \"" + r.getName() + "\" for " + u.getType().toLowerCase() + " " + u.getName() + " at " + 
				getDate() + ". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
		else{
			writeLog(u.getType() + " " + u.getName() + " refunded " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + 
														". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
	}

	private void writeFilesLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added new " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + ".", filesLog, true);
		else if(str.equals("copy"))
			writeLog("Added copy of " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + getDate() + ".", filesLog, true);
		else if(str.equals("rent")){
			writeLog(r.getType() + " \"" + r.getName() + "\" was rented by " + u.getType().toLowerCase() + " " + u.getName() + " at " + getDate() + "."
																																	, filesLog, true);
		}
		else{
			writeLog(r.getType() + " \"" + r.getName() + "\" was refunded by " + u.getType().toLowerCase() + " " + u.getName() + " at " + getDate()
			 																										+ ".", filesLog, true);	
		}
	}

	private void writeLog(String str, String filename, Boolean type){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, type)));
			pw.println(str);
			pw.close();
		}
		catch(IOException e){
			System.out.println("Error trying to open file");
		}
	}
}