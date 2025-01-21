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

import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;
import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;

@Entity
@Table(
	    name="kundin",
	    uniqueConstraints=
	        @UniqueConstraint(columnNames={"tenantId", "kundInnennummer"})
	)
public class KundIn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@TenantId
	private String tenantId;

	private KundInnendaten kundInnendaten;
	
	@ManyToOne
	private Gruppe gruppe;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Gruppe getGruppe() {
		return gruppe;
	}
	public void setGruppe(Gruppe gruppe) {
		this.gruppe = gruppe;
	}
	public KundInnendaten getKundInnendaten() {
		return kundInnendaten;
	}

	/**
	 * Setter für die {@link KundInnendaten}.
	 * @throws IllegalArgumentException Falls eine ungueltige KundInnennummer übergeben wird.
	 * @param kundInnendaten Diese Daten werden gesetzt.
	 */
	public void setKundInnendaten(KundInnendaten kundInnendaten) {
		if (kundInnendaten != null && !kundInnendaten.isKundInnennummerValid()) {
			throw new IllegalArgumentException("Ungütige KundInnennummer!");
		}
		
		this.kundInnendaten = kundInnendaten;
	}
	
	

}
