package com.github.gv2011.util.serviceloader;

import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.setFrom;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.CachedConstant;
import com.github.gv2011.util.Constant;
import com.github.gv2011.util.Constants;
import com.github.gv2011.util.ann.GuardedBy;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingSupplier;
import com.github.gv2011.util.icol.ICollectionFactory;
import com.github.gv2011.util.icol.ICollectionFactorySupplier;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.log.LogAdapter;


public final class RecursiveServiceLoader implements AutoCloseableNt{

  static final String FILE_WATCH_SERVICE = "com.github.gv2011.util.filewatch.FileWatchService";
  static final String DEFAULT_FILE_WATCH_SERVICE = "com.github.gv2011.util.filewatch.DefaultFileWatchService";

  static final String DATA_TYPE_PROVIDER = "com.github.gv2011.util.bytes.DataTypeProvider";
  static final String DEFAULT_DATA_TYPE_PROVIDER = "com.github.gv2011.util.bytes.DefaultDataTypeProvider";

  static final String CLOCK = "com.github.gv2011.util.time.Clock";
  static final String DEFAULT_CLOCK = "com.github.gv2011.util.time.DefaultClock";

  static final String LOCK_FACTORY = "com.github.gv2011.util.lock.Lock$Factory";
  static final String DEFAULT_LOCK_FACTORY = "com.github.gv2011.util.lock.DefaultLockFactory";

  static final String DOWNLOADER_FACTORY = "com.github.gv2011.util.download.DownloadTask$Factory";
  static final String DEFAULT_DOWNLOADER_FACTORY = "com.github.gv2011.util.download.imp.DefaultDownloadTaskFactory";

  static final String UNICODE_PROVIDER = "com.github.gv2011.util.uc.UnicodeProvider";
  static final String DEFAULT_UNICODE_PROVIDER = "com.github.gv2011.util.uc.JdkUnicodeProvider";

  static final String EMAIL_PROVIDER = "com.github.gv2011.util.email.MailProvider";
  static final String DEFAULT_EMAIL_PROVIDER = "com.github.gv2011.util.email.imp.DefaultMailProvider";


  private static final Map<String,String> DEFAULT_SERVICES =
    Collections.unmodifiableMap(
      Arrays.stream(new String[][]{
        new String[]{FILE_WATCH_SERVICE, DEFAULT_FILE_WATCH_SERVICE},
        new String[]{DATA_TYPE_PROVIDER, DEFAULT_DATA_TYPE_PROVIDER},
        new String[]{CLOCK, DEFAULT_CLOCK},
        new String[]{LOCK_FACTORY, DEFAULT_LOCK_FACTORY},
        new String[]{DOWNLOADER_FACTORY, DEFAULT_DOWNLOADER_FACTORY},
        new String[]{UNICODE_PROVIDER, DEFAULT_UNICODE_PROVIDER}
      })
      .collect(Collectors.toMap(e->e[0], e->e[1]))
    )
  ;

  private static final CachedConstant<RecursiveServiceLoader> INSTANCE =
    Constants.cachedConstant(()->{
      final RecursiveServiceLoader loader = new RecursiveServiceLoader();
      Runtime.getRuntime().addShutdownHook(new Thread(
        loader::close,
        RecursiveServiceLoader.class.getSimpleName()+"-shutdown"
      ));
      return loader;
    })
  ;

  public static final <S> S service(final Class<S> serviceClass) {
    return INSTANCE.get().getService(serviceClass);
  }

  public static final <S> Constant<S> lazyService(final Class<S> serviceClass) {
    return Constants.cachedConstant(()->service(serviceClass));
  }

  public static final <S> Constant<S> lazyService(final Class<S> serviceClass, final ThrowingSupplier<S> fallback) {
    return Constants.cachedConstant(()->{
      final Opt<S> tryGetService = tryGetService(serviceClass);
      return tryGetService.orElseGet(fallback);
      });
  }

  public static final <S> Opt<S> tryGetService(final Class<S> serviceClass) {
    return INSTANCE.get().tryGetServiceInternal(serviceClass);
  }

  public static final <S> ISet<S> services(final Class<S> serviceClass) {
    return setFrom(INSTANCE.get().getAllServices(serviceClass));
  }

