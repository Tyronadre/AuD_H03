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
    int counter = 0;
    for (int state = 0; state < searchString.length; state++) {
      List<Transition<T>> singleItemList = new ArrayList<>();
      for (int letterS = 0; letterS < tFunctionToInt.sizeOfAlphabet(); letterS++) {
        if (tFunctionToInt.apply(searchString[state]) == letterS) {
          List<T> l = new ArrayList<>();
          if (singleItemList.isEmpty())
            singleItemList.add(new Transition<>(state + 1, l));
          l.add(searchString[state]);
        }
      }
      theStates[state] = singleItemList;
    }
    //Letzter Zustand
    List<Transition<T>> singleItemList = new ArrayList<>();
    List<T> l = new ArrayList<>();
    int pointer = computePartialMatchLengthUpdateValues(searchString);
    l.add(searchString[pointer]);
    singleItemList.add(new Transition<>(pointer + 1, l));
    theStates[theStates.length - 1] = singleItemList;
  }

  @Override
  public int getPartialMatchLengthUpdate(int num, T t) {

    for (var transition : theStates[num])
      if (transition.list.contains(t)) {
        return transition.index;
      }

    return -1;
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
