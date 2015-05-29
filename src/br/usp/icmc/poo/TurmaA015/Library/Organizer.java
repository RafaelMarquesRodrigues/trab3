
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.time.LocalDate;

public interface Organizer {
	//métodos para adicionar pessoas e arquivos que podem ser alugados
	int addFile(Rentable r);
	int addUser(User u);

	//métodos para verificar as pessoas que tem algum arquivo do local e quais arquivos existem no local
	User getUser(String name);
	Rentable getFile(String name);

	int rentFile(String name, String str);
	int refundFile(String name, String str);

	void exit();
	void begin();

	int reset();

	void showUsers();
	void showUsersAdded();
	void showFiles();
	void showFilesAdded();
	void showRents();
	void showRefunds();

	LocalDate stringToDate(String date);
	String dateToString(LocalDate date);
	
	boolean setDate(int day, int month, int year);

	String getDate();
}