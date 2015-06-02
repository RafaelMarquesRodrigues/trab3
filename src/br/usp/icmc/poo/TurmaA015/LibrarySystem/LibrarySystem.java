
package br.usp.icmc.poo.TurmaA015.LibrarySystem;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;
import br.usp.icmc.poo.TurmaA015.Library.*;
import br.usp.icmc.poo.TurmaA015.MessageBundle.MessageBundle;

import java.io.*;
import java.text.Collator;

public class LibrarySystem {
	private Organizer library;
	private BufferedReader br;
        private MessageBundle messageBundle;

	public static void main(String[] args) {
		LibrarySystem program = new LibrarySystem();
		program.start();
	}

	public void start(){

                messageBundle = new MessageBundle("pt", "BR");
          
		System.out.println(messageBundle.get("start"));

		br = new BufferedReader(new InputStreamReader(System.in));

		library = new Library(messageBundle);
		library.begin();
		
		welcomeScreen();

		String command = "";
	
		try {

			while(!command.equals("exit")){
				command = br.readLine();

				String[] parts = command.split(" ");

				if(parts[0].equals("add")){
				
					if(parts.length > 1)
						commandAdd(parts);
					else
						System.out.println(messageBundle.get("add_usage"));
				}
				
				else if(command.equals("rent file"))
					commandRent(parts);

				else if(command.equals("refund file"))
					commandRefund(parts);

				else if(command.equals("set date"))
					setDate();

				else if(parts[0].equals("show")){
				
					if(parts.length > 1)
						commandShow(parts);
					else
						System.out.println(messageBundle.get("show_usage"));
				
				}
				//limpa todos os arquivos da biblioteca
				else if(command.equals("reset")){
					System.out.println(messageBundle.get("ask_reset"));
					try{
						if(br.readLine().equals(messageBundle.get("yes"))){
							if(library.reset() == 1)
								System.out.println(messageBundle.get("reset_reset"));
							else
								System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
						}
					}
					catch(IOException e){
						System.out.println(messageBundle.get("input_error"));
					}
				}
				else if(command.equals("help"))
					help();

				else if(!command.equals("exit"))
					System.out.println(messageBundle.get("unknown_input"));

			}
		}
		catch(IOException e){
			System.out.println(messageBundle.get("command_error"));
		}

		library.exit();
	}
	
	//escolhe a data para o sistema
	public void setDate(){
		System.out.println(messageBundle.get("date_select"));

		while(!readDate())
			System.out.println(messageBundle.get("date_invalid"));

		System.out.println(messageBundle.get("date_success"));
	}

