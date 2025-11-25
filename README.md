# GitHub Actions Local Demo

Dieses Repository demonstriert die **lokale Ausführung und das Testen von GitHub Actions** mit [nektos/act](https://github.com/nektos/act).
Der Ansatz ermöglicht schnelles Feedback und reduziert die Notwendigkeit von Trial-and-Error-Commits.
Die Anleitung ist in aufeinander aufbauende _"Levels"_ unterteilt.

## Voraussetzungen

1.  **Docker** muss installiert sein und der Daemon muss laufen.
2.  **nektos/act** muss installiert sein.
    *   **macOS (Homebrew):** `brew install act`
    *   **Windows (Chocolatey):** `choco install act-cli`
    *   **Linux (Curl):** `curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash`

## Basis-Setup (Level 0)

Eine Konfigurationsdatei wird verwendet, um die `act`-Befehle kurz und konsistent zu halten.

1.  **Repository klonen**

2.  **Act Konfiguration anlegen:**
    ```
    cp ./act/.actrc.level0.example .actrc
    ```
3. **`act` testen**:
   ```
   # listet alle jobs auf
   act --list

   # validiert alle workflows
   act validate

    # Einen Job ausführen
    act --job <job>

    # Einen Workflow spezifizieren
    act --workflows <path/to/workflow>

    # Einen Job spezifizieren
    act --workflows <path/to/workflow> -j<job>
    act -j <job>
   ```

## Level 1: Die erste lokale Ausführung

**Szenario:**
Nach Code-Änderungen im `backend/` Verzeichnis soll die Korrektheit der CI-Pipeline validiert werden, bevor die Änderungen in die Versionskontrolle eingecheckt werden.

### 1. Workflow-Analyse
Die Datei `.github/workflows/01-basic.yml` definiert zwei Jobs:
*   **`test`:** Führt Maven Unit-Tests aus.
*   **`build`:** Baut das JAR-Artefakt, abhängig vom Erfolg des `test`-Jobs (`needs: test`).

### 2. Lokale Ausführung

**Gesamten Workflow ausführen:**
```bash
act -W .github/workflows/01-basic.yml
```

**Nur den `test`-Job ausführen:**
```bash
act -W .github/workflows/01-basic.yml -j test
```

### 3. Debugging-Demonstration
Um den Nutzen des lokalen Debuggings zu demonstrieren:
1.  absichtlichen Fehler in einer Testdatei ein (z.B. `backend/src/test/...`)
2.  Test Job mit `act -j test` erneut ausführen
3.  Logs auswerten

--- 

## Level 2: Docker Builds

**Szenario:**
In diesem Level wird die Anwendung containerisiert. Das Ziel ist es, ein Docker-Image zu bauen, das sofort lokal ausführbar ist.

**Ausführung:**
```bash
act -W .github/workflows/02-docker-build.yml
```

**Überprüfung**
```
docker images | grep --color -E '^|demo-app|'
docker run --rm -p 8080:8080 demo-app:local
```

---

## Level 3: End-to-End (E2E) Health Checks

**Szenario:**  
Nach dem Build und Deployment einer Anwendung ist es wichtig zu verifizieren, dass sie tatsächlich funktioniert. E2E Health Checks starten die Anwendung in einem Container und testen, ob alle Endpoints erreichbar sind.

### 1. Workflow-Analyse
Die Datei `.github/workflows/03-e2e-healthcheck.yml` demonstriert drei Jobs:

- **`test`:** Führt Unit Tests aus
- **`docker-build`:** Baut das JAR und erstellt ein Docker-Image
- **`test-health-and-api`:** Nutzt das Image für Health Checks und API Tests

**Wichtige Konzepte:**
- **Job-Abhängigkeiten** – `needs:` stellt sicher, dass Jobs in der richtigen Reihenfolge laufen
- **Docker Layer Caching** – `actions/cache@v4` cached Docker-Layers, sodass das Image nur einmal gebaut wird
- **Image Wiederverwendung** – Der Test-Job nutzt das Image statt es neu zu bauen
- **`if: always()`** – Steps laufen auch bei Fehlern (für Cleanup)

### 2. Lokale Ausführung

**Gesamten Workflow ausführen:**
```bash
act -W .github/workflows/03-e2e-healthcheck.yml
```

### 4. Debugging-Demonstration

Um die Health Check Logik zu verstehen:

1. **Timeout verkürzen:**  
   `max_attempts=30` zu `max_attempts=3`
   
2. **Workflow ausführen:**
   ```bash
   act -W .github/workflows/03-e2e-healthcheck.yml
   ```

3. **Beobachten:**  
   Der Workflow könnte fehlschlagen, wenn die App nicht schnell genug startet.
   Die Container-Logs werden automatisch angezeigt.

4. **Korrigieren:**  
   Wert zurück auf `max_attempts=30` setzen → Genug Zeit für Startup


---

## Level 4: Debug – Fehlerhafte Workflows beheben

**Szenario:**  
Dieser Workflow enthält mehrere Fehler. Finde und behebe sie alle, sodass der Workflow erfolgreich durchläuft.

### 1. Aufgabe

Die Datei `.github/workflows/04-debugging-challenge.yml` ist defekt und muss repariert werden.

**Ziel:** Alle Jobs sollen erfolgreich durchlaufen:
- `build` - JAR bauen und hochladen
- `docker` - Docker-Image erstellen
- `test` - Container starten und API testen

### 2. Vorgehen

**Workflow ausführen:**
```bash
act -W .github/workflows/04-debugging-challenge.yml
```

**Fehler finden und beheben:**
- Analysiere die Fehlermeldungen
- Korrigiere die Fehler im Workflow
- Teste nach jeder Änderung

**Erfolgreich, wenn:**
```bash
act -W .github/workflows/04-debugging-challenge.yml
```
→ Alle Jobs laufen durch ohne Fehler

### 3. Hilfe

Falls du nicht weiterkommst:
- **Hinweise:** `.github/workflows/04-ERRORS.md` (enthält Tipps zu den Fehlern)
- **Lösung:** `.github/workflows/04-debugging-challenge-SOLUTION.yml` (erst nach eigenen Versuchen!)

---

## Level 5: Matrix Builds – Parallelisierung

**Szenario:**  
Matrix Builds ermöglichen es, denselben Workflow mit verschiedenen Konfigurationen parallel auszuführen. Dies ist nützlich für Cross-Platform-Tests, Multi-Version-Support oder verschiedene Build-Varianten.

### 1. Workflow-Analyse

Die Datei `.github/workflows/05-matrix-builds.yml` demonstriert drei Matrix-Strategien:

**`test-matrix`:** Einfache Matrix
- Testet verschiedene Maven-Ziele (unit tests, package)
- = 2 parallele Jobs

**`docker-matrix`:** Tag Matrix
- Baut Docker-Images mit verschiedenen Tags
- latest und dev
- Mit Docker Layer Caching für schnellere Builds
- = 2 parallele Jobs

**`integration-test-matrix`:** Include Matrix
- Nutzt `include:` für spezifische Kombinationen
- Test-Typ + Command + Beschreibung
- = 3 parallele Jobs (unit, integration, compile-only)

**`matrix-summary`:** Zusammenfassung
- Läuft **immer** (auch bei Fehlern mit `if: always()`)
- Zeigt Status aller Matrix-Jobs
- Gibt Gesamtergebnis aus

### 2. Lokale Ausführung

**Gesamten Workflow ausführen:**
```bash
act -W .github/workflows/05-matrix-builds.yml
```

**Nur einen Matrix-Job ausführen:**
```bash
act -W .github/workflows/05-matrix-builds.yml -j test-matrix
```

### 3. Matrix-Konzepte

**Einfache Matrix:**
```yaml
strategy:
  matrix:
    test-type: ['unit', 'package']
    tag: ['latest', 'dev']
```
→ Erstellt 4 Jobs (2 × 2 Kombinationen)

**Matrix mit include:**
```yaml
strategy:
  matrix:
    include:
      - test-type: 'unit'
        test-command: 'mvn test'
        description: 'Unit Tests'
      - test-type: 'integration'
        test-command: 'mvn verify'
        description: 'Integration Tests'
```
→ Erstellt genau 2 Jobs (nur die definierten Kombinationen)

**fail-fast:**
- `fail-fast: false` → Alle Jobs laufen weiter, auch bei Fehlern
- `fail-fast: true` (default) → Stoppt alle Jobs bei erstem Fehler

**max-parallel:**
- `max-parallel: 1` → Jobs laufen sequentiell (bessere Log-Lesbarkeit)
- `max-parallel: 2` → Maximal 2 Jobs parallel
- Ohne `max-parallel` → Alle Jobs laufen parallel

---

## Level 6: Secrets & Variables

**Szenario:**  
Secrets (API-Keys, Passwörter) und Variables (Konfiguration) müssen sicher in Workflows verwendet werden. Mit `act` können Secrets lokal getestet werden.

### 1. Workflow-Analyse

Die Datei `.github/workflows/06-secrets-variables.yml` demonstriert:

- **`use-secrets`:** Verwendung von GitHub Secrets
- **`use-variables`:** Verwendung von GitHub Variables
- **`docker-with-secrets`:** Secrets in Docker Builds
- **`test-with-act`:** Secrets lokal mit act testen
- **`mask-values`:** Sensitive Werte maskieren

### 2. Secrets mit act nutzen

**Secrets/Variables beim Ausführen übergeben:**
```bash
act -W .github/workflows/06-secrets-variables.yml \
  -s API_KEY=my-test-key \
  -s DB_PASSWORD=my-test-password
  --var APP_ENV=production \
  --var LOG_LEVEL=info \
  -j <job>
```

**Secrets/Variables aus Datei laden:**
```bash
# .secrets Datei erstellen
echo "API_KEY=my-test-key" > .secrets
echo "DB_PASSWORD=my-test-password" >> .secrets
echo "APP_ENV=production" > .vars
echo "LOG_LEVEL=info" >> .vars

# Mit Secrets-Datei ausführen
act -W .github/workflows/06-secrets-variables.yml --secret-file .secrets --var-file .vars
```

### 3. Wichtige Konzepte

**Secrets vs Variables:**
- **Secrets** (`${{ secrets.NAME }}`): Werden maskiert in Logs, für sensitive Daten
- **Variables** (`${{ vars.NAME }}`): Sichtbar in Logs, für Konfiguration

**Maskierung:**
```yaml
- run: |
    echo "::add-mask::$SENSITIVE_VALUE"
    echo "Wert: $SENSITIVE_VALUE"  # Wird als *** angezeigt
```

**Fallback-Werte:**
```bash
# falls ACTUAL_KEY nicht gesetzt ist, wird der Wert'default' verwendet
ACTUAL_KEY="${API_KEY:-default}"
```
