package h03;

import java.util.Arrays;
import java.util.List;

public class SelectionOfCharsIndex implements FunctionToInt<Character> {
  private final char[] theChars;

  public SelectionOfCharsIndex(List<Character> theAlphabet) {
    theChars = new char[theAlphabet.size()];
    int i = 0;
    for (char c : theAlphabet){
      theChars[i] = c;
      i++;
    }
  }

  @Override
  public int sizeOfAlphabet() {
    return theChars.length;
  }

  @Override
  public int apply(Character character) throws IllegalArgumentException {
    for (int i = 0; i < theChars.length; i++){
      if (character.equals(theChars[i]))
        return i;
    }
    throw new IllegalArgumentException();
  }

  @Override
  public String toString() {
    return "SelectionOfCharsIndex{" +
      "theChars=" + Arrays.toString(theChars) +
      '}';
  }
}
