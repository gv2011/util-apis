package com.github.gv2011.util;

import static com.github.gv2011.util.CollectionUtils.pair;


import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.listOf;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

public final class Comparison {

  private Comparison(){staticClass();}

  @SuppressWarnings("rawtypes")
  private static final Comparator LIST_COMPARATOR = listComparator(Comparator.naturalOrder());

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static final Comparator OPT_COMPARATOR = (opt1,opt2)->{
    return (int)
      ((Opt)opt1)
      .map(o1->
        ((Opt)opt2)
        .map(o2->((Comparable)o1).compareTo(o2))
        .orElse(1)
      )
      .orElseGet(()->((Opt)opt2).isPresent() ? -1 : 0)
    ;
  };

  static final Comparator<Object> OBJECT_COMPARATOR = Comparison::compareObjects;

  private static final Comparator<Entry<?,?>> ENTRY_COMPARATOR = Comparator
    .<Entry<?,?>,Object>comparing(Entry::getKey, OBJECT_COMPARATOR)
    .thenComparing(Entry::getValue, OBJECT_COMPARATOR)
  ;

  static final Comparator<Class<?>> CLASS_COMPARATOR = Comparator
    .<Class<?>,String>comparing(Class::getName)
    .thenComparing(Class::getClassLoader, OBJECT_COMPARATOR)
    .thenComparing(Class::hashCode)
  ;

  private static final Comparator<Object> OBJECT_COMPARATOR_INTERNAL = Comparator
    .comparing(Object::getClass, CLASS_COMPARATOR)
    .thenComparing(Object::hashCode)
  ;

  @SuppressWarnings("rawtypes")
  private static final IList<Pair<Class, Comparator>> OBJECT_COMPARATOR_COMPARATORS = listOf(
    pair(Comparable.class, Comparator.naturalOrder()),
    pair(Entry.class,      Comparison.entryComparator()),
    pair(Opt.class,        Comparison.optComparator()),
    pair(Set.class,        Comparison.setComparator()),
    pair(List.class,       Comparison.listComparator())
  );


  public static <C> Comparator<C> reversed(final Comparator<C> comparator){
    return comparator.reversed();
  }

  public static <C extends Comparable<C>> C min(final C c1, final C c2){
    final int diff = c1.compareTo(c2);
    assert (diff==0)==(c1.equals(c2));
    return diff<=0?c1:c2;
  }

  public static <C extends Comparable<C>> C max(final C c1, final C c2){
    final int diff = c1.compareTo(c2);
    assert (diff==0)==(c1.equals(c2));
    return diff>=0?c1:c2;
  }

  public static <C extends Comparable<? super C>> boolean lessThan(final C c1, final C c2){
    return c1.compareTo(c2)<0;
  }

  public static <C extends Comparable<? super C>> boolean lessOrEqual(final C c1, final C c2){
    return c1.compareTo(c2)<=0;
  }

  public static <C extends Comparable<? super C>> boolean greaterThan(final C c1, final C c2){
    return c1.compareTo(c2)>0;
  }

  public static <C extends Comparable<? super C>> boolean greaterOrEqual(final C c1, final C c2){
    return c1.compareTo(c2)>=0;
  }

  public static <T, C extends Comparable<? super C>> Comparator<T> comparing(
    final Function<? super T, ? extends C> keyExtractor
  ){
    return Comparator.comparing(keyExtractor);
  }

  public static <T, C extends Comparable<? super C>> Comparator<T> comparingOpt(
    final Function<T, Opt<C>> keyExtractor)
  {
    return Comparator.comparing(keyExtractor, optComparator());
  }


  @SuppressWarnings("unchecked")
  public static <C extends Comparable<? super C>> Comparator<Opt<C>> optComparator(){
    return OPT_COMPARATOR;
  }

  public static Comparator<Object> objectComparator(){
    return OBJECT_COMPARATOR;
  }

