package com.github.gv2011.util.serviceloader;


/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2016 - 2018 Vinz (https://github.com/gv2011)
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

import static com.github.gv2011.util.ex.Exceptions.call;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.gv2011.util.LegacyCollections;
import com.github.gv2011.util.icol.ICollectionFactory;
import static com.github.gv2011.util.icol.ICollections.*;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.ISet;


public final class ServiceProviderConfigurationFile<S> {

  public static <S> ISet<ServiceProviderConfigurationFile<S>> files(final Class<S> service){
    final ICollectionFactory iCollections = iCollections();
    return call(()->filesInternal(service).collect(iCollections.setCollector()));
  }


  static <S> Stream<ServiceProviderConfigurationFile<S>> filesInternal(final Class<S> service) throws IOException{
    return
      StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
          getResources(service),
          Spliterator.ORDERED
        ),
        false
      )
      .map(u->{
        try {return new ServiceProviderConfigurationFile<>(service, u);}
        catch (final IOException e) {throw new RuntimeException(e);}
      })
    ;
  }


  private static <S> Iterator<URL> getResources(final Class<S> service) throws IOException {
    final String name = "META-INF/services/"+service.getName();
    final Iterator<URL> it = LegacyCollections.asIterator(
      Thread.currentThread().getContextClassLoader().getResources(name)
    );
    final List<URL> list = new ArrayList<>();
    while(it.hasNext()) list.add(it.next());
    return list.iterator();
//    return it;
  }

  private final Class<S> service;
  private final List<String> implementations;
  private final URL url;


  private ServiceProviderConfigurationFile(final Class<S> service, final URL url) throws IOException {
    this.service = service;
    this.url = url;
    final Set<String> set = new HashSet<>();
    implementations = Collections.unmodifiableList(
      lines(readText(url)).stream()
      .map(this::stripComment)
      .map(String::trim)
      .filter(not(String::isEmpty))
      .filter(l->set.add(l)) //ignore duplicates
      .collect(toList())
    );
  }

  private String readText(final URL url) throws IOException {
    try(InputStream in = url.openStream()){
      return new String(in.readAllBytes(), UTF_8);
    }
  }


  public Class<S> service(){
      return service;
  }

  public URL url(){
      return url;
  }

  @Override
  public String toString() {
    return url.toString();
  }

  public IList<String> implementations(){
      final ICollectionFactory iCollections = iCollections();
      return implementationsInternal().collect(iCollections.listCollector());
  }

  Stream<String> implementationsInternal(){
      return implementations.stream();
  }

  private static List<String> lines(final String text){
    return lines(new StringReader(text)).collect(toList());
  }

  private static Stream<String> lines(final Reader text){
    return new BufferedReader(text).lines();
  }

  private static <T> Predicate<T> not(final Predicate<T> p){
    return o->!p.test(o);
  }

  private final String stripComment(final String line) {
    final int i = line.indexOf('#');
    return i==-1 ? line : line.substring(0, i);
  }
}
