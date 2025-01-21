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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface KundInRepository extends CrudRepository<KundIn, Integer> {

	
	@Query("SELECT k FROM KundIn k WHERE (?1 is null or lower(k.kundInnendaten.nachname) like concat('%', lower(?1), '%') or lower(k.kundInnendaten.kundInnennummer) like concat('%', lower(?1), '%'))")
	List<KundIn> findBySuchbegriff(@Param("suchbegriff") String suchbegriff, Pageable pageable);

	@Query("SELECT k FROM KundIn k")
	List<KundIn> findAllPagable(Pageable pageable);

	@Query("select new de.arbeitsagentur.iab.emu.service.kundin.KundInnenProGruppe(count(k.id),g.bezeichnung) from Gruppe g left join KundIn k on k.gruppe.id = g.id group by g.bezeichnung")
	List<KundInnenProGruppe> getAnzahlInGruppen();

	/**
     * Work-Around für einen Bug in Hibernate. Dieser führt dazu,
     * dass bei der Standard-Implementierung von findById die Tenant-ID
     * nicht berücksichtigt wird. Siehe auch
	 * <a href="https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830">https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830</a>
     */
	@Override
	@NonNull
	@Query("SELECT k FROM KundIn k WHERE k.id = ?1")
	Optional<KundIn> findById(@NonNull Integer id);

	@NonNull
	@Query("SELECT k FROM KundIn k WHERE lower(k.kundInnendaten.kundInnennummer) = lower(?1)")
	Optional<KundIn> findByKundInnennummer(@NonNull String kundInnennummer);
	
}
