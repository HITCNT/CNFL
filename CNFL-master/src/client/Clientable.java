package client;

import java.util.List;

import dataStruct.FileData;
import dataStruct.TotalData;

/**
 * �ͻ��˽ӿڡ�
 * �ͻ���Ӧ���Զ��������KadЭ��Ҫ��Ŀͻ��˲�����
 * �ͻ��˵Ĺ���Ӧ��������������������������¶�����Ϣ��
 * ���������ýӿڵ��ù��ܣ����ϸ�ע��Ȩ��ʵ�֡�
 * @author HIT_1170300119
 *
 */
public interface Clientable {
  /**
   * ��ȡ�ͻ���Ĭ��ʵ�֡�
   * TODO ����ʵ�ֺ��޸ı�������
   * @return �ͻ�������
   */
  public static Clientable getClient(TotalData totalData) {
    return null;
  }
  
  /**
   * ���ؼ��������ļ���
   * @param keyWords �ؼ���
   * @return �ļ��б�����������򷵻�NULL��������������ۡ�
   */
  public List<FileData> searchFile(String keyWords);
  
  /**
   * ����Ŀ���ļ���
   * Ӧ���������ز������޸Ĵ����б��������ļ���δ���ڣ�Ӧ�����𴴽���
   * @param fileData �ļ���������
   * @param fileName ���ش洢�ļ���
   * @param filePath ���ش洢�ļ�·��
   * @return �Ƿ�ɹ���������
   */
  public boolean download(FileData fileData,String fileName,String filePath);
  
  /**
   * ����Ŀ���ļ���
   * Ӧ���������������޸Ĵ����б�Ӧ���޸ĵ�ǰ�ѷ�����������ļ�δ������Ӧ������
   * @param fileData �ļ���������
   * @param fileName ���ش洢�ļ���
   * @param filePath ���ش洢�ļ�·��
   * @return �Ƿ�ɹ������ϴ�
   */
  public boolean upload(FileData fileData,String fileName,String filePath);
}
