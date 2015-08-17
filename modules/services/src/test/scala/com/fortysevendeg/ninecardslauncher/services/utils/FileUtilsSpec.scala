package com.fortysevendeg.ninecardslauncher.services.utils

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.fortysevendeg.ninecardslauncher.services.api.models.User
import com.fortysevendeg.ninecardslauncher.services.utils.impl.StreamWrapper
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait FileUtilsSpecification
  extends Specification
  with Mockito {

  trait FileUtilsScope
    extends Scope {

    val mockStreamWrapper = mock[StreamWrapper]
    val mockFile = mock[File]
    val mockFileInputStream = mock[FileInputStream]
    val mockFileOutputStream = mock[FileOutputStream]
    val mockGZIPInputStream = mock[GZIPInputStream]
    val mockGZIPOutputStream = mock[GZIPOutputStream]
    val mockObjectInputStream = mock[ObjectInputStream]
    val mockObjectOutputStream = mock[ObjectOutputStream]
    val mockUser = mock[User]

    val fileUtils = new FileUtils(mockStreamWrapper)

  }

  trait LoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) returns mockFileInputStream
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) returns mockGZIPInputStream
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) returns mockObjectInputStream
    mockStreamWrapper.readObjectAsInstance[User](mockObjectInputStream) returns mockUser.asInstanceOf[User]

  }

  trait ErrorFileInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) throws new RuntimeException("")
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) returns mockGZIPInputStream
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) returns mockObjectInputStream
    mockStreamWrapper.readObjectAsInstance[User](mockObjectInputStream) returns mockUser.asInstanceOf[User]

  }

  trait ErrorGZIPInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) returns mockFileInputStream
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) throws new RuntimeException("")
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) returns mockObjectInputStream
    mockStreamWrapper.readObjectAsInstance[User](mockObjectInputStream) returns mockUser.asInstanceOf[User]

  }

  trait ErrorObjectInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) returns mockFileInputStream
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) returns mockGZIPInputStream
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) throws new RuntimeException("")
    mockStreamWrapper.readObjectAsInstance[User](mockObjectInputStream) returns mockUser.asInstanceOf[User]

  }

  trait ErrorObjectInputStreamReadLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) returns mockFileInputStream
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) returns mockGZIPInputStream
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) returns mockObjectInputStream
    mockStreamWrapper.readObjectAsInstance[User](mockObjectInputStream) throws new RuntimeException("")

  }

  trait WriteFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) returns mockFileOutputStream
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) returns mockGZIPOutputStream
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) returns mockObjectOutputStream

  }

  trait ErrorFileOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) throws new RuntimeException("")
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) returns mockGZIPOutputStream
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) returns mockObjectOutputStream

  }

  trait ErrorGZIPOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) returns mockFileOutputStream
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) throws new RuntimeException("")
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) returns mockObjectOutputStream

  }

  trait ErrorObjectOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) returns mockFileOutputStream
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) returns mockGZIPOutputStream
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) throws new RuntimeException("")

  }

  trait ErrorObjectOutputStreamWriteLoadFileUtilsScope {

    self: FileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) returns mockFileOutputStream
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) returns mockGZIPOutputStream
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) returns mockObjectOutputStream
    mockStreamWrapper.writeObject(any, any) throws new RuntimeException("")

  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "successfully load a file" in
      new FileUtilsScope with LoadFileUtilsScope {
        val result = fileUtils.loadFile[User](mockFile)
        result must beSuccessfulTry
      }

    "fails loading a file when FileInputStream throws an FileNotFoundException" in
      new FileUtilsScope with ErrorFileInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when GZIPInputStream throws an StreamCorruptedException" in
      new FileUtilsScope with ErrorGZIPInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream throws an StreamCorruptedException" in
      new FileUtilsScope with ErrorObjectInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream.readObject throws an ClassNotFoundException" in
      new FileUtilsScope with ErrorObjectInputStreamReadLoadFileUtilsScope {
        val result = fileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "successfully writes a file" in
      new FileUtilsScope with WriteFileUtilsScope {
        val result = fileUtils.writeFile[User](mockFile, mockUser)
        result must beSuccessfulTry
      }

    "fails writing a file when FileOutputStream throws an FileNotFoundException" in
      new FileUtilsScope with ErrorFileOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when GZIPOutputStream throws an IOException" in
      new FileUtilsScope with ErrorGZIPOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream throws an IOException" in
      new FileUtilsScope with ErrorObjectOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream.writeObject throws an IOException" in
      new FileUtilsScope with ErrorObjectOutputStreamWriteLoadFileUtilsScope {
        val result = fileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

  }

}
