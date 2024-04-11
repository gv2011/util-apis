package com.github.gv2011.util.text;

import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.util.Iterator;
import java.util.stream.Stream;

public final class TextUtils {

  private TextUtils(){staticClass();}

  public static <T extends TreeNode<T>> Stream<String> formatTree(final T node){
    return Stream.concat(Stream.of(node.name()), formatTree2("",node));
  }

  private static <T extends TreeNode<T>> Stream<String> formatTree2(
    final String prefix,
    final T node
  ){
    Stream<String> result = Stream.empty();
    final Iterator<T> it = node.children().iterator();
    while(it.hasNext()){
      final T child = it.next();
      result = Stream.concat(result, Stream.of(prefix+"+- "+child.name()));
      final boolean isLastChild = !it.hasNext();
      final String childPrefix = prefix + (isLastChild ? "   " : "|  ");
      result = Stream.concat(result, formatTree2(childPrefix, child));
    }
    return result;
  }

  public static interface TreeNode<T extends TreeNode<T>>{
      Stream<T> children();
      default String name(){return toString();}
  }

/*

root
+- dir1
|  +- dir1.1
|  |  +- dir1.1.1
|  +. dir1.2
|     +- dir1.2.1
+- dir2
   +- dir3


*/

}
