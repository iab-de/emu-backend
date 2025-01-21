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

import de.arbeitsagentur.iab.emu.service.projekt.ProjektBereitsVorhandenException;
import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import de.arbeitsagentur.iab.emu.service.userin.LoginMehrfachVergebenException;
import de.arbeitsagentur.iab.emu.service.userin.UngueltigeUserInnendatenException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@OpenAPIDefinition(
        info = @Info(
                title = "EMU API (work in progress)",
                version = "0.0.1",
                description = """
                        Copyright (C) 2025 Institut für Arbeitsmarkt und Berufsforschung (<https://www.iab.de>)
                        
                        License: GNU Affero General Public License Version 3
                                                
                        EMU unterstüzt mehrere MandantInnen (Multi-Tenancy). Daher muss immer eine Tenant-ID im Pfad übergeben werden.

                        WARNUNG: AKTUELL SIND KEINE SECURITY-MECHANISMEN IMPLEMENTIERT!
                        
                        # Ablauf zum Anlegen eines neuen Projekts:
                        1. Client erzeugt eine neue Tenant-ID (muss eindeutig über alle MandantInnen sein). Kann später nicht mehr geändert werden! Das Erzeugen erfolgt clientseitig.
                        2. Client legt für eine Tenant-ID ein Projekt an (falls die Tenant-ID bereits vergeben ist, kommt es hier zu einem Fehler).
                        3. Client legt UserInnen an.

                        # Münzwurf:
                        Der Münzwurf erfolgt im Backend, wenn ein Kunde / eine Kundin gespeichert wird."""
        )
)
@RestController()
@RequestMapping(path = "/api/v1/{tenantId}/bestellung/")
@Tag(name = "Bestellung",description = "Ermöglicht dem Client gleichzeitig ein Projekt inkl. Gruppen und UserInnen" +
        " anzulegen. Die Tenant-ID sollte vom Client" +
        " zufällig gewählt werden (z. B. UUID um Kollisionen möglichst zu vermeiden).")
public class BestellungController {

    private final BestellungService service;

    public BestellungController(BestellungService service) {
        this.service = service;
    }

    @PostMapping(path ="/")
    @Transactional
    @Operation(description = "Schickt eine Bestellung ab und legt dadurch Gruppen, UserInnen und ein Projekt für einen Mandanten an.")
    @ApiResponses({
            @ApiResponse(responseCode = "409",description = "Wenn bereits ein Projekt für die verwendete TenantID angelegt ist."),
            @ApiResponse(responseCode = "400",description = "Wenn ungültige Gruppen oder Felddefinitionen im Projekt übergeben werden."),
            @ApiResponse(responseCode = "201",description = "Wenn alles angelegt wurde.")
    })
    public ResponseEntity<?> bestellen(@NonNull @RequestBody Bestellung bestellung) throws ProjektBereitsVorhandenException, UngueltigeGruppenException, UngueltigeFelddefintionException, LoginMehrfachVergebenException, UngueltigeUserInnendatenException {
        Objects.requireNonNull(bestellung);
        try {
            service.bestellen(bestellung);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Datenkonflikt! Wird die Tenant-ID bereits von einem anderen Projekt verwendet?");
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping(path ="/")
    @Operation(description = "Prüft, ob für die verwendete Tenant-ID bereits eine Bestellung abgeschickt wurde.")
    @ApiResponses({
            @ApiResponse(responseCode = "404",description = "Tenant-ID ist noch nicht vorhanden und kann verwendet werden."),
            @ApiResponse(responseCode = "200",description = "Tenant-ID bereits vergeben. Es muss eine andere ID verwendet werden.")
    })
    public ResponseEntity<?> bereitsVorhanden() {
        if (service.bereitsVorhanden()) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
