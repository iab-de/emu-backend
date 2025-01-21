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

import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.Felddefinition;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjektFactory {

    public static Projekt gueltigesProjektErzeugen() {
        Projekt p = new Projekt();
        p.setStart(LocalDate.now());
        p.setName("Test");
        p.setBeschreibung("Beschreibung");

        Felddefinition f1 = new Felddefinition();
        f1.setName("Feld1");
        f1.setTyp("String");

        Felddefinition f2 = new Felddefinition();
        f2.setName("Feld2");
        f2.setTyp("Datum");

        List<Felddefinition> felddefinitionen = new ArrayList<>();
        felddefinitionen.add(f1);
        felddefinitionen.add(f2);
        p.setFelddefinitionen(felddefinitionen);

        Gruppe g1 = new Gruppe();
        g1.setUntergrenze(1);
        g1.setObergrenze(10);
        g1.setBezeichnung("Gruppe 1");

        Gruppe g2 = new Gruppe();
        g2.setUntergrenze(11);
        g2.setObergrenze(20);
        g2.setBezeichnung("Gruppe 2");

        List<Gruppe> gruppen = new ArrayList<>();
        gruppen.add(g1);
        gruppen.add(g2);
        p.setGruppen(gruppen);

        ProjekterstellendePerson projektersteller = new ProjekterstellendePerson();
        projektersteller.setNachname("Nachname");
        projektersteller.setVorname("Vorname");

        p.setProjekterstellendePerson(projektersteller);
        return p;
    }
}
