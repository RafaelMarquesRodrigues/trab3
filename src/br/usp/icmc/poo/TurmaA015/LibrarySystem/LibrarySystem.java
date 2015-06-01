
package br.usp.icmc.poo.TurmaA015.LibrarySystem;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;
import br.usp.icmc.poo.TurmaA015.Library.*;

import java.io.*;

public class LibrarySystem {
	private Organizer library;
	private BufferedReader br;

	public static void main(String[] args) {
		LibrarySystem program = new LibrarySystem();
		program.start();
	}

	public void start(){

		System.out.println("System starting...");

		br = new BufferedReader(new InputStreamReader(System.in));


		System.out.println("Initializing library...");

		library = new Library();
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
						System.out.println("Usage \"command add\": add <type> [book] [note] [student] [teacher] [community].\n");
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
						System.out.println("Usage \"command show\": show <type> [users] [files] [rents] [refunds].\n");
				
				}
				else if(command.equals("reset")){
					System.out.println("Are you sure you want to reset the library ? All information will be lost after the process. [Yes/No]");
					try{
						if(br.readLine().equals("yes")){
							if(library.reset() == 1)
								System.out.println("Reseting library...\n");
							else
								System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
						}
					}
					catch(IOException e){
						System.out.println("Error trying to get user input.");
					}
				}
				else if(command.equals("help"))
					help();

