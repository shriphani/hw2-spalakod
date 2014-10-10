package edu.cmu.lti;

public class Utils {

  /**
   * Returns the number of non whitespace characters in a string
   */
  public static int numNonWhitespace(String text) {
    String noSpace = text.replaceAll("\\s+", "");
    return noSpace.length();
  }
  
  public static String noWhiteSpace(String text) {
    return text.replaceAll("\\s+", "");
  }
  
}
