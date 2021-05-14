package h03;

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
//    h5(new Character[]{'a','b','a','b','a','b'});
//    h5(new Character[]{'a','b','a','b'});
//    h5(new Character[]{'a','b','a'});
//    h5(new Character[]{'a','b','b','b','a'});
//    h5(new Character[]{'e','f','c','d','c','f','e'});
//    h5(new Character[]{'a','b'});
  }

  public static void h3(){
    Character c = 'c';
    System.out.println("enum test");
    System.out.println(Arrays.toString(Letters.values()));
    var enumIndex = new EnumIndex<Letters>(Letters.class);
    System.out.println(enumIndex.sizeOfAlphabet());
    System.out.println(enumIndex.apply(Letters.f));
  }
  public static void t1(){
    var alpharr = new Character[]{'A', 'B', 'C', 'D'};
    var inter1 = new UnicodeNumberOfCharIndex();
    var inter2 = new SelectionOfCharsIndex(Arrays.asList(alpharr));
    System.out.println(inter1 + " "+ inter1.sizeOfAlphabet() + " " + inter1.apply('a'));
    System.out.println(inter2 + " " + inter2.sizeOfAlphabet() + "  " + inter2.apply('A'));
  }

//  public static void h5(Character[] searchword, FunctionToInt<Character> s){
//    var t1 = new PartialMatchLengthUpdateValuesAsMatrix<>(new Character[]{'a', 'b', 'c', 'd','e','f','g'},searchword);
//    System.out.println(Arrays.toString(searchword));
//    System.out.print(t1);
//    System.out.println(", pointer=" + t1.computePartialMatchLengthUpdateValues(searchword));
//    System.out.println(t1.getPartialMatchLengthUpdate(0, 'a'));
//  }
}
