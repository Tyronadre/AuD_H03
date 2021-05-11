package main.java.h03;

public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {
  private int[][] matrix;

  public PartialMatchLengthUpdateValuesAsMatrix(T[] what_is_this, FunctionToInt<T> searchString) {

  }



  @Override
  public int getPartialMatchLengthUpdate(T t, int num) {
    return matrix[tFunctionToInt.apply(t)][num];
  }
}
