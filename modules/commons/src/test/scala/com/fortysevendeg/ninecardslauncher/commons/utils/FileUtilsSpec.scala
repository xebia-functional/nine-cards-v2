package com.fortysevendeg.ninecardslauncher.commons.utils

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.fortysevendeg.ninecardslauncher.commons.contexts.ContextSupport
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import scala.util.Success

trait FileUtilsSpecification
  extends Specification
  with Mockito {

  trait FileUtilsScope
    extends Scope
    with FileUtilsData {

    val mockContextSupport = mock[ContextSupport]
    val mockStreamWrapper = mock[StreamWrapper]
    val mockInputStream = mock[InputStream]

    val fileUtils = new FileUtils(mockStreamWrapper)

  }

  trait ValidUtilsScope {
    self: FileUtilsScope =>

    mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) returns mockInputStream
    mockStreamWrapper.makeStringFromInputStream(mockInputStream) returns fileJson

  }

  trait ErrorUtilsScope {
    self: FileUtilsScope =>

    mockStreamWrapper.openAssetsFile(fileName)(mockContextSupport) throws new RuntimeException("")

  }

  trait StreamFileUtilsScope
    extends FileUtilsScope {

    val mockFile = mock[File]
    val mockFileInputStream = mock[FileInputStream]
    val mockFileOutputStream = mock[FileOutputStream]
    val mockGZIPInputStream = mock[GZIPInputStream]
    val mockGZIPOutputStream = mock[GZIPOutputStream]
    val mockObjectInputStream = mock[ObjectInputStream]
    val mockObjectOutputStream = mock[ObjectOutputStream]
    val mockAny = mock[Any]

  }

  trait LoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) returns mockFileInputStream
    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) returns mockGZIPInputStream
    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) returns mockObjectInputStream
    mockStreamWrapper.readObjectAsInstance[Any](mockObjectInputStream) returns mockAny.asInstanceOf[Any]

  }

  trait ErrorFileInputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createFileInputStream(mockFile) throws new RuntimeException("")

  }

  trait ErrorGZIPInputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createGZIPInputStream(mockFileInputStream) throws new RuntimeException("")

  }

  trait ErrorObjectInputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createObjectInputStream(mockGZIPInputStream) throws new RuntimeException("")

  }

  trait ErrorObjectInputStreamReadLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.readObjectAsInstance[Any](mockObjectInputStream) throws new RuntimeException("")

  }

  trait WriteFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) returns mockFileOutputStream
    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) returns mockGZIPOutputStream
    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) returns mockObjectOutputStream

  }

  trait ErrorFileOutputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createFileOutputStream(mockFile) throws new RuntimeException("")

  }

  trait ErrorGZIPOutputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createGZIPOutputStream(mockFileOutputStream) throws new RuntimeException("")

  }

  trait ErrorObjectOutputStreamLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.createObjectOutputStream(mockGZIPOutputStream) throws new RuntimeException("")

  }

  trait ErrorObjectOutputStreamWriteLoadFileUtilsScope {

    self: StreamFileUtilsScope =>

    mockStreamWrapper.writeObject(any, any) throws new RuntimeException("")

  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "returns a json string when a valid fileName is provided" in
      new FileUtilsScope with ValidUtilsScope {
        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result mustEqual Success(fileJson)
      }

    "returns an Exception when the file can't be opened" in
      new FileUtilsScope with ErrorUtilsScope {
        val result = fileUtils.readFile(fileName)(mockContextSupport)
        result must beFailedTry
      }

    "successfully load a file" in
      new StreamFileUtilsScope with LoadFileUtilsScope {
        val result = fileUtils.loadFile[Any](mockFile)
        result must beSuccessfulTry
      }

    "fails loading a file when FileInputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorFileInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[Any](mockFile)
        result must beFailedTry
      }

    "fails loading a file when GZIPInputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorGZIPInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[Any](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorObjectInputStreamLoadFileUtilsScope {
        val result = fileUtils.loadFile[Any](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream.readObject throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorObjectInputStreamReadLoadFileUtilsScope {
        val result = fileUtils.loadFile[Any](mockFile)
        result must beFailedTry
      }

    "successfully writes a file" in
      new StreamFileUtilsScope with WriteFileUtilsScope {
        val result = fileUtils.writeFile[Any](mockFile, mockAny)
        result must beSuccessfulTry
      }

    "fails writing a file when FileOutputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorFileOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[Any](mockFile, mockAny)
        result must beFailedTry
      }

    "fails writing a file when GZIPOutputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorGZIPOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[Any](mockFile, mockAny)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorObjectOutputStreamLoadFileUtilsScope {
        val result = fileUtils.writeFile[Any](mockFile, mockAny)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream.writeObject throws a RuntimeException" in
      new StreamFileUtilsScope with ErrorObjectOutputStreamWriteLoadFileUtilsScope {
        val result = fileUtils.writeFile[Any](mockFile, mockAny)
        result must beFailedTry
      }

  }

}
