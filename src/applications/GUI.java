package applications;

import javax.swing.JFrame;

public class GUI {
  
  private JFrame mainJFrame;
  
  
  public GUI() {
    initJFrame();
  }
  
  private void initJFrame() {
    mainJFrame=new JFrame("基于DHT的P2P文件共享系统 2019秋 计算机网络");
    mainJFrame.setSize(850,650);
    mainJFrame.setResizable(false);
    
    mainJFrame.setVisible(true);
    
  }
}
