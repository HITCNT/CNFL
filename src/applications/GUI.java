package applications;

import javax.swing.JFrame;

public class GUI {
  
  private JFrame mainJFrame;
  
  
  public GUI() {
    initJFrame();
  }
  
  private void initJFrame() {
    mainJFrame=new JFrame("����DHT��P2P�ļ�����ϵͳ 2019�� ���������");
    mainJFrame.setSize(850,650);
    mainJFrame.setResizable(false);
    
    mainJFrame.setVisible(true);
    
  }
}
