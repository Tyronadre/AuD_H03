package h03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartialMatchLengthUpdateValuesAsAutomaton <T> extends PartialMatchLengthUpdateValues<T>{
  private List<Transition<T>>[] theStates;

  public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> tFunctionToInt, T[] searchString) {
    super(tFunctionToInt);
    theStates = (List<Transition<T>>[]) new List[searchString.length+1];
    int counter = 0;
    for (int state = 0; state < searchString.length; state++ ){
      List<Transition<T>> singleItemList = new ArrayList<>();
      for (int letterS = 0; letterS <tFunctionToInt.sizeOfAlphabet(); letterS++){
        if (tFunctionToInt.apply(searchString[state]) == letterS){
          List<T> l = new ArrayList<>();
          if (singleItemList.isEmpty())
            singleItemList.add(new Transition<>(state, l));
          l.add(searchString[state]);
        }
      }
      theStates[state] = singleItemList;
    }

    System.out.println("jhey" + Arrays.toString(theStates));
  }

  @Override
  public int getPartialMatchLengthUpdate(int num, T t) {
    return 0;
  }


  static class Transition<T> {
    public final int index;
    public final List<T> list;


    Transition(int index, List<T> list) {
      this.index = index;
      this.list = list;
    }

    @Override
    public String toString() {
      return "Transition{" +
        "index=" + index +
        ", list=" + list +
        '}';
    }
  }
}
