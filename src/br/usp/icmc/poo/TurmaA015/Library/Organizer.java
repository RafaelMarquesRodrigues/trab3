
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

public interface Organizer {
	//métodos para adicionar pessoas e arquivos que podem ser alugados
	boolean addFile(Rentable r);
	boolean addUser(Person p);

	//métodos para verificar as pessoas que tem algum arquivo do local e quais arquivos existem no local
	Person getUser(String name);
	Rentable getFile(String name);

	int getUsersSize();
	int getFilesSize();
}