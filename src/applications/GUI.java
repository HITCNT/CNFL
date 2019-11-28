package applications;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.Clientable;
import dataStruct.FileData;
import dataStruct.TotalData;
import dataStruct.TransferData;
import server.Serverable;

public class GUI {

  private JFrame mainJFrame;
  private JPanel mainJPanel;
  private JTabbedPane mainJTabbedPane;
  private JPanel listJPanel;
  private JPanel downloadJPanel;
  private JPanel uploadJPanel;
  private JPanel informationJPanel;
  private JScrollPane listJScrollPane;
  private JList<FileData> uploadJList;
  private JList<TransferData> transferJList;

  private TotalData totalData;
  private Serverable server;
  private Clientable client;

  private FileData aimedSearchFile = null;

  public GUI() {
    initProgram();
    uploadJList = new JList<FileData>();
    transferJList = new JList<TransferData>();
    initJFrame();
    if (server != null) {
      server.run();
    }
    Timer refreshTimer = new Timer();
    refreshTimer.schedule(new TimerTask() {

      @Override
      public void run() {
        uploadJList
            .setListData(totalData.getSharedList().toArray(new FileData[1]));
        transferJList.setListData(
            totalData.getTransferList().toArray(new TransferData[1]));
      }
    }, 0, 1000);
    mainJFrame.setVisible(true);
  }

