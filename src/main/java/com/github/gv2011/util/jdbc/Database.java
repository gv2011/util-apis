package com.github.gv2011.util.jdbc;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.beans.BeanType;

public interface Database extends AutoCloseableNt{

  public <B> DbSet<B> createSet(final BeanType<B> beanType);

}
