package main.java.h03;

import java.util.Arrays;

abstract public class PartialMatchLengthUpdateValues<T> {
  protected FunctionToInt<T> tFunctionToInt;

  public PartialMatchLengthUpdateValues() {
  }

  public PartialMatchLengthUpdateValues(FunctionToInt<T> tFunctionToInt) {
    this.tFunctionToInt = tFunctionToInt;
  }

  abstract public int getPartialMatchLengthUpdate(T t, int num);

  protected int computePartialMathcLengthUpdateValues(T[] searchString) {
    var front = new char[searchString.length - 2];
    var back = new char[searchString.length - 2];
    int k = 0;
    for (int i = 0; i < searchString.length - 1; i++) {
      front[i] = (char) searchString[i];
      System.arraycopy(back, 0, back, 1, i);
      back[0]= (char) searchString[searchString.length-i-1];
      if (Arrays.equals(front, back)) {
        k++;
      }
    }
    return 0;
  }
}
