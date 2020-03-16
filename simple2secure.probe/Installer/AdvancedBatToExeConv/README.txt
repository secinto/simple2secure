1. Im ab2econv411-Ordner ist eine aB2Econv.exe.
	1.1 Doppelklick um die GUI zu starten.
	1.2 batchScript_for_exe.bat im Programm öffnen
	1.3 Auf File -> Build EXE... gehen, es wird ein weiteres Fenster aufgemacht wo man die Pfade für die eingebetteten Dateien VersionsInfo usw. eingeben kann
	1.4 Die Pfade für die eingebetteten Dateien müssen adaptiert werden.
	1.5 Dateien die eigebettet sind:
		* buildRelease.bat
		* deleteService.bat
		* installService.bat
		* Probe Service.exe
		* prunmgr.exe
		* prunsrv.exe
		* startService.bat
		* stopService.bat
		* simple2secure.probe.jar
		* simple2secure.service.jar
	(Der Pfad fürs Icon muss auch adaptiert werden.)
	1.6 Schlussendlich auf Build EXE-Button klicken.
	
	
	
2. Um aus der Powershell die exe zu erstellen sollte man zum ersten mal mit der GUI des Programms ein Batchfile reinladen und
bearbeiten wie oben beschrieben, denn dann wird im Batchfile ein Header hinzugefügt für den Advanced Bat To Exe Converter.
(Da das BatchFile in diesem Ordner schon bearbeitet worden ist, ist der header im script schon vorhanden und man kann die Pfade
direkt im BatchFile adaptieren.)
	2.1 dann einfach in der Powershell in den ordner ab2econv411 wechseln und "aB2Econv.exe pfadZumBatchfile pfadOutputExe" in die Powershell schreiben, am besten die Pfade in Hochkomma wegen Leerzeichen in Pfaden usw.
		- Example:    aB2Econv.exe "c:\Batch Files\input.BAT" "c:\Batch Files\output.EXE"
	