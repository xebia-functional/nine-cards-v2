package com.fortysevendeg.ninecardslauncher.repository.provider

import com.fortysevendeg.ninecardslauncher.repository.provider.NineCardsSqlHelper._

trait DBUtils {

  def execAllVersionsDB() = (1 to databaseVersion) foreach { version => execVersion(version) }

  def execVersionsDB(oldVersion: Int, newVersion: Int) =
    (oldVersion + 1 to newVersion) foreach { version => execVersion(version) }

  def execVersion(version: Int) = {}

}
