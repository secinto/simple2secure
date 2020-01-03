call ..\gradlew -p ..\..\..\simple2secure simple2secure.probe:build -x test
call ..\gradlew -p ..\..\..\simple2secure simple2secure.probe:copyJar
call ..\gradlew -p ..\..\..\simple2secure simple2secure.service:build -x test
call ..\gradlew -p ..\..\..\simple2secure simple2secure.service:copyJar
