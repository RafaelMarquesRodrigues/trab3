
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.util.function.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.io.PrintWriter;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.Period;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class Library implements Organizer {
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca
	private String usersLog;
	private String filesLog;
	private String refundsData;
	private String rentsData;
	private String usersData;
	private String filesData;
	private LocalDate today;
	private boolean systemLoading;

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();

		File file = new File("br/usp/icmc/poo/TurmaA015/Library/logs/users.log");
		System.out.println(file.getAbsolutePath());
		usersLog = file.getAbsolutePath();

		file = new File("br/usp/icmc/poo/TurmaA015/Library/logs/files.log");
		System.out.println(file.getAbsolutePath());
		filesLog = file.getAbsolutePath();

		file = new File("br/usp/icmc/poo/TurmaA015/Library/data/users.csv");
		System.out.println(file.getAbsolutePath());
		usersData = file.getAbsolutePath();

		file = new File("br/usp/icmc/poo/TurmaA015/Library/data/files.csv");
		System.out.println(file.getAbsolutePath());
		filesData = file.getAbsolutePath();

		file = new File("br/usp/icmc/poo/TurmaA015/Library/data/rents.csv");
		System.out.println(file.getAbsolutePath());
		rentsData = file.getAbsolutePath();

		file = new File("br/usp/icmc/poo/TurmaA015/Library/data/refunds.csv");
		System.out.println(file.getAbsolutePath());
		refundsData = file.getAbsolutePath();

		systemLoading = false;
	}

	public boolean setDate(int day, int month, int year){
		if(checkDate(day, month, year)){
			today = LocalDate.of(year, month, day);
			return true;
		}
		return false;
	}

	private boolean checkDate(int day, int month, int year){ //Vê se a data é válida
        //year restriction here
        if (0 >= day | day > 31) return false;
        if (0 >= month | month > 12) return false;
        if (month == 4 | month == 6 | month == 9 | month == 11)
            if (day == 31) return false;
        if (month == 2){
            if (checkLeapYear(year))
                if (day > 29) return false;
            else
                if (day > 28) return false;                    
        }      
        return true;
    }
       
    private boolean checkLeapYear(int year){ //Vê se o ano é bissexto
        if (year % 4 != 0) return false;
        if (year % 100 != 0) return true;
        return year % 400 == 0;        
    }

	public void reset(){
		try {

			File f = new File("br/usp/icmc/poo/TurmaA015/Library/logs/users.log");
			f.delete();
			f = new File("br/usp/icmc/poo/TurmaA015/Library/logs/files.log");
			f.delete();
			f = new File("br/usp/icmc/poo/TurmaA015/Library/data/users.csv");
			f.delete();
			f = new File("br/usp/icmc/poo/TurmaA015/Library/data/files.csv");
			f.delete();
			f = new File("br/usp/icmc/poo/TurmaA015/Library/data/rents.csv");
			f.delete();
			f = new File("br/usp/icmc/poo/TurmaA015/Library/data/refunds.csv");
			f.delete();

			users = new ArrayList<User>();
			files = new ArrayList<Rentable>();
		}
		catch(Exception e){
			System.out.println(e);
		}
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

		if(user == null)												//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)											//não existe o livro requisitado
			return -2;
		
		rentedFile = getAvailableFile(fileName);

		if(rentedFile == null)											//livro indisponível
			return -3;
		if(user.getFilesQuantity() >= user.getMaxFiles())				//o usuário tem o maior número de arquivos que ele pode ters
			return -4;
		if(rentedFile.needsPermission() && !user.hasPermission())		//o usuário não tem permissão para pegar o arquivo e o arquivo precisa de permissão
			return -5;
		if(!systemLoading && user.isBanned())		//se o sistema estiver recuperando os dados do arquivo temos que adicionar os livros mesmo que o usuario 
			return -6;								//tenha um delay, para recuperar os dados de forma correta. Caso contrário, se o usuário tentar pegar um
													//livro e ele estiver banido, ele não poderá concluir o aluguel

		user.rentFile(rentedFile);													//usuário recebe o livro requisitado
		rentedFile.setRentExpirationDate(today.plusDays(user.getMaxRentTime()));	//data máxima para o usuário ficar com o livro
		rentedFile.rent();															//coloca o livro como indisponível

		if(!systemLoading)
			writeEvent(user, rentedFile, rentsData);

		writeUsersLog(user, rentedFile, "rent");									//escreve o que aconteceu no arquivo de logs		
		writeFilesLog(user, rentedFile, "rent");
		
		return 1;	//ok
	}

	public int refundFile(String userName, String fileName){
		ArrayList<String> array;
		User user = getUser(userName);
		Rentable rentedFile = getFile(fileName);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;

		rentedFile = getRentedFile(fileName, userName);

		if(!user.hasFile(rentedFile))			//se o usuário não tiver o livro que ele está tentando devolver
			return -3;
		
		user.refundFile(rentedFile);		 	//o usuário devolve o livro que está com ele
		rentedFile.refund();					//o livro é marcado como disponível novamente
		rentedFile.removeDelay();				//retira-se qualquer possível atraso no livro
	
		if(!systemLoading)
			writeEvent(user, rentedFile, refundsData);

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

		if(users.size() == 0)
			System.out.println("There are no users at the library yet.");

	}

	public void showFiles(){
		//mapeia cada nome de livro com sua respectiva quantidade de cópias
		Map<String, Long> filesMap = files 
									.stream()
									.collect(Collectors.groupingBy(Rentable::getName, Collectors.mapping(Rentable::getName, Collectors.counting())));
		System.out.println("\n================================================\n");
		if(files.size() > 0){
			filesMap
				.forEach((k, v) -> System.out.println(k + " Copies: " + v));
		}
		else
			System.out.println("There are no files at the library yet.");
			
		System.out.println("\n================================================\n");
	}
	
	//mostra todos os alugueis ocorrendo atualmente
	public void showRents(String date){
		BufferedReader br;
		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(rentsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[4].equals(date))
					System.out.println(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3]);
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no rents to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}
	}
	
	public void showRefunds(String date){
		BufferedReader br;

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(refundsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[4].equals(date))
					System.out.println(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3]);
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no refunds to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}
	}
	
	//retorna um arquivo com nome "name" disponível para ser alugado, ou null caso não exista algum que satisfaça as condições
	public Rentable getAvailableFile(String name){
		return files
			.stream()
			.filter(f -> f.getName().equals(name) && f.isAvailable())
			.findAny()
			.orElse(null);	
	}

	//retorna um arquivo com nome "fileName" já alugado pelo usuário com nome "userName", ou null caso não exista algum que satisfaça as condições
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
	private Optional<Rentable> _hasFile(String str){
		return files
			.stream()
			.filter(f -> f.getName().equals(str))
			.findAny();
	}

	private Optional<User> _hasUser(String str){
		return users
			.stream()
			.filter(u -> u.getName().equals(str))
			.findAny();	
	}

	//transforma a data para ser imprimida de uma maneira padrão, tanto na tela quanto nos arquivos .csv. Null caso a LocalDate seja null
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

		systemLoading = true;		//evita que operações desnecessárias sejam feitas nos métodos rent que vão ser utilizados para dar load no conteúdo

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

				if(!content[2].equals("null")){					//caso o usuário tenha uma data de ban, recolocamos ela no status do usuário no programa
					parts = content[2].split("/");
					user.setBan(LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0])));
					
					//se o dia atual for depois do dia máximo de ban, retiriamos o ban do usuário
					if(today.isAfter(user.getBanTime())){
						System.out.println("User " + content[1] + " is no longer banned.");
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

						//se a diferença entre a data atual e a data máxima de entrega do livro for positiva, o usuário atrasou a devolução e deve ser banido
						if(time > 0){
							book.setDelay(time);
							System.out.println("Delay on book " + book.getName() + " - " + time + " days.");
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

	//retorna, em dias, today - date
	private int dateDifference(String today, String date){
		String[] parts = today.split("/");
		LocalDate dateOfToday = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
		parts = date.split("/");
		LocalDate expirationDate = LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
		
		//desnecessário ? period.getDays() retornaria negativo caso today < date ????
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
	}

	private void writeEvent(User u, Rentable r, String fileName){
		String separator = ",";
		String data = "";

		data += u.getType() + separator;
		data += u.getName() + separator;
		data += r.getType() + separator;
		data += r.getName() + separator;
		data += transformDate(today);

		writeLog(data, fileName, true);
	}

	//escreve no arquivo .csv, os dados do usuário. Tipo, Nome, Data de ban ("null" caso não esteja banido)
	private void writeUsersData(){
		String separator = ",";
		String data = "";
		boolean type = false;								//recria o arquivo de dados

		for(User u : users){
			data = "";
			data += u.getType() + separator;
			data += u.getName() + separator;
			data += transformDate(u.getBanTime());

			writeLog(data, usersData, type);				//escreve o tipo e o nome em um arquivo csv
			if(!type) type = true;							//reutiliza o arquivo de dados criado na passagem anterior
		}
	}

	//escreve os dados dos livors no arquivo .csv. Alugador (none caso não esteja alugado), Tipo, Nome, Data máxima de aluguel (null caso não haja)
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