	private boolean readDate(){
		try {
			String date;
			String[] numbers;

			date = br.readLine();
			numbers = date.split("/");

			if(numbers.length != 3)
				return false;
			
			//datas invalidas ?
			return library.setDate(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]));
		
		}
		catch(IOException e){
			System.out.println(messageBundle.get("date_error"));
		}

		return false;
	}

	//metodo para lidar com um novo arquivo ou usuario
	public void commandAdd(String[] parts){
		try{
			if(parts[1].equals("book") || parts[1].equals("note")){
				System.out.println(messageBundle.get("file_name"));
				String filename = br.readLine();
				
				System.out.println(messageBundle.get("file_language"));
				String language = br.readLine();
				
				System.out.println(messageBundle.get("file_house"));
				
				if(parts[1].equals("book")){
					if(library.addFile(new Book(filename, language, br.readLine(), library.stringToDate(library.getDate()))) == 1)
						System.out.println(messageBundle.get("book_add"));
					else
						System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
				}
				else if(parts[1].equals("note")){
					if(library.addFile(new Note(filename, language, br.readLine(), library.stringToDate(library.getDate()))) == 1)
						System.out.println(messageBundle.get("note_add"));
					else
						System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
				}
				else
					System.out.println(messageBundle.get("add_usage"));
			}
			else if(parts[1].equals("teacher") || parts[1].equals("community") || parts[1].equals("student")){
				int addResult = -1;
				
				System.out.println(messageBundle.get("user_name"));
				String username = br.readLine();
				
				System.out.println(messageBundle.get("user_nationality"));
				String nationality = br.readLine();
				
				System.out.println(messageBundle.get("user_ID"));
				
				if(parts[1].equals("student"))
					addResult = library.addUser(new Student(username, br.readLine(), nationality, library.stringToDate(library.getDate())));		
				else if(parts[1].equals("teacher"))
					addResult = library.addUser(new Teacher(username, br.readLine(), nationality, library.stringToDate(library.getDate())));
				else if(parts[1].equals("community"))	
					addResult = library.addUser(new Community(username, br.readLine(), nationality, library.stringToDate(library.getDate())));
				else
					System.out.println(messageBundle.get("add_usage"));
				
				if(addResult == 1)
					System.out.println(messageBundle.get("user_add"));
				else if(addResult == 0)
					System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
				else
					System.out.println(messageBundle.get("user_repeat"));
			}
			else
				System.out.println(messageBundle.get("add_usage"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("input_error"));
		}
	}
	
	//metodo para alugar arquivos da biblioteca
	public void commandRent(String[] parts){
		try{
			System.out.println(messageBundle.get("rent_code"));
			String code = br.readLine();
			
			System.out.println(messageBundle.get("rent_ID"));
			String id = br.readLine();

			int rentResult = library.rentFile(id, code);
			
			if(rentResult == 0)
				System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
			else if(rentResult == -1)
				System.out.println(messageBundle.get("userID") + id + messageBundle.get("userNotFound"));
			else if(rentResult == -2)
				System.out.println(messageBundle.get("book_code") + code + messageBundle.get("fileNotFound"));
			else if(rentResult == -3)
				System.out.println(messageBundle.get("thefile") + library.getFileAtIndex(code).getName() + " (Code: " + code  + "");
			else if(rentResult == -4)
				System.out.println(messageBundle.get("user") + library.getUser(id).getName() + " (ID: " + id  + messageBundle.get("fileAlreadyRented"));
			else if(rentResult == -5)
				System.out.println(messageBundle.get("user") + library.getUser(id).getName() + " (ID: " + id  + messageBundle.get("noPermission") + library.getFileAtIndex(code).getName() + messageBundle.get("code") + code  + ").\n");
			else if(rentResult == -6)
				System.out.println(messageBundle.get("user") + library.getUser(id).getName() + " (ID: " + id  + messageBundle.get("cantRent") + library.getFileAtIndex(code).getName() + messageBundle.get("code") + code  + messageBundle.get("isBanned"));
			else		
				System.out.println(messageBundle.get("fileRented"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("input_error"));
		}
	}

	public void commandRefund(String[] parts){
		try{
			System.out.println(messageBundle.get("rent_code"));
			String code = br.readLine();

			System.out.println(messageBundle.get("refund_code"));
			String id = br.readLine();

			int refundResult = library.refundFile(id, code);

			if(refundResult == 0)
				System.out.println(messageBundle.get("read_mode1") + library.getDate() + messageBundle.get("read_mode2"));
			else if(refundResult == -1)
				System.out.println(messageBundle.get("userID") + id + messageBundle.get("notFound"));
			else if(refundResult == -2)
				System.out.println(messageBundle.get("bookCode") + code + messageBundle.get("notFound"));
			else if(refundResult == -3)
				System.out.println(messageBundle.get("userNoHaveFile"));
			else		
				System.out.println(messageBundle.get("fileRefunded"));
		}
		catch(IOException e){
			System.out.println(messageBundle.get("input_error"));
		}
	}

	public void commandShow(String[] parts){
		if(parts[1].equals("users")){
			if(parts.length < 3)
				library.showUsers((User u) -> true);
			else if(parts[2].equals("added"))
				library.showUsersAdded();
			else
				System.out.println(messageBundle.get("type_unrecognized"));
		}
		else if(parts[1].equals("files")){
			if(parts.length < 3)
				library.showFiles((String s) -> true);
			else if(parts[2].equals("added"))
				library.showFilesAdded();
			else
				System.out.println(messageBundle.get("type_unrecognized"));
		}
		else if(parts[1].equals("filename") && parts.length > 2)
			library.showFiles(s -> {
				Collator c = Collator.getInstance();
				c.setStrength(Collator.PRIMARY);
				return c.compare(s.substring(0, s.indexOf(",")), rebuildName(parts)) == 0;
			});
		else if(parts[1].equals("username") && parts.length > 2){
			library.showUsers(u -> {
				Collator c = Collator.getInstance();
				c.setStrength(Collator.PRIMARY);
				return c.compare(u.getName(), rebuildName(parts)) == 0;
			});
		}
		else if(parts[1].equals("rents"))
			library.showRents();
		else if(parts[1].equals("refunds"))
			library.showRefunds();
		else
			System.out.println(messageBundle.get("type_unrecognized"));
	}
	
	private String rebuildName(String[] parts){
		String str = "";
		for(int i = 2; i < parts.length; i++){
			str += parts[i];
			if(i != parts.length -1)
				str += " ";
		}
		return str;
	}

	public void _help(){
		System.out.println(messageBundle.get("_help1"));
		System.out.println(messageBundle.get("_help2"));
		System.out.println(messageBundle.get("_help3"));
		System.out.println(messageBundle.get("_help4"));
		System.out.println(messageBundle.get("_help5"));
		System.out.println(messageBundle.get("_help6"));
		System.out.println(messageBundle.get("_help7"));
		System.out.println(messageBundle.get("_help8"));
		System.out.println(messageBundle.get("_help9"));
	}

	public void help(){
		System.out.println("\n************************************************************************************");
		_help();
	}

	public void welcomeScreen(){
		System.out.println("\n*************************************************************************************");
		System.out.println(messageBundle.get("welcome"));
		System.out.println("**                                                                                 **");
		_help();
	}
}