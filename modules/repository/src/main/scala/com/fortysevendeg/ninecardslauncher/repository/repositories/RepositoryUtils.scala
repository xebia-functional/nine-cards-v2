package com.fortysevendeg.ninecardslauncher.repository.repositories

object RepositoryUtils {

  // We have a bug in server and sometimes we have a empty strings.
  // We are going to fix this problem here
  def flatOrNull(in: Option[String]): String = in map { value =>
    if (value == "") null else value
  } orNull

}
