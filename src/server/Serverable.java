package server;

import dataStruct.TotalData;

/**
 * �������ӿڡ�
 * ������Ӧ���Զ��������KadЭ��Ҫ��ķ�����������
 * ����������Ӧ��һ���߳�(Runnable)������������ɳ�ʼ�����������̡߳������̺߳�������������߳̽�����
 * ��ע���̰߳�ȫ��
 * 
 * @author HIT_1170300119
 *
 */
public interface Serverable extends Runnable {
  /**
   * ��ȡ������Ĭ��ʵ�֡�
   * TODO ����ʵ�ֺ��޸ı�������
   * @return ����������
   */
  public static Serverable getServer(TotalData totalData) {
    return null;
  }
}