  public static Comparator<Class<?>> classComparator(){
    return CLASS_COMPARATOR;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <K,V> Comparator<Entry<K,V>> entryComparator(){
    return (Comparator)ENTRY_COMPARATOR;
  }

  @SuppressWarnings("unchecked")
  public static <C extends Iterable<? extends E>, E extends Comparable<? super E>> Comparator<C>
  listComparator(){
    return LIST_COMPARATOR;
  }

  public static <C extends Iterable<? extends E>, E> Comparator<C>
  listComparator(final Comparator<? super E> comparator){
    return (c1,c2)->{
      if(c1==c2) return 0;
      else {
        final Iterator<? extends E> it1 = c1.iterator();
        final Iterator<? extends E> it2 = c2.iterator();
        int result = -2;
        while(result == -2){
          final boolean hasNext1 = it1.hasNext();
          final boolean hasNext2 = it2.hasNext();
          if(!hasNext1 || !hasNext2){
            result = hasNext1==hasNext2 ? 0 : hasNext1 ? 1:-1;
          }else{
            final E next1 = it1.next();
            final E next2 = it2.next();
            final int diff = comparator.compare(next1, next2);
            if(diff!=0) result = diff;
          }
        }
        return result;
      }
    };
  }

  static <C extends List<? extends E>, E> Comparator<C>
  listComparator2(final Comparator<? super E> comparator){
    return (c1,c2)->{
      if(c1==c2) return 0;
      else {
        final int min = Math.min(c1.size(), c2.size());
        IntStream.range(0, min).parallel()
          .mapToObj(i->pair(i,comparator.compare(c1.get(i), c2.get(i))))
          .filter(p->p.getValue().intValue()!=0)
          .sorted((p1,p2)->p1.getKey().compareTo(p2.getKey()))
          .findFirst()
        ;
        final Iterator<? extends E> it1 = c1.iterator();
        final Iterator<? extends E> it2 = c2.iterator();
        int result = -2;
        while(result == -2){
          final boolean hasNext1 = it1.hasNext();
          final boolean hasNext2 = it2.hasNext();
          if(!hasNext1 || !hasNext2){
            result = hasNext1==hasNext2 ? 0 : hasNext1 ? 1:-1;
          }else{
            final E next1 = it1.next();
            final E next2 = it2.next();
            final int diff = comparator.compare(next1, next2);
            if(diff!=0) result = diff;
          }
        }
        return result;
      }
    };
  }


  public static <S extends Set<? extends E>, E extends Comparable<? super E>> Comparator<S> setComparator(){
    return setComparator(Comparator.naturalOrder());
  }

  public static <S extends Set<? extends E>, E> Comparator<S> setComparator(final Comparator<? super E> comparator){
    return (s1,s2)->{
      int result = 0;
      if(s1!=s2){
        final TreeSet<E> all = new TreeSet<>(comparator);
        all.addAll(s1);
        all.addAll(s2);
        final Iterator<E> it = all.descendingIterator();
        while(result==0 && it.hasNext()){
          final E e = it.next();
          if(!s1.contains(e)){
            assert s2.contains(e);
            result = -1;
          }else if(!s2.contains(e)){
            result = 1;
          }
        }
      }
      return result;
    };
  }

  public static <E,F extends Comparable<? super F>> Comparator<E> compareByAttribute(final Function<E,F> attribute){
    return (o1,o2)->attribute.apply(o1).compareTo(attribute.apply(o2));
  }


  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static int compareObjects(final Object o1, final Object o2) {
    if(o1==null) return o2==null ? 0 : -1;
    else if(o2==null) return 1;
    else if(o1.equals(o2)) return 0;
    else{
      for(final Pair<Class, Comparator> p: OBJECT_COMPARATOR_COMPARATORS){
        final Class clazz = p.getKey();
        if(clazz.isInstance(o1) && clazz.isInstance(o2)){
          return p.getValue().compare(o1, o2);
        }
      }
      return OBJECT_COMPARATOR_INTERNAL.compare(o1, o2);
    }
  }

}
