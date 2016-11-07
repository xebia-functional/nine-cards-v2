---
layout: docs
title: Architecture
section: docs
---

# Architecture

Our Activities, Fragment and other screen of Android call to action using Jobs. Jobs are a group of methods that contain the things that the UI can do. For example: loadItems, showItem, markAsDone, etc

The principles of the Jobs is that they can connect to the UI (using Ui Actions) and api, repository or whatever (using Services)

The architecture is divided in **3 layers**:

- **Services**: This module contains services for connecting to out of the applications. For example: API, Repository, Disk, so on
- **Process**: This module contains the uses cases. We have connect to every service in the previous layer in order to create your process
- **Android**: This module contains the Android SDK with Activities, Fragments and so on, used in your project. Every screen have jobs, with the actions in your UI and Ui Actions

![architecture](/nine-cards-v2/img/9cards_architecture.png)

In order to can compose the methods of the Ui and Services, all methods must return the same type. The type is define in commons module and it's the next:

```scala
type TaskService[A] = EitherT[Task, NineCardException, A]
```

Our TaskService type is a Task of **Monix** in other to can do async tasks and using a EitherT of **Cats** for exceptions and value of the method

For example, a method of our Job can have calls to Ui and Processes:
 
```scala
  def loadWidgets(): TaskService[Unit] =
    for {
      _ <- actions.showLoading()
      widgets <- di.deviceProcess.getWidgets
      _ <- actions.loadWidgets(widgets)
    } yield ()
```

In the activity we can do that:

```scala
widgetsDialogJobs.loadWidgets().resolveAsyncServiceOr(_ => showErrorLoadingWidgetsInScreen())
```
   
We also can compose the methods of different jobs using _Applicative_ in **Cats** 
 
```scala
import cats.implicits._

(widgetsJobs.hostWidget(widget) *> widgetsDialogJobs.close()).resolveAsync()
``` 