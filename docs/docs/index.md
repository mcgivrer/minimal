# Minimal Java Project

This minimal project intends to set keys to sytart with a good desktop GameDev oriented project.

BAsed on A ONE big Game class, containing all the required components and some services, the project will help you
structure your project.

The first step will consist in exploding this class into multiple ones, with the packaging you want.

This documentation writen using the markdown markup language will tak ebenefits of the MkDocs toools to generate a full
project site with everything inside.

## The java Project

The Java project tself is build arounfd the famous maven build tools with all the dependencies and plugin in place to
tests, release, package jar and create java doc.

```plaintext
minimal
|_ docs/
|_ src
|  |_ main
|  |  |_ java
|  |  |_ resources
|  |_ test
|     |_ java
|     |_ resources
|_ pom.xml
|_ README.md
|_ CODE_OF_CONDUCT.md
|_ LICENSE
|_ .sdkmanrc
|_ .gitpod.yml
|_ .gitpod.Dockerfile
|_ .github/
```

## Welcome to MkDocs

For full documentation visit [mkdocs.org](https://www.mkdocs.org).

### Commands

- `mkdocs new [dir-name]` - Create a new project.
- `mkdocs serve` - Start the live-reloading docs server.
- `mkdocs build` - Build the documentation site.
- `mkdocs -h` - Print help message and exit.

### Documentation layout

mkdocs.yml # The configuration file.
docs/
index.md # The documentation homepage.
... # Other markdown pages, images and other files.
