package main.java.h03;

public class UnicodeNumberOfCharIndex implements FunctionToInt<Character>{
  @Override
  public int sizeOfAlphabet() {
    return Character.MAX_CODE_POINT;
  }

  @Override
  public <T> int apply(T t) throws IllegalArgumentException {
    return (int) t;
  }
}
