package h03;

import java.util.List;

public class PartialMatchLengthUpdateValuesAsAutomaton <T> extends PartialMatchLengthUpdateValues<T>{
  private List<Transition<T>>[] theStates;

  public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> tFunctionToInt, T[] searchString) {
    super(tFunctionToInt);
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
  }
}
