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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ZufallsgeneratorTest {

    @Test
    void zufallsgenerator() {

        int anzahl1=0,anzahl2=0;

        for (int i=0;i<10000000;i++) {
            int wert = Zufallsgenerator.zufallsgenerator(1,2);
            assertTrue(wert>=1);
            assertTrue(wert<=2);
            if (wert == 1) {
                anzahl1++;
            } else {
                anzahl2++;
            }
        }


        assertTrue(Math.abs(anzahl1-anzahl2)<10000,"Die Zufallszahlen sind nicht gleichmäßig verteilt!");
    }
}