  private byte[] getRandomID() {
    byte[] id = new byte[20];
    File data=new File("data");
    if(!data.exists()) {
      data.mkdir();
    }
    
    File file = new File("data/nodeID.txt");
    if (!file.exists()) {
      try {
        file.createNewFile();
        FileWriter fw = new FileWriter(file, false); 
        Random r = new Random();
        r.nextBytes(id);
        fw.write(conver16HexStr(id));
        fw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String idX = reader.readLine();
        id=conver16HexToByte(idX);
        reader.close();
      } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return id;
  }

  private static String conver16HexStr(byte[] b) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      if ((b[i] & 0xff) < 0x10)
        result.append("0");
      result.append(Long.toString(b[i] & 0xff, 16));
    }
    return result.toString().toUpperCase();
  }

  public static byte[] conver16HexToByte(String hex16Str) {
    char[] c = hex16Str.toCharArray();
    byte[] b = new byte[c.length / 2];
    for (int i = 0; i < b.length; i++) {
        int pos = i * 2;
        b[i] = (byte) ("0123456789ABCDEF".indexOf(c[pos]) << 4 | "0123456789ABCDEF".indexOf(c[pos + 1]));
    }
    return b;
}
  
  private void initProgram() {
    List<TransferData> initTransferList = new ArrayList<TransferData>();
    List<FileData> initFileList = new ArrayList<FileData>();

    // initTransferList.add(new
    // TransferData(TransferState.TransferDownload, new
    // FileData("1.txt", new byte[20],123)));
    // initFileList.add(new FileData("1.txt", new
    // byte[20],123));
    //
    // Timer refreshTimer=new Timer();
    // refreshTimer.schedule(new TimerTask() {
    //
    // @Override
    // public void run() {
    // initFileList.add(new FileData("1.txt", new
    // byte[20],123));
    // initTransferList.add(new
    // TransferData(TransferState.TransferDownload, new
    // FileData("1.txt", new byte[20],123)));
    // }
    // }, 0, 500);



    totalData = new TotalData(getRandomID(), initTransferList, initFileList);
    server = Serverable.getServer(totalData);
    client = Clientable.getClient(totalData);
  }

  private void initJFrame() {
    mainJFrame = new JFrame("基于DHT的P2P文件共享系统 2019秋 计算机网络");
    mainJFrame.setSize(800, 600);
    mainJFrame.setResizable(false);

    initMainJPanel();

    mainJFrame.add(mainJPanel);


  }

  private void initMainJPanel() {
    mainJPanel = new JPanel();
    mainJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    mainJPanel.setLayout(new BorderLayout(10, 10));

    initJTabbedPane();
    initListJPanel();

    mainJPanel.add(mainJTabbedPane, BorderLayout.CENTER);
    mainJPanel.add(listJPanel, BorderLayout.EAST);
  }

  private void initJTabbedPane() {
    mainJTabbedPane = new JTabbedPane();
    mainJTabbedPane.setSize(600, 600);

    initDownloadJPanel();
    initUploadJPanel();
    initinformationJPanel();

    mainJTabbedPane.add("资源搜索", downloadJPanel);
    mainJTabbedPane.add("资源上传", uploadJPanel);
    mainJTabbedPane.add("软件信息", informationJPanel);
  }

  private void initListJPanel() {
    listJPanel = new JPanel();
    listJPanel.setSize(200, 600);
    listJPanel.setLayout(new BorderLayout(5, 5));

    initListJScrollPane();



    listJPanel.add(new JLabel("传输列表"), BorderLayout.NORTH);
    listJPanel.add(listJScrollPane, BorderLayout.CENTER);


  }

  private void initDownloadJPanel() {
    JPanel searchJPanel = new JPanel();
    JScrollPane searchListJScrollPane = new JScrollPane();
    JList<FileData> searchJList = new JList<FileData>();
    JPanel downloadingJPanel = new JPanel();
    JPanel filePathJPanel = new JPanel();
    JTextField searchJTextField = new JTextField();
    JTextField fileNameJTextField = new JTextField();
    JTextField filePathJTextField = new JTextField();
    JButton searchJButton = new JButton("搜索");
    JButton selectJButton = new JButton("浏览");
    JButton downloadJButton = new JButton("下载");

    downloadJPanel = new JPanel();
    downloadJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


    downloadJPanel.setLayout(new BorderLayout(5, 5));

    searchJPanel.setLayout(new BorderLayout(5, 5));
    searchJPanel.add(searchJTextField, BorderLayout.CENTER);
    searchJPanel.add(searchJButton, BorderLayout.EAST);
    searchJButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        List<FileData> result = new ArrayList<FileData>();
        if (client != null) {
          result = client.searchFile(searchJTextField.getText());
        } else {
          for (int i = 1; i < 100; i++) {
            result.add(new FileData(i + ".txt", new byte[20], 123));
          }
        }
        searchJList.setListData(result.toArray(new FileData[1]));
      }
    });

    searchJList.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        fileNameJTextField
            .setText(searchJList.getSelectedValue().getFileName());
        aimedSearchFile = searchJList.getSelectedValue();
      }
    });

    selectJButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (jfc.showOpenDialog(null)) {
          case JFileChooser.APPROVE_OPTION:
            filePathJTextField.setText(jfc.getSelectedFile().getPath());
            break;
          default:
            break;
        }

      }
    });

    downloadJButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (aimedSearchFile == null) {
          JOptionPane.showMessageDialog(null, "请选择待下载文件");
          return;
        }
        if (fileNameJTextField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, "请填写存储文件名");
          return;
        }
        if (filePathJTextField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, "请填写存储文件路径");
          return;
        }
        boolean result = false;
        if (client != null) {
          result = client.download(aimedSearchFile,
              fileNameJTextField.getText(), filePathJTextField.getText());
        }
        if (result) {

        } else {
          JOptionPane.showMessageDialog(null, "启动下载失败");
        }
      }
    });

    JPanel fileNameInputJPanel = new JPanel();
    JPanel filePathInputJPanel = new JPanel();
    JPanel fileDownloadJPanel = new JPanel();

    fileNameJTextField.setPreferredSize(new Dimension(120, 30));
    filePathJTextField.setPreferredSize(new Dimension(120, 30));

    fileNameInputJPanel.setLayout(new BorderLayout(5, 5));
    fileNameInputJPanel.add(new JLabel("存储文件名"), BorderLayout.WEST);
    fileNameInputJPanel.add(fileNameJTextField, BorderLayout.EAST);

    filePathInputJPanel.setLayout(new BorderLayout(5, 5));
    filePathInputJPanel.add(new JLabel("存储文件路径"), BorderLayout.WEST);
    filePathInputJPanel.add(filePathJTextField, BorderLayout.EAST);

    filePathJPanel.setLayout(new BorderLayout(5, 5));
    filePathJPanel.add(fileNameInputJPanel, BorderLayout.WEST);
    filePathJPanel.add(filePathInputJPanel, BorderLayout.EAST);

    fileDownloadJPanel.setLayout(new BorderLayout(5, 5));
    fileDownloadJPanel.add(selectJButton, BorderLayout.WEST);
    fileDownloadJPanel.add(downloadJButton, BorderLayout.EAST);

    downloadingJPanel.setLayout(new BorderLayout(5, 5));
    downloadingJPanel.add(filePathJPanel, BorderLayout.CENTER);
    downloadingJPanel.add(fileDownloadJPanel, BorderLayout.EAST);

    searchListJScrollPane.setViewportView(searchJList);

    downloadJPanel.add(searchListJScrollPane, BorderLayout.CENTER);
    downloadJPanel.add(searchJPanel, BorderLayout.NORTH);
    downloadJPanel.add(downloadingJPanel, BorderLayout.SOUTH);

  }

  private void initUploadJPanel() {
    JPanel filePathJPanel = new JPanel();
    JScrollPane updatedListJScrollPane = new JScrollPane();
    JTextField uploadJTextField = new JTextField();
    JTextField nameJTextField = new JTextField();
    JPanel buttonJPanel = new JPanel();
    JPanel textJPanel = new JPanel();
    JPanel nameJPanel = new JPanel();
    JPanel pathJPanel = new JPanel();
    JButton selectJButton = new JButton("浏览");
    JButton uploadJButton = new JButton("上传");



    uploadJPanel = new JPanel();

    uploadJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    uploadJPanel.setLayout(new BorderLayout(5, 5));

    uploadJTextField.setPreferredSize(new Dimension(120, 30));
    nameJTextField.setPreferredSize(new Dimension(120, 30));

    selectJButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        switch (jfc.showOpenDialog(null)) {
          case JFileChooser.APPROVE_OPTION:
            uploadJTextField.setText(jfc.getSelectedFile().getPath());
            nameJTextField.setText(jfc.getSelectedFile().getName());
            break;
          default:
            break;
        }
      }
    });

    uploadJButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (uploadJTextField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, "请选择待共享文件");
          return;
        }
        if (uploadJTextField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, "请填写文件名");
          return;
        }
        boolean result = false;
        if (client != null) {
          result = client.upload(nameJTextField.getText(),
              uploadJTextField.getText());
        }
        if (result) {

        } else {
          JOptionPane.showMessageDialog(null, "启动上传失败");
        }
      }
    });

    nameJPanel.setLayout(new BorderLayout(5, 5));
    nameJPanel.add(new JLabel("共享文件名"), BorderLayout.WEST);
    nameJPanel.add(nameJTextField, BorderLayout.CENTER);

    pathJPanel.setLayout(new BorderLayout(5, 5));
    pathJPanel.add(new JLabel("共享文件路径"), BorderLayout.WEST);
    pathJPanel.add(uploadJTextField, BorderLayout.CENTER);

    textJPanel.setLayout(new BorderLayout(5, 5));
    textJPanel.add(nameJPanel, BorderLayout.WEST);
    textJPanel.add(pathJPanel, BorderLayout.EAST);

    buttonJPanel.setLayout(new BorderLayout(5, 5));
    buttonJPanel.add(selectJButton, BorderLayout.WEST);
    buttonJPanel.add(uploadJButton, BorderLayout.EAST);

    filePathJPanel.setLayout(new BorderLayout(5, 5));
    filePathJPanel.add(textJPanel, BorderLayout.CENTER);
    filePathJPanel.add(buttonJPanel, BorderLayout.EAST);

    updatedListJScrollPane.setViewportView(uploadJList);

    uploadJPanel.add(updatedListJScrollPane, BorderLayout.CENTER);
    uploadJPanel.add(filePathJPanel, BorderLayout.NORTH);

  }

  private void initinformationJPanel() {
    informationJPanel = new JPanel();
    informationJPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

    informationJPanel.setLayout(new GridLayout(20, 1));
    informationJPanel
        .add(new JLabel("本节点ID：" + conver16HexStr(totalData.getId())));
    informationJPanel.add(new JLabel("2019秋 计算机网络 大作业"));
    informationJPanel.add(new JLabel("基于DHT的P2P文件共享系统"));
    informationJPanel.add(new JLabel("1170300119 郭为"));
    informationJPanel.add(new JLabel("学号待填写 罗诚"));
    informationJPanel.add(new JLabel("学号待填写 张纬之"));
  }

  private void initListJScrollPane() {
    listJScrollPane = new JScrollPane();
    listJScrollPane.setPreferredSize(new Dimension(200, 600));

    listJScrollPane.setViewportView(transferJList);
  }

}