				else if(!command.equals("exit"))
					System.out.println("Unrecognized command. Try \"help\" to see available commands.\n");

			}
		}
		catch(IOException e){
			System.out.println("Error trying to get user command.\n");
		}

		library.exit();
	}

	public void setDate(){
		System.out.println("Select the date to start the system: (xx/xx/xxxx)");

		while(!readDate())
			System.out.println("Please enter a valid date. (xx/xx/xxxx)");

		System.out.println("Changed date successfully.\n");
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
			System.out.println("Error trying to read date.");
		}

		return false;
	}

	public void commandAdd(String[] parts){
		try{
			if(parts[1].equals("book") || parts[1].equals("note")){
				System.out.println("Please enter the name of the file you want to add: ");
				String filename = br.readLine();
				
				System.out.println("Please enter the language of the file you want to add: ");
				String language = br.readLine();
				
				System.out.println("Please enter the publishing house of the file you want to add: ");
				
				if(parts[1].equals("book")){
					if(library.addFile(new Book(filename, language, br.readLine(), library.stringToDate(library.getDate()))) == 1)
						System.out.println("Added new book successfully.\n");
					else
						System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
				}
				else if(parts[1].equals("note")){
					if(library.addFile(new Note(filename, language, br.readLine(), library.stringToDate(library.getDate()))) == 1)
						System.out.println("Added new book successfully.\n");
					else
						System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
				}
				else
					System.out.println("Usage command \"add\": add <type> [book] [note] [student] [teacher] [community].");
			}
			else if(parts[1].equals("teacher") || parts[1].equals("community") || parts[1].equals("student")){
				int addResult = -1;
				
				System.out.println("Please enter the name of the user you want to add: ");
				String username = br.readLine();
				
				System.out.println("Please enter the nationality of the user you want to add: ");
				String nationality = br.readLine();
				
				System.out.println("Please enter the id of the user you want to add: ");
				
				if(parts[1].equals("student"))
					addResult = library.addUser(new Student(username, br.readLine(), nationality, library.stringToDate(library.getDate())));		
				else if(parts[1].equals("teacher"))
					addResult = library.addUser(new Teacher(username, br.readLine(), nationality, library.stringToDate(library.getDate())));
				else if(parts[1].equals("community"))	
					addResult = library.addUser(new Community(username, br.readLine(), nationality, library.stringToDate(library.getDate())));
				else
					System.out.println("Usage \"command add\": add <type> [book] [note] [student] [teacher] [community].");
				
				if(addResult == 1)
					System.out.println("Added new user successfully.\n");
				else if(addResult == 0)
					System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
				else
					System.out.println("Theres already a user with this id !\n");
			}
			else
				System.out.println("Usage \"command add\": add <type> [book] [note] [student] [teacher] [community].");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandRent(String[] parts){
		try{
			System.out.println("Please enter the name of the archive: ");
			String fileName = br.readLine();
			
			System.out.println("Please enter the language of the archive: ");
			String language = br.readLine();
			
			System.out.println("Please enter the publishing house of the archive: ");
			String publishingHouse = br.readLine();
			
			System.out.println("Please enter the id of the user: ");
			String id = br.readLine();

			int rentResult = library.rentFile(id, fileName, language, publishingHouse);
			
			if(rentResult == 0)
				System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
			else if(rentResult == -1)
				System.out.println("User id " + id + " not found.\n");
			else if(rentResult == -2)
				System.out.println("File " + fileName + " not found.\n");
			else if(rentResult == -3)
				System.out.println("The book " + fileName + " is already rented, and there are no copies available.\n");
			else if(rentResult == -4)
				System.out.println("User id " + id + " already has max number of rented files.\n");
			else if(rentResult == -5)
				System.out.println("User id " + id + " doesn't have permission to rent the file " + fileName + ".\n");
			else if(rentResult == -6)
				System.out.println("User id " + id + " cant rent the file " + fileName + " because he/she is banned.\n");
			else		
				System.out.println("File rented !\n");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandRefund(String[] parts){
		try{
			System.out.println("Please enter the name of the archive: ");
			String fileName = br.readLine();
			
			System.out.println("Please enter the language of the archive: ");
			String language = br.readLine();
			
			System.out.println("Please enter the publishing house of the archive: ");
			String publishingHouse = br.readLine();
			
			System.out.println("Please enter the id of the user: ");
			String id = br.readLine();

			int refundResult = library.refundFile(id, fileName, language, publishingHouse);

			if(refundResult == 0)
				System.out.println("You are on read only mode ! Please return to " + library.getDate() + " to perform this action.\n");
			if(refundResult == -1)
				System.out.println("User id " + id + " not found.\n");
			else if(refundResult == -2)
				System.out.println("File " + fileName + " not found.\n");
			else if(refundResult == -3)
				System.out.println("The user doesnt have this book.\n");
			else		
				System.out.println("File refunded !\n");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandShow(String[] parts){
		if(parts[1].equals("users")){
			if(parts.length < 3)
				library.showUsers();
			else if(parts[2].equals("added"))
				library.showUsersAdded();
			else
				System.out.println("Unrecognized type. Supported types are [users] [files] [rents] [refunds] [users added] [files added].\n");
		}
		else if(parts[1].equals("files")){
			if(parts.length < 3)
				library.showFiles();
			else if(parts[2].equals("added"))
				library.showFilesAdded();
			else
				System.out.println("Unrecognized type. Supported types are [users] [files] [rents] [refunds] [users added] [files added].\n");
		}
		else if(parts[1].equals("rents"))
			library.showRents();
		else if(parts[1].equals("refunds"))
			library.showRefunds();
		else
			System.out.println("Unrecognized type. Supported types are [users] [files] [rents] [refunds] [users added] [files added].\n");
	}
	
	public void commandSearch(){
		
	}

	public void _help(){
		System.out.println("**       Library available commands:                                               **");
		System.out.println("**       add <type> [book] [note] [student] [teacher] [community]                  **");
		System.out.println("**       rent file                                                                 **");
		System.out.println("**       refund file                                                               **");
		System.out.println("**       show <type> [users] [files] [rents] [refunds] [users added] [files added] **");
		System.out.println("**       set date                                                                  **");
		System.out.println("**       reset                                                                     **");
		System.out.println("*************************************************************************************\n");	
	}

	public void help(){
		System.out.println("\n************************************************************************************");
		_help();
	}

	public void welcomeScreen(){
		System.out.println("\n*************************************************************************************");
		System.out.println("**                    Welcome to the library !                                     **");
		System.out.println("**                                                                                 **");
		_help();
	}
}