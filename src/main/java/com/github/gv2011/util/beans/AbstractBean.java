package com.github.gv2011.util.beans;

import static com.github.gv2011.util.Verify.verify;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;

public abstract class AbstractBean<B extends Bean> implements Bean{

  protected static <B extends Bean> ClassCache createClassCache(final Class<B> clazz){
    return new ClassCache(clazz);
  }

  protected abstract Class<B> clazz();

  protected abstract B self();

  protected abstract ClassCache classCache();

  private final ClassCache checkedClassCache(){
    return verify(classCache(), c->c.clazz==clazz());
  }

  private final Constant<InstanceInfo> instanceInfo = Constants.softRefConstant(InstanceInfo::new);

  @Override
  public final int hashCode() {
    return instanceInfo.get().hash.get();
  }

  @Override
  public final boolean equals(final Object obj) {
    return checkedClassCache().equal(self(), obj);
  }

  @Override
  public String toString() {
    return instanceInfo.get().string.get();
  }

  @SuppressWarnings({"rawtypes","unchecked"}) //avoid outside visibility of generics
  public static final class ClassCache{
    private final Class clazz;
    private final Constant<BeanType> beanType;

    private ClassCache(final Class clazz) {
      this.clazz = clazz;
      beanType = Constants.softRefConstant(()->BeanUtils.typeRegistry().beanType(clazz));
    }

    private boolean equal(final Bean bean, final Object other) {
      return beanType.get().equal(bean, other);
    }

    private int hash(final Bean bean) {
      return beanType.get().hashCode(bean);
    }

    private String toString(final Bean bean) {
      return beanType.get().toString(bean);
    }
  }

  private final class InstanceInfo{
    private final Constant<Integer> hash = Constants.cachedConstant(()->checkedClassCache().hash(self()));
    private final Constant<String> string = Constants.cachedConstant(()->checkedClassCache().toString(self()));
  }

}
