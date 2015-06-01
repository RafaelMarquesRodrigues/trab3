
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.io.PrintWriter;
import java.io.*;
import java.time.*;
import java.time.Period;
import java.util.function.*;

public class Library implements Organizer {
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca
	private String usersLog;
	private String filesLog;
	private String refundsData;
	private String rentsData;
	private String usersData;
	private String filesData;
	private LocalDate systemDate;
	private LocalDate today;
	private boolean systemLoading;
	private boolean readOnly;

	public Library() {
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();

		File file = new File("logs/users.log");
		System.out.println(file.getPath());
		usersLog = file.getPath();

		file = new File("logs/files.log");
		System.out.println(file.getPath());
		filesLog = file.getPath();

		file = new File("data/users.csv");
		System.out.println(file.getPath());
		usersData = file.getPath();

		file = new File("data/files.csv");
		System.out.println(file.getPath());
		filesData = file.getPath();

		file = new File("data/rents.csv");
		System.out.println(file.getPath());
		rentsData = file.getPath();

		file = new File("data/refunds.csv");
		System.out.println(file.getPath());
		refundsData = file.getPath();

		systemDate = LocalDate.now();
		today = systemDate;
		
		System.out.println(dateToString(systemDate));

		readOnly = false;

		systemLoading = false;
	}

	public boolean setDate(int day, int month, int year){
		if(checkDate(day, month, year)){

			systemDate = LocalDate.of(year, month, day);

			if(today.isAfter(systemDate))
				readOnly = true;
			else{
				//salva as alterações e checa por atrasos
				exit();
				users = new ArrayList<User>();
				files = new ArrayList<Rentable>();
				today = systemDate;
				readOnly = false;
				begin();
			}

			return true;
		}

		return false;
	}

