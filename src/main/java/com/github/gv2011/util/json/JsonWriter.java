package com.github.gv2011.util.json;

import com.github.gv2011.util.num.Decimal;
import com.github.gv2011.util.num.NumUtils;

public interface JsonWriter {

    void beginArray();

    void endArray();

    void nullValue();

    void writeString(String value);

    void writeBoolean(boolean value);

    void writeDecimal(Decimal value);

    default void writeInt(final int value) {
        writeDecimal(NumUtils.num(value));
    }

    default void writeLong(final long value) {
        writeDecimal(NumUtils.num(value));
    }

    void beginObject();

    void endObject();

    void name(String key);

    void flush();

}
