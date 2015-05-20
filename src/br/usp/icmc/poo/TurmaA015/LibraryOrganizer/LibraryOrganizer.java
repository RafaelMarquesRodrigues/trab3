
package br.usp.icmc.poo.TurmaA015.LibraryOrganizer;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;
import br.usp.icmc.poo.TurmaA015.Library.*;

import java.io.*;
import java.util.*;

public class LibraryOrganizer {
	private Organizer library;
	private BufferedReader br;

	public static void main(String[] args) {
		LibraryOrganizer program = new LibraryOrganizer();
		program.start();
	}

	public void start(){
		System.out.println("System starting...");
		
		System.out.println("Initializing library...");
		String command = "";
	
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			library = new Library();

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
						System.out.println("Usage \"command show\": show <type> [users] [files].");
				
				}
				else if(command.equals("help"))
					help();
				else if(!command.equals("exit"))
					System.out.println("Unrecognized command. Try \"help\" to see available commands.");

				System.out.println("\n");
			}
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandAdd(String[] parts){
		try{
			if(parts[1].equals("book")){
				System.out.println("Please enter the name of the book you want to add: ");
				library.addFile(new Book(br.readLine()));
			}
			else if(parts[1].equals("note")){
				System.out.println("Please enter the name of the note you want to add: ");
				library.addFile(new Note(br.readLine()));
			}
			else if(parts[1].equals("student")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Student(br.readLine())))
					System.out.println("Added new user successfully.");
				else
					System.out.println("Theres already a student with this name !");
			}
			else if(parts[1].equals("teacher")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Teacher(br.readLine())))
					System.out.println("Added new user successfully");
				else
					System.out.println("Theres already a teacher with this name !");
			}
			else if(parts[1].equals("community")){
				System.out.println("Please enter the name of the user you want to add: ");
				if(library.addUser(new Community(br.readLine())))
					System.out.println("Added new user successfully.");
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
			System.out.println("Please enter the name of the archive and the person: ");
			String fileName = br.readLine();
			String userName = br.readLine();

			int rentResult = library.makeRent(userName, fileName);

			if(rentResult == -1)
				System.out.println("User " + userName + " not found.");
			else if(rentResult == -2)
				System.out.println("File " + fileName + " not found.");
			else if(rentResult == -3)
				System.out.println("This book is already rented and there is no copies of it available.");
			else if(rentResult == -4)
				System.out.println("User " + userName + " already has max number of rented files.");
			else if(rentResult == -5)
				System.out.println("User " + userName + " doesn't have permission to rent the file " + fileName + ".");
			else		
				System.out.println("File rented !");
		}
		catch(IOException e){
			System.out.println("Error trying to get user input.");
		}
	}

	public void commandRefund(String[] parts){
		try{
			System.out.println("Please enter the name of the archive and the person: ");
			String fileName = br.readLine();
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
	}

	public void help(){
		System.out.println("===========================================================\n");
		System.out.println("Library available commands: ");
		System.out.println("add <type> [book] [note] [student] [teacher] [community]");
		System.out.println("rent file");
		System.out.println("refund file");
		System.out.println("show <type> [uses] [files]");
		System.out.println("\n===========================================================");
	}

	public void commandSearch(String[] parts){

	}
}