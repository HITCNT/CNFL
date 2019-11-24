package applications;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

public class GUI {

  private JFrame mainJFrame;
  private JPanel mainJPanel;
  private JTabbedPane mainJTabbedPane;
  private JPanel listJPanel;
  private JPanel downloadJPanel;
  private JPanel uploadJPanel;
  private JPanel informationJPanel;
  private JScrollPane listJScrollPane;


  public GUI() {
    initJFrame();
  }

  private void initJFrame() {
    mainJFrame = new JFrame("����DHT��P2P�ļ�����ϵͳ 2019�� ���������");
    mainJFrame.setSize(800, 600);
    mainJFrame.setResizable(false);
    
    initMainJPanel();

    mainJFrame.add(mainJPanel);
    mainJFrame.setVisible(true);

  }

  private void initMainJPanel() {
    mainJPanel=new JPanel();
    mainJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    mainJPanel.setLayout(new BorderLayout(10,10));
    
    initJTabbedPane();
    initListJPanel();
    
    mainJPanel.add(mainJTabbedPane,BorderLayout.CENTER);
    mainJPanel.add(listJPanel,BorderLayout.EAST);
  }
  
  private void initJTabbedPane() {
    mainJTabbedPane = new JTabbedPane();
    mainJTabbedPane.setSize(600,600);
    
    initDownloadJPanel();
    initUploadJPanel();
    initinformationJPanel();
    
    mainJTabbedPane.add("��Դ����", downloadJPanel);
    mainJTabbedPane.add("��Դ�ϴ�",uploadJPanel);
    mainJTabbedPane.add("�����Ϣ",informationJPanel);
  }
  
  private void initListJPanel() {
    listJPanel=new JPanel();
    listJPanel.setSize(200,600);
    listJPanel.setLayout(new BorderLayout(5,5));
    
    initListJScrollPane();
    
    listJPanel.add(new JLabel("�����б�"),BorderLayout.NORTH);
    listJPanel.add(listJScrollPane,BorderLayout.CENTER);
    
    
  }
  
  private void initDownloadJPanel() {
    downloadJPanel=new JPanel();
  }
  
  private void initUploadJPanel() {
    uploadJPanel=new JPanel();
  }
  
  private void initinformationJPanel() {
    informationJPanel=new JPanel();
  }
  
  private void initListJScrollPane() {
    listJScrollPane=new JScrollPane();
    listJScrollPane.setPreferredSize(new Dimension(200,600));
    
  }
  
}
