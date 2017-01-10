---
layout: docs
title: Cloud Storage
section: docs
---

# Cloud Storage

We store a user's configurations in their **Google Drive**. In order to do this, we need to have Google Service Permission. If the user agrees, we store their collections, dock apps, moments, and other specifications in their Drive.

## What user information do we store?

We store the following information:

1. **Device and Document Info**: the name of the device, Android identifier, and the version of the document.
2. **Collections**: All collections that the user has on their cell phone. Every collection contains the name of the collection, color, type, icon, information related to a shared collection (if it's necessary), and the items of each collection (apps, contacts, and shortcuts).
3. **Moments**: Information related to Moments (timestamp, location, wifi, etc.) when a collection and associated widgets of 9 Cards shows on the home screen.

## JSON Structure in Google Drive

Names of the fields and types of the structure that we store in Google Drive:

### CloudStorageDevice

```
{
  "deviceId": String,
  "deviceName": String,
  "documentVersion": Int,
  "collections": Seq[CloudStorageCollection],
  "moments": Seq[CloudStorageMoment],
  "dockApps": Seq[String] // List of package names of applicactions
}
```

### CloudStorageCollection

```
{
  "name": String,
  "icon": String,
  "category": Option[String],
  "collectionType": String,
  "originalSharedCollectionId": Option[String],
  "sharedCollectionId": Option[String],
  "sharedCollectionSubscribed": Option[Boolean],
  "items": Seq[CloudStorageCollectionItem],
  "moment": Option[CloudStorageMoment]
}
```

### CloudStorageCollectionItem

```
{
  "title": String,
  "itemType": String,
  "intent": String
}
```

### CloudStorageMoment

```
{
  "timeslot": Seq[CloudStorageMomentTimeSlot],
  "wifi": Seq[String],
  "headphones": Boolean
}
```

### CloudStorageMomentTimeSlot

```
{
  "from": String,
  "to": String,
  "days": Seq[Int]
}
```
