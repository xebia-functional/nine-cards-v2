package com.fortysevendeg.ninecardslauncher.repository

case class RepositoryInsertException() extends RuntimeException("Insert element into repository failed")
case class RepositoryDeleteException() extends RuntimeException("Delete element from repository failed")
case class RepositoryUpdateException() extends RuntimeException("Update element in repository failed")
