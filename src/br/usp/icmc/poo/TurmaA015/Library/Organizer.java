
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

public interface Organizer {
	boolean add(Rentable r);
	boolean newUser(Person p);
	Person getUser(String name);
	int getUserSize();
	int getArchivesSize();
	boolean hasArchive(String name);
}