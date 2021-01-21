package com.github.gv2011.util.icol;

import com.github.gv2011.util.UrlEncoding;

public interface Path extends GenericPath<String,Path>{

    static interface Builder extends CollectionBuilder<Path,String,Builder>{}
    
    default String urlEncoded(){
      return UrlEncoding.encodePath(this);
    }

}
