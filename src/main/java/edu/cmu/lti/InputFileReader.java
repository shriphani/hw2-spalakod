package edu.cmu.lti;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

public class InputFileReader extends CollectionReader_ImplBase {

  private static final String INPUT_FILE = "InputFile";
  private File input_file;
  private boolean read_file = false;
  
  /**
   * Get the specified doc
   */
  @Override
  public void initialize() throws ResourceInitializationException {
    input_file = new File((String)getConfigParameterValue(INPUT_FILE));
  };
  
  /**
   * Load the specified doc and set up the CAS
   */
  @Override
  public void getNext(CAS aCas) throws IOException, CollectionException {
    
    JCas jCas;
    
    try {
      jCas = aCas.getJCas();
    } catch (CASException e) {
      throw new CollectionException();
    }
    
    BufferedInputStream fis = new BufferedInputStream(new FileInputStream(input_file));
    byte[] contents = new byte[(int) input_file.length()];
    
    fis.read(contents);
    fis.close();
    
    jCas.setDocumentText(new String(contents));
    
    SourceDocumentInformation srcDocInfo = new SourceDocumentInformation(jCas);
    srcDocInfo.setUri(input_file.getAbsoluteFile().toURI().toString());
    srcDocInfo.setOffsetInSource(0);
    srcDocInfo.setDocumentSize((int)input_file.length());
    srcDocInfo.setLastSegment(true);
    srcDocInfo.addToIndexes();
    
    read_file = true;
  }

  /**
   * Closing is handled in the getNext routine
   */
  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  /**
   * Not implemented since we are reading a single file
   */
  @Override
  public Progress[] getProgress() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Has the specified file been read?
   */
  @Override
  public boolean hasNext() throws IOException, CollectionException {
    // TODO Auto-generated method stub
    return !read_file;
  }

}
