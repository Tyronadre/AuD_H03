package h03;

import java.util.Arrays;

abstract public class PartialMatchLengthUpdateValues<T> {
  protected FunctionToInt<T> tFunctionToInt;
  protected T[] searchString;

  public PartialMatchLengthUpdateValues(FunctionToInt<T> tFunctionToInt) {
    this.tFunctionToInt = tFunctionToInt;
  }

  abstract public int getPartialMatchLengthUpdate(int num, T t);

  abstract public int getSearchStringLength();

  protected int computePartialMatchLengthUpdateValues(T[] searchString) {
    if (searchString.length == 0)
      return 0;
    var front = new Object[searchString.length - 1];
    var back = new Object[searchString.length - 1];
    int k = 0;
    for (int i = 0; i < searchString.length - 1; i++) {
      front[i] = searchString[i];
      System.arraycopy(back, 0, back, 1, i);
      back[0]= searchString[searchString.length-i-1];
      if (Arrays.equals(front, back)) {
        k = i + 1;
      }
    }
    return k;
  }
}
