package com.github.gv2011.util.table;

import com.github.gv2011.util.icol.IList;

public interface Table<E> {

  String formatted();

  int columnCount();

  int rowCount();

  int maxWidth(int column);

  default E cell(final int row, final int column) {
    return row(row).get(column);
  }

  IList<E> row(int row);

}
