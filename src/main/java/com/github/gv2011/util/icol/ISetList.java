package com.github.gv2011.util.icol;

public interface ISetList<E> extends IList<E> {

  public static interface Builder<E> extends CollectionBuilder<ISetList<E>,E,Builder<E>>{

    @Override
    int size();

    E get(int index);

    E set(int index, E element);

    void insert(int index, E element);

    default E getLast(){return get(size()-1);}

    default E setLast(final E element){return set(size()-1, element);}

  }


  @Override
  ISetList<E> subList(int fromIndex, int toIndex);

  @Override
  default ISetList<E> tail(){
    return subList(1, size());
  }

  @Override
  default ISetList<E> asList() {
    return this;
  }

  @Override
  default ISetList<E> asSetList() {
    return this;
  }

  @Override
  ISetList<E> reversed();

  ISet<E> asSet();
}
