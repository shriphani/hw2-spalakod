package edu.cmu.lti;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.omg.CORBA.PUBLIC_MEMBER;

import abner.Tagger;

public class AbnerAnnotator extends JCasAnnotator_ImplBase {
  
  private Tagger tagger;
  
  /*
  * Initialize the tagger
  */
 @Override
 public void initialize(org.apache.uima.UimaContext aContext) throws ResourceInitializationException {
   
   tagger = new Tagger(Tagger.NLPBA);
   
 }

  /*
   * Add annotations to the CAS using the Abner NER tagger
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FSIterator iter = aJCas.getAnnotationIndex(Sentence.type).iterator(); iter.hasNext();) {
      Sentence s = (Sentence) iter.next();
      
      String text = s.getText();
      
      String[][] results = tagger.getEntities(text);
      
      for (int i = 0; i < results[0].length; i++) {
        String segment = results[0][i];
        
        String fixedSegment = segment.replace("( ", "(").replace(" )", ")"); // this is needed since Abner emits annotations that differ from the input text
        
        
        String spacelessSegment = Utils.noWhiteSpace(fixedSegment);
        String spacelessText = Utils.noWhiteSpace(text);
        
        int annotationBegin = spacelessText.indexOf(spacelessSegment);
        int annotationEnd   = annotationBegin + spacelessSegment.length() - 1;
        
        
        NERAnnotation annotation = new NERAnnotation(aJCas, annotationBegin, annotationEnd);
        annotation.setID(s.getID());
        annotation.setText(text);
        annotation.setNamedEntity(fixedSegment);
        annotation.setConfidence(1);
        
        annotation.addToIndexes();
      }
      
    }
    
  };
  
  
}
