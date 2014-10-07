package edu.cmu.lti;

import java.util.Iterator;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.dcoref.CoNLL2011DocumentReader.NamedEntityAnnotation;
/**
 * Uses Stanford CoreNLP POS tagger for annotating
 * @author spalakod
 *
 */
public class POSTaggerNERAnnotator extends JCasAnnotator_ImplBase {

  private PosTagNamedEntityRecognizer ner;
  
  
  /**
   * Set up the POS tagger
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    ner = new PosTagNamedEntityRecognizer();
  }
  
  
  /**
   * Get the annotations for each sentence in the input, annotate and add to the CAS indices
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FSIterator iter = aJCas.getAnnotationIndex(Sentence.type).iterator(); iter.hasNext();) {
      Sentence s = (Sentence)iter.next();
      
      String sentence_text = s.getText();
      Map<Integer, Integer> begin2end = ner.getGeneSpans(sentence_text);
      
      for (Iterator it = begin2end.entrySet().iterator(); it.hasNext();) {
        Map.Entry<Integer, Integer> pairs = (Map.Entry)it.next();
        
        int annotationBegin = numNonWhitespace(sentence_text.substring(0, pairs.getKey()));
        int annotationEnd = annotationBegin + numNonWhitespace(sentence_text.substring(pairs.getKey(), pairs.getValue())) - 1;
        
        NERAnnotation annotation = new NERAnnotation(aJCas, annotationBegin, annotationEnd);
        annotation.setID(s.getID());
        annotation.setText(sentence_text);
        annotation.setNamedEntity(sentence_text.substring(pairs.getKey(), pairs.getValue()));
        
        annotation.addToIndexes();
      }
    }
    
  }
  
  /**
   * Returns the number of non whitespace characters in a string
   */
  public int numNonWhitespace(String text) {
    String noSpace = text.replaceAll("\\s+", "");
    return noSpace.length();
  }

}
