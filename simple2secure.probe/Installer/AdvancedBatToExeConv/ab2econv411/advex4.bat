@echo off
mode con:cols=80 lines=25
rem CenterSelf
set BFolder=-Select Folder-
set BFile=-Select File-
:ReDraw
rem ChangeColor 0 0
CLS
rem ChangeColor 0 7
rem ShadeBoxAt 2 19 1 40 3
rem ShadeBoxAt 3 19 20 1 3
rem ShadeBoxAt 22 20 1 39 3
rem ShadeBoxAt 3 58 19 1 3
rem ChangeColor 8 0
rem ShadeBoxAt 2 18 22 1 1
rem ShadeBoxAt 23 19 1 40 1
rem ShadeBoxAt 2 59 21 1 1
rem ShadeBoxAt 23 59 1 1 3
rem ChangeColor 7 8
rem PrintBoxAt 3 20 19 38 2
rem ChangeColor 8 7
rem ShadeBoxAt 4 21 17 36 4
rem ChangeColor 15 8
rem ShadeBoxAt 7 25 1 28 3
rem ShadeBoxAt 8 25 3 1 3
rem ChangeColor 7 8
rem ShadeBoxAt 8 26 3 27 3
rem ChangeColor 0 8
rem ShadeBoxAt 11 26 1 28 1
rem ShadeBoxAt 8 53 3 1 1
rem ChangeColor 15 8
rem ShadeBoxAt 7 53 1 1 1
rem PrintCenter BrowseFolder 9 0 7 
rem PrintCenter %BFolder% 12 1 8


rem ChangeColor 8 15
rem ShadeBoxAt 11 25 1 1 3
rem ChangeColor 15 8
rem ShadeBoxAt 14 25 1 28 3
rem ShadeBoxAt 15 25 4 1 3
rem ChangeColor 8 15
rem ShadeBoxAt 18 25 1 1 3
rem ChangeColor 7 8
rem ShadeBoxAt 15 26 3 27 3
rem ChangeColor 15 8
rem ShadeBoxAt 14 53 1 1 1
rem ChangeColor 0 8
rem ShadeBoxAt 18 26 1 28 1
rem ShadeBoxAt 15 53 3 1 1
rem ChangeColor 8 0
rem PrintCenter BrowseFiles 16 0 7
rem PrintCenter %BFile% 19 1 8
rem Locate 4 56
rem PrintColor X 1 8
rem MouseCMD 25,7,53,11 25,14,53,18 56,4,56,4
if %result%==1 goto BrowseFolder
if %result%==2 goto BrowseFiles
if %result%==3 goto Close
goto ReDraw

:BrowseFolder
rem ChangeColor 9 0
rem PrintBoxAt 7 25 5 29 2
rem Wait 200
rem BrowseFolder
set BFolder=%result%
goto ReDraw

:BrowseFiles
rem ChangeColor 9 0
rem PrintBoxAt 14 25 5 29 2
rem Wait 200
rem BrowseFiles ??? %~dps0
set BFile=%result%
goto ReDraw

:Close
rem ChangeColor 0 0
CLS




