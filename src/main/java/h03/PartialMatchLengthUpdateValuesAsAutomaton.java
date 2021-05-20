package h03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartialMatchLengthUpdateValuesAsAutomaton<T> extends PartialMatchLengthUpdateValues<T> {
  private List<Transition<T>>[] theStates;

  public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> tFunctionToInt, T[] searchString) {
    super(tFunctionToInt);
    this.searchString = searchString;
    theStates = (List<Transition<T>>[]) new List[searchString.length + 1];
    int pointer = computePartialMatchLengthUpdateValues(searchString);

    for (int state = 0; state < searchString.length; state++) {
      int innerpointer = computePartialMatchLengthUpdateValues(Arrays.copyOfRange(searchString,0,state));
      List<Transition<T>> transitionList = new ArrayList<>();
      transitionList.add(new Transition<>(state + 1, List.of(searchString[state])));
      if (innerpointer != 0  && tFunctionToInt.apply(searchString[innerpointer]) != tFunctionToInt.apply(searchString[state]))
        transitionList.add(new Transition<>(innerpointer + 1, List.of(searchString[innerpointer])));
      if (tFunctionToInt.apply(searchString[0]) != tFunctionToInt.apply(searchString[state])) {
        transitionList.add(new Transition<>(1, List.of(searchString[0])));
      }
      theStates[state] = transitionList;
    }

    //Letzter Zustand
    List<Transition<T>> singleItemList = new ArrayList<>();

    singleItemList.add(new Transition<>(pointer + 1, List.of(searchString[pointer])));
    if (searchString[0] != searchString[pointer])
      singleItemList.add(new Transition<>(1,List.of(searchString[0])));
    theStates[theStates.length - 1] = singleItemList;


      System.out.println("searchString: " + Arrays.toString(searchString) + ", Pointer="+pointer);
    for (var t :theStates
         ) {
      System.out.println(t);
    }
  }

  @Override
  public int getPartialMatchLengthUpdate(int num, T t) {

    for (var transition : theStates[num])
      if (transition.list.contains(t)) {
        return transition.index;
      }

    return 0;
  }

  @Override
  public int getSearchStringLength() {
    return searchString.length;
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
