@echo off
set PGPASSWORD=ghghfrty

echo Creating tables...
psql -U postgres -d hotelAdmin -f ddl.sql

echo Inserting test data...
psql -U postgres -d hotelAdmin -f dml.sql

echo Done!
pause