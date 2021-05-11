package main.java.h03;

import java.util.List;

public class SelectionOfCharsIndex implements FunctionToInt<Character> {
  private char[] theChars;

  public SelectionOfCharsIndex(List<Character> theAlphabet) {
    theChars = new char[theAlphabet.size()];
    int i = 0;
    for (char c : theAlphabet){
      theChars[c] = theAlphabet.get(0);
    }
  }

  @Override
  public int sizeOfAlphabet() {
    return theChars.length;
  }

  @Override
  public <T> int apply(T t) throws IllegalArgumentException {
    for (int i = 0; i < theChars.length; i++){
      if (t.equals(theChars[i]))
        return i;
    }
    throw new IllegalArgumentException();
  }
}
