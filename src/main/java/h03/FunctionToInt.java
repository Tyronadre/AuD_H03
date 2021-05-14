package h03;

public interface FunctionToInt <T> {
  int sizeOfAlphabet();


  /**
   * Gibt eine nicht negative Zahl echt kleiner als {@code sizeOfAlphabet()} zurück
   * @param t t
   * @param <T> T
   * @return Integer
   * @throws IllegalArgumentException Exception
   */
  int apply (T t) throws IllegalArgumentException;

}
