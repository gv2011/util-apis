package com.github.gv2011.util;

import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public final class CharsetUtils {

    private CharsetUtils(){staticClass();}

    public static final CharsetEncoder utf8Encoder(){
      return UTF_8.newEncoder().onMalformedInput(REPORT).onUnmappableCharacter(REPORT);
    }

    public static final CharsetDecoder utf8Decoder(){
      return UTF_8.newDecoder().onMalformedInput(REPORT).onUnmappableCharacter(REPORT);
    }

}
