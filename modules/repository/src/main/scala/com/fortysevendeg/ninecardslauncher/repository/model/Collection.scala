package com.fortysevendeg.ninecardslauncher.repository.model

case class Collection(
    id: Int,
    position: Int,
    name: String,
    `type`: String,
    icon: String,
    themedColorIndex: Int,
    appsCategory: String,
    constrains: String,
    originalSharedCollectionId: String,
    sharedCollectionId: String,
    sharedCollectionSubscribed: Boolean)
