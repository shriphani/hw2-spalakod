package edu.cmu.lti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

public class NERAnnotationPrinter extends CasConsumer_ImplBase {

  private PrintWriter out_handle;
  private static final String OUTPUT_FILE = "OutputFile";
  
  /*
   * Retrieve the specified output file from the config
   * 
   */
  @Override
  public void initialize() throws ResourceInitializationException {
    File output_file = new File((String)getConfigParameterValue(OUTPUT_FILE));
    
    try {
      out_handle = new PrintWriter(output_file);
    } catch (FileNotFoundException e) {
      throw new ResourceInitializationException(e);
    }
  };
  
  /**
   * For each annotation in the index, print it out in the desired format
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {
    
    JCas jCas;
    
    try {
      jCas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    
    for (FSIterator iter = jCas.getAnnotationIndex(NERAnnotation.type).iterator(); iter.hasNext();) {
      NERAnnotation nerAnnotation = (NERAnnotation)iter.next();
      out_handle.println(nerAnnotation.getID() + "|" + nerAnnotation.getBegin() + " " + nerAnnotation.getEnd() + "|" + nerAnnotation.getNamedEntity());
    }
    
    out_handle.close();
  }  
}
