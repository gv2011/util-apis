package com.github.gv2011.util;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.ex.Exceptions.wrap;
import static com.github.gv2011.util.icol.ICollections.xStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypeProvider;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.FileExtension;
import com.github.gv2011.util.bytes.HashAndSize;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.time.Clock;
import com.github.gv2011.util.time.TimeUtils;

public final class FileUtils {

  private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  private FileUtils(){staticClass();}

  public static final Path WORK_DIR = call(()->FileSystems.getDefault().getPath(".").toRealPath());

  public static boolean sameFile(final Path f1, final Path f2){
    return call(()->Files.isSameFile(f1, f2));
  }

  public static XStream<Path> list(final Path dir){
    return XStream.xStream(call(()->Files.list(dir)));
  }

  public static Reader getReader(final String first, final String... more){
    return getReader(Paths.get(first, more));
  }

  public static Reader getReader(final Path path){
    return call(()->new BufferedReader(new InputStreamReader(
      Files.newInputStream(path),
      CharsetUtils.utf8Decoder()
    )));
  }

  public static Writer getWriter(final String first, final String... more){
    return getWriter(Paths.get(first, more));
  }

  public static Writer getWriter(final Path file){
    return getWriter(file, false);
  }

  public static Writer getWriter(final Path file, final boolean append){
    return call(()->new BufferedWriter(new OutputStreamWriter(
      append ? Files.newOutputStream(file, APPEND, CREATE) : Files.newOutputStream(file),
      CharsetUtils.utf8Encoder()
    )));
  }

  public static Reader getReaderRemoveBom(final String first, final String... more){
    return getReaderRemoveBom(Paths.get(first, more));
  }

  public static Reader getReaderRemoveBom(final Path file){
      final Reader reader = getReader(file);
      try{
        final int bom = call(()->reader.read());
        verifyEqual(bom, 0xFEFF);
        return reader;
      }
      catch(final Throwable t){
        call(reader::close); throw t;
      }
  }

  public static InputStream getStream(final String first, final String... more){
    return getStream(Paths.get(first, more));
  }

  public static InputStream getStream(final Path path){
    return call(()->Files.newInputStream(path));
  }

  public static InputStream tryGetStream(final Path path){
    return call(()->{
      InputStream newInputStream;
      try {
        newInputStream = Files.newInputStream(path);
      } catch (final NoSuchFileException e) {
        newInputStream = new ByteArrayInputStream(new byte[0]);
      }
      return newInputStream;
    });
  }

  public static String readText(final String first, final String... more){
    return readText(Paths.get(first, more));
  }

  public static String readText(final Path path){
    return
      tryReadText(path)
      .orElseThrow(()->new NoSuchElementException(format("File {} does not exist.", path.toAbsolutePath())))
    ;
  }

  public static Stream<String> readLines(final Path path) {
    return StreamUtils.readLines(call(()->Files.newInputStream(path)));
  }

  public static final HashAndSize hash(final Path file){
    final Bytes b = ByteUtils.read(file);
    try{return b.hashAndSize();}
    finally{b.close();}
  }

  public static Reader reader(final Path path){
    return StreamUtils.reader(call(()->Files.newInputStream(path)));
  }

  public static Bytes read(final Path path){
    return ByteUtils.read(path);
  }

  public static Opt<String> tryReadText(final Path path){
    return
      call(()->{
        try{
          return Opt.of(Files.newInputStream(path));
        }
        catch(final NoSuchFileException e){
          return Opt.<InputStream>empty();
        }
      })
      .map(in->StreamUtils.readText(()->in))
    ;
  }

  public static void writeText(final String text, final String path, final String... morePathElements) {
    writeText(text, Paths.get(path, morePathElements));
  }

  public static void writeText(final String text, final Path path){
    call(()->Files.write(path, text.getBytes(UTF_8), TRUNCATE_EXISTING, CREATE));
  }

  public static Path writeTextWithTimestamp(final String text, final Path directory, final String suffix){
    return writeBytesWithTimestamp(ByteUtils.asUtf8(text).content(), directory, suffix);
  }

  public static Path writeBytesWithTimestamp(final Bytes bytes, final Path directory, final String suffix){
    boolean written = false;
    Path path = directory.resolve(TimeUtils.fileSafeInstant()+suffix);
    int failedCount = 0;
    while(!written){
      try {
        try(OutputStream out = Files.newOutputStream(path, CREATE_NEW)){
          bytes.write(out);
        }
        written = true;
      } catch (final IOException e) {
        if(++failedCount==10){
          throw new RuntimeException(format("Could not write text file with timestamp {}.", path), e);
        }
        call(()->Thread.sleep(1));
        path = directory.resolve(TimeUtils.fileSafeInstant()+suffix);
      }
    }
    return path;
  }

