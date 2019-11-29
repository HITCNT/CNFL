/**
 * 
 */
package client;

import java.util.List;

import dataStruct.FileData;

/**
 * @author LC12138
 *
 */
public class Client implements Clientable{

  /* (non-Javadoc)
   * @see client.Clientable#searchFile(java.lang.String)
   */
  @Override
  public List<FileData> searchFile(String keyWords) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see client.Clientable#download(dataStruct.FileData, java.lang.String, java.lang.String)
   */
  @Override
  public boolean download(FileData fileData, String fileName, String filePath) {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see client.Clientable#upload(dataStruct.FileData, java.lang.String, java.lang.String)
   */
  @Override
  public boolean upload(FileData fileData, String fileName, String filePath) {
    // TODO Auto-generated method stub
    return false;
  }

}