	private boolean checkDate(int day, int month, int year){ //Vê se a data é válida
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

    public String getDate(){
    	return dateToString(today);
    }

	public int reset(){
		if(readOnly)
			return 0;

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
		
		return 1;
	}

	//adiciona um novo arquivo na biblioteca
	public int addFile(Rentable r){
		if(readOnly)
			return 0;

		files.add(r);
		writeFilesLog(null, r, "new");
		
		return 1;
	}

	//adiciona um novo usuario na biblioteca
	public int addUser(User u){
		if(readOnly)
			return 0;

		User newUser = getUser(u.getId());
	
		if(newUser == null){
			users.add(u);
			writeUsersLog(u, null, "new");
			return 1;
		}

		return -1;
	}

	public int rentFile(String id, String fileName, String language, String publishingHouse){
		if(readOnly)
			return 0;

		User user = getUser(id);
		Rentable rentedFile = getFile(fileName, language, publishingHouse);

		if(user == null)												//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)											//não existe o livro requisitado
			return -2;
		
		rentedFile = getAvailableFile(fileName, language, publishingHouse);

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
		rentedFile.setRentExpirationDate(systemDate.plusDays(user.getMaxRentTime()));	//data máxima para o usuário ficar com o livro
		rentedFile.rent();															//coloca o livro como indisponível

		if(!systemLoading)
			writeEvent(user, rentedFile, rentsData);

		writeUsersLog(user, rentedFile, "rent");									//escreve o que aconteceu no arquivo de logs		
		writeFilesLog(user, rentedFile, "rent");
		
		return 1;	//ok
	}

	public int refundFile(String id, String fileName, String language, String publishingHouse){
		if(readOnly)
			return 0;

		User user = getUser(id);
		Rentable rentedFile = getFile(fileName, language, publishingHouse);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;

		rentedFile = getRentedFile(id, fileName, language, publishingHouse);

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
		System.out.println("\n\n** Showing currently registered users **\n");
		
		for(User user : users){
			System.out.println("\n================================================\n");
			System.out.println(user.getType() + " " + user.getName());

			System.out.println("User added in " + dateToString(user.getCreationDate()));
			if(user.getFilesQuantity() > 0){
				System.out.println("Rented books for this user: \n");

				for(Rentable r : files){
					if(user.hasFile(r)){
						System.out.print(r.getType() + " " + r.getName() + " - Expiration date: " + dateToString(r.getRentExpirationDate()));
						
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

		System.out.println("\n================================================\n");
	}

	public void showFiles(){
		System.out.println("\n\n** Showing currently registered files **\n");
		//mapeia cada nome de livro com sua respectiva quantidade de cópias
		Map<String, Long> filesMap = files 
									.stream()
									.collect(Collectors.groupingBy((r) -> r.getType() + "," + r.getName() + "," + r.getLanguage() + "," + r.getPublishingHouse(), Collectors.mapping((r) -> r.getType() + "," + r.getName() + "," + r.getLanguage() + "," + r.getPublishingHouse(), Collectors.counting())));
		
		if(files.size() > 0){
			filesMap
				.forEach((k, v) -> {
					System.out.println("\n================================================\n");
					String[] parts = k.split(",");
					Rentable r = getFile(parts[1], parts[2], parts[3]);
					System.out.println(r.getType() + " \"" + r.getName() + "\"");
					System.out.println("Language: " + r.getLanguage());
					System.out.println("Publishing house: " + r.getPublishingHouse());
					System.out.println("Copies at stock: " + v);
				});
		}
		else
			System.out.println("There are no files at the library yet.");
			
		System.out.println("\n================================================\n");
	}

	public void showUsersAdded(){
		BufferedReader br;
		boolean userAdded = false;

		System.out.println("\n\n** Showing users added in " + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(usersData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					userAdded = true;
					System.out.println(parts[0] + " " + parts[1] + " was added in " + parts[3]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no users to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}

		if(!userAdded)
			System.out.println("No users were added in " + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}

	public void showFilesAdded(){
		BufferedReader br;
		boolean fileAdded = false;

		System.out.println("\n\n** Showing files added in " + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(filesData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					fileAdded = true;
					System.out.println(parts[0] + " " + parts[1] + " was added in " + parts[3]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no files to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}

		if(!fileAdded)
			System.out.println("No files were added in " + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}
	
	//mostra todos os alugueis ocorrendo atualmente
	public void showRents(){
		BufferedReader br;
		boolean rentMade = false;

		System.out.println("\n\n** Showing rents made in " + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(rentsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[4].equals(dateToString(systemDate))){
					rentMade = true;
					System.out.println(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no rents to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}

		if(!rentMade)
			System.out.println("No rents were made in " + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}
	
	public void showRefunds(){
		BufferedReader br;
		boolean refundMade = false;

		System.out.println("\n\n** Showing refunds made in " + dateToString(systemDate) + " **\n");

		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(refundsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[4].equals(dateToString(systemDate))){
					refundMade = true;
					System.out.println(parts[0] + " " + parts[1] + " " + parts[2] + " " + parts[3]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no refunds to show.");
		}
		catch(IOException e){
			System.out.println("Error trying to load content.");
		}

		if(!refundMade)
			System.out.println("No refunds were made in " + dateToString(systemDate) + ".");

		System.out.println("\n================================================\n");
	}
	
	//retorna um arquivo com nome "name" disponível para ser alugado, ou null caso não exista algum que satisfaça as condições
	public Rentable getAvailableFile(String fileName, String language, String publishingHouse){
		return _hasFile(f -> f.isAvailable() && f.getName().equals(fileName) && f.getLanguage().equals(language) && f.getPublishingHouse().equals(publishingHouse)).orElse(null);	
	}

	//retorna um arquivo com nome "fileName" já alugado pelo usuário com nome "userName", ou null caso não exista algum que satisfaça as condições
	public Rentable getRentedFile(String id, String fileName, String language, String publishingHouse){
		if(getUser(id).hasFile(getFile(fileName, language, publishingHouse))){
			return getFile(fileName, language, publishingHouse);
		}
		return null;
	}
	
	public User getUser(String id){
		return _hasUser(u -> u.getId().equals(id)).orElse(null);
	}

	public Rentable getFile(String name, String language, String publishingHouse){
		return _hasFile(r -> r.getName().equals(name) && r.getLanguage().equals(language) && r.getPublishingHouse().equals(publishingHouse)).orElse(null);
	}

	//ambas as funções _has retornam o primeiro elemento compatível que encontrarem, porque nao sao aceitos duas pessoas com mesmo nome na biblioteca
	//e os livros com nomes repetidos sao adicionados como cópias de um mesmo livro
	private Optional<Rentable> _hasFile(Predicate<Rentable> filter){
		return files
			.stream()
			.filter(filter)
			.findAny();
	}

	private Optional<User> _hasUser(Predicate<User> filter){
		return users
			.stream()
			.filter(filter)
			.findAny();	
	}

	public LocalDate stringToDate(String date){
		String[] parts = date.split("/");
		return LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));

	}

	//transforma a data para ser imprimida de uma maneira padrão, tanto na tela quanto nos arquivos .csv. Null caso a LocalDate seja null
	public String dateToString(LocalDate date){
		if(date != null)
			return date.getDayOfMonth() + "/" + date.getMonthValue() + "/" + date.getYear();
		else
			return "null";
	}

	public void begin(){
		String[] content = null;
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
					user = new Student(content[1], content[2], content[3], stringToDate(content[5]));
				else if(content[0].equals("Teacher"))
					user = new Teacher(content[1], content[2], content[3], stringToDate(content[5]));
				else if(content[0].equals("Community"))
					user = new Community(content[1], content[2], content[3], stringToDate(content[5]));
		
				addUser(user);

				if(!content[4].equals("null")){					//caso o usuário tenha uma data de ban, recolocamos ela no status do usuário no programa
					user.setBan(stringToDate(content[4]));
					
					//se o dia atual for depois do dia máximo de ban, retiriamos o ban do usuário
					if(systemDate.isAfter(user.getBanTime())){
						System.out.println("User " + content[1] + " is no longer banned.");
						user.setBan(null);
					}
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no users content to load.");
		}
		catch(IOException e){
			System.out.println("Error trying to load users content.");
		}

		try{
			br = new BufferedReader(new FileReader(filesData));

			while((input = br.readLine()) != null){
				content = input.split(",");

				Rentable r;

				if(content[1].equals("Book"))
					r = new Book(content[2], content[3], content[4], stringToDate(content[5]));
				else
					r = new Note(content[2], content[3], content[4], stringToDate(content[5]));

					addFile(r);
					
					if(!content[0].equals("none")){
						rentFile(content[0], content[2], content[3], content[4]);
						r.setRentExpirationDate(stringToDate(content[6]));

						time = dateDifference(dateToString(systemDate), content[6]);

						//se a diferença entre a data atual e a data máxima de entrega do livro for positiva, o usuário atrasou a devolução e deve ser banido
						if(time > 0){
							r.setDelay(time);
							System.out.println("Delay on file " + r.getName() + " - " + time + " days.");
							getUser(content[0]).setBan(systemDate.plusDays(time));
						}
					}
				}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Found no files content to load.");
		}
		catch(IOException e){
			System.out.println("Error trying to load files content.");
		}

		systemLoading = false;
	}

	//retorna, em dias, systemDate - date
	private int dateDifference(String systemDate, String date){
		LocalDate dateOfToday = stringToDate(systemDate);
		LocalDate expirationDate = stringToDate(date);
		
		//desnecessário ? period.getDays() retornaria negativo caso systemDate < date ????
		if(dateOfToday.isAfter(expirationDate)){
			Period period = Period.between(expirationDate, dateOfToday);
			System.out.println("New ban " + dateToString(expirationDate) + " " + dateToString(dateOfToday) +" " + period.getDays());
			return period.getDays();
		}
		else
			return 0;
	}

	public void exit(){
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
		data += dateToString(systemDate);

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
			data += u.getId() + separator;
			data += u.getNationality() + separator;
			data += dateToString(u.getBanTime()) + separator;
			data += dateToString(u.getCreationDate());
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
						.map(User::getId)
						.orElse("none") + separator;
			data += r.getType() + separator;
			data += r.getName() + separator;
			data += r.getLanguage() + separator;
			data += r.getPublishingHouse() + separator;
			data += dateToString(r.getCreationDate()) + separator;
			data += dateToString(r.getRentExpirationDate());

			writeLog(data, filesData, type);				//escreve o tipo e o nome em um arquivo csv
			if(!type) type = true;
		}
	}

	private void writeUsersLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added " + u.getType().toLowerCase() + " \"" + u.getName() + "\" at " + dateToString(systemDate) + ".", usersLog, true);
		else if(str.equals("rent")){
			writeLog("Rented " + r.getType().toLowerCase() + " \"" + r.getName() + "\" for " + u.getType().toLowerCase() + " " + u.getName() + " at " + 
				dateToString(systemDate) + ". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
		else{
			writeLog(u.getType() + " " + u.getName() + " refunded " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + dateToString(systemDate) + 
														". User has " + u.getFilesQuantity() + " files now.", usersLog, true);	
		}
	}

	private void writeFilesLog(User u, Rentable r, String str){
		if(str.equals("new"))
			writeLog("Added new " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + dateToString(systemDate) + ".", filesLog, true);
		else if(str.equals("copy"))
			writeLog("Added copy of " + r.getType().toLowerCase() + " \"" + r.getName() + "\" at " + dateToString(systemDate) + ".", filesLog, true);
		else if(str.equals("rent")){
			writeLog(r.getType() + " \"" + r.getName() + "\" was rented by " + u.getType().toLowerCase() + " " + u.getName() + " at " + dateToString(systemDate) +
																													"." , filesLog, true);
		}
		else{
			writeLog(r.getType() + " \"" + r.getName() + "\" was refunded by " + u.getType().toLowerCase() + " " + u.getName() + " at " + dateToString(systemDate)
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