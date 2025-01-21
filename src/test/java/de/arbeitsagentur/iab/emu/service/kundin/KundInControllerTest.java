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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KundInController.class)
class KundInControllerTest {

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TenantResolver tenantResolver;

	@MockBean
	private KundInService service;

	@Captor
	ArgumentCaptor<KundInnendaten> kundendatenCaptor;

	@BeforeEach
	void setupTenantID() {
		when(tenantResolver.resolveCurrentTenantIdentifier()).thenReturn("1");
	}

	@Test
	void create() throws Exception {

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("123A567890");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		KundIn kundIn = Mockito.mock(KundIn.class);
		when(kundIn.getId()).thenReturn(42);
		Mockito.when(service.create(Mockito.any(KundInnendaten.class))).thenReturn(kundIn);

		KundIn kundInGeladen = mapper.readValue(mockMvc.perform(post("/api/v1/"+1+"/kundinnen").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(k))).andExpect(
				status().isCreated()).andReturn().getResponse().getContentAsString(), KundIn.class);

		assertNotNull(kundIn.getId());
		
		assertEquals(kundIn.getId(), kundInGeladen.getId());

	}
	
	@Test
	void createDoppelteKundennummer() throws Exception {
		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		Mockito.when(service.create(Mockito.any(KundInnendaten.class))).thenThrow(KundInnennummerBereitsVorhandenException.class);

		mockMvc
			.perform(
					post("/api/v1/"+1+"/kundinnen")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
			.andExpect(MockMvcResultMatchers
					.status()
					.is(HttpStatus.CONFLICT.value()));

	}

	@Test
	void createDataIntegrityViolationException() throws Exception {
		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		Mockito.when(service.create(Mockito.any(KundInnendaten.class))).thenThrow(DataIntegrityViolationException.class);

		mockMvc
				.perform(
						post("/api/v1/"+1+"/kundinnen")
								.contentType(MediaType.APPLICATION_JSON)
								.content(mapper.writeValueAsString(k)))
				.andExpect(MockMvcResultMatchers
						.status()
						.is(HttpStatus.CONFLICT.value()));

	}
	
	@Test
	void createUngueltigeKundennummer() throws Exception {

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		Mockito.when(service.create(Mockito.any(KundInnendaten.class))).thenThrow(UngueltigeKundInnennummerException.class);

		mockMvc
			.perform(
					post("/api/v1/"+1+"/kundinnen")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
			.andExpect(MockMvcResultMatchers
					.status()
					.is(HttpStatus.BAD_REQUEST.value()));

	}

	@Test
	void update() throws Exception {

		KundIn kundIn = Mockito.mock(KundIn.class);
		when(kundIn.getId()).thenReturn(42);
		Mockito.when(service.update(Mockito.anyInt(),Mockito.any(KundInnendaten.class))).thenReturn(kundIn);

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		KundIn kundInNachUpdate = mapper.readValue(mockMvc.perform(patch("/api/v1/"+1+"/kundinnen/42")
				.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
						.andExpect(MockMvcResultMatchers
								.status()
								.is(HttpStatus.OK.value()))
				.andReturn()
				.getResponse()
				.getContentAsString(), KundIn.class);

		assertEquals(kundIn.getId(), kundInNachUpdate.getId());

		verify(service,times(1)).update(Mockito.eq(42),kundendatenCaptor.capture());

		KundInnendaten kundInnendatenArg = kundendatenCaptor.getValue();

		assertEquals(k.getNachname(), kundInnendatenArg.getNachname());
		assertEquals(k.getVorname(), kundInnendatenArg.getVorname());
		assertEquals(k.getKundInnenennummer(), kundInnendatenArg.getKundInnenennummer());
		assertEquals(k.getGeburtsdatum(), kundInnendatenArg.getGeburtsdatum());
		assertEquals(k.getTeilnahmeAbsagegrund(), kundInnendatenArg.getTeilnahmeAbsagegrund());
	}

	@Test
	void updateUngueltigeKundInnennummer() throws Exception {

		Mockito.when(service.update(Mockito.anyInt(),Mockito.any(KundInnendaten.class))).thenThrow(UngueltigeKundInnennummerException.class);

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		mockMvc.perform(patch("/api/v1/"+1+"/kundinnen/42")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
				.andExpect(MockMvcResultMatchers
						.status()
						.is(HttpStatus.BAD_REQUEST.value()))
				.andReturn();

	}

	@Test
	void updateKundInNichtGefunden() throws Exception {

		Mockito.when(service.update(Mockito.anyInt(),Mockito.any(KundInnendaten.class))).thenThrow(KundInNichtGefundenException.class);

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		mockMvc.perform(patch("/api/v1/"+1+"/kundinnen/42")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
				.andExpect(MockMvcResultMatchers
						.status()
						.is(HttpStatus.NOT_FOUND.value()))
				.andReturn();

	}

	@Test
	void updateKundInnennummerBereitsVorhanden() throws Exception {

		Mockito.when(service.update(Mockito.anyInt(),Mockito.any(KundInnendaten.class))).thenThrow(KundInnennummerBereitsVorhandenException.class);

		KundInnendaten k = new KundInnendaten();
		k.setGeburtsdatum(LocalDate.now());
		k.setNachname("Unittest");
		k.setVorname("J");
		k.setKundInnenennummer("ABC");
		k.setTeilnahmeAbsagegrund(TeilnahmeAbsagegrund.Teilnahme);

		mockMvc.perform(patch("/api/v1/"+1+"/kundinnen/42")
						.contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(k)))
				.andExpect(MockMvcResultMatchers
						.status()
						.is(HttpStatus.CONFLICT.value()))
				.andReturn();

	}


	@Test
	void getById() throws Exception {
		KundIn kundIn = Mockito.mock(KundIn.class);
		when(kundIn.getId()).thenReturn(42);
		Mockito.when(service.getById(Mockito.eq(42))).thenReturn(kundIn);

		KundIn kundInGeladen = mapper.readValue(mockMvc.perform(get("/api/v1/"+1+"/kundinnen/"+42)).andExpect(
				status().isOk()).andReturn().getResponse().getContentAsString(), KundIn.class);

		assertEquals(kundIn.getId(), kundInGeladen.getId());
	}

	@Test
	void getByIdNichtGefunden() throws Exception {

		Mockito.when(service.getById(Mockito.eq(42))).thenThrow(new KundInNichtGefundenException(""));

		mockMvc.perform(get("/api/v1/"+1+"/kundinnen/"+42)).andExpect(status().isNotFound());


	}

	@Test
	void getBySuchbegriff() throws Exception {
		KundIn kundIn = Mockito.mock(KundIn.class);
		when(kundIn.getId()).thenReturn(42);
		Mockito.when(service.getBySuchbegriff(Mockito.eq("suchwort"))).thenReturn(List.of(kundIn));

		List<KundIn> kundenGeladen = mapper.readValue(mockMvc.perform(get("/api/v1/"+1+"/kundinnen/suche/?suchbegriff=suchwort")).andExpect(MockMvcResultMatchers
				.status()
				.is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString(),new TypeReference<List<KundIn>>() {});

		assertEquals(1, kundenGeladen.size());

		assertEquals(kundIn.getId(),kundenGeladen.get(0).getId());

	}

	@Test
	void getByKundInnennummer() throws Exception {
		KundIn kundIn = Mockito.mock(KundIn.class);
		when(kundIn.getId()).thenReturn(42);

		Mockito.when(service.getByKundInnennummer(Mockito.eq("abc"))).thenReturn(kundIn);

		KundIn kundInGeladen = mapper.readValue(mockMvc.perform(get("/api/v1/"+1+"/kundinnen/?kundinnennummer=abc")).andExpect(MockMvcResultMatchers
				.status()
				.is(HttpStatus.OK.value())).andReturn().getResponse().getContentAsString(),KundIn.class);

		assertEquals(kundIn.getId(),kundInGeladen.getId());

	}

	@Test
	void getByKundInnennummerNichtGefunden() throws Exception {

		Mockito.when(service.getByKundInnennummer(Mockito.anyString())).thenThrow(KundInNichtGefundenException.class);

		mockMvc.perform(get("/api/v1/"+1+"/kundinnen/?kundinnennummer=abc")).andExpect(MockMvcResultMatchers
				.status()
				.is(HttpStatus.NOT_FOUND.value())).andReturn();

	}

	@Test
	void testGetKundInnenProGruppe() throws Exception {

		KundInnenProGruppe kundInnenProGruppe = new KundInnenProGruppe();
		kundInnenProGruppe.setGruppe("Gruppe");
		kundInnenProGruppe.setAnzahl(10);


		Mockito.when(service.getKundInnenProGruppe()).thenReturn(List.of(kundInnenProGruppe));

		List<KundInnenProGruppe> report = mapper.readValue(mockMvc.perform(get("/api/v1/"+1+"/kundinnenreport/")).andExpect(
				status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<KundInnenProGruppe>>() {});

		assertEquals(1,report.size());
		assertEquals(kundInnenProGruppe.getGruppe(),report.get(0).getGruppe());
		assertEquals(kundInnenProGruppe.getAnzahl(),report.get(0).getAnzahl());
	}
}

