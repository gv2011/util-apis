package com.github.gv2011.util.table;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import com.github.gv2011.util.icol.IList;

public final class Tables {

  private Tables(){staticClass();}


  public static <E> Table<E> createTable(final IList<String> columnNames, final IList<IList<E>> rows) {
     return new TableImp<E>(columnNames, rows);
  }


 }
