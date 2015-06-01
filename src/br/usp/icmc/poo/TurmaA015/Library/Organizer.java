
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.User.*;

import java.time.LocalDate;
import java.util.function.Predicate;

public interface Organizer {
	//métodos para adicionar pessoas e arquivos que podem ser alugados
	int addFile(Rentable r);
	int addUser(User u);

	int rentFile(String id, String index);
	int refundFile(String id, String index);

	void exit();
	void begin();

	int reset();

	void showUsers(Predicate<User> filter);
	void showUsersAdded();
	void showFiles(Predicate<String> filter);
	void showFilesAdded();
	void showRents();
	void showRefunds();

	LocalDate stringToDate(String date);
	String dateToString(LocalDate date);
	
	boolean setDate(int day, int month, int year);

	String getDate();
}