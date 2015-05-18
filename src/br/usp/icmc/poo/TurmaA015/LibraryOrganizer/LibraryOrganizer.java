
package br.usp.icmc.poo.TurmaA015.LibraryOrganizer;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;
import br.usp.icmc.poo.TurmaA015.Library.*;

import java.io.*;
import java.util.*;

public class LibraryOrganizer {
	public static void main(String[] args){
		System.out.println("System starting...");
		
		System.out.println("Initializing library...");
		Organizer library = new Library();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = "";


		//TA UM LIXO ISSO é só pra testar se os comandos funcionam e se as coisas funcionam, tem que fazer mais bonito depois
		//pra o usuario pegar um livro, ele vai ter que fazer "login" na biblioteca, aí qualquer pedido que ele fizer vai tar no nome dele
		//se ele pedir pra adicionar alguma pessoa tem que dar erro pq ele nao tem permissao pra fazer isso.
		try {
			while(!command.equals("exit")){
				command = br.readLine();

				if(command.equals("add book")){
					System.out.println("Please enter the name of the book you want to add: ");
					library.add(new Book(br.readLine()));
				}
				else if(command.equals("add student")){
					System.out.println("Please enter the name of the user you want to add: ");

					if(library.newUser(new Student(br.readLine())))
						System.out.println("Added new user successfully");
					else
						System.out.println("Theres already a student with this name !");

				}
				else if(command.equals("search archive")){
					System.out.println("Please enter the name of the archive you want to search: ");

					String book = br.readLine();

					if(library.hasArchive(book))
						System.out.println("Library has book " + book +  " !");
					else		
						System.out.println("Library doesn't have the book " + book + " :(");
				}
				else if(command.equals("search user")){
					System.out.println("Please enter the name of the archive you want to search: ");
					String name = br.readLine();

					Person p = library.getUser(name);

					if(p != null)
						System.out.println("Someone called " + p.getName() + " uses the library resources !");
					else		
						System.out.println("User " + name + " isn't registered :(");
				}
			}
		}
		catch(Exception e){
			System.out.println("Error trying to get user input");
		}
	}
}