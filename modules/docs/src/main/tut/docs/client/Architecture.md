---
layout: docs
title: Client Architecture
section: docs
---

# Architecture

Our Activities, Fragment, and Other screen of Android’s call-to-action use Jobs. Jobs are a group of methods that contain the things that the UI can do. For example, `loadItems`, `showItem`, `markAsDone`, etc. 

The principle of Jobs is that they can connect to the UI (using UI Actions) and API, repository, or anything else (using Services).

The architecture is divided into **three layers**:

- **Services**: This module contains services for connecting to out of the applications. For example: API, Repository, Disk, so on
- **Process**: This module contains the uses cases which access the services layer to create semantic methods for the app, for example, get collections, sync device, add an application, etc.
- **Android**: This module contains the Android SDK with Activities, Fragments, and so on, used in the project. Every screen has jobs, that allows us to combine UI actions and calls to our processes.

Our architecture is based on two Typelevel libraries: Cats and Monix. These are the main libraries we use to create a functional architecture in our project. 9 Cards does not use Android libraries for views; the project only uses the Android SDK, and all views have been created in Scala.

![architecture](/img/9cards_architecture.png)

In order to be able to compose the methods of the UI and Services, all methods must return the same type. The type is define in the commons module:

```scala
type TaskService[A] = EitherT[Task, NineCardException, A]
```

Our TaskService type is a Task of **Monix** in order to async tasks and uses a EitherT of **Cats** for exceptions and value of the method.

For example, a method of our Job can have calls to both the UI and Processes:
 
```scala
  def loadWidgets(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      widgets <- di.deviceProcess.getWidgets
      _ <- actions.loadWidgets(widgets)
    } yield ()
```

We can do that in the activity:

```scala
widgetsDialogJobs.loadWidgets().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())
```
   
We can also compose the methods of different jobs using _Applicative_ in **Cats**: 
 
```scala
import cats.implicits._

(widgetsJobs.hostWidget(widget) *> widgetsDialogJobs.close()).resolveAsync()
``` 