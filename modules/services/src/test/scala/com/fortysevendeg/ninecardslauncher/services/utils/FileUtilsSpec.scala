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

    val mockFile = mock[File]
    val mockFileInputStream = mock[FileInputStream]
    val mockFileOutputStream = mock[FileOutputStream]
    val mockGZIPInputStream = mock[GZIPInputStream]
    val mockGZIPOutputStream = mock[GZIPOutputStream]
    val mockObjectInputStream = mock[ObjectInputStream]
    val mockObjectOutputStream = mock[ObjectOutputStream]
    val mockUser = mock[User]

  }

  trait LoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = mockUser.asInstanceOf[User]
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }

  }

  trait ErrorFileInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileInputStream(file: File) = throw new FileNotFoundException
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = mockUser.asInstanceOf[User]
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorGZIPInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = throw new StreamCorruptedException
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = mockUser.asInstanceOf[User]
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }

  }

  trait ErrorObjectInputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileInputStream(file: File) = mockFileInputStream
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = throw new StreamCorruptedException
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = mockUser.asInstanceOf[User]
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorObjectInputStreamReadLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileInputStream(file: File) = throw new FileNotFoundException
      override def createGZIPInputStream(fileInputStream: FileInputStream) = mockGZIPInputStream
      override def createObjectInputStream(gzipInputStream: GZIPInputStream) = mockObjectInputStream
      override def readObjectAsInstance[User](objectInputStream: ObjectInputStream) = throw new ClassNotFoundException
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait WriteFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](objectOutputStream: ObjectOutputStream,  obj: User): Unit = ()
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorFileOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileOutputStream(file: File) = throw new FileNotFoundException
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](objectOutputStream: ObjectOutputStream,  obj: User): Unit = ()
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorGZIPOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = throw new IOException
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](objectOutputStream: ObjectOutputStream,  obj: User): Unit = ()
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorObjectOutputStreamLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = throw new IOException
      override def writeObject[User](objectOutputStream: ObjectOutputStream,  obj: User): Unit = ()
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

  trait ErrorObjectOutputStreamWriteLoadFileUtilsScope {

    self: FileUtilsScope =>

    val mockStreamWrapper = new StreamWrapperImpl {
      override def createFileOutputStream(file: File) = mockFileOutputStream
      override def createGZIPOutputStream(fileOutputStream: FileOutputStream) = mockGZIPOutputStream
      override def createObjectOutputStream(gzipOutputStream: GZIPOutputStream) = mockObjectOutputStream
      override def writeObject[User](objectOutputStream: ObjectOutputStream,  obj: User): Unit = throw new IOException
    }

    val mockFileUtils = new FileUtils {
      override val streamWrapper = mockStreamWrapper
    }
  }

}

class FileUtilsSpec
  extends FileUtilsSpecification {

  "File Utils" should {

    "successfully load a file" in
      new FileUtilsScope with LoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beSuccessfulTry
      }

    "fails loading a file when FileInputStream throws an FileNotFoundException" in
      new FileUtilsScope with ErrorFileInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when GZIPInputStream throws an StreamCorruptedException" in
      new FileUtilsScope with ErrorGZIPInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream throws an StreamCorruptedException" in
      new FileUtilsScope with ErrorObjectInputStreamLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "fails loading a file when ObjectInputStream.readObject throws an ClassNotFoundException" in
      new FileUtilsScope with ErrorObjectInputStreamReadLoadFileUtilsScope {
        val result = mockFileUtils.loadFile[User](mockFile)
        result must beFailedTry
      }

    "successfully writes a file" in
      new FileUtilsScope with WriteFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beSuccessfulTry
      }

    "fails writing a file when FileOutputStream throws an FileNotFoundException" in
      new FileUtilsScope with ErrorFileOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when GZIPOutputStream throws an IOException" in
      new FileUtilsScope with ErrorGZIPOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream throws an IOException" in
      new FileUtilsScope with ErrorObjectOutputStreamLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

    "fails writing a file when ObjectOutputStream.writeObject throws an IOException" in
      new FileUtilsScope with ErrorObjectOutputStreamWriteLoadFileUtilsScope {
        val result = mockFileUtils.writeFile[User](mockFile, mockUser)
        result must beFailedTry
      }

  }

}
