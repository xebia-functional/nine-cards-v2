package java.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;



public class FileInputStream extends InputStream {
    public FileInputStream(File file) throws FileNotFoundException {
    }

    public FileInputStream(FileDescriptor fd) {
        throw new RuntimeException("Stub!");
    }

    public FileInputStream(String path) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public int available() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public void close() throws IOException {
        throw new RuntimeException("Stub!");
    }

    protected void finalize() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public FileChannel getChannel() {
        throw new RuntimeException("Stub!");
    }

    public final FileDescriptor getFD() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read() throws IOException {
        throw new RuntimeException("Stub!");
    }

    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }

    public long skip(long byteCount) throws IOException {
        throw new RuntimeException("Stub!");
    }
}
