package edu.cmu.lti;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.util.AbstractExternalizable;

/**
 * Implementation of lingpipe statistical annotation
 * @author spalakod
 *
 */
public class LingpipeFirstBestNERAnnotator extends JCasAnnotator_ImplBase {

  private Chunker chunker;
  private final String MODEL_FILE = "ModelFile";
  
  
  /**
   * Read the model file from the specified location in the config
   */
  @Override
  public void initialize(org.apache.uima.UimaContext aContext) throws ResourceInitializationException {
    String modelFilePath = "/" + (String) aContext.getConfigParameterValue(MODEL_FILE); // need the slash in the front
    try {
      chunker = (Chunker)AbstractExternalizable.readResourceObject(modelFilePath);
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    } catch (ClassNotFoundException e) {
      throw new ResourceInitializationException(e);
    }
  };
  
  /**
   * For each sentence, use the trained model to retrieve the gene mentions
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    for (FSIterator iter = aJCas.getAnnotationIndex(Sentence.type).iterator(); iter.hasNext();) {
      Sentence s = (Sentence)iter.next();
      
      String sentence_text = s.getText();
      
      Set<Chunk> chunks = chunker.chunk(sentence_text).chunkSet();
      
      for (Iterator chunkIter = chunks.iterator(); chunkIter.hasNext();) {
        Chunk nextChunk = (Chunk) chunkIter.next();
        
        int chunkStart = nextChunk.start();
        int chunkEnd   = nextChunk.end();
        
        int annotationBegin = Utils.numNonWhitespace(sentence_text.substring(0, chunkStart));
        int annotationEnd   = annotationBegin + Utils.numNonWhitespace(sentence_text.substring(chunkStart, chunkEnd)) - 1;
        
        String chunkText = sentence_text.substring(chunkStart, chunkEnd);
        
        NERAnnotation annotation = new NERAnnotation(aJCas, annotationBegin, annotationEnd);
        annotation.setID(s.getID());
        annotation.setText(sentence_text);
        annotation.setNamedEntity(chunkText);
        annotation.setConfidence(1);
        annotation.setCasProcessorId("lingpipe-hmm");
        
        annotation.addToIndexes();
      }
    }
    
  }
}
