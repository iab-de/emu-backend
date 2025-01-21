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

import de.arbeitsagentur.iab.emu.config.TenantResolver;
import de.arbeitsagentur.iab.emu.service.projekt.Projekt;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektBereitsVorhandenException;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektFactory;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektService;
import de.arbeitsagentur.iab.emu.service.userin.UserIn;
import de.arbeitsagentur.iab.emu.service.userin.UserInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BestellungServiceTest {

    @Autowired
    BestellungService service;

    @Autowired
    ProjektService projektService;

    @Autowired
    UserInService userInService;

    @Autowired
    TenantResolver tenantResolver;

    @BeforeEach
    public void beforeEach() {
        tenantResolver.setCurrentTenant(String.valueOf(System.currentTimeMillis()));
    }

    @Test
    void bestellen() throws Exception {
        String tenant = UUID.randomUUID().toString();
        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        bestellung.setProjekt(bestelltesProjekt);

        bestellung.setUserInnen(Collections.singletonList(user));

        service.bestellen(bestellung);

        Projekt geladenesProjekt = projektService.getProjekt();

        assertNotNull(geladenesProjekt.getId());

        assertEquals(2,geladenesProjekt.getFelddefinitionen().size());
        assertEquals(2,geladenesProjekt.getGruppen().size());

        List<UserIn> geladenesUserInnen = new ArrayList<>();

        userInService.getAll().forEach(geladenesUserInnen::add);

        assertEquals(1,geladenesUserInnen.size());

    }

    @Test
    void bestellenTenantIDBereitsVergeben() throws Exception {
        String tenant = UUID.randomUUID().toString();
        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        bestellung.setUserInnen(Collections.singletonList(user));

        bestellung.setProjekt(bestelltesProjekt);
        service.bestellen(bestellung);

        Projekt geladenesProjekt = projektService.getProjekt();

        assertNotNull(geladenesProjekt.getId());

        bestelltesProjekt.setBeschreibung("Testprojekt 2");

        // Erneute Bestellung führt zu Exception
        assertThrows(ProjektBereitsVorhandenException.class, () -> service.bestellen(bestellung));

    }

    @Test
    public void bereitsVorhandenTrue() throws Exception {

        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        bestellung.setUserInnen(Collections.singletonList(user));
        bestellung.setProjekt(bestelltesProjekt);

        service.bestellen(bestellung);

        assertTrue(service.bereitsVorhanden());

    }

    @Test
    public void bereitsVorhandenFalse() {

        assertFalse(service.bereitsVorhanden());

    }
}