package com.github.gv2011.util.icol;

import java.util.Collection;
import java.util.Optional;

public interface GenericPath<E extends Comparable<? super E>,P extends GenericPath<E,P>>
extends IList<E>, Comparable<P>{

  default boolean startsWith(final P other){
    if(other.size()>size()) return false;
    else return subList(0, other.size()).equals(other);
  }
  
  @Override
  P tail();

  Optional<P> parent();

  @Override
  P addElement(E element);

  @Override
  P join(Collection<? extends E> other);
  
  P removePrefix(P prefix);

  @Override
  P subList(final int fromIndex, final int toIndex);
  
}
