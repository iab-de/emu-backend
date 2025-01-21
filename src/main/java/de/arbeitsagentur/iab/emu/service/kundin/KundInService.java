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

import de.arbeitsagentur.iab.emu.service.projekt.gruppe.Gruppe;
import de.arbeitsagentur.iab.emu.service.projekt.gruppe.GruppeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class KundInService {

    private final KundInRepository kundInRepository;

    private final GruppeRepository gruppeRepository;

    public KundInService(KundInRepository kundInRepository, GruppeRepository gruppeRepository) {
        this.kundInRepository = kundInRepository;
        this.gruppeRepository = gruppeRepository;
    }


    /**
     * Ordnet der Kundin / dem Kunden eine zufällige {@link Gruppe} zu.
     * @param kundIn Diese(r) Kundin / Kunde wird einer Gruppe zugeordnet.
     * @throws IllegalArgumentException Falls der Kundin / dem Kunden bereits eine Gruppe zugeordnert ist.
     * @throws GruppeNichtGefundenException Falls die Gruppendaten inkonsistent sind.
     */
    private void muenzwurf(@NonNull KundIn kundIn) throws GruppeNichtGefundenException {

        if (kundIn.getGruppe() != null) {
            // Die Gruppe darf nur einmalig zugeordnet werden.
            // Wenn dieser Fehler auftritt, handelt es sich um einen
            // Programmierfehler.
            throw new IllegalArgumentException("Gruppe bereits gesetzt.");
        }

        final int maxObergrenze = gruppeRepository.getMaxObergrenze();
        final int minUntergrenze = gruppeRepository.getMinUntergrenze();
        final int zufallswert = Zufallsgenerator.zufallsgenerator(minUntergrenze,maxObergrenze);
        final Gruppe gruppe = gruppeRepository.findGruppeByZufallswert(zufallswert)
                .orElseThrow(() -> new GruppeNichtGefundenException("Gruppe für Zufallswert "+zufallswert+" nicht gefunden!"));

        kundIn.setGruppe(gruppe);

    }

    /**
     * Anlegen einer Kundin / eines Kunden. Nur bei Teilnahme erfolgt der Münzwurf.
     * @throws UngueltigeKundInnennummerException Falls KundInnennummer ungültig.
     * @throws GruppeNichtGefundenException Falls die Gruppendaten inkonsitent sind.
     * @throws KundInnennummerBereitsVorhandenException Falls eine KundInnennummer in der Datenbank vorhanden ist.
     * @throws DataIntegrityViolationException Falls eine KundInnennummer in der Datenbank vorhanden ist.
     * @param kundInnendaten Erzeugt eine neue Kundin / einen neuen Kunden mit diesen Daten.
     * @return Das angelegte Objekt.
     */
    public KundIn create(@NonNull KundInnendaten kundInnendaten) throws UngueltigeKundInnennummerException, GruppeNichtGefundenException, KundInnennummerBereitsVorhandenException {

        Objects.requireNonNull(kundInnendaten);
        checkKundinnennummer(kundInnendaten,null);

        final KundIn kundIn = new KundIn();
        kundIn.setKundInnendaten(kundInnendaten);
        if (kundInnendaten.getTeilnahmeAbsagegrund() == TeilnahmeAbsagegrund.Teilnahme) {
            muenzwurf(kundIn);
        }

        return kundInRepository.save(kundIn);
    }

    /**
     * Überprüft die Gültigkeit einer KundInnennummer und ob diese bereits vergeben wurde.
     * @throws UngueltigeKundInnennummerException Falls KundInnennummer ungültig.
     * @throws KundInnennummerBereitsVorhandenException Falls die KundInnennummer bereits vorhanden ist.
     * @param kundInnendaten Die KundInnennummer aus diesen Daten wird geprüft.
     * @param kundInnenenidAusgeschlossen Falls dieser Parameter nicht NULL ist, wird diese KundInnen-ID bei der Prüfung,
     *                 ob die KundInnennummer bereits vergeben wurde, nicht berücksichtigt (aktuell
     *                 wird dieser Ausschluss bei Updates benötigt).
     */
    private void checkKundinnennummer(@NonNull KundInnendaten kundInnendaten, Integer kundInnenenidAusgeschlossen) throws UngueltigeKundInnennummerException, KundInnennummerBereitsVorhandenException {
        if (!kundInnendaten.isKundInnennummerValid()) {
            throw new UngueltigeKundInnennummerException("Ungültige kundInnenennummer: " + kundInnendaten.getKundInnenennummer());
        }

        if (isKundInnennummerVergeben(kundInnendaten.getKundInnenennummer(),kundInnenenidAusgeschlossen)) {
            throw new KundInnennummerBereitsVorhandenException("Die KundInnenennummer '"+ kundInnendaten.getKundInnenennummer()+"' ist bereits im System vergeben.");
        }
    }

    /**
     * Aktualisiert die Daten eienr Kundin / eines Kunden.
     * @throws ResponseStatusException Falls die kundInnenennummer ungültig.
     * @throws DataIntegrityViolationException Falls eine kundInnenennummer doppelt vergeben wird.
     * @param id ID der Kundin / des Kunden.
     * @param kundInnendaten Diese Daten werden bei der Kundin / beim Kunden eingetagen, sofern diese gültig sind.
     */
    public KundIn update(int id, @NonNull KundInnendaten kundInnendaten) throws UngueltigeKundInnennummerException, KundInNichtGefundenException,
            KundInnennummerBereitsVorhandenException,
            GruppeNichtGefundenException {
        Objects.requireNonNull(kundInnendaten);
        checkKundinnennummer(kundInnendaten,id);

        final KundIn kundIn = kundInRepository.findById(id)
                .orElseThrow(() -> new KundInNichtGefundenException("KundIn "+id+" nicht gefunden!"));

        kundIn.setKundInnendaten(kundInnendaten);
        if (kundInnendaten.getTeilnahmeAbsagegrund() == TeilnahmeAbsagegrund.Teilnahme && kundIn.getGruppe() == null) {
            muenzwurf(kundIn);
        }

        kundInRepository.save(kundIn);
        return kundIn;
    }

    public KundIn getById(@NonNull int id) throws KundInNichtGefundenException {
        return kundInRepository.findById(id)
                .orElseThrow(() -> new KundInNichtGefundenException("KundIn "+id+" nicht gefunden!"));
    }

    public KundIn getByKundInnennummer(@NonNull String kundInnennummer) throws KundInNichtGefundenException {
        return kundInRepository.findByKundInnennummer(kundInnennummer)
                .orElseThrow(() -> new KundInNichtGefundenException("KundIn mit KundInnennummer "+kundInnennummer+" nicht gefunden!"));
    }

    public Iterable<KundInnenProGruppe> getKundInnenProGruppe() {
        return kundInRepository.getAnzahlInGruppen();
    }

    public Iterable<KundIn> getBySuchbegriff(String suchbegriff) {
        if (suchbegriff == null || suchbegriff.isEmpty()) {

            return kundInRepository.findAllPagable(PageRequest.of(0,101));
        }
        return kundInRepository.findBySuchbegriff(suchbegriff,PageRequest.of(0,101));
    }

    /**
     * Prüft, ob eine KundInnenennummer bereits vergeben ist.
     * @param kundInnennummer Diese KundInnennummer wird geprüft.
     * @param kundInIdAusgeschlossen (Optional) KundIn-ID, die nicht bei der Prüfung berücksichtigt werden soll.
     * @return Gibt True zurück, falls die KundInnenennummer bereits vergeben ist.
     */
    boolean isKundInnennummerVergeben(final @NonNull String kundInnennummer,final Integer kundInIdAusgeschlossen) {

        final Optional<KundIn> kundInOption = kundInRepository.findByKundInnennummer(kundInnennummer);
        if (kundInOption.isPresent()) {
            if (kundInIdAusgeschlossen != null) {
                final KundIn kundIn = kundInOption.get();
                return !kundInIdAusgeschlossen.equals(kundIn.getId());
            } else {
                return true;
            }
        }
        return false;
    }
}
