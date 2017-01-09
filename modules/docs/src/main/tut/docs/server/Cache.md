---
layout: docs
title: Cache
section: docs
---

# Cache

In an effort to reduce the number of requests to Google Play API, we store the Google Play info on the app in an intermediate cache using REDIS. 

##Redis cache management

Redis cache contains only one key per package and can store four types of values depending on the results of getting information from Google Play:
- `resolved`: Those packages for which the process of getting Google Play info has been successfully completed will have this tag.
- `pending`: If while the execution of a multi-package request, the Google Play API returns an error (`401 Unauthorized` or `429 Too many requests`), all the packages without info will be tagged as pending. An internal process will try to get the info later.
- `error`: If the request to get the Google Play information returns a `404 Not Found` error, the package will be tagged as error.
- `permanent`: All the packages with this tag are known applications that are not published in Google Play, but we can categorize them (for instance, the Samsung camera).

The format of the key and value will be:
- **Key:** `"com.package.name:TYPE"`
- **Value:**
  - Example of a `resolved` item:
    
```
{
    "packageName": "com.package.name",
    "title": "Package title",
    "free": true,
    "icon": "http://lh3.googleusercontent.com/aYbdIM1abwyVSUZLDKoE0CDZGRhlkpsaPOg9tNnBktUQYsXflwknnOn2Ge1Yr7rImGk",
    "stars": 4.5,
    "downloads": "500,000,000 - 1,000,000,000",
    "categories": ["SOCIAL"],
    "screenshoots": ["url1", "url2"]
}
```
  - Example of a `pending` item:
    
```
{}
```
  - Example of an `error` item with the dates when the errors were produced:
    
```
[
    "date1",
    "date2"
] 
```
  - Example of a `permanent` item:
    
```
{
    "packageName": "com.package.name",
    "categories": ["SOCIAL"]
}
```

## Workflow

The described workflow will be performed for each package of the list:

1. Check if a key exists in Redis for the package and the value type is either `resolved` or `permanent`.
   - If the key/value exists, the process will return the stored package info as resolved.
   - Otherwise, the process will continue in step 2.
   
2. Try to get the package info by using Google Play API.
   - If the API returns a valid response:
     - A new item of type `resolved` will be created in Redis.
     - If a previous `error` or `pending` item exists, this key will be removed.
     - The process will return the package info as resolved.
     - If an error is thrown (like `401 Unauthorized` or `429 Too many requests`), the process will continue in step 3.
     
3. Check if the package exists in Google Play by requesting the server headers (the response will be quick and smaller in size).
   - If the package exists, the process will continue in step 4.
   - Otherwise, the process will continue in step 5.
   
4. Check if a key exists in Redis for the package and the type of the value is either `pending` or `error`.
   - If a value type of `pending` exists, the process will do nothing.
   - If a value type of `error` exists, the process will change the type of the item to `pending`.
   - Otherwise, a new item of type `pending` will be created in Redis.
   - In all cases, the package will return as `pending`.
   
5. Check if a key exists in Redis for the package and the value type is `error`.
   - If the key exists, a new date will be appended to the value.
   - Otherwise a new item of type `error` will be created in Redis.
   - In all cases, the package will return as `error`.
