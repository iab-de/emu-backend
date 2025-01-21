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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KundInnennummerValidatorTest {

	@Test
	void isValid() {
		assertTrue(KundInnennummerValidator.isValid("123A567890"));
		assertTrue(KundInnennummerValidator.isValid("123a567890"));
		
		assertFalse(KundInnennummerValidator.isValid("123a56789"));
		assertFalse(KundInnennummerValidator.isValid("123a5!7890"));
		assertFalse(KundInnennummerValidator.isValid("1!3a56789"));
		assertFalse(KundInnennummerValidator.isValid("13a56789"));
	}

}
