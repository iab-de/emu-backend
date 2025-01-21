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

package de.arbeitsagentur.iab.emu.service.userin;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserInRepository extends CrudRepository<UserIn, Integer> {

    /**
     * Work-Around für einen Bug in Hibernate. Dieser führt dazu,
     * dass bei der Standard-Implementierung von findById die Tenant-ID
     * nicht berücksichtigt wird. Siehe auch
     * <a href="https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830">https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830</a>
     */
    @Override
    @NonNull
    @Query("SELECT u FROM UserIn u WHERE u.id = ?1")
    Optional<UserIn> findById(@NonNull Integer id);

    /**
     * Work-Around für einen Bug in Hibernate. Dieser führt dazu,
     * dass ggf. die Tenant-ID nicht berücksichtigt wird.
     * Siehe auch
     * <a href="https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830">https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830</a>
     */
    @Override
    @NonNull
    @Query("SELECT CASE WHEN count(u)> 0 THEN true ELSE false END FROM UserIn u WHERE u.id = ?1")
    boolean existsById(@NonNull Integer id);

    @NonNull
    @Query("SELECT u FROM UserIn u WHERE u.login = ?1")
    Optional<UserIn> findByLogin(@NonNull String login);

    /**
     * Work-Around für einen Bug in Hibernate. Dieser führt dazu,
     * dass bei der Standard-Implementierung von deleteById die Tenant-ID
     * nicht berücksichtigt wird. Siehe auch
     * <a href="https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830">https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830</a>
     */
    @Override
    @NonNull
    @Modifying
    @Query("DELETE UserIn u WHERE u.id = ?1")
    void deleteById(@NonNull Integer id);
}
