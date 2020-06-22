# todo-app

Todo App, an example SPA based on [re-frame](https://github.com/day8/re-frame).

This app depends on [todo-api](../todo-api).

## Prerequisites

- [Java (JDK)](http://openjdk.java.net/)
    - `java -version` >= 8 (1.8.0)
- [Node.js](https://nodejs.org/en/)
    - `node --version` >= v12

## Development

### Run

```sh
$ npm install
$ npx shadow-cljs watch app
```

By default this creates a development web server at <http://localhost:8080>.

## Production

### Build

```sh
$ npx shadow-cljs release app --config-merge '{:closure-defines {todo-app.config/API_URL "http://localhost:3000"}}'
$ cp -r public/index.html public/css dist/
```

### Run

```sh
# WARNING: Python's http.server is not recommended for production
$ python -m http.server --directory dist 8080
```
