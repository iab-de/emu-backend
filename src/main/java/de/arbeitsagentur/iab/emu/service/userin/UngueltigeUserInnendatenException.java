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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Ungültige Daten in einem UserIn-Objekt!")
public class UngueltigeUserInnendatenException extends Exception{
    public UngueltigeUserInnendatenException(String message) {
        super(message);
    }
}
