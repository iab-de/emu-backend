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

import java.util.concurrent.ThreadLocalRandom;

public class Zufallsgenerator {

    private Zufallsgenerator() {}

    /**
     * Erzeugt einen Zufallswert zwischen Unter- und Obergrenze.
     * @param untergrenze Untergrenze inklusiv.
     * @param obergrenze Obergrenze inklusiv.
     * @return Erzeugte Zufallszahl.
     */
    public static int zufallsgenerator(int untergrenze, int obergrenze) {
        return ThreadLocalRandom.current().nextInt(untergrenze,obergrenze+1);
    }
}
