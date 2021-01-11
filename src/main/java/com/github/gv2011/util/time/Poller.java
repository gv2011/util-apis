package com.github.gv2011.util.time;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.Opt;

public interface Poller extends AutoCloseableNt{

  <T> Opt<T> poll(ThrowingSupplier<Opt<T>> operation);

}
