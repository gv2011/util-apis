package com.github.gv2011.util.icol;

import java.util.Collection;

public interface ICollectionG<E,C extends ICollectionG<E,C>> extends ICollection<E>{

  C join(Collection<? extends E> other);

  C addElement(E other);

  C subtract(Collection<?> other);

  ISet<E> intersection(Collection<?> other);

}
