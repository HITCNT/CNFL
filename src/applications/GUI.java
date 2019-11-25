package applications;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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
    JPanel searchJPanel=new JPanel();
    JScrollPane searchListJScrollPane=new JScrollPane();
    JPanel downloadingJPanel=new JPanel();
    JPanel filePathJPanel=new JPanel();
    JTextField searchJTextField=new JTextField();
    JTextField fileNameJTextField=new JTextField();
    JTextField filePathJTextField=new JTextField();
    
    downloadJPanel=new JPanel();
    downloadJPanel.setLayout(new BorderLayout(5,5));
    
    searchJPanel.setLayout(new BorderLayout(5,5));
    searchJPanel.add(searchJTextField,BorderLayout.CENTER);
    searchJPanel.add(new JButton("����"),BorderLayout.EAST);
    
    fileNameJTextField.setPreferredSize(new Dimension(200,30));
    
    filePathJTextField.setPreferredSize(new Dimension(200,30));
    
    filePathJPanel.add(fileNameJTextField);
    filePathJPanel.add(filePathJTextField);
    
    downloadingJPanel.setLayout(new BorderLayout(5,5));
    downloadingJPanel.add(filePathJPanel,BorderLayout.CENTER);
    downloadingJPanel.add(new JButton("����"),BorderLayout.EAST);
    
    downloadJPanel.add(searchListJScrollPane,BorderLayout.CENTER);
    downloadJPanel.add(searchJPanel,BorderLayout.NORTH);
    downloadJPanel.add(downloadingJPanel,BorderLayout.SOUTH);
    
  }
  
  private void initUploadJPanel() {
    JPanel filePathJPanel=new JPanel();
    JScrollPane updatedListJScrollPane=new JScrollPane();
    JTextField searchJTextField=new JTextField();
    JPanel buttonJPanel=new JPanel();
    JButton selectJButton=new JButton("���");
    JButton uploadJButton=new JButton("�ϴ�");
    
    uploadJPanel=new JPanel();
    uploadJPanel.setLayout(new BorderLayout(5,5));
    
    searchJTextField.setPreferredSize(new Dimension(600,30));
    
    buttonJPanel.setLayout(new BorderLayout(5,5));
    buttonJPanel.add(selectJButton,BorderLayout.WEST);
    buttonJPanel.add(uploadJButton,BorderLayout.EAST);
    
    filePathJPanel.setLayout(new BorderLayout(5,5));
    filePathJPanel.add(searchJTextField,BorderLayout.CENTER);
    filePathJPanel.add(buttonJPanel,BorderLayout.EAST);
    
    uploadJPanel.add(updatedListJScrollPane,BorderLayout.CENTER);
    uploadJPanel.add(filePathJPanel,BorderLayout.NORTH);
    
  }
  
  private void initinformationJPanel() {
    informationJPanel=new JPanel();
    informationJPanel.add(new JLabel("��ҳΪ�����Ϣҳ��û�в�������"));
  }
  
  private void initListJScrollPane() {
    listJScrollPane=new JScrollPane();
    listJScrollPane.setPreferredSize(new Dimension(200,600));
    
  }
  
}
