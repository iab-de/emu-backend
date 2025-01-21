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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KundInnennummerValidator {

	private KundInnennummerValidator() {}
	
	public static boolean isValid(String kundInnennummer) {
		if (kundInnennummer == null) {
			return false;
		}
		Pattern pattern = Pattern.compile("\\d{3}[a-z]\\d{6}", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(kundInnennummer);
	    return matcher.find();
	}
}
