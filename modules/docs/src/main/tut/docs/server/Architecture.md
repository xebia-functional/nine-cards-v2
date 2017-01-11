---
layout: docs
title: Server Architecture
section: docs
---

# Architecture

This file describes the architecture of the 9 Cards Backend.

## Overview

In this section, we will describe the environment of the application, and how it interacts with this environment.

### API

The 9 Cards backend is a HTTP/REST application. Every operation is initiated by a call to an endpoint in the API. The backend has no other internal state changing operations.
Most of the endpoints in the API are assumed to receive messages from a 9 Cards client.
All the information sent from the backend to any client is sent in response to a client's HTTP request.
The backend does not communicate directly with 9 Cards' clients. For notifications, it uses
the [Firebase Cloud Messaging](http://firebase.google.com/docs/cloud-messaging/) API.


### External Systems

The backend application interacts with several **external** systems and APIs outside of the application, for retrieving or storing information, or for sending notifications. The external systems are the following:

* **Database**. A [PostgreSQL](http://www.postgresql.org/) database is used to store information that's needed by the backend.
  This includes information about 9 Cards' clients (users and devices), shared collections, the user's subscriptions of shared collections, and the information about countries.
* **Google API**. The Google API is used upon a client's signup, to check the identity of a new client.
* **Google Analytics**. A Google Analytics report collects aggregated statistics about the applications used in 9 Cards, such as which applications are more frequently added or removed to a _moment_, or which applications are most frequently used within each category. This data doesn't contain any information about the user with the exception of the country, as given by Google Analytics.
  From this data, the backend builds the _rankings_ of applications.
* **Android Market**. The API of the Android Market is used to retrieve information about the Android applications in the Play store, and also for searching lists of new applications either by name, by their category, or by their similarity to apps that are already installed.
* **Play Store Web**. Sometimes, the information about an app is not available in the Market API; instead, we retrieve it from each app's page in the [Play Store website](http://play.google.com/store/).
* **Redis**. We use a [Redis](http://redis.io/) in-memory store as a _cache_ to keep non-original data; that is to say, data that is retrieved from a remote source, or computed from a remote source. This includes the data about each application fetched from the Android Market API, and the most-valued application rankings computed from the Google Analytics Report.
* **Firebase**. The [Firebase Cloud Messaging](http://firebase.google.com/docs/cloud-messaging/) API is used to notify the subscribers of any changes to the list of apps in a shared collection.

### Core Modules

The backend source code is organized into four modules, or `sbt` projects, called `api`, `processes`, `services`, `googleplay`, and `commons`.

* The [`commons`](/modules/commons) module contains the definition of the backend's `domain` classes,
  and utility methods for common libraries, such as `cats` or `scalaz`. It also handles the environment configuration variables needed for each service interpreter.
* The [`api`](/modules/api) module implements the `REST-HTTP` application's API, and the application's
  main method. It uses the libraries `spray`, and `spray-json` (with a bit of `circe`).
* The [`processes`](/modules/processes) module contains the application's process methods. Each process method implements the functionality of one endpoint, using one or more services. The implementation follows a functional programming style, based on the use of monadic operations.
* The [`services`](/modules/services) module implements the backend's services; each services retrieves and
  communicates data with an external system. The services layer is based on the `doobie` and `http4s` libraries.
* The [`googleplay`](/modules/googleplay) implements a subset of services, originally developed as a separate
  module that interacts with the Android Market API, and which uses the Redis cache for internal storage.

The `commons` module is available to all other modules. The dependencies among other modules are as follows:
the `api` only depends on `processes`; the `processes` only depends on `services`, and `services` depends
on `googleplay`.

## Functional Programming Design

This section describes the Functional Programming design followed in writing the processes and services for the backend. This design is inspired by the [Data Types à la carte](http://dblp.org/rec/html/journals/jfp/Swierstra08) paper. The key elements of this design are _1)_ a separation between a service's algebra of operations and
its interpreter, and _2)_ the use of the `Free` monad for combining operations of different algebras.

### Services: Algebra and Interpreter

Each service in the `services` layer declares an algebra of operations, and an interpreter of those operations.

The **algebra** defines an abstract data type `Ops[A]`, which represents the operations that can be done in that service.
Each operation can be seen as a [command](http://en.wikipedia.org/wiki/Command_pattern) object,
which carries the input parameters needed to specify the value of the result.
As a guideline, each operation in an algebra should be atomic and involve at the most, one interaction
with the external system in question.

The **interpreter** is the part of the service that takes the operations of the algebra and computes the results of that operation.
An `Interpreter` is a (class of) object that implements a function from the service's algebra ADT
`Ops[A]` to another parametric type, `F[A]`, with the same base type `A`.
Operations that transform from one parametric type `F[A]` to another parametric type `G[A]` are called _natural transformations_;
and in the backend, every service interpreters must be a subtype of [`cats.arrow.FunctionK`](http://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/arrow/FunctionK.scala).

The target type `F` of the interpretation represents the kind of computation involved to obtain that result.
It can be [`Task`](http://github.com/scalaz/scalaz/blob/series/7.3.x/concurrent/src/main/scala/scalaz/concurrent/Task.scala),
to represent an asynchronous computation of the result,
or [`ConnectionIO`](http://github.com/tpolecat/doobie/blob/master/core/src/main/scala/doobie/free/connection.scala),
to represent a computation that performs queries with `doobie`,
or just `Id`, to represent a result obtained just within the application (for testing).

#### Processes: Free Monad and Coproducts

Each process in the processes layer implements the functionality of one endpoint, and it usually involves
using different services.
For instance, the process to retrieve the details of a shared collection involves reading the collection information
out of the database and then retrieving from the Android Market API (or from the cache) the data of each
application in that collection. However, each service defines its operation in a separate algebra,
such as `OpsDB[A]` or `OpsRK[B]`.

To combine operations from several services, each operation of each algebra is _injected_ into a `Free` monad
`Free[F,A]`. If the `F` type parameter is the same, then the
[`Free`](http://github.com/typelevel/cats/blob/master/free/src/main/scala/cats/free/Free.scala) datatype provides
the operations for passing the results between the operations from different algebras.

This is achieved by using as that `F`, a `Coproduct` of the different algebras operation types `Ops[A]`.
The interpretation of the `Coproduct` operations, then, becomes an alternative between them.