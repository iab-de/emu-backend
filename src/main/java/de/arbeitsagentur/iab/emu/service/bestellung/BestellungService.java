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

package de.arbeitsagentur.iab.emu.service.bestellung;

import de.arbeitsagentur.iab.emu.service.projekt.ProjektBereitsVorhandenException;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektService;
import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import de.arbeitsagentur.iab.emu.service.userin.LoginMehrfachVergebenException;
import de.arbeitsagentur.iab.emu.service.userin.UngueltigeUserInnendatenException;
import de.arbeitsagentur.iab.emu.service.userin.UserInService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class BestellungService {

    private final ProjektService projektService;

    private final UserInService userInService;


    public BestellungService(ProjektService projektService, UserInService userInService) {
        this.projektService = projektService;
        this.userInService = userInService;

    }

    public void bestellen(@NonNull Bestellung bestellung) throws ProjektBereitsVorhandenException, UngueltigeGruppenException, UngueltigeFelddefintionException, LoginMehrfachVergebenException, UngueltigeUserInnendatenException {
        Objects.requireNonNull(bestellung);

        projektService.create(bestellung.getProjekt());
        userInService.create(bestellung.getUserInnen());

    }

    public boolean bereitsVorhanden() {
        return projektService.isProjektBereitsVorhanden();
    }
}
