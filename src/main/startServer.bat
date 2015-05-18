@echo off

rem start Ping'n'Pong server
start java -cp lib\*;PingPongServer.jar ru.glumobile.server.Main

rem start hsqldb database server
cd ../hsqldb
start java -classpath lib/hsqldb.jar org.hsqldb.server.Server --database.0 file:hsqldb/hemrajdb --dbname.0 testdb