  public static long getSize(final String first, final String... more){
    return getSize(Paths.get(first, more));
  }

  public static long getSize(final Path path){
    return call(()->Files.size(path));
  }

  public static String removeExtension(final Path path){
    final Path fileName = path.getFileName();
    if(fileName==null) throw new IllegalArgumentException();
    final String n = fileName.toString();
    final int i = n.lastIndexOf('.');
    return i==-1?n:n.substring(0, i);
  }

  public static Path withoutExtension(final Path path){
    return Opt
      .ofNullable(path.getParent())
      .map(parent->parent.resolve(removeExtension(path)))
      .orElseGet(()->Paths.get(removeExtension(path)))
    ;
  }

  public static FileExtension getExtension(final Path path){
    final Path fileName = path.getFileName();
    if(fileName==null) throw new IllegalArgumentException();
    return getExtension(fileName.toString());
  }

  public static FileExtension getExtension(final URL url){
    return getExtension(url.getPath());
  }

  private static FileExtension getExtension(final String path){
    final int i = path.lastIndexOf('.');
    return FileExtension.parse(i==-1?"":path.substring(i+1, path.length()).toLowerCase(Locale.ROOT));
  }

  public static boolean isTextFile(final Path file){
    final ISet<DataType> dataTypes = DataTypeProvider.instance().dataTypesForExtension(getExtension(file));
    return !dataTypes.isEmpty()
      && (
        dataTypes.stream().allMatch(dt->dt.primaryType().equals("text")) ||
        dataTypes.size()==1 ? dataTypes.single().baseType().equals(DataTypes.SVG) : false
      )
      && Files.isRegularFile(file)
    ;
  }

  public static void delete(final Path file) {
    delete(file.toFile());
//    if(Files.isRegularFile(file)) deleteFile(file);
//    else if(Files.isDirectory(file)) deleteFolder(file);
//    else{
//      if(!Files.notExists(file)){
//        try{
//          deleteFile(file);
//        }
//        catch(final Exception ex){
//          deleteFolder(file);
//        }
//      }
//    }
  }

  public static void delete(final File file) {
    if(file.isDirectory()){
      for(final File child: file.listFiles()) delete(child);
    }
    if(file.exists()){
      verify(file.delete());
      verify(!file.exists());
    }
  }

  public static boolean exists(final Path file){
    final Path f = file.toAbsolutePath().normalize();
    if(Files.exists(file)) return true;
    else if(Files.notExists(file)) return false;
    else{
      final Path parent = call(()->
        Opt.ofNullable(f.getParent())
        .orElseThrow(()->new RuntimeException(format("Existence of {} unknown.", f)))
        .toRealPath()
      );
      return call(()->Files.list(parent)).anyMatch(ch->call(()->Files.isSameFile(ch, file)));
    }
  }

  /**
   * Deletes a file if it exists. Cannot be used with directories.
   *
   * @return  {@code true} if the file was deleted by this method;
   *          {@code false} if the file could not be deleted because it did not exist.
   */
  public static boolean deleteFile(final Path file) {
    verify(!Files.isDirectory(file));
    int retries = 10;
    boolean result = false;
    while(retries>0){
      try {
        result = call(()->Files.deleteIfExists(file));
        retries = 0;
      } catch (final Exception e) {
        retries--;
        if(retries==0) throw e;
        else{
          call(()->Thread.sleep(100));
//          Clock.get().sleep(Duration.ofMillis(100));
          if(Files.exists(file)) LOG.warn(format("Failed to delete {}. Retrying.", file), e);
          else retries=0;
        }
      }
    }
    verify(Files.notExists(file));
    return result;
  }

  public static void deleteContents(final Path folder) {
    while(!isEmpty(folder)){
      for(final String f: folder.toFile().list()){
        delete(folder.resolve(f));
      }
    }
  }

