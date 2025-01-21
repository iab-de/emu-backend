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

import de.arbeitsagentur.iab.emu.service.AbstractServiceTest;
import de.arbeitsagentur.iab.emu.service.projekt.Projekt;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektBereitsVorhandenException;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektFactory;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektService;
import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class KundInServiceTest extends AbstractServiceTest {

    @Autowired
    KundInService service;

    @Autowired
    ProjektService projektService;



    private List<Gruppe> alleGruppen;

    @BeforeEach
    void gruppenAufsetzenBeforeEach() throws UngueltigeGruppenException, UngueltigeFelddefintionException, ProjektBereitsVorhandenException {

        Projekt p = ProjektFactory.gueltigesProjektErzeugen();
        p = projektService.create(p);
        alleGruppen = p.getGruppen();

    }

    @Test
    void createMitTeilnahme() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");
        Zusatzinformation zi = new Zusatzinformation();
        zi.setName("test");
        zi.setTyp("typ");
        zi.setWert("wert");

        kd.setZusatzinformationen(Arrays.asList(zi));

        KundIn kundIn = service.create(kd);

        assertNotNull(kundIn.getId());
        assertEquals(kd.getGeburtsdatum(), kundIn.getKundInnendaten().getGeburtsdatum());
        assertEquals(kd.getKundInnenennummer(), kundIn.getKundInnendaten().getKundInnenennummer());
        assertEquals(kd.getTeilnahmeAbsagegrund(), kundIn.getKundInnendaten().getTeilnahmeAbsagegrund());
        assertEquals(kd.getNachname(), kundIn.getKundInnendaten().getNachname());
        assertNotNull(kundIn.getGruppe());

        assertTrue(alleGruppen.stream().anyMatch(g -> g.getId().equals(kundIn.getGruppe().getId())));
        assertEquals(1, kundIn.getKundInnendaten().getZusatzinformationen().size());
        Zusatzinformation ziGeladen = kundIn.getKundInnendaten().getZusatzinformationen().get(0);

        assertEquals("test",ziGeladen.getName());
        assertEquals("typ",ziGeladen.getTyp());
        assertEquals("wert",ziGeladen.getWert());
    }

    @Test
    void createOhneTeilnahme() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Absage);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        assertNotNull(kundIn.getId());
        assertEquals(kd.getGeburtsdatum(), kundIn.getKundInnendaten().getGeburtsdatum());
        assertEquals(kd.getKundInnenennummer(), kundIn.getKundInnendaten().getKundInnenennummer());
        assertEquals(kd.getTeilnahmeAbsagegrund(), kundIn.getKundInnendaten().getTeilnahmeAbsagegrund());
        assertEquals(kd.getNachname(), kundIn.getKundInnendaten().getNachname());
        assertNull(kundIn.getGruppe());


    }

    @Test
    void createUngueltigeKundInnennummer() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A45678");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Absage);
        kd.setNachname("Nachname");


        assertThrows(UngueltigeKundInnennummerException.class, () -> {
            service.create(kd);
        });
    }

    @Test
    void updateKundInnennummerBereitsVergeben() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException, KundInNichtGefundenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn1 = service.create(kd);

        kd.setKundInnenennummer("123A456788");
        // Kunde 2 anlegen
        service.create(kd);


        kd.setNachname("123A456788");
        // Kunde 1 mit Kundennummer von Kunde 2 belegen:
        assertThrows(KundInnennummerBereitsVorhandenException.class, () -> {
            service.update(kundIn1.getId(),kd);
        });

    }

    @Test
    void updateFalscheId() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException, KundInNichtGefundenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        assertThrows(KundInNichtGefundenException.class, () -> {
            service.update(-1,kd);
        });

    }

    @Test
    void update() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        kd.setNachname("Nachname geändert");

        service.update(kundIn.getId(),kd);

        KundIn kundInNachUpdate = service.getById(kundIn.getId());
        assertEquals(kundIn.getGruppe().getId(), kundInNachUpdate.getGruppe().getId());
        assertEquals(kd.getNachname(), kundInNachUpdate.getKundInnendaten().getNachname());
    }

    @Test
    void updateNachtraeglicherMuenzwurf() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Absage);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);
        assertNull(kundIn.getGruppe());

        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

        service.update(kundIn.getId(),kd);

        KundIn kundInNachUpdate = service.getById(kundIn.getId());
        assertNotNull(kundInNachUpdate.getGruppe());
    }

    @Test
    // Test ist nicht ideal: Falls auch beim Update ein Münzwurf erfolgen würde, würde dies
    // nur dann aufgedeckt, wenn beim Münzwurf ein anderes Ergebnis erzeugt würde.
    void updateKeinNachtraeglicherMuenzwurf() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);
        assertNotNull(kundIn.getGruppe());

        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

        service.update(kundIn.getId(),kd);

        KundIn kundInNachUpdate = service.getById(kundIn.getId());

        assertEquals(kundIn.getGruppe().getId(), kundInNachUpdate.getGruppe().getId());
    }


    @Test
    void getById() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException, KundInnennummerBereitsVorhandenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        KundIn kundInGeladen = service.getById(kundIn.getId());
        assertEquals(kundIn.getGruppe().getId(), kundInGeladen.getGruppe().getId());
        assertEquals(kd.getNachname(), kundInGeladen.getKundInnendaten().getNachname());
    }

    @Test
    void getByIdNichtGefunden() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException {
        assertThrows(KundInNichtGefundenException.class, () -> {
            service.getById(1);
        });
    }

    @Test
    void getByKundInnenNummer() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException, KundInnennummerBereitsVorhandenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        KundIn kundInGeladen = service.getByKundInnennummer(kd.getKundInnenennummer());
        assertEquals(kundIn.getId(),kundInGeladen.getId());
        assertEquals(kundIn.getGruppe().getId(), kundInGeladen.getGruppe().getId());
        assertEquals(kd.getNachname(), kundInGeladen.getKundInnendaten().getNachname());
    }

    @Test
    void getByKundInnenNummerNichtGefunden() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInNichtGefundenException {
        assertThrows(KundInNichtGefundenException.class, () -> {
            service.getByKundInnennummer("GIBT_ES_NICHT");
        });
    }

    @Test
    void getKundInnenProGruppe() throws GruppeNichtGefundenException, KundInnennummerBereitsVorhandenException, UngueltigeKundInnennummerException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456780");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        Map<String,Integer> anzahlProGruppeGegeben = new HashMap<>();
        KundIn kundIn = service.create(kd);
        int anzahl = anzahlProGruppeGegeben.getOrDefault(kundIn.getGruppe().getBezeichnung(),0);
        anzahl++;
        anzahlProGruppeGegeben.put(kundIn.getGruppe().getBezeichnung(),anzahl);

        kd.setKundInnenennummer("123A456781");


        kundIn = service.create(kd);
        anzahl = anzahlProGruppeGegeben.getOrDefault(kundIn.getGruppe().getBezeichnung(),0);
        anzahl++;
        anzahlProGruppeGegeben.put(kundIn.getGruppe().getBezeichnung(),anzahl);

        kd.setKundInnenennummer("123A456782");


        kundIn = service.create(kd);
        anzahl = anzahlProGruppeGegeben.getOrDefault(kundIn.getGruppe().getBezeichnung(),0);
        anzahl++;
        anzahlProGruppeGegeben.put(kundIn.getGruppe().getBezeichnung(),anzahl);

        Iterable<KundInnenProGruppe> kundInnenProGruppeIt = service.getKundInnenProGruppe();
        List<KundInnenProGruppe> kundInnenProGruppeListGeladen = new ArrayList<>();
        kundInnenProGruppeIt.forEach(kundInnenProGruppeListGeladen::add);



        assertEquals(alleGruppen.size(), kundInnenProGruppeListGeladen.size());

        for (KundInnenProGruppe kundInnenProGruppe : kundInnenProGruppeListGeladen) {
            int anzahlGegeben = anzahlProGruppeGegeben.getOrDefault(kundInnenProGruppe.getGruppe(),0);
            assertEquals(anzahlGegeben, kundInnenProGruppe.getAnzahl());
        }

    }

    @Test
    void getKundInnenProGruppeKeineKundInnen() throws GruppeNichtGefundenException, KundInnennummerBereitsVorhandenException, UngueltigeKundInnennummerException {


        Iterable<KundInnenProGruppe> kundInnenProGruppeIt = service.getKundInnenProGruppe();
        List<KundInnenProGruppe> kundInnenProGruppeList = new ArrayList<>();
        kundInnenProGruppeIt.forEach(kundInnenProGruppeList::add);

        assertEquals(alleGruppen.size(), kundInnenProGruppeList.size());

        for (KundInnenProGruppe kundInnenProGruppe : kundInnenProGruppeList) {

            assertEquals(0, kundInnenProGruppe.getAnzahl());
        }

    }

    @Test
    void getBySuchbegriff() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn1 = service.create(kd);

        KundInnendaten kd2 = new KundInnendaten();
        kd2.setGeburtsdatum(LocalDate.now());
        kd2.setKundInnenennummer("123A456788");
        kd2.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd2.setNachname("Lastname");

        KundIn kundIn2 = service.create(kd2);

        Iterable<KundIn> kundInnenIt = service.getBySuchbegriff("ach");
        Iterator<KundIn> iterator = kundInnenIt.iterator();
        assertTrue(iterator.hasNext());
        KundIn gefunden = iterator.next();

        assertEquals(kundIn1.getId(),gefunden.getId());

        assertFalse(iterator.hasNext());

    }

    @Test
    void getBySuchbegiffAlle() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {
        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn1 = service.create(kd);

        KundInnendaten kd2 = new KundInnendaten();
        kd2.setGeburtsdatum(LocalDate.now());
        kd2.setKundInnenennummer("123A456788");
        kd2.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd2.setNachname("Lastname");

        KundIn kundIn2 = service.create(kd2);

        Iterable<KundIn> kundInnenIt = service.getBySuchbegriff(null);
        Iterator<KundIn> iterator = kundInnenIt.iterator();

        List<KundIn> kundInnen = new ArrayList<>();
        kundInnenIt.forEach(kundInnen::add);

        assertEquals(2, kundInnen.size());
        assertTrue(kundInnen.stream().anyMatch(k -> k.getId().equals(kundIn1.getId())));
        assertTrue(kundInnen.stream().anyMatch(k -> k.getId().equals(kundIn2.getId())));
    }

    @Test
    void isKundennummerVergebenFalse() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException {
        assertFalse(service.isKundInnennummerVergeben("123A456789",null));
    }

    @Test
    void isKundennummerVergebenTrue() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        assertTrue(service.isKundInnennummerVergeben("123A456789",null));
    }

    @Test
    void isKundennummerVergebenFalseIDAusgeschlossen() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn = service.create(kd);

        assertFalse(service.isKundInnennummerVergeben("123A456789", kundIn.getId()));
    }

    @Test
    void isKundennummerVergebenTrueIDAusgeschlossen() throws GruppeNichtGefundenException, UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {

        KundInnendaten kd = new KundInnendaten();
        kd.setGeburtsdatum(LocalDate.now());
        kd.setKundInnenennummer("123A456789");
        kd.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);
        kd.setNachname("Nachname");

        KundIn kundIn1 = service.create(kd);

        kd.setKundInnenennummer("123A456788");
        KundIn kundIn2 = service.create(kd);

        assertTrue(service.isKundInnennummerVergeben("123A456789", kundIn2.getId()));
    }
}