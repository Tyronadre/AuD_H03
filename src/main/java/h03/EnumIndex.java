package main.java.h03;

public class EnumIndex<T extends Enum<T>> implements FunctionToInt<T> {
  Class<T> enumClass;

  public EnumIndex(Class<T> enumClass){
    this.enumClass = enumClass;
  }

  @Override
  public int sizeOfAlphabet() {
    return enumClass.getEnumConstants().length;
  }

  @Override
  public <T1> int apply(T1 t1) throws IllegalArgumentException {
    int i = 0;
    for (T t: enumClass.getEnumConstants()){
      if (t.equals(t1)){
        return i;
      }
      i++;
    }
    return -1;
  }
}
