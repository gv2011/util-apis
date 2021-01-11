package com.github.gv2011.util.sec;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.icol.ICollections.asList;

import java.security.KeyStore;

final class KeyStoreDestroyer implements DestroyingCloseable {

  private KeyStore keyStore;

  KeyStoreDestroyer(KeyStore keyStore) {
    this.keyStore = keyStore;
  }

  @Override
  public void destroy() {
    asList(call(keyStore::aliases)).forEach(a->call(()->keyStore.deleteEntry(a)));
  }

  @Override
  public boolean isDestroyed() {
    return !call(keyStore::aliases).hasMoreElements();
  }

}
