package com.github.gv2011.util;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */




import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Enumeration;
import java.util.Iterator;

public final class LegacyCollections {

  private LegacyCollections(){staticClass();}

  public static <T> Iterator<T> asIterator(final Enumeration<? extends T> en){
    return new Iterator<>(){
      @Override
      public boolean hasNext() {return en.hasMoreElements();}
      @Override
      public T next() {return en.nextElement();}
    };
  }

  public static <T> Enumeration<T> asEnumeration(final Iterator<? extends T> it){
    return new Enumeration<>(){
      @Override
      public boolean hasMoreElements() {
        return it.hasNext();
      }
      @Override
      public T nextElement() {
        return it.next();
      }
    };
  }

  public static <T> XStream<T> stream(final Enumeration<? extends T> en){
    return XStream.fromIterator(asIterator(en));
  }
}
