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

import de.arbeitsagentur.iab.emu.service.AbstractServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest

class UserInServiceTest extends AbstractServiceTest {
    @Autowired
    UserInService service;


    @Test
    void get() throws UserInNichtGefundenException, LoginMehrfachVergebenException, UngueltigeUserInnendatenException {
        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        List<Integer> ids = toIdList(service.create(List.of(u)));

        assertEquals(1,ids.size());

        UserIn userInnenGeladen = service.get(ids.get(0));

        assertEquals(u.getRolle(),userInnenGeladen.getRolle());
        assertEquals(u.getLogin(),userInnenGeladen.getLogin());
    }

    @Test
    void getUserNichtGefunden() {

        assertThrows(UserInNichtGefundenException.class, () -> service.get(1));
    }

    @Test
    void getAll() throws LoginMehrfachVergebenException, UngueltigeUserInnendatenException {
        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");

        UserIn u2 = new UserIn();
        u2.setLogin("test2");
        u2.setRolle("testrolle");

        List<Integer> ids = toIdList(service.create(List.of(u1,u2)));

        Iterable<UserIn> userGeladenIterable = service.getAll();
        List<UserIn> userGeladen = new ArrayList<>();
        userGeladenIterable.forEach(userGeladen::add);

        assertEquals(ids.size(),userGeladen.size());

        assertTrue(ids.contains(userGeladen.get(0).getId()));
        assertTrue(ids.contains(userGeladen.get(1).getId()));

        UserIn u1Geladen = userGeladen.stream().filter(u -> u.getLogin().equals(u1.getLogin())).findAny().get();
        assertEquals(u1.getRolle(),u1Geladen.getRolle());

        UserIn u2Geladen = userGeladen.stream().filter(u -> u.getLogin().equals(u1.getLogin())).findAny().get();
        assertEquals(u2.getRolle(),u2Geladen.getRolle());
    }

    @Test
    void createList() throws Exception {
        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");

        UserIn u2 = new UserIn();
        u2.setLogin("test2");
        u2.setRolle("testrolle");

        List<Integer> ids = toIdList(service.create(List.of(u1,u2)));

        assertEquals(2,ids.size());

        UserIn userGeladen1 = service.get(ids.get(0));

        assertEquals(u1.getRolle(),userGeladen1.getRolle());
        assertEquals(u1.getLogin(),userGeladen1.getLogin());

        UserIn userGeladen2 = service.get(ids.get(1));
        assertEquals(u2.getRolle(),userGeladen2.getRolle());
        assertEquals(u2.getLogin(),userGeladen2.getLogin());
    }

    @Test
    void createListLoginMehrfachVergeben()  {
        UserIn u1 = new UserIn();
        u1.setLogin("test");
        u1.setRolle("testrolle");

        UserIn u2 = new UserIn();
        u2.setLogin("test");
        u2.setRolle("testrolle");

        assertThrows(LoginMehrfachVergebenException.class, () -> service.create(List.of(u1,u2)));
    }

    private List<Integer> toIdList(Iterable<UserIn> it) {
        return StreamSupport.stream(it.spliterator(),false)
                .map(UserIn::getId).toList();
    }

    @Test
    void create() throws Exception {
        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        List<Integer> ids = toIdList(service.create(List.of(u)));

        assertEquals(1,ids.size());

        UserIn userGeladen = service.get(ids.get(0));

        assertEquals(u.getRolle(),userGeladen.getRolle());
        assertEquals(u.getLogin(),userGeladen.getLogin());
    }

    @Test
    void createLoginMehrfachVergeben() throws Exception {

        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");

        service.create(u1);

        UserIn u2 = new UserIn();
        u2.setLogin(u1.getLogin());
        u2.setRolle("testrolle");

        assertThrows(LoginMehrfachVergebenException.class,()-> service.create(u2));
    }

    @Test
    void createLoginLeer() {

        UserIn u = new UserIn();
        u.setLogin("");
        u.setRolle("testrolle");

        assertThrows(UngueltigeUserInnendatenException.class,()-> service.create(u));
    }

    @Test
    void createRolleLeer() {

        UserIn u = new UserIn();
        u.setLogin("Login");
        u.setRolle("");

        assertThrows(UngueltigeUserInnendatenException.class,()-> service.create(u));
    }

    @Test
    void update() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("test");
        u.setRolle("testrolle");

        UserIn angelegt = service.create(u);

        assertNotNull(angelegt.getId());

        angelegt.setRolle("geaendert rolle");
        angelegt.setLogin("geaendert login");

        service.update(angelegt);

        UserIn userGeladenNachUpdate = service.get(angelegt.getId());

        assertEquals(angelegt.getRolle(),userGeladenNachUpdate.getRolle());
        assertEquals(angelegt.getLogin(),userGeladenNachUpdate.getLogin());
    }

    @Test
    void updateLoginMehrfachVergeben() throws Exception {

        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");

        service.create(u1);

        UserIn u2 = new UserIn();
        u2.setLogin("test2");
        u2.setRolle("testrolle");

        UserIn angelegt2 = service.create(u2);

        assertNotNull(angelegt2.getId());

        angelegt2.setLogin(u1.getLogin());

        assertThrows(LoginMehrfachVergebenException.class, () -> service.update(angelegt2));
    }

    @Test
    void updateFalscheId() throws Exception {

        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");
        u1.setId(-1);

        assertThrows(UserInNichtGefundenException.class, () -> service.update(u1));
    }

    @Test
    void updateLoginLeer() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("testLogin");
        u.setRolle("testrolle");

        UserIn angelegt = service.create(u);

        angelegt.setLogin("");

        assertThrows(UngueltigeUserInnendatenException.class, () -> service.update(angelegt));
    }

    @Test
    void updateLoginRolle() throws Exception {

        UserIn u = new UserIn();
        u.setLogin("testLogin");
        u.setRolle("testrolle");

        UserIn angelegt = service.create(u);

        angelegt.setRolle("");

        assertThrows(UngueltigeUserInnendatenException.class, () -> service.update(angelegt));
    }


    @Test
    void delete() throws LoginMehrfachVergebenException, UngueltigeUserInnendatenException {

        UserIn u1 = new UserIn();
        u1.setLogin("test1");
        u1.setRolle("testrolle");

        UserIn u2 = new UserIn();
        u2.setLogin("test2");
        u2.setRolle("testrolle");

        List<Integer> ids = toIdList(service.create(List.of(u1,u2)));

        service.delete(ids.get(1));

        Iterable<UserIn> userGeladenIterable = service.getAll();
        List<UserIn> userGeladen = new ArrayList<>();
        userGeladenIterable.forEach(userGeladen::add);

        assertEquals(1,userGeladen.size());
        assertEquals(u1.getRolle(),userGeladen.get(0).getRolle());
        assertEquals(u1.getLogin(),userGeladen.get(0).getLogin());

    }
}