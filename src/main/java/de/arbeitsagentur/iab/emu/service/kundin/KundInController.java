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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController()
@RequestMapping(path = "/api/v1/{tenantId}/")
@Tag(name = "KundInnen",description = "Hauptbestandteil der Anwendung. Verwaltet die KundInnen und ordnet diese ggf. Gruppen zu. Bei KundInnen " +
		"können zusätzliche Informationen als Key-Value-Paare hinterlegt werden. Diese Key-Value-Paare sollten mit den Felddefinitonen, " +
		"die beim Projekt hinterlegt sind, übereinstimmen. Jede KundInnennummer darf nur einmalig pro Tenant-ID vergeben werden.")
public class KundInController {
	
	private final KundInService service;

	public KundInController(@NonNull KundInService service) {
		this.service = service;
	}

	@PostMapping(path="/kundinnen")
	@Operation(summary = "Legt eine neue Kundin / einen neuen Kunden mit den übergebenen KundInnendaten an.",
			description = "Beim Anlegen wird eine gültige KundInnennummer vorausgesetzt. Wenn der Teilnahmegrund auf 'Teilnahme' steht, wird" +
					" die Kundin / der Kunde zufällig einer Gruppe zugeordnet.")
	@ApiResponses({
			@ApiResponse(responseCode = "400",description = "Falls eine ungültige KundInnennummer übergeben wird."),
			@ApiResponse(responseCode = "409",description = "Falls die KundInnennummer bereits vorhanden ist."),
			@ApiResponse(responseCode = "201",description = "Falls eine Kundin / ein Kunde mit den übergebenen Daten angelegt wurde.")
	})
	public ResponseEntity<?> create(@NonNull @RequestBody KundInnendaten kundInnendaten) throws UngueltigeKundInnennummerException,
			GruppeNichtGefundenException, KundInnennummerBereitsVorhandenException {

		try {
			KundIn kundIn = service.create(kundInnendaten);
			return ResponseEntity.status(HttpStatus.CREATED).body(kundIn);
		} catch (DataIntegrityViolationException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,"Datenkonflikt! KundInnennummer mehrfach vergeben?");
		}
	}

	@PatchMapping(path="/kundinnen/{id}")
	@Operation(summary = "KundInnendaten aktualisieren.",
			description = "Aktualisiert die KundInnendaten einer Kundin / eines Kunden, sofern diese gültig sind. " +
			"Falls die Kundin / der Kunde noch keiner Gruppe zugeordnet war und der Teilnahmegrund auf 'Teilnahme' " +
			"gesetzt ist, erfolgt eine zufällige Gruppenzuordnung.")
	@ApiResponses({
			@ApiResponse(responseCode = "200",description = "Falls die Kundin / der Kunde mit den übergebenen Daten aktualisiert wurde."),
			@ApiResponse(responseCode = "400",description = "Falls eine ungültige KundInnennummer übergeben wird."),
			@ApiResponse(responseCode = "404", description = "KundIn wurde nicht gefunden,"),
			@ApiResponse(responseCode = "409",description = "Falls die KundInnennummer bereits vorhanden ist.")
	})
	public ResponseEntity<?> update(@PathVariable("id") int id,@NonNull @RequestBody KundInnendaten kundInnendaten)
			throws KundInNichtGefundenException,
			UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException, GruppeNichtGefundenException {

		try {
			final KundIn kundIn = service.update(id, kundInnendaten);
			return ResponseEntity.status(HttpStatus.OK).body(kundIn);
		} catch (DataIntegrityViolationException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,"Datenkonflikt! KundInnennummer mehrfach vergeben?");
		}
	}

	@GetMapping(path="/kundinnen/{id}")
	@Operation(description = "Lädt eine Kundin / einen Kunden samt KundInnendaten und ggf. zugeordneter Gruppe. Die Gruppe kann nicht geändert werden.")
	@ApiResponses({
			@ApiResponse(responseCode = "404", description = "KundIn wurde nicht gefunden,"),
			@ApiResponse(responseCode = "200", description = "Wenn die Kundin / der Kunde in der Datenbank gefunden wurde.")
	})
	public KundIn getById(@PathVariable("id") int id) throws KundInNichtGefundenException {
		return service.getById(id);
	}

	@GetMapping(path="/kundinnenreport/")
	@Operation(description = "Reporting-Funktion: Lädt die Anzahl der KundInnen pro Gruppe.")
	@ApiResponse(responseCode = "200")
	public Iterable<KundInnenProGruppe> getKundInnenProGruppe() {
		return service.getKundInnenProGruppe();
	}

	@Operation(description = "Lädt bis zu 101 KundInnen einer Tenant-ID anhand eines Suchbegriffs. " +
			"Wenn mehr als 100 KundInnen gefunden werden, sollte den Nutzenden ggf. der Hinweis gegeben werden, dass die " +
			"Suche verfeinert werden sollte.")
	@Parameter(name = "suchbegriff",description = "'Like'-Suche im Nachnamen und der KundInnennummer. Case-Insensitive. " +
			"Suchbegriff muss im Nachnamen ODER in der KundInnennummer vorkommen. " +
			"Wenn kein Suchbegriff oder ein leerer Suchbegriff übergeben wird, werden KundInnen ohne Sucheinschränkung " +
			"geladen.")
	@GetMapping(path="/kundinnen/suche/" ,params = {"suchbegriff"})
	@ApiResponse(responseCode = "200")
	public Iterable<KundIn> getBySuchbegiff(final @RequestParam(name = "suchbegriff",required = false) String suchbegriff) {
		return service.getBySuchbegriff(suchbegriff);
	}

	@Operation(description = "Lädt einen Kunden / eine Kundin anhand der KundInnennummer aus der Datenbank.")
	@GetMapping(path="/kundinnen/",params = {"kundinnennummer"})
	@ApiResponses({
			@ApiResponse(responseCode = "404", description = "KundIn wurde nicht gefunden,"),
			@ApiResponse(responseCode = "200", description = "Wenn die Kundin / der Kunde in der Datenbank gefunden wurde.")
	})
	public KundIn getByKundInnennummer(final @NonNull @RequestParam(name = "kundinnennummer") String kundInnennummer) throws KundInNichtGefundenException {
		return service.getByKundInnennummer(kundInnennummer);
	}
}
