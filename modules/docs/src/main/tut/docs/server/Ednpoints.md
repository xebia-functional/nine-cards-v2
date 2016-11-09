---
layout: docs
title: Endpoints
section: docs
---

# 9Cards Backend V2, List of Endpoints

This file gives a description of the endpoints in the API of the Nine Cards Backend.
The Nine Cards Backend is a server-side HTTP/REST application, which supports the
actions of the [Nine Cards launcher](https://github.com/47deg/nine-cards-v2),
provides it with needed information, and implements the communication across
several clients through the use of shared collections.

<!-- markdown-toc start - Don't edit this section. Run M-x markdown-toc-generate-toc again -->
**Table of Contents**

- [NineCards Backend V2, List of Endpoints](#ninecards-backend-v2-list-of-endpoints)
    - [Glossary](#glossary)
    - [Headers](#headers)
        - [Client Authentication Headers](#client-authentication-headers)
            - [Client Authentication failure](#client-authentication-failure)
            - [Google Play Token Header](#google-play-token-header)
    - [Body Entity Objects](#body-entity-objects)
        - [Application Cards](#application-cards)
        - [Shared Collection Card](#shared-collection-card)
        - [Shared Collection List](#shared-collection-list)
    - [Endpoints](#endpoints)
        - [Users and installations](#users-and-installations)
            - [User Signup](#user-signup)
            - [Update an installation](#update-an-installation)
        - [Application endpoints](#application-endpoints)
            - [Categorize apps](#categorize-apps)
            - [Get Apps Details](#get-apps-details)
            - [Rank Applications](#get-ranking-of-applications)
        - [Recommendation Endpoints](#recommendation-endpoints)
            - [Recommend by list of apps](#recommend-by-list-of-apps)
            - [Recommend by category](#recommend-by-category)
        - [Collection endpoints](#collection-endpoints)
            - [Publish a shared collection](#publish-a-shared-collection)
            - [Read a collection](#read-a-collection)
            - [Edit a collection](#edit-a-collection)
            - [Collections published by user](#collections-published-by-user)
            - [List of collections by category](#list-of-collections-by-category)
        - [Subscription endpoints](#subscription-endpoints)
            - [List of subscribed collections](#list-of-subscribed-collections)
            - [Subscribe to a shared collection](#subscribe-to-a-shared-collection)
            - [Unsubscribe from a shared collection](#unsubscribe-from-a-shared-collection)
        - [Rankings Endpoints](#rankings-endpoints)
            - [Read a Ranking](#read-a-ranking)
            - [Refresh a Ranking](#refresh-a-ranking)

<!-- markdown-toc end -->

## Glossary

The headers and data fields within the backend server refer to several concept of the
application's domain, involving the Google Play Store for Android applications.

* An **Android user** is a user account within the Android platform, which may be within
  one or more Android devices. The user account is identified with an email address.
* An **Android ID** is a globally unique ID, issued for each device running Android.
* A **client** refers to both the Android user account and the Android Id of a device
  running the [Nine Cards launcher](https://github.com/47deg/nine-cards-v2)
* A **device token** identifies a notification mailbox, unique for each client (user account and device).
  The backend uses the device token to notify a client of any updates in a public collection the client
  is has subscribed to. This is done through [Firebase notification API](https://firebase.google.com/).
  Note that a _client_ may have no _device token_ associated to it.
* A **package name** (or _package_ for short) is a unique identifier for each Android app.
  The syntax of package names is like that of Java packages. It is a dot-separated sequence of
  one or more segments, where each segment is a lowercase word.
  For instance, the [Youtube app](https://play.google.com/store/apps/details?id=com.google.android.youtube)
  has `com.google.android.youtube` as its package name.
* A **category** within the Google Play Store is a name for a group of applications that solve
  similar needs. Syntactically, a category is a list of underscore-separated upper-case words,
  such as `SOCIAL` or `GAME_ACTION`.
  Categories are not exclusive, so an app may belong to several categories.



## Headers

The Backend API requires in his request some standard or non-standard request headers.
We group these headers depending on the kind of purpose they are used for.

#### Standard Headers

All of the endpoints in this API that either require a body entity in the request
or provide one in the response use [JSON](https://tools.ietf.org/html/rfc7159) for its serialization.
Those endpoints will need the following standard content negotiation headers
 `Content-Type` (for the request) and `Accept` (for the response), with the value
 `application/json` in both headers.

#### Client Authentication Headers

These headers are used to authenticate the sender, and to identify the user account
and the Android device that corresponds to this server.

* `X-Android-ID`: should give the Android id of the client's device. Note that, since the
  process in the signup involves a user and device, not only the user but the device as well should be signed up beforehand.
* `X-Session-Token`: should carry the user's `sessionToken` that the backend generated and gave to the client
  in Step 3 of the signup process (see the README).
  This value acts as a way to identify the user within the backend.
* `X-Auth-Token`: the [Hash-based message authentication code](https://en.wikipedia.org/wiki/Hash-based_message_authentication_code),
  used for authenticating the request. It ensures that the client using the `sessionToken` is the one that is acting for that user.

You can find more details about these headers in the `README`.

##### Client Authentication failure
Most endpoints in the backend API require the client authentication headers,
to identify the sender as a client, on whose account the endpoint operates.
Those endpoints fail with a `401 Unauthorized` status code if
_a)_ any of the authentication headers is missing; or
_b)_ the `X-Session-Token` or the `X-Android-ID` correspond to no client signed up in the backend; or
_c)_ the `X-Auth-Token` does not correspond with the result of hashing the request URL
    with the api key that the backend gave that client.

#### Google Play Token Header

The Google Play API is the main source of information about Android apps,
and the backend uses it to issue request to those APIs on behalf of the user.
The header `X-Google-Play-Token` is used to pass an access token for the Google Play API,
which is issued by this API to each client.
Access to this API may sometimes fail, because the given token is unauthorized or expired.
It may also fail if the request quota for that token is exhausted.

Although the Google Play API is the main source of information, the backend uses a
cache to store any information it has already fetched, and it sometimes uses
the public [web page of play store](https://play.google.com/store/) to retrieve it.
For this reason, the endpoints generally do not inform if the `X-Google-Play-Token`
is unauthorized, or its quota expired. They do, however, return a `401 Unauthorized`
response status code if this header is missing.

Along this header, it is possible to use the optional header `X-Android-Market-Localization`.
This one is used to identify the localization, language and availability,
for which the information about each Android App should be fetched.
Values for this header are [BCP-47 codes](https://tools.ietf.org/html/bcp47), such as `en-US` or `es-ES`.

## Body Entity Objects

All of the body entities passed by the endpoints of this API, be it as requests or responses,
re encoded in [JSON](https://tools.ietf.org/html/rfc7159).
When describing the entities, we use the names of types and constants as specified in the standard,
for example _object_ means [JSON Object](https://tools.ietf.org/html/rfc7159#section-4).

### Application Cards

An application card is a set of information about an android app, that the backend fetches and gives back.
The information of an application card is provided by Google's Play store, either through its API or its web page.
The backend does not edit such information, only fetches it and stores it in a cache.
The fields in an application card object are usually some or all of these:
* `packageName`: a string with the _package name_ of the application.
* `title`: a string with a human-readable name of the app.
* `categories`: a list of strings, which are the _categories_ in which the app is listed.
  These categories are usually sorted from greater to smaller _relevance_ for the app.
* `category`: a string that gives the _first_ (thus most relevant) category the app is associated to.
* `free`: a boolean value that indicates if the app is free or not.
* `stars`: a floating-point number between `1.0` and `5.0`, with the average score given by the users of this app.
* `downloads`: a string which gives an estimate on the number of times the app has been downloaded.
    This estimate is sometimes a range, such as  `"100-1000"`, or `1000000+`.
* `icon`: a string with the URL inside the play store  of the app's icon.
* `screenshots`: a list of strings, each of which is a URL to a screenshot of the google app execution.

The fields `category` and `categories` do not appear together. Here is an example of an application card:

```json
{
  "packageName" : "one.valid.package",
  "title" : "The One Valid Package",
  "free"  : true,
  "stars" : 3.52,
  "downloads" : "1.000.000.000 - 5.000.000.000",
  "icon" : "https://lh5.ggpht.com/jZ8XCjpCQWWZ5GLhbjRA",
  "categories": [ "SOCIAL"]
}
```

### Shared Collection Card

A shared collection card is the information about a shared collection that the backend manages.
Shared collection are stored and managed by the backend, so the endpoints allow to edit
the data in them.

The fields in a shared collection card are some or all of these:

* `publicIdentifier`: a string with the public identifier of the collection.
* `publishedOn`: the time and date (in [UTC](https://en.wikipedia.org/wiki/Coordinated_Universal_Time),
  in which the collection was published. This time is written with the format `2013-11-23T05:07:13.109`.
* `author`: a human-readable alias  for the user who published the collection.
* `category`: the category in which the collection falls.
* `icon`: a string with the URL to the icon for this shared collection.
* `community`: a boolean value to indicate if this is a community collection or not.
* `packages`: a list of strings, that includes the _package names_ of every app in the collection.
* `subscriptions` (optional field): the number of Nine Cards users who have subscribed to this collection.
* `installations`: the number of devices in which the shared collection is installed.
* `views`: the number of visits to the shared collection
* `appsInfo`: a list of objects, where each object contains the information of one `apps` in the collection.
  Each of these objects contains the fields `packageName`, `title`, `free`, `icon`, `stars`, `downloads`, and `categories`,
  as described in [the previous section](#application-cards).

An example for this kind of object would be the following one:

```json
{
  "publicIdentifier" : "sociappathy",
  "publisedOn" : "2015-11-23T12:32:44.555",
  "author" : "Jon Doe",
  "name" : "Social Appathy",
  "category" : "SOCIAL",
  "icon" : "http://photo.blogs/url/of/icon",
  "community" : false,
  "installations": 11,
  "subscriptions": 12,
  "views": 13,
  "packages" : [ "one.valid.package", "non.existing.app" ],
  "appsInfo" : [
    {
       "packageName" : "one.valid.package",
       "title" : "The One Valid Package",
       "free"  : true,
       "stars" : 3.52,
       "downloads" : "1.000.000.000 - 5.000.000.000",
       "icon" : "https://lh5.ggpht.com/jZ8XCjpCQWWZ5GL",
       "categories": [ "SOCIAL"]
    }
  ]
}
```

### Shared Collection List

A shared collection list carries several shared collections, given by the backend.
As a body entity, a shared collection list is an object with a single field,
`collections`, which is a list of objects.
Each of these objects is just a [shared collection card](#shared-collection-card).

```json
{
  "collections" : [
    {
      "publicIdentifier" : "sociappathy",
      "publisedOn" : "2015-11-23T12:32:44.555",
      "author" : "",
      "name" : "Social Appathy",
      "category" : "SOCIAL",
      "icon" : "http://photo.blogs/url/of/icon",
      "community" : false,
      "subscriptions": 27564,
      "packages" : [ "one.valid.package", "non.existing.app" ],
      "appsInfo" : [
        {
           "packageName" : "one.valid.package",
           "title" : "The One Valid Package",
           "free"  : true,
           "stars" : 3.52,
           "downloads" : "1.000.000.000 - 5.000.000.000",
           "icon" : "https://lh5.ggpht.com/jZ8XCjpCQWWZ5",
           "categories": [ "SOCIAL"]
        }
      ]
    }
  ]
}
```


## Endpoints

The specification of the endpoints follows the `REST` style of interfaces as a guideline.
In the description of each endpoint's functionality, we use _"the client"_ to refer
to the sender of the HTTP request, which would usually be an instance of the Nine Cards Launcher.
The response status of most endpoints, in general, will be  a `200 OK` status code in case of success;
or a `400 BadRequest` if the request is missing a needed entity body or if this one is present but does
not fit the JSON schema outlined.

### Users and installations

These endpoints serve to manage the information about client instances
registered within the server, and the communication with them.

#### User Signup

The `POST /login` endpoint signs up a client (the sender) within the backend.
Its operation reaches out to the API of Google to check the identity of the client's user account.
The request entity body must be an object with the following fields:
* `email`: a string that contains the email address of an Android user.
  Its syntax should be that of an email address, and should always include the domain.
* `androidId`: a string with the id of the Android device in which the client instance is running.
  Syntactically, it is an uppercase hexadecimal string.
* `tokenId`: a string which contains an OAuth2 access token, issued by the Google OAuth server to the client.
  The backend only uses this token for this endpoint, to check the identity of the client's user account.
  It is not stored afterwards.

For example, a valid request body would be the following one:

```json
{
  "email"     : "jon.doe@gmail.com",
  "androidId" : "1CAFE80C",
  "tokenId"   : "k23.k1Li4iliMa_"
}
```

If successful, the body response should be an object with the following fields:
* `sessionToken`: a string containing the session token, as defined above,
  that the backend uses as an alternative identity for the client.
* `apiKey`: a string with the cryptographic key that the backend computes for the client.
  The client uses this key to compute the value of the `X-Auth-Token` header for subsequent requests.

Here is an example of a valid response:

```json
{
  "sessionToken" : "qwertyuiop90",
  "apiKey" : "azerty40"
}
```

#### Update an installation

The endpoint `PUT /installations` allows editing the _device token_  associated to the client.
This client is identified through the [client authentication headers](#client-authentication-headers),
which the request must provide.
The request body must be an object which may contain the optional field `deviceToken`.
This field's value is either a string, or `null`. Thus, each of these is a valid body:

```json
{ "deviceToken" : "tokentoken" }
{ "deviceToken" : null }
{}
```

The action of this endpoint is to modify the `deviceToken` associated to the client.
If the `deviceToken` field in the request is a string, then this value replaces the previous one.
If it is `null`, or the field is missing, then the client is left with no `deviceToken`.

If successful, the response body is an object that contains two fields:
the `androidId` of the client that issued the request,
and the `deviceToken` which that client has been assigned to.
For instance, a valid response would be the following one:

```json
{
  "androidId" : "1CAFE80C",
  "deviceToken" : "tokentoken"
}
```

The response status for this endpoint can be `200 OK`, if the request was correct and it was processed correctly;
or `401 Unauthorized` if there was a [client authentication failure](#client-authentication-failure).


### Application endpoints

These endpoints are used for querying data about android apps, irrespective of the
shared collections in which they may appear.


#### Categorize apps

The `POST /applications/categorize` endpoint takes a list of app _package names_,
classifies them by each app's main categories, and reports for which apps it could
not find the category.
The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
The request body must be an object with one field `items`, whose value is a list of strings.
Each one is a _package name_. An example would be the following one:

```json
{
  "items" : [ "com.google.android.youtube", "package.which.does.not.exists" ]
}
```

If successful, the response body should be an object with the following fields:
* `errors`: a list of strings, each of them a _package names_ from the request body.
  This list contains the names of the apps which could not be categorized.
* `items`: a list of objects, each of which corresponds to an android app that was successfully
  categorized. Each object has two fields, `packageName` and `category`, which are as described
  [above](#application-cards). The `packageName` must be one listed in the request body.

An example would be the following response:

```json
{
  "errors" : [ "package.which.does.not.exists" ],
  "items" : [
     {
       "packageName" : "com.google.android.youtube",
       "category" : "VIDEO_PLAYERS"
     }
  ]
}
```
Failure to categorize an app may be because the Google Play token is unauthorized, or its
quota exhausted. It may also be because a package name is not an app.
It may also be an app bundled by some Android vendors, which is not published in the Play Store.
These failures do not affect the response status for this endpoint.
The response status  can be `200 OK`, if the request was correct and it could be processed;
or `401 Unauthorized`, either because of [wrong client authentication](#client-authentication-failure)
or because the [Google Play Token Header](#google-play-token-header) is missing.


#### Get Apps Details

The `POST /applications/details` endpoint takes a list of app _package names_
and for each app it gives back a _card_ with the details about that app.
It also reports those apps for which it could not find details.
The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
The body should be an object with a single field `items`,
whose value is a list of strings. Each string is the _package name_ of an android app.
An example would be the following one:

```json
{
  "items" : [ "com.google.android.youtube", "package.which.does.not.exists" ]
}
```

If successful, the response body is an object with two fields:
* `errors`: a list of strings, each one being one of the _package names_
  from the `items` field in the request body. This list contains the names of the apps
  for which it could not find out the details.
* `items`: a list of objects, where each object contains some of the details of an android app.
  These objects contain the fields `packageName`, `title`, `free`, `stars`, `downloads`, `icon`,
  and `categories`, which are as described in the section for [application cards](#application-cards).
  Note that the value of `packageName` should be one of the packages listed in the request body.

Here is an example response:

```json
{
  "errors" : [ "package.which.does.not.exists" ],
  "items" : [
    {
      "packageName" : "com.google.android.youtube",
      "title" : "Youtube",
      "free"  : true,
      "stars" : 4.231,
      "downloads" : "1.000.000.000 - 5.000.000.000",
      "icon" : "https://lh5.ggpht.com/jZ8XCjpCQWWZ5GLh",
      "categories": [ "VIDEO_PLAYERS"]
    }
  ]
}
```

These failures do not affect the response status for this endpoint. This status can be
 `200 OK`, if the request was correct and it could be processed;
or  `401 Unauthorized`, either because of wrong [client auth headers](#client-authentication-failure),
or because the [Google Play Token Header](#google-play-token-header) is missing.



#### Get Ranking of Applications

The `POST /applications/rank` endpoint takes as input several lists of package names,
and returns each list with the apps sorted according to the app's popularity
amongst users of Nine Cards.

The request must include the [client authentication headers](#client-authentication-headers).
The request body must be an object with two fields:
* `location`: an optional string which gives the geographic location of the client,
  by giving the [two-letter code](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2 ISO-2) of the
  country in which the client device is located. This is used to choose a ranking with a geographic scope
  (country, continent) that better fits the client.
* `items`: an object that contains the lists of apps. Each of the fields in this object corresponds to a
  category, so the field key is the name of the category. The value of the field is the list of app
  package names that are to be ranked within that category.

Here is an example of this body entity.

```json
{
  "location" : "ES",
  "items" : {
    "SOCIAL" : [ "com.facebook", "com.twitter"],
    "VIDEO_PLAYER" : ["com.youtube", "com.vimeo"]
  }
}
```

If successful, the response body is an object with a single field `items`. The value of this field
is an object like the request, whose keys are the categories, and whose values are the lists of app
package names, sorted by ranking from most valued  to less valued by the users of Nine Cards.

```json
{
  "items" : {
    "SOCIAL" : [ "com.facebook", "com.twitter"],
    "VIDEO_PLAYER" : ["com.vimeo", "com.youtube"]
  }
}
```

### Recommendation Endpoints

These endpoints are used by the client to search for more apps that the client's user
may be interested to.
In these endpoints, the backend is not using any information of its own, about collections
or rankings. It is just acting as an intermediary and cache for these results.

#### Recommend by list of apps

The endpoint `POST /recommendations` can be used to recommend a client a list of
apps to install, based on those already installed in it.
The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
The request body must be an object with the following fields:
* `packages`: a list of strings, each of them a package name. These are the
   apps which are to be used as reference for the recommendations.
* `excludePackages`: a list of strings, each of them a package name. These are the packages
  that should not appear in the list of recommendations in the response.
* `limitPerApp`: an integer number, which says how many related packages should be
  explored for each of the apps in `packages`.
* `limit`: an integer number, that sets the maximum number of recommended elements
  in the response.

Here is an example of a request body:

```json
{
  "packages" : [ "one.package.lamp", "two.package.cat"],
  "excludePackages": [ "the.bad.package", "once.upon.time"],
  "limitPerApp" : 4,
  "limit" : 20
}
```

If this endpoint succeeds, then the response entity is an object with the only field
`items`, which is a list of objects that represent the recommended apps.
Each object has the fields `packageName`, `title`, `free`, `icon`, `stars`, `downloads`,
and `screenshots`, as described in the [app card section](#application-cards).
Here is an example of the response:

```json
{
  "items" : [
    {
      "packageName" : "one.valid.package",
      "title" : "The One Valid Package",
      "free"  : true,
      "stars" : 3.52,
      "downloads" : "1.000.000.000 - 5.000.000.000",
      "icon" : "https://lh5.ggpht.com/icon999999",
      "categories": [ "SOCIAL"]
    }
  ]
}
```

The response status for this endpoints can be
`200 OK`,  if the request was correct and it could be processed; or
or  `401 Unauthorized`, either because of wrong [client auth headers](#client-authentication-failure),
or because the [Google Play Token Header](#google-play-token-header) is missing.


#### Recommend by category

The `POST /recommendations/{category}/{priceFilter}` endpoint is used to recommend
the client a list of apps to install for a particular category.
The endpoints takes two parameters as path segments:
* `category`, the category in which to look for recommended apps; and
* `priceFilter`, which can be one of `FREE`, `PAID`, or `ALL`, which filters the
  recommendations to include only free, paid, or all apps.

The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
The request body should be an object with two fields, `excludePackages` and `limit`,
which are just like the ones described for [the previous endpoint](recommend-by-list-of-apps).
A request body example is the following one:

```json
{
  "excludePackages": [ "the.bad.package", "once.upon.time"],
  "limit" : 20
}
```

If successful, the response entity is just like that of the
[previous endpoint](#recommend-by-list-of-apps).
The response status for this endpoint can be
`200 OK`,  if the request was correct and it could be processed; or
`404 NotFound`, if either the `category` is not a valid category name
or the  `priceFilter` is other that `FREE`, `PAID`, `ALL`;
or `401 Unauthorized`, either because of wrong [client auth headers](#client-authentication-failure),
or because the [Google Play Token Header](#google-play-token-header) is missing.



### Collection endpoints



#### Publish a shared collection

The `POST /collections` serves to publish a new shared collection, whose author is the client's user.
The request must include the [client authentication headers](#client-authentication-headers).
The body entity of the request should be an object with the information of the new collection.
This object is a reduced version of a [shared collection card](#shared-collection-card),
restricted to the fields  `author`, `name`, `installations`, `views`, `category`, `icon`,
`community`, and `packages`, only. Of these, `views` and `installations` are optional fields.
Here is an example of this body request:

```json
{
  "author" : "Jon Doe",
  "name" : "Social Appathy",
  "category" : "SOCIAL",
  "icon" : "http://photo.blogs/url/of/icon",
  "community" : false,
  "installations": 11,
  "views": 13,
  "packages" : [ "one.valid.package", "non.existing.app" ]
}
```

If successful, the response body is an object with two fields:
* `publicIdentifier`: a string, with the public identifier of the new collection, as given to it by the backend.
* `packageStats`: an object with a single field `added`, which is the number of packages added to the collection.

The response status of this endpoint can be `200 OK`, if it was successful; or `401 Unauthorized`, if there is a
[client authentication failure](#client-authentication-failure); or `400 BadRequest` if the request body is malformed.


#### Read a collection

The endpoint `GET /collections/{collectionId}` gets all the information about a shared collection,
whose public identifier is passed in the `collectionId` segment.
The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).

If successful, the response body is an object, whose fields are those of a
[_shared collection_ card](#application-cards). Note that the `publicIdentifier` field
in this card should be identical to the `collectionId` path segment.
See [here for ab example of a response of this endpoint](#application-cards)

The response status can be  `200 OK` if the shared collection exists;
or  `404 NotFound`, if there is no collection with the given `collectionId`;
or `401 Unauthorized`, either because of wrong [client auth headers](#client-authentication-failure),
 or because the [Google Play Token Header](#google-play-token-header) is missing.


#### Edit a collection

The `PUT /collections/{collectionId}` endpoint serves to edit the data of the collection
whose `publicIdentifier` is given by the `collectionId` path segment.
This can only be done if the collection was published by the client's user.
The request must include the [client authentication headers](#client-authentication-headers).
The body entity should be an object with two fields:
* `collectionInfo` (optional field): an object with a single field `title`.
* `packages`: a list of strings, each of which is a _package name_, which gives the new set of
  applications contained in the collection.

Here is an example of the request body, for a call to `PUT /collections/sociappathy`,

```json
{
  "collectionInfo" : { "title" : "More Social Appathy"},
  "packages" : [ "com.twitter", "com.facebook" ]
}
```

If successful, the response body is an object with two fields:
* `publicIdentifier`: a string exactly equal to the `collectionId` segment.
* `packageStats`: an object which counts the number of changes in the collection list of applications.
  This object has up to two fields: `added`, an integer number that counts how many packages were added to the collection,
  and the optional field `removed`, another integer number that counts how many packages were removed.
Here is an example of the response body:

```json
{
  "publicIdentifier" : "sociappathy" ,
  "packageStats" : { "added" : 2, "removed" : 1 }
}
```

The response status can be
* `200 OK` if the request could be performed.
* `401 Unauthorized` if there is a [client authentication failure](#client-authentication-failure).
* `403 Forbidden` if the client is authenticated, but is not the author of the collection.
* `404 NotFound` if there is no collection whose public identifier is the `collectionId` given in a path param.


#### Collections published by user

This endpoint, `GET /collections`, gives the list of collections published by the client user.
The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
If successful, the response entity is a [shared collection list](#shared-collection-list).
The response status can be one of the following:
* `200 OK` if the request could be performed.
* `401 Unauthorized` if there is a [client authentication failure](#client-authentication-failure).

#### List of collections by category

The endpoints `GET /collections/{sort}/{category}/{pageNumber}/{pageSize}` are used to
obtain a list of shared collections within a categoru, sorted according to a criterion of interest,
and paginated. The endpoint takes as parameters the following path segments:
* The `sort` parameter indicates the order in which to sort the collections. It can be one of the following:
..* `latest`, to sort collections from most recently to less recently published.
..* `top`, to sort collections from most downloaded to less downloaded.
* The `category` parameter indicates the category in which we look for collections.
* The `pageSize` is the number of elements in each page.
* The `pageNumber` is the number of the page that is retrieved.

The request must include the [client authentication headers](#client-authentication-headers)
and the [Google Play Token Header](#google-play-token-header).
If successful, the response body is a [shared collection list](#shared-collection-list).
The response status for this endpoint can be one of the following:
* `200 OK` if the request is successful.
* `401 Unauthorized` if the client authentication headers are wrong or missing.
* `404 NotFound` if the `category` path segment corresponds to no category,
    or if the `sort` path segment is other than `latest` or `top`.

### Subscription endpoints

This endpoints are used to manage the subscription relation between the client and those collections
published by other users. All of these endpoints require from the request the
[client authentication headers](#client-authentication-headers),
and fail with a `401 Unauthorized` [if they fail](#client-authorization-failure).

#### List of subscribed collections

The `GET /collections/subscriptions` endpoint gives back a list of public collections
that the client's user is subscribed to.
The request must include the [client authentication headers](#client-authentication-headers).
If successful, the response is a list of strings, each of which is the public identifier of
one of the collections the user is subscribed to. An example of a body response would be

```json
[ "one.pack.age", "two.pack.age" ]
```

The response status can be one of the following:
* `200 OK` if the shared collection exists, in which case the user has been unsubscribed (if it was)
* `401 Unauthorized` if there is a [client authentication failure](#client-authentication-failure).

#### Subscribe to a shared collection

The `PUT /collections/subscriptions/{collectionId}` endpoint subscribes the client's user
to the public shared collection whose public identifier is given in the `collectionId` path segment.
The request must include the [client authentication headers](#client-authentication-headers).
The endpoint takes no request entity body. If it succeeds it gives in the response body an empty object `{}`.
The response status can be one of the following:
* `200 OK` if the shared collection exists, in which case the user has been unsubscribed (if it was)
* `401 Unauthorized` if any of the headers for client authentication is missing.
* `404 NotFound` if there is no collection with the given `collectionId`.

#### Unsubscribe from a shared collection

The  `DELETE /collections/subscriptions/{collectionId}` endpoint unsubscribes the sender
from the public collection whose public identifier is given by the `collectionId` path segment.
The endpoint requires  the [client authentication headers](#client-authentication-headers).
If it succeeds, it gives in the response body an empty object `{}`.
The response status can be one of the following:
* `200 OK` if the shared collection exists, in which case the user has been unsubscribed (if it was)
* `401 Unauthorized` if there is a [client authentication failure](#client-authentication-failure).
* `404 NotFound` if there is no collection with the given `collectionId`.



### Rankings Endpoints

These endpoints allow for reading and refreshing the rankings that the backend collects
from the Google Analytics API, irrespective of the apps in the client.
These are special  endpoints, in that they  are not to be used by a client, but by the
management of the backend. For this reason, they do _not_ require the
[client authentication headers](#client-authentication-headers).

In these endpoints, we use a path segment to indicate the geographic scope of the ranking.
This geographic scope can be one of the following:
* The whole world, which is represented as the path prefix `/world`.
* A continent, which is expressed as the path segments `/continents/{cont}`, where `cont`
  can be one of `Africa`, `Americas`, `Oceania`, `Europe` or `Asia`.
* A country, which is expressed as the path segments `/countries/{count}`, where `count` is the name
  of a country supported by the backend.
  At present, we only support `Spain`, `United_Kingdom`, and `United_States`.


#### Read a Ranking

The `GET /rankings/{geographic}` endpoints gives the full list of rankings for a geographic scope.
This endpoint need no special headers.
If successful, the response body entity is an an object with a single field, `categories`,
whose value is a list of objects. Each of this object is the ranking for a given category.
Its fields are:
* `category`: a string with the name of the category.
* `apps`: a list of app package names, orded by ranking (first position to last one).
Here is an example of this response:

```json
{
  "categories": [
    {
      "category" : "SOCIAL",
      "apps" : [ "com.facebook", "com.twitter" ]
    },
    {
      "category" : "GAME_ARCADE",
      "apps" : [ "old.games.doom]
    }
  ]
}
```

The response status can be `200 OK`, if it succeeds; or `404 NotFound`, if the geographic scope
does not correspond to one of those  [described above](#rankings-endpoints).

#### Refresh a Ranking
The `POST /rankings/{geographic}` endpoints serve to refresh the rankings for the given geographic scope.

This endpoint requires a special `X-Google-Analytics-Token`, whose value carries the OAuth2 token
that grants the backend server the access to the Google Analytics Report that collects the statistics
of Nine Cards users, from which the rankings are made.

The entity body in the request must be an object with the following three fields:
* `startDate`: a string that gives the start day from which we start the sample of the analytics.
  Syntactically, it has the format `"yyyy-MM-dd"`, which gives the number of the year, the number of the month,
  and the number of the day of the month, for the desired date.
* `endDate`: a string, much like the `startDate`, that gives the end day of the sample period.
  Syntactically, it is like the `startDate` field.
* `rankingLength`: an integer number which gives the number of applications that each category's ranking should have _at most_.
  These applications, of course, would be the most valued.

An example of this request would be the following one:

```json
{
  "startDate" : "2016-01-31",
  "endDate" : "2016-03-21",
  "rankingLength" : 10
}
```

If successful, the endpoint returns an empty object `{}`. If there is a failure in the
execution of the endpoint, it returns an object with these fields:
* `error`: a number with the status code of the error,
* `message`: a message with the error.
* `status`: a string describing the reason for the error.

The response status can be `200 OK`, if it succeeds, or `404 NotFound`, if the geographic scope
does not correspond to one of those  [described above](#rankings-endpoints).
The response can be `400 BadRequest` if the dates in the request are wrong,
either because any of them is a wrong date (such as `"2016-02-30"`),
or because the `endDate` precedes the `startDate`,
or because the `startDate` precedes the launch of Google Analytics.