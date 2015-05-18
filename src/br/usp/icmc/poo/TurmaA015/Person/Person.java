package br.usp.icmc.poo.TurmaA015.Person;

import br.usp.icmc.poo.TurmaA015.Rentable.*;

interface Person {
	int rent(String str);
	int refund(Rentable r);
}