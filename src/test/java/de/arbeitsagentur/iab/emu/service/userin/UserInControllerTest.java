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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.arbeitsagentur.iab.emu.config.TenantResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.xml.crypto.Data;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@WebMvcTest(UserInController.class)
class UserInControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TenantResolver tenantResolver;

    @MockBean
    private UserInService service;

    @Captor
    ArgumentCaptor<UserIn> userCaptor;

    @Captor
    ArgumentCaptor<Integer> intCaptor;

    @BeforeEach
    void setupTenantID() {
        when(tenantResolver.resolveCurrentTenantIdentifier()).thenReturn("1");
    }
    @Test
    void get() throws Exception {

        UserIn u1 = new UserIn();
        u1.setLogin("login1");


        when(service.get(Mockito.eq(42))).thenReturn(u1);

        UserIn userInGeladen = mapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/1/userinnen/42")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString(), UserIn.class);


        assertEquals(u1.getLogin(),userInGeladen.getLogin());

    }

    @Test
    void getNichtGefunden() throws Exception {

        when(service.get(Mockito.eq(42))).thenThrow(UserInNichtGefundenException.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/1/userinnen/42")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAll() throws Exception {

        UserIn u1 = new UserIn();
        u1.setLogin("login1");
        UserIn u2 = new UserIn();
        u2.setLogin("login2");

        when(service.getAll()).thenReturn(List.of(u1,u2));

        List<UserIn> userInnenGeladen = mapper.readValue(mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/1/userinnen/").contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers
                .status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<UserIn>>(){});

        assertEquals(2,userInnenGeladen.size());
        assertEquals(u1.getLogin(),userInnenGeladen.get(0).getLogin());
        assertEquals(u2.getLogin(),userInnenGeladen.get(1).getLogin());


    }

    @Test
    void create() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        when(service.create(Mockito.any(UserIn.class))).thenReturn(u);

        UserIn userInGeladen = mapper.readValue(mockMvc.perform(post("/api/v1/1/userinnen/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.CREATED.value())).andReturn().getResponse().getContentAsString(), UserIn.class);

        assertEquals(u.getLogin(),userInGeladen.getLogin());
        assertEquals(u.getRolle(),userInGeladen.getRolle());

        verify(service,times(1)).create(userCaptor.capture());

        UserIn userArgument = userCaptor.getValue();

        assertEquals(u.getLogin(),userArgument.getLogin());
        assertEquals(u.getRolle(),userArgument.getRolle());
    }

    @Test
    void createUngueltigeUserInnendatenException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        when(service.create(Mockito.any(UserIn.class))).thenThrow(UngueltigeUserInnendatenException.class);

        mockMvc.perform(post("/api/v1/1/userinnen/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.BAD_REQUEST.value())).andReturn();

    }

    @Test
    void createLoginMehrfachVergebenException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        when(service.create(Mockito.any(UserIn.class))).thenThrow(LoginMehrfachVergebenException.class);

        mockMvc.perform(post("/api/v1/1/userinnen/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.CONFLICT.value())).andReturn();

    }

    @Test
    void createDataIntegrityViolationException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        when(service.create(Mockito.any(UserIn.class))).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/api/v1/1/userinnen/").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.CONFLICT.value())).andReturn();

    }
    @Test
    void update() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");
        u.setId(42);

        when(service.update(Mockito.any())).thenReturn(u);

        UserIn userRueckgabe = mapper.readValue(mockMvc.perform(put("/api/v1/1/userinnen/"+u.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString(), UserIn.class);

        assertEquals(u.getLogin(),userRueckgabe.getLogin());
        assertEquals(u.getRolle(),userRueckgabe.getRolle());
        assertEquals(u.getId(),userRueckgabe.getId());

        verify(service,times(1)).update(userCaptor.capture());

        UserIn userAktualisiert = userCaptor.getValue();
        assertEquals(u.getLogin(),userAktualisiert.getLogin());
        assertEquals(u.getRolle(),userAktualisiert.getRolle());
        assertEquals(u.getId(),userAktualisiert.getId());

    }

    @Test
    void updateUngueltigeUserInnendatenException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");
        u.setId(42);

        when(service.update(Mockito.any())).thenThrow(UngueltigeUserInnendatenException.class);

        mockMvc.perform(put("/api/v1/1/userinnen/"+u.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.BAD_REQUEST.value())).andReturn();


    }

    @Test
    void updateLoginMehrfachVergebenException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");
        u.setId(42);

        when(service.update(Mockito.any())).thenThrow(LoginMehrfachVergebenException.class);

        mockMvc.perform(put("/api/v1/1/userinnen/"+u.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.CONFLICT.value())).andReturn();
    }


    @Test
    void updateDataIntegrityViolationException() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");
        u.setId(42);

        when(service.update(Mockito.any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(put("/api/v1/1/userinnen/"+u.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.CONFLICT.value())).andReturn();
    }

    @Test
    void delete() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");
        u.setId(42);

        when(service.update(Mockito.any())).thenReturn(u);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/1/userinnen/"+u.getId()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(u))).andExpect(MockMvcResultMatchers
                .status()
                .is(HttpStatus.OK.value())).andReturn().getResponse();

        verify(service,times(1)).delete(intCaptor.capture());

        assertEquals(u.getId(),intCaptor.getValue());
    }
}