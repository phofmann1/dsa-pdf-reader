package de.pho.dsapdfreader.tools.roman;

import java.util.HashMap;
import java.util.Map;

public class RomanNumberHelper
{

  public static int romanToInt(String s)
  {

    Map<Character, Integer> map = new HashMap<Character, Integer>();
    //adding elements to the Map
    map.put('I', 1);
    map.put('V', 5);
    map.put('X', 10);
    map.put('L', 50);
    map.put('C', 100);
    map.put('D', 500);
    map.put('M', 1000);
    s = s.replace("IV", "IIII");
    s = s.replace("IX", "VIIII");
    s = s.replace("XL", "XXXX");
    s = s.replace("XC", "LXXXX");
    s = s.replace("CD", "CCCC");
    s = s.replace("CM", "DCCCC");
    int number = 0;
    //loop iterates over the roman numeral
    for (int i = 0; i < s.length(); i++)
    {
      //getting each character of roman numeral and adding it to the variable number
      number = number + (map.get(s.charAt(i)));
    }
    //prints the corresponding integer value
    return number;
  }

  public static String intToRoman(int num)
  {
    int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    StringBuilder roman = new StringBuilder();
    for (int i = 0; i < values.length; i++)
    {
      while (num >= values[i])
      {
        num = num - values[i];
        roman.append(romanLetters[i]);
      }
    }
    return roman.toString();
  }
}
