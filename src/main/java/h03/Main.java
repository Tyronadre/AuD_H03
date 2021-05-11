package h03;

import main.java.h03.EnumIndex;

import java.util.Arrays;

public class Main {


  enum Letters{
    A,
    B,
    C,
    d,
    e,
    f
  }

  public static void main(String[] args) {
    Character c = 'c';
    System.out.println("enum test");
    System.out.println(Arrays.toString(Letters.values()));
    var enumIndex = new EnumIndex<Letters>(Letters.class);
    System.out.println(enumIndex.sizeOfAlphabet());
    System.out.println(enumIndex.apply(Letters.f));
  }

}
