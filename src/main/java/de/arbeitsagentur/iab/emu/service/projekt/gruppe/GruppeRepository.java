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

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GruppeRepository extends CrudRepository<Gruppe,Integer> {

	@Query("SELECT g FROM Gruppe g WHERE g.untergrenze <= :zufallszahl AND g.obergrenze >= :zufallszahl ")
	public Optional<Gruppe> findGruppeByZufallswert(@Param("zufallszahl") int zufallszahl);
	
	@Query("SELECT max(obergrenze) FROM Gruppe")
	public int getMaxObergrenze();

	@Query("SELECT min(untergrenze) FROM Gruppe")
	public int getMinUntergrenze();
	
}
