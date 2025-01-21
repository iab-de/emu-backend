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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = "/api/v1/{tenantId}/userinnen/")
@Tag(name="UserInnen", description = "Verwaltet die UserInnen für eine Tenant-ID. Die Logins der UserInnen müssen " +
        "eindeutig sein (bezogen auf eine Tenant-ID). Login und Rolle dürfen nicht leer sein.")
public class UserInController {

    private final UserInService service;

    public UserInController(final UserInService service) {
        this.service = service;
    }

    @GetMapping(path = "/{id}")
    @Operation(description = "Lädt die Userin / den User mit der übergebenen ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "UserIn wurde nicht gefunden."),
            @ApiResponse(responseCode = "200",description = "UserIn wurde gefunden.")
    })
    public UserIn get(final @NonNull @PathVariable int id) throws UserInNichtGefundenException {
        return service.get(id);
    }

    @GetMapping(path = "/")
    @Operation(description = "Lädt alle UserInnen.")
    @ApiResponse(responseCode = "200",description = "UserIn wurde gefunden.")
    public Iterable<UserIn> getAll() {
        return service.getAll();
    }

    @PostMapping(path ="/")
    @Operation(summary = "UserIn anlegen.",
            description = "Legt das übergebene Objekt an. Falls hier eine ID übergeben wird, wird diese still ignoriert. " +
                    "D. h. die ID kann nicht vom Client vergeben werden.")
    @ApiResponses ({
        @ApiResponse(responseCode = "201",description = "UserIn wurden angelegt."),
        @ApiResponse(responseCode = "400",description = "Bei Übergabe ungültiger Daten."),
        @ApiResponse(responseCode = "409",description = "Falls das Login bereits verwendet wird.")
    })
    public ResponseEntity<?> create(@NonNull @RequestBody UserIn userIn) throws UngueltigeUserInnendatenException, LoginMehrfachVergebenException {
        try {
            userIn = service.create(userIn);
            return ResponseEntity.status(HttpStatus.CREATED).body(userIn);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Datenkonflikt! Login mehrfach vergeben?");
        }
    }

    @PutMapping(path ="/{id}")
    @Operation(summary = "UserIn aktualisieren.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "UserIn wurde aktualisiert."),
            @ApiResponse(responseCode = "400",description = "Falls im Request-Body eine andere ID " +
                    "angegeben ist als im Request-Pfad oder bei Übergabe ungültiger Daten."),
            @ApiResponse(responseCode = "404",description = "Falls kein UserIn-Objekt mit der übergebenen ID vorhanden ist."),
            @ApiResponse(responseCode = "409",description = "Falls das Login bereits verwendet wird.")
    })
    public UserIn update(final @PathVariable("id") int id,final @NonNull @RequestBody UserIn userIn) throws UngueltigeUserInnendatenException, LoginMehrfachVergebenException,UserInNichtGefundenException {
        if (userIn.getId() != id) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Es wurden widersprüchliche IDs übergeben.");
        }

        try {
            return service.update(userIn);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Datenkonflikt! Login mehrfach vergeben?");
        }
    }

    @DeleteMapping(path="/{id}")
    @Operation(summary = "UserIn löschen.",
    description = "UserIn mit der übergebene ID löschen. Falls die ID nicht vorhanden ist, wird dies still ignoriert.")
    @ApiResponse(responseCode = "200",description = "UserIn wurde gelöscht oder war nicht vorhanden.")
    public void delete(final @PathVariable("id") int id) {
        service.delete(id);
    }

}
