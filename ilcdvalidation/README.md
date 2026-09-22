# ILCD Validation API

## Using via Apache Maven

The ILCD Validation library is on Maven Central. Simply add it as a dependency using

```
<dependency>
	<groupId>com.okworx.ilcd.validation</groupId>
	<artifactId>ilcd-validation</artifactId>
	<version>3.0.0</version>
</dependency>
```
.

The profile `ILCD-1.1` (default profile) is always available by default (unless explicitly disabled).

If relying on the secondary default profiles

- `ILCD-1.1-EL`
- `EF-1.0`
- `EF-2.0`
- `EF-3.0`

for example when initializing the `ProfileManager` with `registerDefaultProfiles(true, true)`, 
they have to be included explicitly with:

```
<dependency>
	<groupId>com.okworx.ilcd.validation.profiles</groupId>
	<artifactId>EF-1.0</artifactId>
	<version>1.0.11</version>
</dependency>
<dependency>
	<groupId>com.okworx.ilcd.validation.profiles</groupId>
	<artifactId>EF-2.0</artifactId>
	<version>2.0.9</version>
</dependency>
<dependency>
	<groupId>com.okworx.ilcd.validation.profiles</groupId>
	<artifactId>EF-3.0</artifactId>
	<version>3.1.0</version>
</dependency>
<dependency>
	<groupId>com.okworx.ilcd.validation.profiles</groupId>
	<artifactId>ILCD-1.1-EL</artifactId>
	<version>1.8.3</version>
</dependency>
```

## Documentation

See the document [Validating ILCD Data](./doc/Validating_ILCD_Data.md) for an introduction to the concepts and usage of the library.


## Example for Using the ILCD Validation API

You can find a working [example](./src/example/java/com/okworx/ilcd/validation/example/ValidationExample.java) that demonstrates how to use the library.


## Branches and release lines

Two release lines are maintained:

| Line | Branch | Requires | Status |
| --- | --- | --- | --- |
| 3.x | `develop` | Java 11 | active development |
| 2.x | `release/2.12.0` | Java 8 | maintenance (bugfixes only) |

`master` is **retired**. It carries the abandoned 2.13.0-M line: the `2.13.0-M1`,
`2.13.0-M2` and `2.13.0-M3` milestones published from it were never production-grade,
as they contain an unfinished removal of TrueVFS. Do not branch from `master` and do
not release from it. It is kept only so those milestone tags remain annotated in
context; the next release on the 2.x line will be `2.14.0`, skipping `2.13.x`
entirely.

## Building from source
In order to build this project from source you need to have the following tools
installed:

* A [Java Development Kit >= 11](https://adoptium.net/) (>= 8 for the 2.x line)
* [Apache Maven](https://maven.apache.org/)
* [Apache Ant](http://ant.apache.org/) 

It is a standard Maven project. When you have Maven installed, you should be able
to build the project with the following steps:

Get the source via Git (or simply download it) and switch to the project folder:

```bash
git clone https://bitbucket.org/okusche/ilcdvalidation.git
cd ilcdvalidation
```

Build the library and install it into your local Maven repository:

```bash
mvn clean install
```

** From Eclipse, due to https://github.com/apache/maven-dependency-plugin/issues/1180 this does not work, 
a mvn clean install is necessary from the command line to prepare all dependencies.**


## Using the Reference List Creator

After successful build, you can execute ReferenceListCreator with maven:

```
mvn exec:java -Dexec.args="[SourceFile] [DestinationPath] [ReferenceFlowDest] [ReferenceObjectsFilename]"
``` 

## License

Copyright 2015-2025 Oliver Kusche

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.