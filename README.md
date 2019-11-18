# postgresql-notify-listener 
Provides the possibility to trace DB changes using PostgreSQL LISTEN/NOTIFY (trigger) mechanism.

## build
```sh
mvn clean install
```

## generate triggers on tables
```sh
java -jar target/postgresql-notify-listener.jar app=generate host=localhost port=5432 database=<databaseName> user=<dbUser> password=<dbPassword> pattern=t_%
```

## run listener
```sh
java -jar target/postgresql-notify-listener.jar app=listen host=localhost port=5432 database=<databaseName> user=<dbUser> password=<dbPassword>
```
