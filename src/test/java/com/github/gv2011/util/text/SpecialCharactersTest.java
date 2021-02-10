package com.github.gv2011.util.text;

import java.util.Arrays;

import org.junit.Test;

public class SpecialCharactersTest {

  @Test
  public void test() {
    Arrays.stream(SpecialCharacters.values()).forEach(sc->
      System.out.println(sc.name()+" ("+sc.longName()+"):<"+sc+">")
    );
  }

}
