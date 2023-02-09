# README

[![Java CI with Maven](https://github.com/SnapGames/minimal/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/SnapGames/minimal/actions/workflows/maven.yml)

This is a small Minimalistic Game project , based on a master Game Java class, with some subclasses to let keep the code
ass short as possible.

## Compile

Based on standard Maven tooling, a simple command line will provide a JAR:

```shell
$ mvn clean install
```

## Run

To run the produced artifact, multiple solution :

1. Execute from maven command

```shell
$ mvn exec:java
```

2. execute the produced jar from the target directory:

```shell
$ java -jar target/game-0.0.1-SNAPSHOT-shaded.jar
```

## Demo

After starting the latest jar file, the following picture may appear:

![Latest enhanced minimal game framework](docs/docs/illustrations/figure-screenshot-0.0.3.png "Latest enhanced minimal game framework")

_figure 1 - Latest enhanced minimal game framework_

You can interact with the demo with the following keys :

| Key                              | Description                                                   |
|----------------------------------|---------------------------------------------------------------|
| <kbd>ESCAPE</kbd>                | Exit from demo                                                |
| <kbd>D</kbd>                     | Switch debug display mode from level 0 to 4 (O=off)           |
| <kbd>UP</kbd>                    | move character up                                             |
| <kbd>DOWN</kbd>                  | move character down                                           |
| <kbd>LEFT</kbd>                  | move character left                                           |
| <kbd>RIGHT</kbd>                 | move character right                                          |
| <kbd>PAGE_UP</kbd>               | Add more coins                                                |
| <kbd>PAGE_DOWN</kbd>             | remove some coins                                             |
| <kbd>DELETE</kbd>                | remove all coins                                              |
| <kbd>P</kbd> or <kbd>PAUSE</kbd> | Pause the update physic engine phase                          |
| <kbd>R</kbd>                     | Switch rain (demonstrate particles effects)                   |
| <kbd>B</kbd>                     | Switch background image/stars (show background possibilities) |
| <kbd>G</kbd>                     | Switch gravity (show the PhysicEngine World context effect)   |


## Contribute

if you want to contribute to the experiment, just contact me via github 

