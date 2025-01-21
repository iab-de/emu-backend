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

import de.arbeitsagentur.iab.emu.service.projekt.felddefinition.Felddefinition;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name="projekt",
        // Nur ein Projekt pro MandantIn.
        uniqueConstraints=
        @UniqueConstraint(columnNames={"tenantId"})

)
public class Projekt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @TenantId
    private String tenantId;

    @Schema(description = "Name des Projekts", maxLength = 1000)
    @Column(length = 1000)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    private List<Felddefinition> felddefinitionen;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Gruppe> gruppen;

    @Schema(description = "Priojektbeschreibung", maxLength = 10000)
    @Column(length = 10000)
    private String beschreibung;

    @Schema(maxLength = 10000)
    @Column(length = 10000)
    private String hilfetext;

    private LocalDate start;

    private LocalDate ende;

    private boolean aktiv;

    @Embedded
    private ProjekterstellendePerson projekterstellendePerson;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHilfetext() {
        return hilfetext;
    }

    public void setHilfetext(String hilfetext) {
        this.hilfetext = hilfetext;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnde() {
        return ende;
    }

    public void setEnde(LocalDate ende) {
        this.ende = ende;
    }

    public ProjekterstellendePerson getProjekterstellendePerson() {
        return projekterstellendePerson;
    }

    public void setProjekterstellendePerson(ProjekterstellendePerson projekterstellendePerson) {
        this.projekterstellendePerson = projekterstellendePerson;
    }

    public List<Felddefinition> getFelddefinitionen() {
        return felddefinitionen;
    }

    public void setFelddefinitionen(List<Felddefinition> felddefinitionen) {
        this.felddefinitionen = felddefinitionen;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Gruppe> getGruppen() {
        return gruppen;
    }

    public void setGruppen(List<Gruppe> gruppen) {
        this.gruppen = gruppen;
    }

    /**
     * Gibt einen String mit einer Fehlerbeschreibung zurück, falls
     * ungültige Felddefinitionen vorhanden sind.
     * @return Fehlermeldung oder Null.
     */
    public String validiereFelddefinitionen() {
        if (felddefinitionen==null) {
            return null;
        }

        if (felddefinitionen.size()>felddefinitionen.stream().map(Felddefinition::getName).distinct().count()) {
            return "Die Namen der Felddefinitionen dürfen nicht mehrfach vorkommen.";
        }

        return null;
    }

    /**
     * Gibt einen String mit einer Fehlerbeschreibung zurück, falls
     * die Gruppen ungütlig sind.
     * @return Fehlermeldung oder Null.
     */
    public String validiereGruppen() {
        if (gruppen == null || gruppen.size() < 2) {
            return "Es sind mindestens zwei Gruppen erforderlich.";
        }

        long anzahlUnterschiedlicheBezeichnungen = gruppen.stream().map(Gruppe::getBezeichnung).distinct().count();
        long anzahlBezeichnungen = gruppen.size();
        if (anzahlBezeichnungen != anzahlUnterschiedlicheBezeichnungen) {
            return "Mindestens eine Gruppenbezeichnung wurde mehrfach vergeben.";
        }

        for (int i = 0; i < gruppen.size(); i++) {
            Gruppe g0 = gruppen.get(i);
            if (g0.getUntergrenze() >= g0.getObergrenze()) {
                return "Fehlerhafte Gruppengrenzen: Untergrenze >= Obergrenze.";
            }
            if (gruppen.size() > i + 1) {
                Gruppe g1 = gruppen.get(i + 1);
                if (g0.getObergrenze() + 1 != g1.getUntergrenze()) {
                    return "Fehlerhafte Gruppengrenzen: Gruppen nicht lückenlos.";
                }

            }
        }

        return null;
    }
}
