
package br.usp.icmc.poo.TurmaA015.LibraryOrganizer;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;
import br.usp.icmc.poo.TurmaA015.Library.*;

import java.io.*;
import java.util.*;

public class LibraryOrganizer {
	public static void main(String[] args){
		System.out.println("System starting...");
		Organizer library = new Library();

		library.add(new Book("Book x"));
		library.newUser(new Student("Jorgolias"));
	
		if(library.hasArchive("Book x"))
			System.out.println("Library has book x !");

		Person p = library.getUser("Jorgolias");

		if(p != null)
			System.out.println(p.getName());
	}
}