

/* First created by JCasGen Mon Sep 22 21:13:19 EDT 2014 */
package edu.cmu.lti;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Mon Oct 06 21:51:52 EDT 2014
 * XML source: /usr0/home/spalakod/git/hw2-spalakod/src/main/resources/descriptors/deiis_types.xml
 * @generated */
public class NERAnnotation extends Sentence {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NERAnnotation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected NERAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NERAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NERAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NERAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: NamedEntity

  /** getter for NamedEntity - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNamedEntity() {
    if (NERAnnotation_Type.featOkTst && ((NERAnnotation_Type)jcasType).casFeat_NamedEntity == null)
      jcasType.jcas.throwFeatMissing("NamedEntity", "edu.cmu.lti.NERAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NERAnnotation_Type)jcasType).casFeatCode_NamedEntity);}
    
  /** setter for NamedEntity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNamedEntity(String v) {
    if (NERAnnotation_Type.featOkTst && ((NERAnnotation_Type)jcasType).casFeat_NamedEntity == null)
      jcasType.jcas.throwFeatMissing("NamedEntity", "edu.cmu.lti.NERAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((NERAnnotation_Type)jcasType).casFeatCode_NamedEntity, v);}    
  }

    