package br.usp.icmc.poo.TurmaA015.Person;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

public interface Person {
	boolean rentFile(Rentable r);
	boolean refundFile(Rentable r);
	String getName();
}