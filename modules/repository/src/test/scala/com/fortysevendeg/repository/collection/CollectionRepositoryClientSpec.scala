package com.fortysevendeg.repository.collection

import com.fortysevendeg.ninecardslauncher.provider.CollectionEntity._
import com.fortysevendeg.ninecardslauncher.provider.DBUtils._
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class CollectionRepositoryClientSpec
    extends Specification
    with Mockito {

  "CollectionRepositoryClient component" should {

    "addCollection should return a valid Collection object" in new AddCollectionSupport {

      val response = await(repoAddCollection(createAddCollectionRequest))

      response.collection.id shouldEqual collectionId
      response.collection.data.name shouldEqual name
    }

    "deleteCollection should return a successful response when a valid cache category id is given" in
        new DeleteCollectionSupport {
          val response = await(repoDeleteCollection(createDeleteCollectionRequest))

          response.deleted shouldEqual 1
        }

    "findCollectionById should return a Collection object when a existing id is given" in
        new FindCollectionByIdSupport {
          val response = await(repoFindCollectionById(createFindCollectionByIdRequest(id = collectionId)))

          response.collection.get.id shouldEqual collectionId
          response.collection.get.data.name shouldEqual name
        }

    "findCollectionById should return None when a non-existing id is given" in
        new FindCollectionByIdSupport {
          val response = await(repoFindCollectionById(createFindCollectionByIdRequest(id = nonExistingCollectionId)))

          response.collection shouldEqual None
        }

    "fetchCollectionByOriginalSharedCollectionId should return a Collection object when a existing shared collection id is given" in
        new FetchCollectionByOriginalSharedCollectionIdSupport {
          val response = await(repoFetchCollectionByOriginalSharedCollectionId(
            createFetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = sharedCollectionIdInt)))

          response.collection.get.id shouldEqual collectionId
          response.collection.get.data.name shouldEqual name
        }

    "fetchCollectionByOriginalSharedCollectionId should return None when a non-existing shared collection id is given" in
        new FetchCollectionByOriginalSharedCollectionIdSupport {
          val response = await(repoFetchCollectionByOriginalSharedCollectionId(
            createFetchCollectionByOriginalSharedCollectionIdRequest(sharedCollectionId = nonExistingSharedCollectionIdInt)))

          response.collection shouldEqual None
        }

    "fetchCollectionByPosition should return a Collection object when a existing position is given" in
        new FetchCollectionByPositionSupport {
          val response = await(repoFetchCollectionByPosition(createFetchCollectionByPositionRequest(position = position)))

          response.collection.get.id shouldEqual collectionId
          response.collection.get.data.name shouldEqual name
        }

    "fetchCollectionByPosition should return None when a non-existing position is given" in
        new FetchCollectionByPositionSupport {
          val response = await(repoFetchCollectionByPosition(createFetchCollectionByPositionRequest(position = nonExistingPosition)))

          response.collection shouldEqual None
        }

    "fetchSortedCollections should return all the cache categories stored in the database" in
        new FetchSortedCollectionsSupport {
          val response = await(repoFetchSortedCollections(createFetchSortedCollectionsRequest))

          response.collections shouldEqual collectionSeq
        }

    "updateCollection should return a successful response when the collection is updated" in
        new UpdateCollectionSupport {
          val response = await(repoUpdateCollection(createUpdateCollectionRequest))

          response.updated shouldEqual 1
        }

    "getEntityFromCursor should return None when an empty cursor is given" in
        new EmptyCollectionMockCursor {
          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual None
        }

    "getEntityFromCursor should return a Collection object when a cursor with data is given" in
        new CollectionMockCursor {
          val result = getEntityFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual Some(collectionEntity)
        }

    "getListFromCursor should return an empty sequence when an empty cursor is given" in
        new EmptyCollectionMockCursor {
          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual Seq.empty
        }

    "getEntityFromCursor should return a Collection sequence when a cursor with data is given" in
        new CollectionMockCursor {
          val result = getListFromCursor(collectionEntityFromCursor)(mockCursor)

          result shouldEqual collectionEntitySeq
        }
  }
}
