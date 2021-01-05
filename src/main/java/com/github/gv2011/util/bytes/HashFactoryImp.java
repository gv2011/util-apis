package com.github.gv2011.util.bytes;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.HashAlgorithm;
import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.ex.ThrowingSupplier;

public class HashFactoryImp implements HashFactory {

  @Override
  public TypedBytes hash(final HashAlgorithm algorithm, final ThrowingSupplier<InputStream> input){
    return callWithCloseable(input, in->{
      final MessageDigest md = algorithm.createMessageDigest();
      final DigestInputStream dis = new DigestInputStream(in, md);
      StreamUtils.count(dis);
      final Bytes hash = ByteUtils.newBytes(md.digest());
      if(algorithm.equals(Hash256.ALGORITHM)){
        return new Hash256Imp(md);
      }
      else return new AbstractTypedBytes(){
        @Override
        public Bytes content() {return hash;}
        @Override
        public DataType dataType() {return algorithm.getDataType();}
      };
    });
  }

  @Override
  public HashAndSize hash256(ThrowingSupplier<InputStream> in, OutputStream out) {
    final MessageDigest md = Hash256.ALGORITHM.createMessageDigest();
    final DigestOutputStream dos = new DigestOutputStream(out, md);
    final long size = StreamUtils.copy(in, dos);
    call(dos::flush);
    return (HashAndSize) BeanUtils.beanBuilder(HashAndSize.class)
      .set(HashAndSize::size).to(size)
      .set(HashAndSize::hash).to(new Hash256Imp(md))
      .build()
    ;
  }

}
