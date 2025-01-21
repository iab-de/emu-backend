/*
 * Backend "Elektronischer Münzwurf"
 * Copyright (C) 2025 Institut für Arbeitsmarkt und Berufsforschung <https://www.iab.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.arbeitsagentur.iab.emu.service.kundin;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Embeddable
public class KundInnendaten {
	@Schema(maxLength = 255)
	private String vorname;
	@Schema(maxLength = 255)
	private String nachname;
	private LocalDate geburtsdatum;
	@Schema(description = "KundInnennummer im Format 123x123456. Der Buchstabe wird immer als Kleinbuchstabe gespeichert."
			,minLength = 10, maxLength = 10,pattern = "\\d{3}[a-z]\\d{6}")
	@Column(length = 10)
	private String kundInnennummer;
	private TeilnahmeAbsagegrund teilnahmeAbsagegrund;

	@ElementCollection(fetch = FetchType.EAGER)
	@OrderColumn
	private List<Zusatzinformation> zusatzinformationen;

	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	public LocalDate getGeburtsdatum() {
		return geburtsdatum;
	}
	public void setGeburtsdatum(LocalDate geburtsdatum) {
		this.geburtsdatum = geburtsdatum;
	}
	@JsonGetter
	public String getKundInnenennummer() {
		
		if (kundInnennummer != null) {
			kundInnennummer = kundInnennummer.toLowerCase();
		}
		
		return kundInnennummer;
	}
	
	/**
	 * Die KundInnennummer wird immer in Kleinbuchstaben gespeichert.
	 * 
	 */
	public void setKundInnenennummer(String kundInnennummer) {
		this.kundInnennummer = kundInnennummer.toLowerCase();
	}

	@JsonIgnore
	public boolean isKundInnennummerValid() {
		return KundInnennummerValidator.isValid(getKundInnenennummer());
	}

	public TeilnahmeAbsagegrund getTeilnahmeAbsagegrund() {
		return teilnahmeAbsagegrund;
	}
	public void setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund teilnahmeAbsagegrund) {
		this.teilnahmeAbsagegrund = teilnahmeAbsagegrund;
	}

	public List<Zusatzinformation> getZusatzinformationen() {
		return zusatzinformationen;
	}

	public void setZusatzinformationen(List<Zusatzinformation> zusatzinformationen) {
		this.zusatzinformationen = zusatzinformationen;
	}
}
