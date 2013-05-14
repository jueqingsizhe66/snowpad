package com.xiboliya.snowpad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * "插入字符"对话框
 * 
 * @author 冰原
 * 
 */
public class InsertCharDialog extends BaseDialog implements ActionListener,
    FocusListener {
  private static final long serialVersionUID = 1L;
  private JPanel pnlMain = (JPanel) this.getContentPane();
  private JPanel pnlLeft = new JPanel(new BorderLayout());
  private JPanel pnlRight = new JPanel(null);
  private JTabbedPane tpnMain = new JTabbedPane();
  private GridLayout gridLayout = new GridLayout(Util.INSERT_MAX_ROW,
      Util.INSERT_MAX_COLUMN, 0, 0);
  private JButton btnOk = new JButton("插入");
  private JButton btnCancel = new JButton("关闭");
  private JLabel lblView = new JLabel();
  private BaseKeyAdapter keyAdapter = new BaseKeyAdapter(this);
  private EtchedBorder etchedBorder = new EtchedBorder();
  private MouseAdapter mouseAdapter = null;

  /**
   * 构造方法
   * 
   * @param owner
   *          用于显示该对话框的父组件
   * @param modal
   *          是否为模式对话框
   * @param txaSource
   *          针对操作的文本域
   * @param hashtable
   *          用于显示字符的哈希表。键为标签，值为该标签下的字符序列
   */
  public InsertCharDialog(JFrame owner, boolean modal, JTextArea txaSource,
      Hashtable<String, String> hashtable) {
    super(owner, modal);
    if (txaSource == null) {
      return;
    }
    this.txaSource = txaSource;
    this.init();
    this.addListeners();
    this.setSize(340, 275);
    this.fillTabbedPane(hashtable);
    this.setVisible(true);
  }

  /**
   * 初始化界面
   */
  private void init() {
    this.setTitle("插入字符");
    this.pnlMain.setLayout(null);
    this.pnlLeft.setBounds(0, 0, 230, 245);
    this.pnlRight.setBounds(230, 0, 110, 240);
    this.pnlMain.add(this.pnlLeft);
    this.pnlMain.add(this.pnlRight);
    this.pnlLeft.add(this.tpnMain, BorderLayout.CENTER);
    this.btnOk.setBounds(10, 30, 85, Util.BUTTON_HEIGHT);
    this.btnCancel.setBounds(10, 70, 85, Util.BUTTON_HEIGHT);
    this.lblView.setBounds(6, 125, 96, 96);
    this.lblView.setBorder(new EtchedBorder());
    this.lblView.setHorizontalAlignment(SwingConstants.CENTER);
    this.lblView.setOpaque(true);
    this.pnlRight.add(this.btnOk);
    this.pnlRight.add(this.btnCancel);
    this.pnlRight.add(this.lblView);
    this.tpnMain.setFocusable(false);
    this.lblView.setFont(Util.INSERT_VIEW_FONT);
    this.btnOk.setFocusable(false);
    this.btnCancel.setFocusable(false);
  }

  /**
   * 填充所有标签页的字符
   * 
   * @param hashtable
   *          用于显示字符的哈希表。键为标签，值为该标签下的字符序列
   */
  private void fillTabbedPane(Hashtable<String, String> hashtable) {
    if (hashtable.isEmpty()) {
      return;
    }
    Enumeration<String> enumeration = hashtable.keys();
    while (enumeration.hasMoreElements()) {
      String strTitle = enumeration.nextElement();
      String strElement = hashtable.get(strTitle);
      this.fillElements(strElement, strTitle);
    }
  }

  /**
   * 填充一个标签页的字符
   * 
   * @param strElement
   *          字符序列
   * @param strTitle
   *          标签页标题
   */
  private void fillElements(String strElement, String strTitle) {
    if (strElement == null || strTitle == null || strElement.isEmpty()
        || strTitle.isEmpty()) {
      return;
    }
    JPanel pnlTemp = new JPanel(this.gridLayout);
    int elementCount = strElement.length();
    if (elementCount > Util.INSERT_MAX_ELEMENT) {
      elementCount = Util.INSERT_MAX_ELEMENT;
    }
    int index = 0;
    for (; index < elementCount; index++) {
      char charElement = strElement.charAt(index);
      JLabel lblElement = this.createElement(String.valueOf(charElement));
      pnlTemp.add(lblElement);
    }
    for (; index < Util.INSERT_MAX_ELEMENT; index++) {
      JLabel lblElement = this.createElement("");
      pnlTemp.add(lblElement);
    }
    this.tpnMain.add(pnlTemp, strTitle);
  }

  /**
   * 创建一个文本标签
   * 
   * @param strElement
   *          显示的字符
   * @return 新创建的文本标签
   */
  private JLabel createElement(String strElement) {
    JLabel lblElement = new JLabel(strElement);
    if (!strElement.isEmpty()) {
      lblElement.setHorizontalAlignment(SwingConstants.CENTER);
      lblElement.setFocusable(true); // 设置标签可以获得焦点
      lblElement.setOpaque(true); // 设置标签可以绘制背景
      lblElement.setBorder(this.etchedBorder);
      lblElement.setBackground(Color.WHITE);
      lblElement.addKeyListener(this.keyAdapter);
      lblElement.addFocusListener(this);
      lblElement.addMouseListener(this.mouseAdapter);
    }
    return lblElement;
  }

  /**
   * 预览当前选中的字符
   * 
   * @param strView
   *          当前选中文本标签的字符
   */
  private void setView(String strView) {
    this.lblView.setText(strView);
  }

  /**
   * 添加事件监听器
   */
  private void addListeners() {
    this.btnOk.addActionListener(this);
    this.btnCancel.addActionListener(this);
    this.mouseAdapter = new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        JLabel lblTemp = (JLabel) e.getSource();
        lblTemp.requestFocus(); // 当鼠标单击时，获得焦点
        if (e.getClickCount() == 2) {
          onEnter();
        }
      }
    };
  }

  /**
   * 为各组件添加事件的处理方法
   */
  public void actionPerformed(ActionEvent e) {
    if (this.btnOk.equals(e.getSource())) {
      this.onEnter();
    } else if (this.btnCancel.equals(e.getSource())) {
      this.onCancel();
    }
  }

  /**
   * 默认的"确定"操作方法
   */
  public void onEnter() {
    this.txaSource.replaceSelection(this.lblView.getText());
  }

  /**
   * 默认的"取消"操作方法
   */
  public void onCancel() {
    this.dispose();
  }

  /**
   * 当文本标签获得焦点时，将触发此事件
   */
  public void focusGained(FocusEvent e) {
    JLabel lblTemp = (JLabel) e.getSource();
    lblTemp.setBackground(Color.PINK);
    this.setView(lblTemp.getText());
  }

  /**
   * 当文本标签失去焦点时，将触发此事件
   */
  public void focusLost(FocusEvent e) {
    JLabel lblTemp = (JLabel) e.getSource();
    lblTemp.setBackground(Color.WHITE);
  }
}
