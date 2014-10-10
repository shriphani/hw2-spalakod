package edu.cmu.lti;

/**
 * General utils visible to classes in the namespace.
 * @author spalakod
 *
 */
public class Utils {

  /*
   * the number of whitespace characters in a string 
   */
  public static int numNonWhitespace(String text) {
    String noSpace = text.replaceAll("\\s+", "");
    return noSpace.length();
  }
  
  /*
   * removes all whitespace chars in the string
   */
  public static String noWhiteSpace(String text) {
    return text.replaceAll("\\s+", "");
  }
  
}
