package org.springframework.samples.petclinic.owner;

import java.text.ParseException;
import java.util.Collection;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Component;

@Component
public class VetFormatter  implements Formatter<Vet> {
	
	private final VetRepository vets;
	
	@Autowired
	public VetFormatter(VetRepository vets) {
		this.vets=vets;
	}

	@Override
	public String print(Vet vet, Locale local) {
		return vet.toString();
	}

	@Override
	public Vet parse(String text, Locale local) throws ParseException {
		Collection<Vet> vets = this.vets.findAll();
		for (Vet i : vets) {
			if (i.toString().equals(text))
				return i;
		}
		throw new ParseException("veterinarian not found: " + text, 0);
	}
	
}
