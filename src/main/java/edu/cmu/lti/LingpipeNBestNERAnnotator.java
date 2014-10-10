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
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.NBestChunker;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.ScoredObject;

/**
 * Implementation of lingpipe statistical annotation.
 * A HMM model trained on the Genetag corpus is used. In addition to the top
 * ranked hypothesis, a few other hypotheses are included.
 * @author spalakod
 *
 */
public class LingpipeNBestNERAnnotator extends JCasAnnotator_ImplBase {

  private NBestChunker chunker;
  private final String MODEL_FILE = "ModelFile";
  
  
  /**
   * Read the model file from the specified location in the config
   */
  @Override
  public void initialize(org.apache.uima.UimaContext aContext) throws ResourceInitializationException {
    String modelFilePath = (String) aContext.getConfigParameterValue(MODEL_FILE);
    File modelFile = new File(modelFilePath);
    try {
      chunker = (NBestChunker)AbstractExternalizable.readObject(modelFile);
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
      
      Iterator<ScoredObject<Chunking>> innerIter = chunker.nBest(sentence_text.toCharArray(), 0, sentence_text.length(), 1);
      
      for (Iterator<ScoredObject<Chunking>> chunkIter = innerIter; chunkIter.hasNext();) {
        ScoredObject scoredObject = chunkIter.next();
        Chunking chunking = (Chunking)scoredObject.getObject();
        
        for (Iterator it = chunking.chunkSet().iterator(); it.hasNext();) {
         
          Chunk nextChunk = (Chunk)it.next();
          
          int chunkStart = nextChunk.start();
          int chunkEnd   = nextChunk.end();
          
          int annotationBegin = numNonWhitespace(sentence_text.substring(0, chunkStart));
          int annotationEnd   = annotationBegin + numNonWhitespace(sentence_text.substring(chunkStart, chunkEnd)) - 1;
          
          String chunkText = sentence_text.substring(chunkStart, chunkEnd);
          
          NERAnnotation annotation = new NERAnnotation(aJCas, annotationBegin, annotationEnd);
          annotation.setID(s.getID());
          annotation.setText(sentence_text);
          annotation.setNamedEntity(chunkText);
          annotation.setConfidence(scoredObject.score());
          
          System.out.println(scoredObject.score());
          
          annotation.addToIndexes();
        }
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
