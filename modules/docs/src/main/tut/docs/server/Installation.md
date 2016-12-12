---
layout: docs
title: Installation
section: docs
---

## Installation

In this section, we describe the steps you have to take to setup your computer for developing and
running the Nine-Cards-Back-End (NCBE) application.

### Scala Development Tools

The NCBE is written in [Scala](www.scala-lang.org), using the [Scala Build Tool (SBT)](http://www.scala-sbt.org/) for automatic build.

*   Java SE 8 Development Kit: you can use [OpenJDK](http://openjdk.java.net/projects/jdk8/).
    In Ubuntu/Debian, if you have several versions of the JDK installed, you may need to use the `update-java-alternatives` program.
*   [Scala 2.11.8](http://www.scala-lang.org/download/2.11.8.html).
*   [SBT version 0.13.8](http://www.scala-sbt.org/download.html) or later.

If you have an older version of SBT, or Scala, often `sbt` can bootstrap itself to a newer versions.

You should ensure that the `PATH` environment variable contains the directories in which the programs `scala`, `scalac`, and `sbt` are located.

### Postgres Database Setup

The NCBE stores its information in a database, using the [[postgresql]] database system.
It uses a database called `ninecards`. To write, run, and test NCBE in your machine, you can create
a user `ninecards_user`, with password `ninecards_pass`.

#### Installation

In a Debian-based Linux distribution, you can use the `apt-get` command to install the packages

    sudo apt-get install postgresql postgresql-contrib postgresql-client pgadmin3

For OS-X users, you can use any of the tools mentioned [here](http://www.postgresql.org/download/macosx/).

#### Setting Client authentication

In PostgreSQL, the "Client Authentication" method, used for opening a client's session, can be set differently for each user, database, or connection.
This configuration is kept in a file called [`pg_hba.conf`](http://www.postgresql.org/docs/9.1/static/auth-pg-hba-conf.html).

* In Debian-based distributions, it is located in the directory `/etc/postgresql/{version}/main/pg_hba.conf`.
* In OS-X, you can find it using the command `locate pg_hba.conf`, or following [these instructions](http://stackoverflow.com/questions/14025972/postgresql-how-to-find-pg-hba-conf-file-using-mac-os-x).

To run and test the NCBE on our local host using the `ninecards_user` user, we need to open channels for the command line and for the JDBC driver.

* The JDBC used by the NCBE enters the database trough a local IPv4 connection. To allow it, the following line should be in `pg_hba.conf`:

        host    ninecards       all  127.0.0.1/32            md5

* For setting up the database for tests, we want to enter the database from a shell terminal, using  the command `psql`
as the `ninecards_user`. To allow this, you should have the following line in `pg_hba.conf`:

        local   ninecards       ninecards_user                          md5

* You need to restart the Postgres server for the changes to take effect. To do this, run the following command in a terminal:

    	sudo service postgresql restart

#### Setting user and password for local development:

To create the `ninecards` database and the `ninecards_user` we need to open a session as the PostgreSQL-server administrator.
The administrator is the DBMS user called `postgres`, and by default it is configured to use  `peer` authentication.
Under this method, you can only open a DBMS session from a OS user with the same name.
Thus, you need to follow these steps:

1. Start `psql`, the PostgreSQL command-line client, as the `postgres` OS user:

    	sudo -u postgres psql

2. Inside `psql`, create the database, the user, the permissions, and exit.

        create database ninecards ;
        create user ninecards_user PASSWORD 'ninecards_pass';
        GRANT ALL ON DATABASE ninecards TO ninecards_user;
    	\q

3. From your own OS user, you should now be able to open a postgres-client session using the following command:

        psql --username=ninecards_user ninecards --password

#### Database Schema Migrations

The evolutions for the data schema of the `ninecards` database are managed by `sbt`, the build system, using the [flyway SBT plugin](https://flywaydb.org/documentation/sbt/).
Flyway needs some configuration parameters to access the database.
An overview on how to pass these settings is given in the [Database Connection Configuration](#database-connection-configuration) section.
Suffice it to say that, to run the migrations on your local database, you can use the configuration values written in the [`localhost.conf`](modules/api/src/main/resources/localhost.conf) file.
You can pass this file to `sbt`, by opening a shell session in the `nine-cards-backend` root directory and executing the following command:

        sbt -Dconfig.file="modules/api/src/main/resources/localhost.conf"

This should open an interactive `sbt` session. Inside this session,
you can clear the database with the command `flywayClean`, or perform the database migrations with `flywayMigrate`.

**Note**: since `flyway` connects to the database through JDBC, you would need to configure the PostgreSQL authentication file `pg_hba.conf`, as explained [in a previous section](#setting-client-authentication)).


## Running and testing the application

From a command line, within the root directory of the project, run the following:

    $ sbt -Dconfig.file="modules/api/src/main/resources/localhost.conf"
    > project api
    > run

To check that the application has started correctly, you can try accessing the Swagger apidocs in the
`http://localhost:8080/apiDocs` URL.

### Database Connection Configuration

The configuration is managed using Lightbend's [configuration library](https://github.com/typesafehub/config).
The default configuration is at the `modules/api/src/main/resources/application.conf` file, which
loads the values for some configuration settings from the environment. This gives you several ways to
define your configuration settings:

a. Run `sbt` passing the configuration settings, each setting having the form `-D{key}}={{value}}`.
   For example, to run the application in your local host, you would pass the databae configuration as follows:

        sbt -Ddb.default.driver="org.postgresql.Driver" -Ddb.default.url="jdbc:postgresql://localhost/ninecards" -Ddb.default.user="ninecards" -Ddb.default.password="ninecards_pass"

b. Write a configuration file with your settings, and pass that file to `sbt` using the `-Dconfig.file` option.
    For example, to run the application in yout local host, you can pass the [`localhost.conf` file](modules/api/src/main/resources/localhost.conf), as follows:

        sbt -Dconfig.file="modules/api/src/main/resources/localhost.conf"

c. Set the shell environment variables used by the default configuration file.
    In `bash`, this is done with the command `export VAR=[VALUE]`, without spaces.
    For instance, to initialize the environment variables related to the database configuration, and set them for local execution, you would run the following:

        export DB_DEFAULT_DRIVER="org.postgresql.Driver"
        export DB_DEFAULT_URL="jdbc:postgresql://localhost/ninecards"
        export DB_DEFAULT_USER="ninecards"
        export DB_DEFAULT_PASSWORD="ninecards_pass"

    Note that there should be no whitespace around the `=` sign. Note also that the settings only remain for the bash session.
    You can write such settings in the `.bashrc` file, or in a executable shell script.

### Testing and running the endpoints with Postman

Once the application is running and bound to the chosen port, you can run the endpoints by issuing HTTP
requests with any HTTP client, like [`curl`](https://en.wikipedia.org/wiki/CURL).
In particular, we use the [Postman](https://www.getpostman.com/) graphic client.
Postman allows us to write a collection of HTTP requests and store it as a text file.
These requests can depend on variables read from an environment that is also stored as a text file.

To test the endpoints of the application, we provide a [collection](assets/postman/collection.json) of Postman requests,
as well as an [environment](assets/postman/environment.json) for those requests.



## Deployment - Preparing the Application

The NCBE is a server side application, and it should be deployed as a
[Infrastracture as a Service (IaaS)](https://en.wikipedia.org/wiki/Cloud_computing#Infrastructure_as_a_service_.28IaaS.29)
or [Platform as a Service (PaaS)](https://en.wikipedia.org/wiki/Platform_as_a_service).
To do this, we need to pack the application's source code, the binary classes, its transitive dependencies,
and the configuration values into a self-contained executable file (or *fat-JAR*).
This is done with the [`sbt-assembly` plugin](https://github.com/sbt/sbt-assembly).
This plugin was originally ported from codahale's assembly-sbt, and may have been inspired by Maven's assembly plugin.
Its goal is to build a fat JAR of your project with all of its dependencies.

To execute the plugin, you should open a shell session at the project's root directory and run the following command:

    $ sbt "project api" assembly

Note that you should provide the database configuration variables to the `sbt` command, using any of the methods described above.
Otherwise, the `sbt` fails due to the `flyway` plugin.
By default, the fat jar will be created in the `{appPath}/modules/api/target/scala-2.11/` folder.

### Running SQL evolutions in Heroku

This task should be done manually in this way:

    $ heroku pg:psql --app nine-cards < /path/to/file.sql