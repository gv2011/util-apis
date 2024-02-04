package com.github.gv2011.util.bytes;

import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.github.gv2011.util.FileUtils;


final class FileBackedBytesImp extends CachedFileBytes{

  private static final Logger LOG = getLogger(FileBackedBytesImp.class);
  private final HashAndSize hash;
  private final int hashCode;
  private volatile boolean closed;

  FileBackedBytesImp(final Path file, final int hashCode, final HashAndSize hash) {
    super(file, 0, hash.size());
    this.hashCode = hashCode;
    this.hash = hash;
  }

  @Override
  protected void finalize() throws Throwable {
    close();
  }

  @Override
  protected HashAndSize hashImp() {
    return hash;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public void close() {
    closed = true;
    FileUtils.deleteFile(file());
    LOG.debug("File {} deleted.", file().toAbsolutePath());
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public Bytes loadInMemory() {
    return ArrayBytes.create(toByteArray());
  }

}
