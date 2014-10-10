package edu.cmu.lti;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import edu.cmu.lti.Sentence;

/**
 * Takes the document text, generate Sentences and add them to the CAS.
 * @author spalakod
 *
 */

public class SentenceAnnotator extends JCasAnnotator_ImplBase implements AnalysisComponent {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    
    String documentText = aJCas.getDocumentText();
    String lines[] = documentText.split("\\r?\\n");
    
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      int firstSpace = line.indexOf(' ');
      
      // line has no spaces
      if (firstSpace < 0) {
        continue;
      }
      
      String sentence_id   = line.substring(0, firstSpace);
      String sentence_text = line.substring(firstSpace + 1);
      
      // id is supposed to be P followed by a hex string
      if (sentence_id.indexOf('|') >= 0){
        continue;
      }
      
      // finally add the annotation
      Sentence annotation = new Sentence(aJCas,firstSpace + 1, line.length());
      annotation.setID(sentence_id);
      annotation.setText(sentence_text);
      annotation.addToIndexes();
    }
  }
}
