
package br.usp.icmc.poo.TurmaA015.Library;

import br.usp.icmc.poo.TurmaA015.Rentable.*;
import br.usp.icmc.poo.TurmaA015.Person.*;

public interface Organizer {
	void add(Rentable r);
	void newUser(Person p);
	Person getUser(String name);
	int getUserSize();
	int getArchives();
	boolean hasArchive(String name);
}