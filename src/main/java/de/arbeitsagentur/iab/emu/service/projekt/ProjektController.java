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

package de.arbeitsagentur.iab.emu.service.projekt;

import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.UngueltigeFelddefintionException;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.UngueltigeGruppenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/v1/{tenantId}/projekt/")
@Tag(name = "Projekt",description = "Verwaltet das Projekt einer Tenant-ID. Pro Tenant-ID gibt es ein Projekt. Ein Projekt " +
        "besteht aus beschreibenden Attributen, den Gruppen und optionalen Felddefinitionen. Die Felddefinitionen dienen dazu," +
        " bei den einzelnen KundInnen zusätzliche Informationen als Key-Value-Paare abzuspeichern. Dabei kann" +
        " bei jedem Feld ein Datentyp definiert werden. Die Datentypen sind vom Client zu interpretieren.")
public class ProjektController {

    private final ProjektService service;

    public ProjektController(final @Autowired ProjektService service) {
        this.service = service;
    }

    @GetMapping(path = "/")
    @Operation(description = "Lädt das Projekt für eine Tenant-ID. Pro Tenant-ID kann es maximal ein Projekt geben.")
    @ApiResponse(responseCode = "200")
    public Projekt getProjekt() throws ProjektNichtGefundenException {
        return service.getProjekt();
    }

    @PutMapping(path ="/")
    @Operation(summary = "Projekt aktualisieren.",description = """
            Aktualisiert das Projekt für eine Tenant-ID.

            Das Projekt muss mindestens zwei Gruppen  enthalten. Die Gruppengrenzen müssen lückenlos sein und dürfen sich nicht überschneiden.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", description = "Falls ungültige Gruppen oder Felddefinitionen übergeben wurden."),
            @ApiResponse(responseCode = "404", description = "Falls kein Projekt mit der übergebenen ID vorhanden ist.")
    })
    public Projekt update(final @NonNull @RequestBody Projekt projekt) throws UngueltigeGruppenException, UngueltigeFelddefintionException, ProjektNichtGefundenException {
        return service.update(projekt);
    }

}
