package edu.cmu.lti;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class DictionaryNERAnnotator extends JCasAnnotator_ImplBase {

  /**
   * Load the dictionary into memory
   */
  
  private MapDictionary<String> dictionary;
  
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    dictionary = new MapDictionary<String>();
    
    BufferedReader rdr; 
    try{
      rdr = new BufferedReader(new FileReader("src/main/resources/gene_entities.txt"));
      String line;
      
      while ((line = rdr.readLine()) != null) {
        dictionary.addEntry(new DictionaryEntry<String>(line.trim(), "GENE"));
      }
      
      rdr.close();
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
    
  };
  
  /**
   * Go over the sentences list read from the input file
   * and add annotations independenty.
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
        //annotation.addToIndexes();
      }
    }
    
    for (NERAnnotation annotation : toAnnotate) {
      annotation.addToIndexes();
    }
    
  }

}
