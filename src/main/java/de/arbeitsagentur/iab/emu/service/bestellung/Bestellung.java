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
import de.arbeitsagentur.iab.emu.service.projekt.Projekt;
import de.arbeitsagentur.iab.emu.service.userin.UserIn;

import java.util.List;

public class Bestellung {
    private Projekt projekt;
    private List<UserIn> userInnen;

    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
    }

    public List<UserIn> getUserInnen() {
        return userInnen;
    }

    public void setUserInnen(List<UserIn> userInnen) {
        this.userInnen = userInnen;
    }
}
