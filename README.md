# SimpleTP Permissions — experimenteller Quellcode

**NICHT PRODUKTIONSREIF / NICHT GETESTET.** Dies ist ein GitHub-Actions-Buildversuch für Minecraft 26.3, Fabric Loader 0.20.1 und Fabric API 0.165.0+26.3.

## Ziel
- `/tp <Spieler>` und `/tp <x> <y> <z>` sollen ausschließlich den ausführenden Spieler bewegen.
- Auch `/teleport` wird registriert.
- Serverseitige Installation; keine zusätzlichen OP-Rechte beabsichtigt.

## Wichtige Einschränkungen
- **Kein erfolgreicher Build nachgewiesen.** Yarn-, Loom- und Minecraft-Versionen sowie Java-API-Signaturen sind unbestätigt. Der Build kann fehlschlagen.
- **Die Sicherheitsgrenze ist nicht nachgewiesen.** Vanilla-Command-Nodes können beim Registrieren zusammengeführt werden; insbesondere darf man nicht annehmen, dass die Befehle ohne OP wirklich sichtbar sind oder dass keine unerwünschten Varianten verfügbar werden. NICHT auf einem produktiven Server installieren, bevor das explizit getestet wurde.
- Relative Koordinaten (`~`) und lokale Koordinaten (`^`) werden nicht unterstützt. Xaero's Minimap ist nicht getestet.
- Dimensionen: `/tp <Spieler>` ist für Zielspieler in anderen Dimensionen gedacht; freie Koordinaten gelten in der eigenen Dimension.
- Diese ZIP ist **Quellcode**, keine installierbare JAR.

## GitHub
1. ZIP entpacken.
2. Den **Inhalt** des Ordners in ein neues GitHub-Repository hochladen, einschließlich `.github/workflows/build.yml`.
3. Unter Actions → Build Fabric Mod → Run workflow den Build starten.
4. Nur bei erfolgreichem Build erscheint unter Artifacts ein Download. In dem Archiv ggf. die JAR ohne `-sources` wählen.
5. Vor jeglicher Installation: auf einem separaten Testserver prüfen, dass Nicht-OPs die gewünschten Befehle nutzen können und keinerlei andere Spieler oder Admin-Aktionen kontrollieren können.

Falls GitHub Actions fehlschlägt, das Fehlerprotokoll aus dem Build hier teilen. 
