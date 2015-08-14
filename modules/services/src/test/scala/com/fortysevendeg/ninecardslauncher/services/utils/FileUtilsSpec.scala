package com.fortysevendeg.ninecardslauncher.services.utils

import java.io._
import java.util.zip.{GZIPOutputStream, GZIPInputStream}

import com.fortysevendeg.ninecardslauncher.services.api.models.User
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

trait FileUtilsSpecification
  extends Specification
  with Mockito {

  trait FileUtilsScope
    extends Scope {

    class FileUtilsImpl extends FileUtils

    val mockFileUtils = new FileUtilsImpl

    val mockFile = mock[File]
    val mockFileInputStream = mock[FileInputStream]
    val mockFileOutputStream = mock[FileOutputStream]
    val mockGZIPInputStream = mock[GZIPInputStream]
    val mockGZIPOutputStream = mock[GZIPOutputStream]
    val mockObjectInputStream = mock[ObjectInputStream]
    val mockObjectOutputStream = mock[ObjectOutputStream]
    val mockUser = mock[User]

  }

  trait LoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = mockUser.asInstanceOf[User]
    }

  }

  trait ErrorFileInputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileInputStream(file: File) = {
        throw new FileNotFoundException
      }
    }

  }

  trait ErrorGZIPInputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = {
        throw new StreamCorruptedException
      }
    }

  }

  trait ErrorObjectInputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = {
        throw new StreamCorruptedException
      }
    }

  }

  trait ErrorObjectInputStreamReadLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = {
        throw new ClassNotFoundException
      }
    }

  }

  trait WriteFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](out: ObjectOutputStream, obj: User): Unit = ()
     }

  }

  trait ErrorFileOutputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileOutputStream(file: File) = {
        throw new FileNotFoundException
      }
    }

  }

  trait ErrorGZIPOutputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) =  {
        throw new IOException
      }
    }

  }

  trait ErrorObjectOutputStreamLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = {
        throw new IOException
      }
    }

  }

  trait ErrorObjectOutputStreamWriteLoadFileUtilsScope
    extends Scope
    with FileUtilsScope {

    override val mockFileUtils = new FileUtilsImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](out: ObjectOutputStream, obj: User): Unit = {
        throw new IOException
      }
    }
  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "successfully load a file" in
      new LoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beSuccessfulTry
      }

    "fails loading a file when FileInputStream throws an FileNotFoundException" in
      new ErrorFileInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when GZIPInputStream throws an StreamCorruptedException" in
      new ErrorGZIPInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream throws an StreamCorruptedException" in
      new ErrorObjectInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream.readObject throws an ClassNotFoundException" in
      new ErrorObjectInputStreamReadLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "successfully writes a file" in
      new WriteFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beSuccessfulTry
      }

    "fails writing a file when FileOutputStream throws an FileNotFoundException" in
      new ErrorFileOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when GZIPOutputStream throws an IOException" in
      new ErrorGZIPOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream throws an IOException" in
      new ErrorObjectOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream.writeObject throws an IOException" in
      new ErrorObjectOutputStreamWriteLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

  }

}
