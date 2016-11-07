---
layout: docs
title: Cloud Storage
section: docs
---

# Cloud Storage

We store the configuration of the user in **Google Drive**. For that, we need Google Service Permission, if the user allows store information in Google Drive, we store collections, dock apps, moments and so on in Drive

## What's the user information that we store

We store the next following information:

1. **Device and Document Info**: the name of the device, android identifier, the version of document...
2. **Collections**: All collection that the user has in his cell phone. Every collection contains the name of the collection, color, type, icon, information related with shared collection (if it's necessary) and items of collection (apps, contacts and shortcuts)
3. **Moments**: it's information related with the moment (timestamp, location, wifi, so on) when a collection and widget you want to 9cards shows in main screen

## JSON Structure in Google Drive

Names of fields and types of the structure that we store in Google Drive

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
