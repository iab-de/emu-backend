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

import de.arbeitsagentur.iab.emu.service.AbstractServiceTest;
import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjektServiceTest extends AbstractServiceTest {

    @Autowired
    ProjektService service;

    @Test
    void getProjekt() throws ProjektBereitsVorhandenException, ProjektNichtGefundenException, UngueltigeGruppenException, UngueltigeFelddefintionException {

        Projekt created = service.create(ProjektFactory.gueltigesProjektErzeugen());

        Projekt angelegtesProjekt = service.getProjekt();

        assertEquals(created.getId(),angelegtesProjekt.getId());

        assertEquals(2,angelegtesProjekt.getFelddefinitionen().size());
        assertEquals(2,angelegtesProjekt.getGruppen().size());
    }

    @Test
    void updateNamenAendernFelddefintionenLoeschen() throws ProjektBereitsVorhandenException, ProjektNichtGefundenException, UngueltigeGruppenException, UngueltigeFelddefintionException {

        Projekt angelegtesProjekt = service.create(ProjektFactory.gueltigesProjektErzeugen());

        angelegtesProjekt.getFelddefinitionen().clear();

        angelegtesProjekt.setName("geändert");

        service.update(angelegtesProjekt);

        Projekt geladenesProjekt = service.getProjekt();

        assertEquals(angelegtesProjekt.getId(),geladenesProjekt.getId());

        assertEquals(0,geladenesProjekt.getFelddefinitionen().size());
        assertEquals(angelegtesProjekt.getName(),geladenesProjekt.getName());

    }
    @Test
    void updateGruppeEntfernen() throws UngueltigeGruppenException, ProjektBereitsVorhandenException, ProjektNichtGefundenException, UngueltigeFelddefintionException {

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        Gruppe g3 = new Gruppe();
        g3.setUntergrenze(21);
        g3.setObergrenze(30);
        g3.setBezeichnung("Gruppe 3");
        bestelltesProjekt.getGruppen().add(g3);
        // Projekt mit 3 Gruppen anlegen.
        Projekt geladenesProjekt = service.create(bestelltesProjekt);

        // Eine Gruppe wieder entfernen und speichern.
        geladenesProjekt.getGruppen().remove(2);
        service.update(geladenesProjekt);

        List<Gruppe> geladeneGruppenNachUpdate = service.getProjekt().getGruppen();

        assertEquals(2,geladeneGruppenNachUpdate.size());
    }

    @Test
    void updateFalscheId()  {

        Projekt projekt = ProjektFactory.gueltigesProjektErzeugen();

        projekt.setId(-1);

        assertThrows(ProjektNichtGefundenException.class,()->service.update(projekt));
    }

    @Test
    void updateGruppeUmbenennung() throws UngueltigeGruppenException, ProjektBereitsVorhandenException, ProjektNichtGefundenException, UngueltigeFelddefintionException {

        Projekt angelegtesProjekt = service.create(ProjektFactory.gueltigesProjektErzeugen());

        angelegtesProjekt.getGruppen().get(1).setBezeichnung("Umbenannt");

        service.update(angelegtesProjekt);

        Projekt geladenesProjekt = service.getProjekt();

        // Gruppen sortieren, weil die API keine Sortierung vorsieht.
        // Die Sortierung ist nötig, damit der Vergleich funktiniert.
        Collections.sort(angelegtesProjekt.getGruppen());
        Collections.sort(geladenesProjekt.getGruppen());

        assertEquals(angelegtesProjekt.getGruppen().get(1).getBezeichnung(),geladenesProjekt.getGruppen().get(1).getBezeichnung());
    }

    @Test
    void isProjektBereitsVorhandenNein() {
        assertFalse(service.isProjektBereitsVorhanden());
    }

    @Test
    void isProjektBereitsVorhandenJa() throws ProjektNichtGefundenException, ProjektBereitsVorhandenException, UngueltigeGruppenException, UngueltigeFelddefintionException {
        create();
        assertTrue(service.isProjektBereitsVorhanden());
    }

    @Test
    void create() throws ProjektBereitsVorhandenException, ProjektNichtGefundenException, UngueltigeGruppenException, UngueltigeFelddefintionException {

        Projekt angelegtesProjekt = service.create(ProjektFactory.gueltigesProjektErzeugen());

        Projekt geladenesProjekt = service.getProjekt();

        assertEquals(angelegtesProjekt.getId(),geladenesProjekt.getId());

        assertEquals(2,geladenesProjekt.getFelddefinitionen().size());
        assertEquals(2,geladenesProjekt.getGruppen().size());
    }

    @Test
    void createGruppenGrenzueberschneidung() {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();
        // Ober- und Untergrenzen von zwei Gruppen überschneiden lassen:
        p.getGruppen().get(1).setUntergrenze(p.getGruppen().get(0).getObergrenze());

        assertThrows(UngueltigeGruppenException.class, () -> service.create(p));
    }

    @Test
    void createGruppenGrenzenUngueltig() {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();
        // Obergrenze = Untergrenze
        p.getGruppen().get(1).setUntergrenze(p.getGruppen().get(1).getObergrenze());

        assertThrows(UngueltigeGruppenException.class, () -> service.create(p));

    }

    @Test
    void createGruppenBezeichnungMehrfach() {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();

        p.getGruppen().get(1).setBezeichnung(p.getGruppen().get(0).getBezeichnung());

        assertThrows(UngueltigeGruppenException.class, () -> service.create(p));
    }

    @Test
    void createNurEineGruppe() {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();

        p.getGruppen().remove(1);

        assertThrows(UngueltigeGruppenException.class, () -> service.create(p));

    }

    @Test
    void createFelddefinitionsnameMehrfachVergeben() {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();

        p.getFelddefinitionen().forEach(f->f.setName("Name"));

        assertThrows(UngueltigeFelddefintionException.class, () -> service.create(p));

    }
}