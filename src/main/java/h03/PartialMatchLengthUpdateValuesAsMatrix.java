package h03;

import java.util.Arrays;

public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {
  private final int[][] matrix;

  public PartialMatchLengthUpdateValuesAsMatrix(FunctionToInt<T> functionToInt, T[] searchString) {
    super(functionToInt);
    matrix = new int[searchString.length + 1][tFunctionToInt.sizeOfAlphabet()];
    for (int state = 0; state < searchString.length; state++)
      for (int letterS = 0; letterS < tFunctionToInt.sizeOfAlphabet(); letterS++) {
        //Matrix machen
        if (tFunctionToInt.apply(searchString[state]) == letterS)
          matrix[state][letterS] = state + 1;
        else if (tFunctionToInt.apply((searchString[0])) == letterS)
          matrix[state][tFunctionToInt.apply(searchString[0])] = 1;
        else
          matrix[state][letterS] = 0;
      }
    int pointer = computePartialMatchLengthUpdateValues(searchString);
    //Wiederholungen beachten
    if (pointer > 0) {
      matrix[searchString.length][tFunctionToInt.apply(searchString[pointer])] = pointer + 1;
    }
    if (matrix[searchString.length][tFunctionToInt.apply(searchString[0])] == 0)
      matrix[searchString.length][tFunctionToInt.apply(searchString[0])] = 1;


    System.out.println("{ AlphLength=" + tFunctionToInt.sizeOfAlphabet() + ", Searchstring=" + Arrays.toString(searchString) + ", Pointer=" + pointer + " }");
    System.out.println("al:\t" + Arrays.toString(new Character[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'}));
    int counter = 0;
    for (int[] i : matrix) {
      counter++;
      System.out.println(counter + "\t" + Arrays.toString(i));
    }
  }

  @Override
  public String toString() {
    return "PartialMatchLengthUpdateValuesAsMatrix{" +
      "tFunctionToInt=" + tFunctionToInt +
      ", matrix=" + Arrays.deepToString(matrix) +
      '}';
  }

  @Override
  public int getPartialMatchLengthUpdate(int num, T t) {
    return matrix[num][tFunctionToInt.apply(t)];
  }
}