  @SuppressWarnings("unused")
  private static void deleteFolder(final Path folder) {
    verify(folder, f->Files.isDirectory(f), f->format("{} is not a folder.", f));
    int retries = 3;
    final Duration retryDelay = Duration.ofMillis(100);
    boolean exists = true;
    while(exists){
      try {
        deleteContents(folder);
        folder.toFile().delete();
      } catch (final Exception e) {
        LOG.warn(format("Could not delete contents of {}.{}", folder, retries>0 ? " Retrying." : ""), e);
      }
      exists = exists(folder);
      if(exists){
        if(retries==0) throw new RuntimeException(format("Could not delete {}.", folder));
        else LOG.warn("{} still exists. Trying again to delete after {}.", folder, retryDelay);
        retries--;
        Clock.get().sleep(retryDelay);
      }
    }
    verify(!exists(folder));
  }

  public static boolean contains(final Path folder, final Path file){
    final Path c = folder.toAbsolutePath();
    final Path f = file.toAbsolutePath();
    boolean done = false;
    boolean result = false;
    Path parent = f.getParent();
    while(!done){
      if(parent==null) done=true;
      else{
        if(parent.equals(c)){
          done = true;
          result = true;
        }
        else parent = parent.getParent();
      }
    }
    return result;
  }

  public static void zip(final Path source, final Path target) {
    new Zipper().zip(source, target);
  }

  public static void unZip(final Path zipFile, final Path targetFolder) {
    new Zipper().unZip(zipFile, targetFolder);
  }

  public static <T> T callWithTempFolder(final Class<?> clazz, final ThrowingFunction<Path,? extends T> f) {
    return call(()->{
      final Path folder = Files.createTempDirectory(clazz.getName());
      try{return f.applyThrowing(folder);}
      finally{
        delete(folder);
      }
    });
  }

  public static Writer writer(final Path file) {
    return StreamUtils.writer(call(()->Files.newOutputStream(file)));
  }

  public static long copy(final Path src, final Path target) {
    return copy(()->Files.newInputStream(src), target);
  }

  public static long copy(final URL url, final Path target) {
    return copy(url::openStream, target);
  }

  public static long copy(final ThrowingSupplier<InputStream> in, final Path target) {
    return callWithCloseable(()->Files.newOutputStream(target, CREATE, TRUNCATE_EXISTING), out->{
      return StreamUtils.copy(in, out);
    });
  }

  public static Opt<Instant> tryGetLastModified(final Path file) {
    try {return Opt.of(Files.getLastModifiedTime(file).toInstant());}
    catch (final FileNotFoundException | NoSuchFileException e) {return Opt.empty();}
    catch (final IOException e) {throw wrap(e);}
  }

  public static boolean isEmpty(final Path folder) {
    verify(folder, f->Files.isDirectory(f), f->format("{} is not a folder.", f));
    final boolean empty1 = call(()->!Files.list(folder).findAny().isPresent());
    final boolean empty2 = folder.toFile().list().length==0;
    verifyEqual(empty1, empty2);
    return empty1;
  }

  public static boolean isInside(final Path path, final Path directory) {
    return path.toAbsolutePath().normalize().getParent().startsWith(directory.toAbsolutePath().normalize());
  }

  public static Opt<Path> resolveSafely(final Path file, final com.github.gv2011.util.icol.Path path) {
    if(path.isEmpty()){
      return Opt.empty();
    }
    else{
      if(!Files.isDirectory(file, NOFOLLOW_LINKS)) return Opt.empty();
      else{
        if(path.size()==1){
          return resolveSafelyInternal(file, path.first());
        }
        else{
          assert !path.isEmpty();
          return
            resolveSafelyInternal(file, path.first())
            .flatMap(f->resolveSafely(f, path.tail()))
          ;
        }
      }
    }
  }

  public static Opt<Path> resolveSafely(final Path file, final String child) {
    if(!Files.isDirectory(file)) return Opt.empty();
    else{
      return resolveSafelyInternal(file, child);
    }
  }

  private static Opt<Path> resolveSafelyInternal(final Path dir, final String child) {
    //Assumes Files.isDirectory(dir) has been checked.
    return child.isEmpty()
      ? Opt.of(dir)
      : (
        callWithCloseable(()->Files.list(dir), s->{
          return xStream(s)
            .filter(f->f.getFileName().toString().equals(child))
            .tryFindAny()
          ;
        })
      )
    ;
  }

  public static DataType getType(final Path p) {
    return DataTypeProvider.instance().dataTypeForExtension(getExtension(p));
  }

  public static Path newName(Path path) {
    int i = 1;
    final String base = FileUtils.removeExtension(path);
    final FileExtension extension = FileUtils.getExtension(path);
    while(Files.exists(path)){
      path = path.getParent().resolve(extension.appendTo(base + "."+ i));
      i++;
    }
    return path;
  }

}
