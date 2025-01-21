# EMU (work in progress)
EMU steht für elektronischer Münzwurf.

Die Software wird in der Forschung verwendet, um KundInnen zufällig in Gruppen einzuteilen. Die Vorgängerversion von EMU 
wurde z. B. im Forschungsprojekt "Vertragsbeziehungen zwischen Jobcentern und Arbeitslosen - eine Mixed-Methods-Studie 
zu den Inhalten von Eingliederungsvereinbarungen im SGB II" des Instituts für Arbeitsmarkt- und Berufsforschung eingesetzt.

## Wie erfolgt die Einteilung in Gruppen?
Zunächst werden bei Anlage eines neuen Projektes die Gruppen definiert. Eine Gruppe besteht aus Bezeichnung und den 
Zahlenwerten Ober- und Untergrenze (eine Gruppe entspricht somit einem Zahlenintervall mit inklusiven Grenzen). Pro 
Projekt müssen mindestens zwei Gruppen vorhanden sein. Die Grenzen der Gruppen dürfen sich nicht überschneiden. Es darf 
keine Lücken zwischen den Gruppen geben. 

Um die Eingruppierung einer Kundin / eines Kunden auszulösen, werden einige Stammdaten zur Person erfasst und die 
Einwilligung zur Teilnahme eingeholt.
Sofern die Kundin / der Kunde teilnimmt, ermittelt das System einen zufälligen Zahlenwert, der 
sich zwischen niedrigster Untergrenze aller Gruppen und höchster Obergrenze aller Gruppen befindet. 
Die Kundin / der Kunde wird dann der Gruppe zugeordnet, in deren Zahlenbereich der Zufallswert fällt.

### Beispiel:
Gruppe 1: 1 bis 100

Gruppe 2: 101 bis 200

Zufallswert: 100

Ergebnis: Kundin / Kunde wird Gruppe 1 zugeordnet.

### Warum werden Zahlenintervalle verwendet?
Dies ermöglicht, die Wahrscheinlichkeit, mit der ein Kunde in eine bestimmte Gruppe fällt, zu steuern.

# Inhalt der Quellcodeverwaltung
Dieses Repository enthält einen ersten Entwurf für ein neues Backend für EMU. D. h. die Entwicklung ist noch nicht 
abgeschlossen.

Es handelt sich um einen Webservice, der mittels Spring-Boot implementiert ist. Die Datenhaltung erfolgt in einer 
relationalen SQL-Datenbank. Das System unterstützt Multi-Tenancy (Mandantenfähigkeit) mittels Hibernate in einer gemeinsamen
Datenbank. Im Webservice sind lediglich die Operationen implementiert, die vom aktuell geplanten Client benötigt werden.

# Lizenzierung
Der Quellcode ist unter der GNU Affero General Public License in Version 3 veröffentlicht. Siehe LICENSE-Datei im 
Repository.

