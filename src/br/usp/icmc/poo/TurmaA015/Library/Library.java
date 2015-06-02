
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.MessageBundle.MessageBundle;
import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.io.PrintWriter;
import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.function.*;

public class Library implements Organizer {
	private ArrayList<User> users;			//guarda os dados de cada usuário
	private ArrayList<Rentable> files;	 	//guarda todos os arquivos da biblioteca
	private String refundsData;
	private String rentsData;
	private String usersData;
	private String filesData;
	private String systemData;
	private LocalDate systemDate;
	private LocalDate today;
	private boolean systemLoading;
	private boolean readOnly;
        private MessageBundle messageBundle;

	public Library(MessageBundle messageBundle) {
                this.messageBundle = messageBundle;
		users = new ArrayList<User>();
		files = new ArrayList<Rentable>();


		File file = new File("data/users.csv");
		usersData = file.getPath();

		file = new File("data/files.csv");
		filesData = file.getPath();

		file = new File("data/rents.csv");
		rentsData = file.getPath();

		file = new File("data/refunds.csv");
		refundsData = file.getPath();
		
		systemDate = LocalDate.now();
		today = systemDate;
		readOnly = false;

	}

	public boolean setDate(int day, int month, int year){
		if(checkDate(day, month, year)){

			systemDate = LocalDate.of(year, month, day);

			if(today.isAfter(systemDate)){
				readOnly = true;
				System.out.println(messageBundle.get("sysOperating") + dateToString(systemDate) + messageBundle.get("read_mode3"));
			}
			else{
				exit();
				try {
					PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(systemData, false)));
					pw.println(getSystemDate());
					pw.close();
				}
				catch(FileNotFoundException e){}
				catch(IOException e){
					System.out.println(e);
				}
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
    
    public String getSystemDate(){
    	return dateToString(systemDate);
    }

    public String getDate(){
    	return dateToString(today);
    }

	public int reset(){
		if(readOnly)
			return 0;

		try {

			File f = new File("logs/users.log");
			f.delete();
			f = new File("logs/files.log");
			f.delete();
			f = new File("data/users.csv");
			f.delete();
			f = new File("data/files.csv");
			f.delete();
			f = new File("data/rents.csv");
			f.delete();
			f = new File("data/refunds.csv");
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
		
		return 1;
	}

	//adiciona um novo usuario na biblioteca
	public int addUser(User u){
		if(readOnly)
			return 0;

		User newUser = getUser(u.getId());
	
		if(newUser == null){
			users.add(u);
			return 1;
		}

		return -1;
	}

	public int rentFile(String id, String index){
		if(readOnly)
			return 0;

		User user = getUser(id);
		Rentable rentedFile = getFileAtIndex(index);

		if(user == null)												//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)											//não existe o livro requisitado
			return -2;
		
		rentedFile = getAvailableFileAtIndex(index);

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
		
		return 1;	//ok
	}

	public int refundFile(String id, String index){
		if(readOnly)
			return 0;

		User user = getUser(id);
		Rentable rentedFile = getFileAtIndex(index);
		
		if(user == null)						//não existe a pessoa requisitada
			return -1;
		if(rentedFile == null)					//não existe o livro requisitado
			return -2;

		rentedFile = getRentedFileAtIndex(id, index);

		if(!user.hasFile(rentedFile))			//se o usuário não tiver o livro que ele está tentando devolver
			return -3;
		
		user.refundFile(rentedFile);		 	//o usuário devolve o livro que está com ele
		rentedFile.refund();					//o livro é marcado como disponível novamente
		rentedFile.removeDelay();				//retira-se qualquer possível atraso no livro
	
		if(!systemLoading)
			writeEvent(user, rentedFile, refundsData);

		return 1;
	}

	public void showUsers(Predicate<User> filter){
		System.out.println(messageBundle.get("usersRegistered"));
		
		users
			.stream()
			.filter(filter)
			.forEach((user) -> {
				System.out.println("\n================================================\n");
				System.out.println(user.getType() + " " + user.getName());
	
				System.out.println(messageBundle.get("userAddedIn") + " " + dateToString(user.getCreationDate()));
				System.out.println(messageBundle.get("ID") + " " + user.getId());
				System.out.println(messageBundle.get("nationality") + " " + user.getNationality());
				
				if(user.isBanned())
					System.out.println(messageBundle.get("bannedUntil") + " " +dateToString(user.getBanTime()));
				
				if(user.getFilesQuantity() > 0){
					System.out.println(messageBundle.get("rentedForUser"));
	
					for(Rentable r : files){
						if(user.hasFile(r)){
							System.out.print(r.getType() + " " + r.getName() + " " + messageBundle.get("expirationDate") + " " + dateToString(r.getRentExpirationDate()) + " " + messageBundle.get("fileCode") + " " + files.indexOf(r));
							
							if(r.getDelay() != 0)
								System.out.print(messageBundle.get("pleaseRefund"));
						
						System.out.print("\n");
						}
					}
				}
				else
					System.out.println(messageBundle.get("noBooksRented"));
			});
		if(users.size() == 0)
			System.out.println(messageBundle.get("noUsersYet"));

		System.out.println("\n================================================\n");
	}

	public void showFiles(Predicate<String> filter){
		
		//mapeia cada nome de livro com sua respectiva quantidade de cópias
		Map<String, Long> filesMap = files 
									.stream()
									.collect(Collectors.groupingBy((r) -> r.getName() + "," + r.getType() + "," + r.getLanguage() + "," + r.getPublishingHouse(), Collectors.mapping((r) -> r.getType() + "," + r.getName() + "," + r.getLanguage() + "," + r.getPublishingHouse(), Collectors.counting())));
		
		if(files.size() > 0){
			System.out.println(messageBundle.get("filesRegistered"));
			
			filesMap.keySet()
				.stream()
				.filter(filter)
				.sorted(String.CASE_INSENSITIVE_ORDER.reversed())
				.forEach((s) -> {
					String parts[] = s.split(",");
					Rentable r = getFile(parts[0], parts[2], parts[3]);
					System.out.println("\n================================================\n");
					System.out.println(r.getType() + " \"" + r.getName() + "\"");
					System.out.println(messageBundle.get("language2") + " " + r.getLanguage());
					System.out.println(messageBundle.get("house2") + " " +  r.getPublishingHouse());
					System.out.println(messageBundle.get("copies2") + " " +  filesMap.get(s));
					System.out.println(messageBundle.get("fileCode2") + " " +  (getIndexOfAvailableCopy(r) == -1 ? messageBundle.get("fileNotAvaible") : getIndexOfAvailableCopy(r) + "."));
				});
		}
		else
			System.out.println(messageBundle.get("noFilesYet"));
			
		System.out.println("\n================================================\n");
	}
	
	private int getIndexOfAvailableCopy(Rentable r){
		for(Rentable file : files){
			if(file.getName().equals(r.getName()) && file.getLanguage().equals(r.getLanguage()) && file.getPublishingHouse().equals(r.getPublishingHouse()) && file.isAvailable())
				return files.indexOf(file);
		}
		
		return -1;
	}

	public void showUsersAdded(){
		BufferedReader br;
		boolean userAdded = false;

		System.out.println(messageBundle.get("usersShow") + " " + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(usersData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					userAdded = true;
					System.out.println(parts[0] + " " + parts[1] + " - " + messageBundle.get("nationality") + parts[3] + " - " + messageBundle.get("ID") + parts[2]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noUsersShow"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("loadError"));
		}

		if(!userAdded)
			System.out.println(messageBundle.get("noUsersAdded") + " " + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}

	public void showFilesAdded(){
		BufferedReader br;
		boolean fileAdded = false;

		System.out.println(messageBundle.get("filesShow") + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(filesData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					fileAdded = true;
					System.out.println(parts[1] + " " + parts[2] + messageBundle.get("language") + parts[3] + messageBundle.get("house") + parts[4]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noFilesShow"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("loadError"));
		}

		if(!fileAdded)
			System.out.println(messageBundle.get("noFilesAdded") + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}
	
	//mostra todos os alugueis ocorrendo atualmente
	public void showRents(){
		BufferedReader br;
		boolean rentMade = false;

		System.out.println(messageBundle.get("rentShow") + dateToString(systemDate) + " **\n");
		
		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(rentsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					rentMade = true;
					User u = getUser(parts[0]);
					System.out.println(u.getType() + " " + u.getName() + messageBundle.get("rented") + parts[1].toLowerCase() + " " + parts[2] + messageBundle.get("language") + parts[3] + messageBundle.get("house") + parts[4]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noRentsShow"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("load_error"));
		}

		if(!rentMade)
			System.out.println(messageBundle.get("NoRentsMade") + dateToString(systemDate) + ".");
		
		System.out.println("\n================================================\n");
	}
	
	public void showRefunds(){
		BufferedReader br;
		boolean refundMade = false;

		System.out.println(messageBundle.get("refundShow") + dateToString(systemDate) + " **\n");

		System.out.println("\n================================================\n");

		try{
			String input = null;
			String[] parts = null;

			br = new BufferedReader(new FileReader(refundsData));

			while((input = br.readLine()) != null){
				parts = input.split(",");

				if(parts[5].equals(dateToString(systemDate))){
					refundMade = true;
					User u = getUser(parts[0]);
					System.out.println(u.getType() + " " + u.getName() + messageBundle.get("refunded") + parts[1].toLowerCase() + " " + parts[2] + messageBundle.get("language") + parts[3] + messageBundle.get("house") + parts[4]);
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noRefundsShow"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("loadError"));
		}

		if(!refundMade)
			System.out.println(messageBundle.get("noRefundsMade") + dateToString(systemDate) + ".");

		System.out.println("\n================================================\n");
	}

	public Rentable getRentedFileAtIndex(String id, String index){
		if(files.size() > Integer.parseInt(index) && getUser(id).hasFile(files.get(Integer.parseInt(index)))){
			return files.get(Integer.parseInt(index));
		}		
		return null;
	}
	
	public Rentable getFileAtIndex(String index){
		if(files.size() > Integer.parseInt(index) && Integer.parseInt(index) > 0)
			return files.get(Integer.parseInt(index));
		return null;
	}
	
	public Rentable getAvailableFileAtIndex(String index){
		if(files.size() > Integer.parseInt(index) && files.get(Integer.parseInt(index)).isAvailable())
				return files.get(Integer.parseInt(index));
		return null;
	}
	
	public User getUser(String id){
		return _hasUser(u -> u.getId().equals(id)).orElse(null);
	}

	public Rentable getAvailableFile(String name, String language, String publishingHouse){
		return _hasFile(r -> r.isAvailable() && r.getName().equals(name) && r.getLanguage().equals(language) && r.getPublishingHouse().equals(publishingHouse)).orElse(null);
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
		long time;

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
						System.out.println(messageBundle.get("user") + content[1] + messageBundle.get("cancelBan"));
						user.setBan(null);
					}
				}
			}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noUsers"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("loadUsersError"));
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
						rentFile(content[0], new Integer(files.indexOf(getAvailableFile(content[2], content[3], content[4]))).toString());
						r.setRentExpirationDate(stringToDate(content[6]));

						time = stringToDate(content[6]).until(systemDate, ChronoUnit.DAYS);

						//se a diferença entre a data atual e a data máxima de entrega do livro for positiva, o usuário atrasou a devolução e deve ser banido
						if(time > 0){
							r.setDelay((int) time);
							System.out.println(messageBundle.get("delay") + r.getName() + " - " + time + messageBundle.get("days"));
							getUser(content[0]).setBan(systemDate.plusDays(time));
						}
					}
				}

			br.close();
		}
		catch(FileNotFoundException e){
			System.out.println(messageBundle.get("noFiles"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("loadFilesError"));
		}
		
		
		File file = new File("data/system.csv");
		systemData = file.getPath();
		
		if(file.exists()){
			try {
				br = new BufferedReader(new FileReader(systemData));
				String date = br.readLine();
				
				//se a ultima altera��o foi feita no futuro, o sistema reinicia na data atual, mas no modo readOnly
				//e poe o dia da ultima altera��o em "today"
				if(stringToDate(date).isAfter(systemDate)){
					today = stringToDate(date);
					readOnly = true;
					System.out.println(messageBundle.get("sysOperating") + dateToString(systemDate) + messageBundle.get("read_mode3"));
				}
				else{
					today = systemDate;
					readOnly = false;
				}
				
				br.close();
			}
			catch(FileNotFoundException e){}
			catch(IOException e){
				System.out.println(e);
			}
		}
		
		systemLoading = false;
	}

	public void exit(){
		writeUsersData();
		writeFilesData();
	}

	private void writeEvent(User u, Rentable r, String fileName){
		String separator = ",";
		String data = "";

		data += u.getId() + separator;
		data += r.getType() + separator;
		data += r.getName() + separator;
		data += r.getLanguage() + separator;
		data += r.getPublishingHouse() + separator;
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

	private void writeLog(String str, String filename, Boolean type){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename, type)));
			pw.println(str);
			pw.close();
		}
		catch(IOException e){
			System.out.println(messageBundle.get("open_error"));
		}
	}
}