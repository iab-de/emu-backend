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

package de.arbeitsagentur.iab.emu.service.projekt.gruppe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;
import org.springframework.data.annotation.Version;

import java.util.Objects;

@Entity
@Table(
		name="gruppe",
		uniqueConstraints=
		@UniqueConstraint(columnNames={"tenantId", "bezeichnung"})
)
public class Gruppe implements Comparable<Gruppe> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Integer id;
	@TenantId
	private String tenantId;
	
	@Version
	private int version;

	@Schema(description = "Die Gruppenbezeichnung darf pro Tenant-ID nicht mehrfach vergeben werden.",maxLength = 255)
	private String bezeichnung;
	
	private int untergrenze;
	private int obergrenze;
	//@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBezeichnung() {
		return bezeichnung;
	}
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	public int getUntergrenze() {
		return untergrenze;
	}
	public void setUntergrenze(int untergrenze) {
		this.untergrenze = untergrenze;
	}
	public int getObergrenze() {
		return obergrenze;
	}
	public void setObergrenze(int obergrenze) {
		this.obergrenze = obergrenze;
	}
	@Override
	public int compareTo(Gruppe o) {
		Objects.requireNonNull(o);

		int ret = Integer.compare(this.getUntergrenze(),o.getUntergrenze());
		if (ret == 0) {
			ret = Integer.compare(this.getObergrenze(),o.getObergrenze());
		}
		return ret;
	}
	
}
