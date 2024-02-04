package com.github.gv2011.util.jdbc;

import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;
import static com.github.gv2011.util.icol.ICollections.toIList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.CloseHolder;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ServiceLoaderUtils;
import com.github.gv2011.util.ex.ThrowingConsumer;
import com.github.gv2011.util.ex.ThrowingFunction;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.lock.Lock;
import com.github.gv2011.util.table.Table;
import com.github.gv2011.util.table.Tables;

public final class JdbcUtils {

  private JdbcUtils(){staticClass();}

  private static final Constant<DbProvider> DB = Constants.softRefConstant(
      ()->ServiceLoaderUtils.loadService(DbProvider.class)
  );

  public static final DbProvider dbProvider() {return DB.get();}

  public static final Database createDatabase() {
    return dbProvider().createDatabase();
  }

  public static <T> Stream<T> stream(final ResultSet rs, final ThrowingFunction<ResultSet, T> extractor) {
    final Lock lock = Lock.create();
    return lock.call(rs::next)
      ?(
        Stream.iterate(
          lock.call(()->(Opt<T>) Opt.of(extractor.apply(rs))),
          Opt::isPresent,
          h->lock.call(()->rs.next() ? Opt.of(extractor.apply(rs)) : Opt.empty())
        )
        .map(Opt::get)
        .onClose(()->lock.call(rs::close))
      )
      : lock.call(()->{rs.close(); return Stream.empty();})
    ;
  }

  public static <T> T executeQuery(
      final ThrowingSupplier<CloseHolder<Connection>> connectionSource,
      final String sql,
      final ThrowingFunction<ResultSet,T> resultSetHandler
  ){
    return callWithCloseable(connectionSource, cn->{
      try(PreparedStatement stmt = cn.get().prepareStatement(sql)){
        try(ResultSet rs = stmt.executeQuery()){
          return resultSetHandler.apply(rs);
        }
      }
    });
  }


  public static boolean execute(
    final Connection cn,
    final String sql
  ) {
    return callWithCloseable(cn::createStatement, stmt->(Boolean)stmt.execute(sql));
  }

  public static <T> T executeSingle(
    final Connection cn,
    final String sql,
    final ThrowingFunction<ResultSet, T> extractor
  ) {
    final AtomicBoolean done = new AtomicBoolean();
    return executeQuery(
        cn, sql, s->{},
        rs->{
          verify(!done.getAndSet(true));
          return extractor.apply(rs);
        }
      )
      .findAny().orElseThrow()
    ;
  }

  public static <T> Stream<T> executeQuery(
    final Connection cn,
    final String sql,
    final ThrowingFunction<ResultSet, T> extractor
  ) {
    return executeQuery(
      cn, sql, s->{}, extractor
    );
  }

  public static <T> Stream<T> executeQuery(
    final Connection cn,
    final String sql,
    final ThrowingConsumer<PreparedStatement> statementInitializer,
    final ThrowingFunction<ResultSet,T> extractor
  ) {
    return call(()->{
      final PreparedStatement stmt = cn.prepareStatement(sql);
      try{
        statementInitializer.accept(stmt);
        final ResultSet rs = stmt.executeQuery();
        try{
          return JdbcUtils
            .stream(rs, extractor)
            .onClose(()->call(stmt::close))
            .onClose(()->call(cn::close))
          ;
        }
        catch(final Throwable t){
          rs.close(); throw t;
        }
      }
      catch(final Throwable t){
        stmt.close(); throw t;
      }
    });
  }

  public static Table<String> convertToTable(final ResultSet rs) {
    return call(()->{
      final ResultSetMetaData meta = rs.getMetaData();
      final IList<String> columns = JdbcUtils.<ResultSetMetaData,String,String>toList(
        meta,
        ResultSetMetaData::getColumnCount,
        (md,c)->call(()->md.getColumnName(c)),
        n->n
      )
      ;
      final int colCount = columns.size();
      final IList<IList<String>> rows =
        stream(
          rs,
          r->toList(rs, r2->colCount, (r2,c)->call(()->Opt.ofNullable(r2.getString(c)).orElse("")), s->s)
        )
        .collect(toIList())
      ;
      return Tables.createTable(columns, rows);
    });
  }

  private static <S,I,R> IList<R> toList(
    final S sqlContainer,
    final ThrowingFunction<S,Integer> count,
    final BiFunction<S,Integer,I> extractor,
    final Function<I,R> converter
  ) {
    return IntStream.rangeClosed(1, count.apply(sqlContainer))
      .mapToObj(c->extractor.apply(sqlContainer, c))
      .map(converter)
      .collect(toIList());
  }


 }
