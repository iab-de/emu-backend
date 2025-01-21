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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.arbeitsagentur.iab.emu.service.projekt.Projekt;
import de.arbeitsagentur.iab.emu.service.projekt.ProjektFactory;
import de.arbeitsagentur.iab.emu.service.userin.UserIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BestellungControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    private String tenant;

    @BeforeEach
    void beforeEach() {
        tenant = UUID.randomUUID().toString();
    }

    @Test
    void bestellen() throws Exception {

        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();

        bestellung.setUserInnen(Collections.singletonList(user));

        bestellung.setProjekt(bestelltesProjekt);

        String bestellungJson = mapper.writeValueAsString(bestellung);

        mockMvc.perform(post("/api/v1/"+tenant+"/bestellung/").contentType(MediaType.APPLICATION_JSON).content(bestellungJson)).andReturn();

        Projekt geladenesProjekt = mapper.readValue( mockMvc.perform(get("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(),
                Projekt.class);

        assertNotNull(geladenesProjekt.getId());

        assertEquals(2,geladenesProjekt.getFelddefinitionen().size());
        assertEquals(2,geladenesProjekt.getGruppen().size());

        List<UserIn> loadedUsers = mapper.readValue( mockMvc.perform(get("/api/v1/" + tenant + "/userinnen/").contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(),
                new TypeReference<List<UserIn>>(){});

        assertEquals(1,loadedUsers.size());

    }

    @Test
    void bestellenTenantIDBereitsVergeben() throws Exception {

        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt bestelltesProjekt = ProjektFactory.gueltigesProjektErzeugen();
        String urspruenglicheBeschreibung = bestelltesProjekt.getBeschreibung();
        bestellung.setUserInnen(Collections.singletonList(user));
        bestellung.setProjekt(bestelltesProjekt);


        mockMvc.perform(post("/api/v1/" + tenant + "/bestellung/").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(bestellung)))
                .andExpect(status().isCreated());

        bestelltesProjekt.setBeschreibung("Testprojekt 2");
        // Zweite Bestellung mit derselben TenantID-Funktioniert nicht.
        mockMvc.perform(post("/api/v1/" + tenant + "/bestellung/").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(bestellung)))
                .andExpect(status().isConflict());

        // Prüfung: Sind die ursprünglichen Daten noch vorhanden oder wurden sie überschrieben?
        Projekt geladenesProjekt = mapper.readValue( mockMvc.perform(get("/api/v1/" + tenant + "/projekt/").contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(),
                Projekt.class);

        assertEquals(urspruenglicheBeschreibung,geladenesProjekt.getBeschreibung());

    }

    @Test
    void bestellenUngueltigeGruppen() throws Exception {

        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt projekt = ProjektFactory.gueltigesProjektErzeugen();
        projekt.getGruppen().clear();

        bestellung.setUserInnen(Collections.singletonList(user));
        bestellung.setProjekt(projekt);


        mockMvc.perform(post("/api/v1/" + tenant + "/bestellung/").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(bestellung)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void bereitsVorhandenJa() throws Exception {

        Bestellung bestellung = new Bestellung();

        UserIn user = new UserIn();
        user.setLogin("login");
        user.setRolle("rolle");

        Projekt projekt = ProjektFactory.gueltigesProjektErzeugen();

        bestellung.setUserInnen(Collections.singletonList(user));
        bestellung.setProjekt(projekt);
        String bestellungJson = mapper.writeValueAsString(bestellung);
        mockMvc.perform(post("/api/v1/" + tenant + "/bestellung/").contentType(MediaType.APPLICATION_JSON).content(bestellungJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/" + tenant + "/bestellung/"))
                .andExpect(status().isOk());
    }

    @Test
    public void bereitsVorhandenNein() throws Exception {
        mockMvc.perform(get("/api/v1/" + tenant + "/bestellung/"))
                    .andExpect(status().isNotFound());
    }
}