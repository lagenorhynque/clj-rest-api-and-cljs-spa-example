# todo-api

Todo API, an example REST API based on [Duct](https://github.com/duct-framework/duct).

## Prerequisites

- [Java (JDK)](http://openjdk.java.net/)
    - `java -version` >= 8 (1.8.0)
- [Leiningen](https://leiningen.org/)
- [Docker](https://www.docker.com/)

## Development

### Setup

When you first clone this repository, run:

```sh
$ lein duct setup
```

This will create files for local configuration, and prep your system
for the project.

### Start and migrate database

```sh
# Start local DB
$ docker-compose up -d
# Migrate local DB
$ lein db-migrate dev
```

### Run

To begin developing, start with a REPL.

```sh
$ lein repl
```

Then load the development environment.

```clojure
user=> (dev)
:loaded
```

Run `go` to prep and initiate the system.

```clojure
dev=> (go)
:duct.server.http.jetty/starting-server {:port 3000}
:initiated
```

By default this creates a API server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```clojure
dev=> (reset)
:reloading (...)
:resumed
```

## Production

### Build

```sh
$ lein uberjar
```

### Migrate database

```sh
$ DATABASE_URL='jdbc:postgresql://localhost:5432/todo?user=dev&password=pass' java -jar target/todo-api.jar :duct/migrator
```

### Run

```sh
$ PORT=3000 DATABASE_URL='jdbc:postgresql://localhost:5432/todo?user=dev&password=pass' java -jar target/todo-api.jar
```
