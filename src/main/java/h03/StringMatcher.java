package h03;

import java.util.LinkedList;
import java.util.List;

public class StringMatcher <T> {
  private PartialMatchLengthUpdateValues<T> tPartialMatchLengthUpdateValues;

  public StringMatcher(PartialMatchLengthUpdateValues<T> tPartialMatchLengthUpdateValues) {
    this.tPartialMatchLengthUpdateValues = tPartialMatchLengthUpdateValues;
  }

  public List<Integer> findAllMatches(T[] source){
    List<Integer> out = new LinkedList<>();
    int thisState = 0;
    for(int i = 0; i < source.length; i++){
      thisState = tPartialMatchLengthUpdateValues.getPartialMatchLengthUpdate(thisState,source[i]);
      if (thisState == tPartialMatchLengthUpdateValues.getSearchStringLength())
        out.add(i-thisState + 2);
    }
    return out;
  }
}