  public static AutoCloseableNt externallyClosedInstance(){
    RecursiveServiceLoader loader;
    try {
      loader = new RecursiveServiceLoader();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
    INSTANCE.set(loader);
    return loader;
  }

  private final Object lock = new Object();
  private final Map<Class<?>,Set<?>> services = new HashMap<>();
  private final Set<Class<?>> loading = new HashSet<>();
  private final List<AutoCloseable> closeableServices = new ArrayList<>();
  private final ICollectionFactory iCollections;
  private final @Nullable LogAdapter logAdapter;

  @GuardedBy("lock")
  private boolean closing = false;

  private volatile @Nullable Logger logger = null;

  private RecursiveServiceLoader() throws Exception {
    final ICollectionFactorySupplier iCollectionFactorySupplier = loadBasicService(ICollectionFactorySupplier.class, true);
    iCollections = iCollectionFactorySupplier.get();
    services.put(
      ICollectionFactorySupplier.class,
      Collections.singleton(iCollectionFactorySupplier)
    );
    logAdapter = loadBasicService(LogAdapter.class, false);
    if(logAdapter!=null){
      services.put(LogAdapter.class, Collections.singleton(logAdapter));
    }
    else{
      services.put(LogAdapter.class, Collections.emptySet());
    }
  }

  private static <S> @Nullable S loadBasicService(
    final Class<S> serviceClass, final boolean required
  ) throws Exception {
    final Set<String> implementationClassNames =
      ServiceProviderConfigurationFile.filesInternal(serviceClass)
      .flatMap(ServiceProviderConfigurationFile::implementationsInternal)
      .collect(Collectors.toSet())
    ;
    if(implementationClassNames.isEmpty()){
      if(required){
        throw new IllegalStateException(format("No implementation for {} found.", serviceClass));
      }
      return null;
    }
    else{
      if(implementationClassNames.size()>1){throw new IllegalStateException(
        format("Multiple implementations for service {}: {}.", serviceClass, implementationClassNames)
      );}
      return
        Class.forName(implementationClassNames.iterator().next())
        .asSubclass(serviceClass)
        .getConstructor()
        .newInstance()
      ;
    }
  }

  private <S> S getService(final Class<S> serviceClass) {
    return
      tryGetServiceInternal(serviceClass)
      .orElseThrow(()->new IllegalStateException(format("No implementation for {} found.", serviceClass)))
    ;
  }

  private <S> Opt<S> tryGetServiceInternal(final Class<S> serviceClass) {
    final Set<S> services = getAllServices(serviceClass);
    if(services.isEmpty()) return iCollections.empty();
    else{
      verify(services, s->s.size()==1, s->format("Multiple implementations for service {}: {}.", serviceClass, s));
      return iCollections.single(services.iterator().next());
    }
  }

  @SuppressWarnings("unchecked")
  private <S> Set<S> getAllServices(final Class<S> serviceClass) {
    synchronized(lock) {
      verify(!closing);
      Opt<Set<?>> entry = iCollections.ofNullable(services.get(serviceClass));
      if(!entry.isPresent()) {
        final boolean added = loading.add(serviceClass);
        try{
          if(!added){
            throw new RuntimeException(format("Infinite recursion: already loading {}.", serviceClass.getName()));
          }
          loadServices(serviceClass);
          entry = iCollections.single(services.get(serviceClass));
        }
        finally{loading.remove(serviceClass);}
      }
      return (Set<S>) entry.get();
    }
  }

  private <S> void loadServices(final Class<S> serviceClass) {
    final Set<S> implementations = Collections.unmodifiableSet(
      Stream.of(
        call(()->ServiceProviderConfigurationFile.filesInternal(serviceClass))
        .flatMap(f->f.implementationsInternal())
        .collect(toSet())
      )
      .flatMap(s->s.isEmpty()
        ? Optional.ofNullable(DEFAULT_SERVICES.get(serviceClass.getName())).stream()
        : s.stream()
      )
      .map(n->createInstance(serviceClass, n))
      .collect(toSet())
    );
    synchronized(lock){
      services.put(serviceClass, implementations);
    }
  }

  private <S> S createInstance(final Class<S> serviceClass, final String implementationClassName) {
    final Class<?> implClass = call(()->Class.forName(implementationClassName));
    verify(serviceClass.isAssignableFrom(implClass));
    final Constructor<?> constr = Arrays.stream(implClass.getConstructors())
      .filter(c->c.getParameterCount()==0)
      .findAny()
      .orElseThrow(()->new NoSuchElementException(format("{} has no no-arg constructor.", implClass)))
    ;
    final Class<?>[] pTypes = constr.getParameterTypes();
    final Object[] initargs = new Object[pTypes.length];
    for(int i=0; i<initargs.length; i++) {
      initargs[i] = getService(pTypes[i]);
    }
    final S serviceInstance = serviceClass.cast(call(()->constr.newInstance(initargs)));
    if(serviceInstance instanceof AutoCloseable){
      synchronized(lock){
        verify(!closing);
        closeableServices.add((AutoCloseable) serviceInstance);
      }
      getLogger().info(
        "Loaded closeable service {} - implementation class: {}.", serviceClass.getName(), implClass.getName()
      );
    }
    else{
      getLogger().info(
        "Loaded service {} - implementation class: {}.", serviceClass.getName(), implClass.getName()
      );
    }
    return serviceInstance;
  }

  @Override
  public void close() {
    final boolean doClose;
    final Logger logger = getLogger();
    synchronized(lock){
      doClose = !closing;
      closing = true;
    }
    if(doClose){
      logger.info("Closing.");
      Opt<AutoCloseable> toClose = getLast();
      while(notNull(toClose).isPresent()){
        final AutoCloseable service = toClose.get();
        try {
          logger.debug("Closing service {}", service);
          service.close();
          logger.debug("Closed service {}", service);
        } catch (final Exception e) {
          logger.error(format("Could not close {}.", service), e);
        }
        synchronized(lock){
          closeableServices.remove(service);
        }
        toClose = getLast();
      }
      if(logAdapter!=null){
        logger.warn("Closing logging as last action - goodbye.");
        logAdapter.close();
      }
      else logger.info("Closed - goodbye.");
    }
  }

  private Logger getLogger() {
    Logger logger = this.logger;
    if(logger==null){
      logger = LoggerFactory.getLogger(RecursiveServiceLoader.class);
      this.logger = logger;
    }
    return logger;
  }

  private Opt<AutoCloseable> getLast() {
    synchronized(lock){
      return
        closeableServices.isEmpty()
        ? iCollections.empty()
        : iCollections.single(closeableServices.get(closeableServices.size()-1))
      ;
    }
  }


}
