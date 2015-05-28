
package br.usp.icmc.poo.TurmaA015.LibrarySystem;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;
import br.usp.icmc.poo.TurmaA015.Library.*;

import java.io.*;
import java.util.*;

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

		library = new Library();
		System.out.println("Select the date to start the system: (xx/xx/xxxx)");

		while(!readDate())
			System.out.println("Please enter a valid date. (xx/xx/xxxx)");

		welcomeScreen();

		System.out.println("Initializing library...");
		library.loadContent();
		
		String command = "";
	
		try {

			while(!command.equals("exit")){
				command = br.readLine();

				String[] parts = command.split(" ");

				if(parts[0].equals("add")){
				
					if(parts.length > 1)
						commandAdd(parts);
					else
						System.out.println("Usage \"command add\": add <type> [book] [note] [student] [teacher] [community].");
				
				}
				
				else if(command.equals("rent file"))
					commandRent(parts);

				else if(command.equals("refund file"))
					commandRefund(parts);

				else if(parts[0].equals("show")){
				
					if(parts.length > 1)
						commandShow(parts);
					else
						System.out.println("Usage \"command show\": show <type> [users] [files] [rents] [refunds].");
				
				}
				else if(command.equals("reset")){
					System.out.println("Are you sure you want to reset the library ? All information will be lost after the process. [Yes/No]");
					try{
						if(br.readLine().equals("yes")){
							System.out.println("Reseting library...");
							library.reset();
						}
					}
					catch(IOException e){
						System.out.println("Error trying to get user input.");
					}
				}
				else if(command.equals("help"))
					help();

				else if(!command.equals("exit"))
					System.out.println("Unrecognized command. Try \"help\" to see available commands.");

			}
		

			System.out.println("\n\n");

		}
		catch(IOException e){
			System.out.println("Error trying to get user command.");
		}

		library.saveContent();
	}

	public boolean readDate(){
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
			if(parts[1].equals("book")){
				System.out.println("Please enter the name of the book you want to add: ");
				library.addFile(new Book(br.readLine()));
				System.out.println("Added new book successfully.\n");
			}
			else if(parts[1].equals("note")){
				System.out.println("Please enter the name of the note you want to add: ");
				library.addFile(new Note(br.readLine()));
				System.out.println("Added new note successfully.\n");
			}
			else if(parts[1].equals("student")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Student(br.readLine())))
					System.out.println("Added new user successfully.\n");
				else
					System.out.println("Theres already a student with this name !");
			}
			else if(parts[1].equals("teacher")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Teacher(br.readLine())))
					System.out.println("Added new user successfully\n");
				else
					System.out.println("Theres already a teacher with this name !");
			}
			else if(parts[1].equals("community")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Community(br.readLine())))
					System.out.println("Added new user successfully.\n");
				else
					System.out.println("Theres already a community with this name !");
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
			
			System.out.println("Please enter the name of the user: ");
			String userName = br.readLine();

			int rentResult = library.rentFile(userName, fileName);

			if(rentResult == -1)
				System.out.println("User " + userName + " not found.");
			else if(rentResult == -2)
				System.out.println("File " + fileName + " not found.");
			else if(rentResult == -3)
				System.out.println("The book " + fileName + " is already rented, and there are no copies available.");
			else if(rentResult == -4)
				System.out.println("User " + userName + " already has max number of rented files.");
			else if(rentResult == -5)
				System.out.println("User " + userName + " doesn't have permission to rent the file " + fileName + ".");
			else if(rentResult == -6)
				System.out.println("User " + userName + " cant rent the file " + fileName + " because he/she is banned.");
			else		
				System.out.println("File rented !");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandRefund(String[] parts){
		try{
			System.out.println("Please enter the name of the archive: ");
			String fileName = br.readLine();

			System.out.println("Please enter the name of the user: ");
			String userName = br.readLine();

			int refundResult = library.refundFile(userName, fileName);

			if(refundResult == -1)
				System.out.println("User " + userName + " not found.");
			else if(refundResult == -2)
				System.out.println("File " + fileName + " not found.");
			else if(refundResult == -3)
				System.out.println("The user doesnt have this book.");
			else		
				System.out.println("File refunded !");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandShow(String[] parts){
		if(parts[1].equals("users"))
			library.showUsers();
		else if(parts[1].equals("files"))
			library.showFiles();
		else if(parts[1].equals("rents")){
			System.out.println("Insert the date pls:");
			try {
				library.showRents(br.readLine());
			}
			catch(IOException e){
				System.out.println("Problem reading user input.");
			}
		}
		else if(parts[1].equals("refunds")){
			System.out.println("Insert the date pls:");
			try {
				library.showRefunds(br.readLine());
			}
			catch(IOException e){
				System.out.println("Problem reading user input.");
			}
		}
		else
			System.out.println("Unrecognized type. Supported types are [users] [files] [rents] [refunds].");
	}

	public void help(){
		System.out.println("\n===========================================================\n");
		System.out.println("Library available commands: ");
		System.out.println("add <type> [book] [note] [student] [teacher] [community]");
		System.out.println("rent file");
		System.out.println("refund file");
		System.out.println("show <type> [users] [files] [rents] [refunds]");
		System.out.println("reset");
		System.out.println("\n===========================================================\n");
	}

	public void welcomeScreen(){
		System.out.println("\n***********************************************************************");
		System.out.println("**                    Welcome to the library !                       **");
		System.out.println("**                                                                   **");
		System.out.println("**       Library available commands:                                 **");
		System.out.println("**       add <type> [book] [note] [student] [teacher] [community]    **");
		System.out.println("**       rent file                                                   **");
		System.out.println("**       refund file                                                 **");
		System.out.println("**       show <type> [users] [files] [rents] [refunds]               **");
		System.out.println("**       reset                                                       **");
		System.out.println("***********************************************************************\n");
	}
}