package h03;

public class PartialMatchLengthUpdateValuesAsAutomaton<T> extends PartialMatchLengthUpdateValues<T>{
  
  public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> tFunctionToInt) {
    super(tFunctionToInt);
  }

  @Override
  public int getPartialMatchLengthUpdate(int num, T t) {
    return 0;
  }
}
