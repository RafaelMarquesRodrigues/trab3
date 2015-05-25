
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.io.PrintWriter;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.Period;

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
	private LocalDate today;
	private boolean systemLoading;

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();

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
		today = LocalDate.of(year, month, day);
		System.out.println(transformDate(today));
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
		rentedFile.setRentExpirationDate(today.plusDays(user.getMaxRentTime()));
		rentedFile.rent();

		writeUsersLog(user, rentedFile, "rent");			
		writeFilesLog(user, rentedFile, "rent");

		if(!systemLoading)
			writeLog(user.getType() + "," + user.getName() + "," + rentedFile.getType() + "," + rentedFile.getName() + "," + transformDate(today) + "," + 
										transformDate(today.plusDays(user.getMaxRentTime())), rentsData, true);
		
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
		rentedFile.refund();
		
		//if(rentedFile.getDelay() > 0){
		//	user.setBan(today.plusDays(rentedFile.getDelay()));
			rentedFile.removeDelay();
		//}

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
						System.out.print(r.getType() + " " + r.getName() + " - Expiration date: " + transformDate(r.getRentExpirationDate().orElse(null)));
						
						if(r.getDelay() != 0)
							System.out.print(" (Please refund this book to the library as soon as possible.)");
					
					System.out.print("\n");
					}
				}
			}
			else
				System.out.println("This user doens't have any book rented.");
		}

		System.out.println("\n================================================\n");
	}

	public void showFiles(){
		Map<String, Long> filesMap = files 
									.stream()
									.collect(Collectors.groupingBy(Rentable::getName, Collectors.mapping(Rentable::getName, Collectors.counting())));

		filesMap
			.forEach((k, v) -> System.out.println(k + " Copies: " + v));
	}
	
	public void showRents(){
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
				str += transformDate(r.getRentExpirationDate().orElse(null));

				System.out.println(str);
			}
		}
	}

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

	private String transformDate(LocalDate date){
		if(date != null)
			return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
		else
			return "null";
	}

	public void loadContent(){
		String[] content = null;
		String[] parts = null;
		String input = null;
		BufferedReader br = null;
		int time;

		systemLoading = true;		//faz com que os livros adicionados na operação de load não sejam guardados novamente no arquivos de rents
									//e evitar que o arquivo seja refeito desnecessariamente

		try {

			br = new BufferedReader(new FileReader(usersData));

			User user = null;

			while((input = br.readLine()) != null){
				content = input.split(",");

				if(content[0].equals("Student"))
					user = new Student(content[1]);
				else if(content[0].equals("Teacher"))
					user = new Teacher(content[1]);
				else if(content[0].equals("Community"))
					user = new Community(content[1]);
		
				addUser(user);

				if(!content[2].equals("null")){
					parts = content[2].split("/");
					user.setBan(LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
	
					if(today.isAfter(user.getBanTime())){
						System.out.println("user is not more banned");
						user.setBan(null);
					}
				}

			}

			br.close();

			br = new BufferedReader(new FileReader(filesData));

			while((input = br.readLine()) != null){
				content = input.split(",");

				if(content[1].equals("Book")){
					Book book = new Book(content[2]);
					addFile(book);
					
					if(!content[0].equals("none")){
						parts = content[3].split("/");
						rentFile(content[0], content[2]);
						book.setRentExpirationDate(LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));

						time = dateDifference(transformDate(today), content[3]);

						if(time > 0){
							book.setDelay(time);
							System.out.println("delay on book " + book.getName() + " " + time);
							getUser(content[0]).setBan(today.plusDays(time));
						}
					}

				}
				else if(content[1].equals("Note")){
					Note note = new Note(content[2]);
					addFile(note);

					//podemos juntar as linhas do set expiration date com add file e new note
					if(!content[0].equals("none")){
						parts = content[3].split("/");
						rentFile(content[0], content[2]);
						note.setRentExpirationDate(LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
	
						time = dateDifference(transformDate(today), content[3]);

						if(time > 0){
							note.setDelay(time);
							System.out.println("delay on note " + note.getName() + " " + time);
							getUser(content[0]).setBan(today.plusDays(time));
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

	private int dateDifference(String today, String date){
		String[] parts = today.split("/");
		LocalDate dateOfToday = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
		parts = date.split("/");
		LocalDate expirationDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
		


		if(dateOfToday.isAfter(expirationDate)){
			Period period = Period.between(expirationDate, dateOfToday);
			System.out.println("New ban " + transformDate(expirationDate) + " " + transformDate(dateOfToday) +" " + period.getDays());
			return period.getDays();
		}
		else
			return 0;
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
			data += transformDate(u.getBanTime());

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
			data += transformDate(r.getRentExpirationDate().orElse(null));

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
			writeLog("Added " + u.getType().toLowerCase() + " \"" + u.getName() + "\" at " + transformDate(today) + ".", usersLog, true);
		else if(str.equals("rent")){
			writeLog("Rented " + r.getType().toLowerCase() + " \"" + r.getName() + "\" for " + u.getType().toLowerCase() + " " + u.getName() + " at " + 
				transformDate(today) + ". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
		else{
			writeLog(u.getType() + " " + u.getName() + " refunded " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + transformDate(today) + 
														". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
	}

	private void writeFilesLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added new " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + transformDate(today) + ".", filesLog, true);
		else if(str.equals("copy"))
			writeLog("Added copy of " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + transformDate(today) + ".", filesLog, true);
		else if(str.equals("rent")){
			writeLog(r.getType() + " \"" + r.getName() + "\" was rented by " + u.getType().toLowerCase() + " " + u.getName() + " at " + transformDate(today) +
																													"." , filesLog, true);
		}
		else{
			writeLog(r.getType() + " \"" + r.getName() + "\" was refunded by " + u.getType().toLowerCase() + " " + u.getName() + " at " + transformDate(today)
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