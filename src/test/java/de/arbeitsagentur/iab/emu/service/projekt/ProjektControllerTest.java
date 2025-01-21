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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.arbeitsagentur.iab.emu.config.TenantResolver;
import de.arbeitsagentur.iab.emu.service.kundin.KundInRepository;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.GruppeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjektControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    GruppeRepository gr;

    @Autowired
    KundInRepository kr;

    @Autowired
    TenantResolver tenantResolver;

    @Autowired
    ProjektService projektService;

    String tenant;

    @BeforeEach
    public void beforeEach() {
        tenant = System.currentTimeMillis()+ProjektControllerTest.class.getName();
        tenantResolver.setCurrentTenant(tenant);
    }

    @Test
    void getProjekt() throws Exception {
        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        Projekt angelegtesProjekt = projektService.create(bestelltesProjekt);

        Projekt geladenesProjekt = mapper.readValue( mockMvc.perform(get("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status()
                                .is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString(),
                Projekt.class);

        assertEquals(angelegtesProjekt.getId(),geladenesProjekt.getId());

        assertEquals(2,geladenesProjekt.getFelddefinitionen().size());
    }

    @Test
    void getProjektNichtVorhanden() throws Exception {
        mockMvc.perform(get("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());
    }

    @Test
    void update() throws Exception {

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        Projekt angelegtesProjekt = projektService.create(bestelltesProjekt);

        angelegtesProjekt.getFelddefinitionen().clear();

        mockMvc.perform(put("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(angelegtesProjekt))).andExpect(status()
                .is(HttpStatus.OK.value())).andReturn();

        Projekt geladenesProjekt = mapper.readValue( mockMvc.perform(get("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(),
                Projekt.class);

        assertEquals(angelegtesProjekt.getId(),geladenesProjekt.getId());

        assertEquals(0,geladenesProjekt.getFelddefinitionen().size());

    }

    @Test
    void updateNichtVorhanden() throws Exception {

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        mockMvc.perform(put("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(bestelltesProjekt))).andExpect(status()
                .is(HttpStatus.NOT_FOUND.value())).andReturn().getResponse();

    }

    @Test
    void updateUngueltigeGruppen() throws Exception {

        Projekt bestellesProjekt = ProjektFactory.gueltigesProjektErzeugen();


        Projekt angelegtes = projektService.create(bestellesProjekt);
        angelegtes.getGruppen().remove(0);

        mockMvc.perform(put("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(angelegtes))).andExpect(status().isBadRequest());

    }
}