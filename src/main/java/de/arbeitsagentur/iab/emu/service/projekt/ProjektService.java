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

package de.arbeitsagentur.iab.emu.service.projekt;

import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Iterator;

@Service
@Transactional
public class ProjektService {

    private final ProjektRepository projektRepository;

    public ProjektService(final @NonNull ProjektRepository projektRepository) {

        this.projektRepository = projektRepository;
    }

    public Projekt getProjekt() throws ProjektNichtGefundenException {
        Iterator<Projekt> it = projektRepository.findAll().iterator();
        if (it.hasNext()) {
            return it.next();
        }
        throw new ProjektNichtGefundenException("Kein Projekt vorhanden!");
    }

    public Projekt update(@NonNull Projekt projekt) throws UngueltigeGruppenException, UngueltigeFelddefintionException, ProjektNichtGefundenException {

        if (projekt.getId() == null || !projektRepository.existsById(projekt.getId())) {
            throw new ProjektNichtGefundenException("Projekt mit ID "+projekt.getId()+" wurde nicht gefunden.");
        }

        return save(projekt);

    }

    private Projekt save(@NonNull Projekt projekt) throws UngueltigeGruppenException, UngueltigeFelddefintionException {
        String validierungsfehlerGruppen = projekt.validiereGruppen();
        if (validierungsfehlerGruppen != null) {
            throw new UngueltigeGruppenException(validierungsfehlerGruppen);
        }

        String validierungsfehlerFelddefinitionen = projekt.validiereFelddefinitionen();
        if (validierungsfehlerFelddefinitionen != null) {
            throw new UngueltigeFelddefintionException(validierungsfehlerFelddefinitionen);
        }
        return projektRepository.save(projekt);
    }

    public boolean isProjektBereitsVorhanden() {
        return projektRepository.count()>0;
    }

    public Projekt create(final @NonNull @RequestBody Projekt projekt) throws ProjektBereitsVorhandenException, UngueltigeGruppenException, UngueltigeFelddefintionException {

        if (isProjektBereitsVorhanden()) {
            // Zusätzlich wird die Anlage mehrerer Projekte pro Mandant über einen
            // Unique-Constraint in der Datenbank verhindert.
            throw new ProjektBereitsVorhandenException("Es wurde bereits ein Projekt bei dieser Tenant-ID angelegt!");
        }
        return save(projekt);
    }


}
