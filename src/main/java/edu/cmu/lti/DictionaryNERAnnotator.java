package edu.cmu.lti;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.aliasi.chunk.Chunk;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;

/**
 * Implementation of a tagger using the Lingpipe Exact Dictionary Method
 * 
 * @author spalakod
 *
 */

public class DictionaryNERAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Load the dictionary from the location specified in the config
   */
  
  private MapDictionary<String> dictionary;
  private String resourceLoc;
  private final String RESOURCE_LOC_PROPERTY = "ResourceLoc";
  private final String ANNOTATION_TYPE = "GENE";
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    resourceLoc = (String)aContext.getConfigParameterValue(RESOURCE_LOC_PROPERTY);
    
    dictionary = new MapDictionary<String>();
    
    BufferedReader rdr; 
    try{
      InputStream is = DictionaryNERAnnotator.class.getClassLoader().getResourceAsStream(resourceLoc);
      rdr = new BufferedReader(new InputStreamReader(is));
      String line;
      
      while ((line = rdr.readLine()) != null) {
        dictionary.addEntry(new DictionaryEntry<String>(line.trim(), ANNOTATION_TYPE));
      }
      
      rdr.close();
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    
  };
  
  /**
   * Go over the sentences list read from the input file
   * and add annotations generated using the Exact Dictionary Chunking tagger.
   */
  
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    ExactDictionaryChunker chunker = new ExactDictionaryChunker(dictionary, 
                                                                IndoEuropeanTokenizerFactory.INSTANCE,
                                                                false,
                                                                false);

    ArrayList<NERAnnotation> toAnnotate = new ArrayList<NERAnnotation>();
    
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
        annotation.setCasProcessorId("lingpipe-dictionary");
        
        toAnnotate.add(annotation);
      }
    }
    
    for (NERAnnotation annotation : toAnnotate) {
      annotation.addToIndexes();
    }
    
  }

}
