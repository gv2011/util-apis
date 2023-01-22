package com.github.gv2011.util.table;

import static com.github.gv2011.util.CollectionUtils.intStream;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static java.util.stream.Collectors.joining;

import java.util.function.IntFunction;
import java.util.stream.Stream;

import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.icol.IList;

final class TableImp<E> implements Table<E>{

  private final IList<String> columnNames;
  private final IList<IList<E>> rows;

  TableImp(final IList<String> columnNames, final IList<IList<E>> rows) {
    intStream(rows).forEach(i->verify(i, i2->rows.get(i2).size()==columnNames.size()));
    this.columnNames = columnNames;
    this.rows = rows;
  }

  @Override
  public int columnCount() {
    return columnNames.size();
  }

  @Override
  public int rowCount() {
    return rows.size();
  }

  @Override
  public IList<E> row(final int row) {
    return rows.get(row);
  }

  @Override
  public int maxWidth(final int column) {
    return Stream
      .concat(
        Stream.of(columnNames.get(column)),
        rows.stream().map(r->toString(r.get(column)))
      )
      .mapToInt(String::length)
      .max().getAsInt()
    ;
  }

  private String toString(final E element) {
    return element.toString();
  }

  @Override
  public String formatted() {
    final IList<Integer> widths = intStream(columnNames).map(this::maxWidth).boxed().collect(toIList());
    return Stream
      .concat(
        Stream.of(
          formatRow(widths, columnNames::get)
        ),
        rows.stream().map(row->formatRow(widths, c->toString(row.get(c))))
        )
      .collect(joining("\n"))
    ;
  }

  private String formatRow(final IList<Integer> widths, final IntFunction<String> data) {
    final String sep = " ";
    return intStream(columnNames).mapToObj(c->align(data.apply(c), widths.get(c))).collect(joining(sep));
  }

  private String align(final String in, final int width) {
    return StringUtils.alignLeft(in, width);
  }

}
