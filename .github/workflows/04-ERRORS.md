# Level 4: Debugging Challenge - Fehlerliste

Dieser Workflow enthält **9 absichtliche Fehler**. Finde und behebe sie alle!


## Debugging-Strategie

1. **Workflow ausführen:**
   ```bash
   act -W .github/workflows/04-debugging-challenge.yml
   ```

2. **Fehler identifizieren:**
   - Lies die Fehlermeldungen sorgfältig
   - Prüfe welcher Job/Step fehlschlägt
   - Nutze die Hinweise oben

3. **Fehler beheben:**
   - Korrigiere einen Fehler nach dem anderen
   - Teste nach jeder Korrektur

4. **Validieren:**
   - Workflow sollte erfolgreich durchlaufen
   - Alle Jobs sollten grün sein

## Tipps

- Nutze `act --list` um die Job-Struktur zu sehen
- Nutze `act -j <job-name>` um einzelne Jobs zu testen
- Die Lösung findest du in `04-debugging-challenge-SOLUTION.yml`
- Versuche die Fehler selbst zu finden, bevor du die Lösung anschaust!

## Erfolgskriterien

Der Workflow ist korrekt, wenn:
- [ ] Alle 3 Jobs erfolgreich durchlaufen
- [ ] Das JAR korrekt gebaut und hochgeladen wird
- [ ] Das Docker-Image erfolgreich gebaut wird
- [ ] Der Container startet und erreichbar ist
- [ ] Der API-Test erfolgreich ist
- [ ] Der Container am Ende gestoppt wird
- [ ] Alle Spuren beseitigt werden

---

## Fehlerübersicht

### Fehler 1: Falscher Cache-Pfad
- **Symptom:** Maven-Cache funktioniert nicht korrekt
- **Hinweis:** Wo liegt pom.xml?

### Fehler 2: Falsches Backend Ansteurung
- **Symptom:** Maven findet die pom.xml nicht
- **Hinweis:** Wo liegt das Backend-Projekt?

### Fehler 3: Falscher Artefakt-Pfad
- **Symptom:** Artefakt wird nicht gefunden/hochgeladen
- **Hinweis:** Der Pfad muss relativ zum Repository-Root sein

### Fehler 4: Fehlende Job-Abhängigkeit
- **Symptom:** Docker-Job startet bevor das JAR verfügbar ist
- **Hinweis:** Welcher Job muss zuerst fertig sein?

### Fehler 5: Falsche Dockerfile
- **Symptom:** Dockerfile wird nicht gefunden
- **Hinweis:** Wo liegt das Dockerfile wirklich?

### Fehler 6: Port-Mapping-Fehler
- **Symptom:** Container läuft, ist aber nicht erreichbar
- **Hinweis:** Auf welchem Port läuft die Anwendung?

### Fehler 7: Falscher Port in Health Check
- **Symptom:** Health Check schlägt fehl
- **Hinweis:** Muss zum Port-Mapping passen

### Fehler 8: Falscher API-Endpoint
- **Symptom:** Test schlägt fehl, leere Response
- **Hinweis:** Welche Endpoints gibt es wirklich? (Siehe HelloController.java)

### Fehler 9: Cleanup läuft nicht bei Fehlern
- **Symptom:** Container bleibt laufen wenn Tests fehlschlagen
- **Hinweis:** Wie stellt man sicher, dass ein Step immer läuft?
