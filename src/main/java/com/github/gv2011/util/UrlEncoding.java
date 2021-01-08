package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import java.util.stream.IntStream;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.icol.ICollections;
import com.github.gv2011.util.icol.Path;

public final class UrlEncoding {

  private UrlEncoding(){staticClass();}

  public static String decode(final String raw) {
    final BytesBuilder result = ByteUtils.newBytesBuilder();
    final int[] chars = raw.codePoints().toArray();
    int i = 0;
    final int length = chars.length;
    while(i<length){
      final int c = chars[i++];
      if(c=='%' && i+1<length){
        {
          final int d1 = Character.digit(chars[i], 16);
          final int d2 = Character.digit(chars[i+1], 16);
          if(d1!=-1 && d2!=-1){
            i+=2;
            result.write((d1*16)+d2);
          }
          else result.write(c);
        }
      }
      else{
        if(c<0x80) result.write(c);
        else result.write(new String(Character.toChars(c)).getBytes(UTF_8));
      }
    }
    return result.build().utf8ToString();
  }

  public static Path decodePath(String raw) {
    final int query = raw.indexOf('?');
    if(query!=-1) raw = raw.substring(0, query);
    final int fragment = raw.indexOf('#');
    if(fragment!=-1) raw = raw.substring(0, fragment);
    return StringUtils.split(raw, '/').stream().map(UrlEncoding::decode).collect(ICollections.toPath());
  }
  
  public static String encodePathElement(String pathElement){
    return pathElement.codePoints()
      .flatMap(cp->encodeChar(cp))
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString()
    ;
  }

  private static IntStream encodeChar(int cp) {
    if(cp>='a' && cp<='z' || cp>='A' && cp<='Z' || cp>='0' && cp<='9' || cp=='-' || cp=='.' || cp=='_' || cp=='~'){
      return IntStream.of(cp);
    }
    else {
      final byte[] bytes = new String(Character.toChars(cp)).getBytes(UTF_8);
      return IntStream.range(0, bytes.length).flatMap(i -> {
        final byte b = bytes[i];
        return IntStream.of('%', ByteUtils.firstHex(b), ByteUtils.secondHex(b));
      });
    }
  }

  public static String encodePath(Path path) {
    return path.stream().map(UrlEncoding::encodePathElement).collect(joining("/"));
  }

}
