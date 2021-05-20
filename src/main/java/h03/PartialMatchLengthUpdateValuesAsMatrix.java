package h03;

import java.util.Arrays;

public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {
  private final int[][] matrix;

  public PartialMatchLengthUpdateValuesAsMatrix(FunctionToInt<T> functionToInt, T[] searchString) {
    super(functionToInt);
    this.searchString = searchString;
    int pointer = computePartialMatchLengthUpdateValues(searchString);
    matrix = new int[searchString.length + 1][tFunctionToInt.sizeOfAlphabet()];
    for (int state = 0; state < searchString.length; state++) {
      int innerPointer = computePartialMatchLengthUpdateValues(Arrays.copyOfRange(searchString, 0, state));
      for (int letterS = 0; letterS < tFunctionToInt.sizeOfAlphabet(); letterS++) {
        //Matrix machen
        if (tFunctionToInt.apply(searchString[state]) == letterS) {
          matrix[state][letterS] = state + 1;
        } else if (state >= pointer && searchString[0] == searchString[pointer] && tFunctionToInt.apply(searchString[state - pointer]) == letterS) {
          matrix[state][letterS] = state - pointer;
        } else if (letterS == tFunctionToInt.apply(searchString[0]) && matrix[state][letterS] == 0)
          matrix[state][letterS] = 1;
        else if (state > 1 && innerPointer > 0 && tFunctionToInt.apply(searchString[innerPointer]) == letterS) {
            matrix[state][letterS]  = innerPointer+1;
        } else
          matrix[state][letterS] = 0;
      }
    }

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

      System.out.println(counter + "\t" + Arrays.toString(i));
      counter++;
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
    System.out.println("Jump into: " + matrix[num][tFunctionToInt.apply(t)] + " for: " + num + " and " + t);
    return matrix[num][tFunctionToInt.apply(t)];
  }

  @Override
  public int getSearchStringLength() {
    return searchString.length;
  }
}
