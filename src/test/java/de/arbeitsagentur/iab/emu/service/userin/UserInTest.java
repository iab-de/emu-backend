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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserInTest {

    @Test
    void isValidLogin() {
        UserIn u = new UserIn();
        u.setLogin("Login");
        u.setRolle("Rolle");

        assertTrue(u.isValid());

        u.setLogin(null);
        assertFalse(u.isValid());

        u.setLogin("");
        assertFalse(u.isValid());

    }

    @Test
    void isValidRolle() {
        UserIn u = new UserIn();
        u.setLogin("Login");
        u.setRolle("Rolle");

        assertTrue(u.isValid());

        u.setRolle(null);
        assertFalse(u.isValid());

        u.setRolle("");
        assertFalse(u.isValid());

    }
}