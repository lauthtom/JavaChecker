## Java Checker
Java Checker ist ein Open-Source Programm, welches für die Korrektur von Übungsblättern benutzt wird.

## Benutzung
Als erstes sollte man sich das Repository in einen beliebigen Pfad klonen mit
`git clone https://github.com/lauthtom/JavaChecker.git`

Danach sollte man sicher gehen, dass man ist im richtigen Verzeichnis wo auch der Ordner **JavaChecker** ist. 

Bevor allerdings der Checker gestartet werden kann, müssen zuerst Benutzerspezifische Änderungen im Code vorgenommen werden. 

`private  static  final  String  desktopPath = "/Users/BenutzerXY/Desktop/";`
`private  static  final  String  zipFile = "Uebungsblatt_5.zip";`
`private  static  final  String  correctionPath = desktopPath + "Korrektur/"`
`private  static  final  int  COUNT_OF_TASKS = 5;`

Wie oben zu sehen ist gibt es 4 wichtige Datenelemente die vom Benutzer aus spezifisch immer angepasst werden müssen. Die Variable `desktopPath` benötigt einen gültigen Pfad zum Desktop. Die Variable `zipFile` fordert den Namen der .zip Datei. Außerdem braucht die Variable `correctionPath` einen gültigen Pfad zum Korrektur Ordner (Der Ordner muss zu der Zeit noch nicht existieren). Zuletzt verlangt die Variable `COUNT_OF_TASKS` eine Anzahl der Aufgaben die im aktuellen Aufgabenblatt vorhanden sind.

Wenn diese dann angepasst wurden steht dem Start nichts mehr im Wege. Den Checker kann man nun von der Kommandozeile mit folgendem Befehl `javac Checker.java && java Checker` aus starten. 

Wenn nun der Checker zu Ende gelaufen ist, dann findet man den Korrektur Ordner in dem oben angegeben Pfad. Bevor wir zu den einzelnen Gruppen gehen ist eine Sache noch zu erwähnen. Im ***Korrektur*** Ordner gibt es noch eine eine ***Check.txt*** Datei, da drinnen sind die einzelnen Gruppen mit den Mitgliedern enthalten. Jetzt aber zu den klein Gruppen. Der Ordner enthält jeweils die verschiedenen Gruppen der Veranstaltung. Jede Gruppe besitzt zwei Ordner ***1_task*** und ***2_submissions*** . Den ersten Ordner kann man ignorieren. Der zweite interessiert uns hier. In dem Unterordner findet man einen ***Result*** Ordner vor. In diesem Ordner  werden einem alle Dateien aufgelistet die von der jeweiligen Gruppe abgegeben wurden.

## Features
-	Es kann eine .zip Datei (mit mehreren .zip Dateien als Inhalt) entpackt werden. 
-	Das Programm geht rekursiv durch alle Gruppen plus deren Unterordner und sucht nach der Abgabe der jeweiligen Gruppen (die Abgabe ist zu dem Zeitpunkt noch eine .zip  Datei) Wenn eine .zip Datei vorhanden ist, wird diese in einen ***Result*** Ordner entpackt. 
-	In dem ***Result*** Ordner liegen danach alle Dateien (ohne Unterordner etc.)
-	Es wird zudem eine ***Check.txt*** Datei erstellt, welche jede Gruppe mit deren Mitglieder enthält. 
-	Am Anfang kann außerdem noch die Anzahl der Aufgaben im Programm festgelegt werden. 
Damit wird eine Vorlage für die Korrektur der jeweiligen Gruppen erstellt. 