# Copyright
Copyright 2025 [Institut für Arbeitsmarkt- und Berufsforschung](https://iab.de/)

# Was müssen Nutzende beachten?
Aktuell enthält der Quellcode keinerlei Berechtigungsprüfungen. D. h. falls Nutzende eine solche Prüfung benötigen, muss 
entsprechender Code ergänzt werden! Weil keine Berechtigungslogik implementiert ist und dem System die aktuelle Userin
bzw. der aktuelle User nicht bekannt ist, ist kein Locking implementiert (auch nicht auf Datenbankebene). In einer 
vollständigen Implementierung sollte ggf. ein [Pessimistic-Offline-Lock](https://martinfowler.com/eaaCatalog/pessimisticOfflineLock.html) implementiert werden. Alternativ könnte
[optimistisches Locking mittels Hibernate](https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#locking) 
implementiert werden.

# Warum werden keine Binärdateien geliefert?
Insb. weil keine Berechtigungsprüfung implementiert ist, werden keine fertigen Binärdateien angeboten. 
Berechtigungsprüfungen müssen von den Nutzenden des Codes selbstständig implementiert werden.

# Build?
Das Projekt kann mittels Maven kompiliert werden.

# API-Dokumentation
Die API-Dokumentation wird mit SpringDoc aus dem Code erzeugt. Sie steht nach dem Start des Service
zur Verfügung (http://[HOSTNAME:PORT]/swagger-ui/index.html).

# Die wichtigsten Use-Cases
1. Neue Mandantin / neuen Mandanten bestellen. Dabei werden Projektdaten, Gruppen und UserInnen für eine Mandantin / 
einen Mandanten angelegt.
2. Neue Kundin / neuen Kunden anlegen (Teilnahme am Forschungsprojekt ist freiwillig).
   1. KundIn nimmt teil und wird zufällig einer Gruppe zugeordnet.
   2. KundIn nimmt nicht teil und wird keiner Gruppe zugeordnet.
3. Daten einer Kundin / eines Kunden anpassen.
   1. Stammdaten der Kundin / des Kunden ändern (keine Änderung der Gruppenzuordnung).
   2. KundIn nahm bisher nicht teil und möchte teilnehmen: Es erfolgt eine nachträgliche Gruppenzuordnung.
4. Daten einer Mandantin / eines Mandanten anpassen.
5. Anzahl der Gruppenzuordnungen berechnen.

# Anbindung eines Frontends
## Der Service bildet drei Hauptprozesse ab:
1. Initiale Konfiguration einer Mandantin / eines Mandanten: Über /api/v1/{tenantId}/bestellung/ kann eine neue 
Mandantin / ein neuer Mandant bestellt bzw. konfiguriert werden. Hierbei werden alle nötigen Informationen 
erfasst. Die tenantId identifiziert eine Mandantin / einen Mandanten und wird vom Client vergeben.
2. Konfigurationsänderung: Über /api/v1/{tenantId}/projekt/ und /api/v1/{tenantId}/userinnen/ 
kann die bestehende Konfiguration einer Mandantin / eines Mandanten angepasst werden.
3. KundInnen verwalten und Gruppenzuordnung (Hauptprozess): Über /api/v1/{tenantId}/kundinnen/ können insb. neue KundInnen 
angelegt und zufällig Gruppen zugeordnet werden. Zum Finden bestehender KundInnen steht eine Suche zur Verfügung.

## Sonstiges:
1. Über /api/v1/{tenantId}/kundinnenreport/ kann die Anzahl der KundInnen, die sich in den einzelnen Gruppen befinden,
abgerufen werden.
2. Der Quellcode der Anwendung soll möglichst geschlechtsneutral formuliert sein. Um die Klassennamen möglichst 
kurzzuhalten, werden daher z. B. die Bezeichnungen KundIn und UserIn verwendet.
3. In application.properties muss eine Datenbank konfiguriert werden. Aktuell wird das Projekt mit H2 entwickelt.
4. Die Daten aller MandantInnen werden in einer gemeinsamen Datenbank gespeichert. Dafür wird bei den einzelnen
Entities eine Tenant-ID gespeichert, über die die Mandantin / der Mandant identifiziert werden. Es wird das
entsprechende Hibernate-Feature verwendet. Insb. bei der Implementierung von Native-Queries ist dies zu beachten.

# Wichtige Hinweise:
1. In einigen Hibernate-Versionen funktioniert Multi-Tenancy nicht richtig. Es können teilweise Daten einer fremden 
Tenant-ID abgerufen und bearbeitet werden. Daher wurde in UserIn-Repository und in KundIn-Repository ein Work-Around für 
die Methode "findById" integriert. In UserIn-Repository wurden zusätzlich die Methoden existsById und deleteById 
überschrieben. 
Siehe auch https://hibernate.atlassian.net/browse/HHH-16830#icft=HHH-16830.
2. Als Zufallsgenerator wird ThreadLocalRandom.current().nextInt verwendet. Falls ein besserer Zufallsgenerator
benötigt wird, muss die Implementierung in der Klasse Zufallsgenerator geändert werden.

# Wer sind wir?

[Institut für Arbeitsmarkt- und Berufsforschung - Geschäftsbereich Informationsmanagement und Bibliothek (IMAB)](https://iab.de/bereich/?id=12)

[Impressum](https://iab.de/impressum)


