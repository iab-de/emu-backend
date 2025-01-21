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

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserInService {

    final
    UserInRepository userInRepository;


    public UserInService(@NonNull UserInRepository userInRepository) {
        this.userInRepository = userInRepository;
    }

    public UserIn get(@PathVariable int id) throws UserInNichtGefundenException {
        return userInRepository.findById(id).orElseThrow(() -> new UserInNichtGefundenException("UserIn mit " + id + " wurde nicht gefunden!"));
    }

    public Iterable<UserIn> getAll() {
        return userInRepository.findAll();
    }

    public Iterable<UserIn> create(@NonNull @RequestBody List<UserIn> userInnen) throws LoginMehrfachVergebenException, UngueltigeUserInnendatenException {
        for (final UserIn u : userInnen) {
            if (!u.isValid()) {
                throw new UngueltigeUserInnendatenException("Es wurden ungültige UserInnendaten übergeben!");
            }
        }
        final Set<String> logins = userInnen.stream().map(UserIn::getLogin).collect(Collectors.toSet());
        if (logins.size()<userInnen.size()) {
            throw new LoginMehrfachVergebenException("Mindestens ein Login wurde mehrfach vergeben!");
        }
        return userInRepository.saveAll(userInnen);
    }

    public UserIn create(@NonNull @RequestBody UserIn userIn) throws UngueltigeUserInnendatenException, LoginMehrfachVergebenException {
        if (!userIn.isValid()) {
            throw new UngueltigeUserInnendatenException("Es wurden ungültige UserInnendaten übergeben!");
        }
        final Optional<UserIn> userInOption = userInRepository.findByLogin(userIn.getLogin());
        if (userInOption.isPresent()) {
            throw new LoginMehrfachVergebenException("Login wurde bereits bei einem anderen User / einer anderen UserIn verwendet!");
        }

        userIn.setId(null);

        return userInRepository.save(userIn);
    }


    public UserIn update(@NonNull @RequestBody UserIn userIn) throws UngueltigeUserInnendatenException, LoginMehrfachVergebenException, UserInNichtGefundenException {
        if (userIn.getId() == null || !userInRepository.existsById(userIn.getId())) {
            throw new UserInNichtGefundenException("UserIn mit ID "+userIn.getId()+" wurde nicht gefunden.");
        }

        if (!userIn.isValid()) {
            throw new UngueltigeUserInnendatenException("Es wurden ungültige Daten im UserIn-Objekt übergeben!");
        }
        final Optional<UserIn> userInOption = userInRepository.findByLogin(userIn.getLogin());
        if (userInOption.isPresent() && !userInOption.get().getId().equals(userIn.getId())) {
            throw new LoginMehrfachVergebenException("Login wurde bereits bei einem anderen User / einer anderen UserIn verwendet!");
        }
        return userInRepository.save(userIn);
    }

    public void delete(@PathVariable("id") int id) {
        userInRepository.deleteById(id);
    }

}
