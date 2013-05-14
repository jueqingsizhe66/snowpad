package com.xiboliya.snowpad;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

/**
 * ������"�г�"�Ի���
 * 
 * @author ��ԭ
 * 
 */
public class BatchRemoveDialog extends BaseDialog implements ActionListener {
  private static final long serialVersionUID = 1L;
  private JPanel pnlMain = (JPanel) this.getContentPane();
  private JRadioButton radLineStart = new JRadioButton("����(S)", false);
  private JRadioButton radLineEnd = new JRadioButton("��β(E)", true);
  private JPanel pnlLineStartEnd = new JPanel(new GridLayout(2, 1));
  private JLabel lblOffset = new JLabel("ƫ������");
  private BaseTextField txtOffset = new BaseTextField(true, "\\d*"); // �����û�ֻ����������
  private JButton btnOk = new JButton("ȷ��");
  private JButton btnCancel = new JButton("ȡ��");
  private BaseKeyAdapter keyAdapter = new BaseKeyAdapter(this);
  private BaseKeyAdapter buttonKeyAdapter = new BaseKeyAdapter(this, false);
  private ButtonGroup bgpLineStartEnd = new ButtonGroup();

  public BatchRemoveDialog(JFrame owner, boolean modal, JTextArea txaSource) {
    super(owner, modal);
    if (txaSource == null) {
      return;
    }
    this.txaSource = txaSource;
    this.init();
    this.addListeners();
    this.setSize(220, 150);
    this.setVisible(true);
  }

  /**
   * ��д����ķ��������ñ������Ƿ�ɼ�
   */
  public void setVisible(boolean visible) {
    if (visible) {
      this.txtOffset.setText("");
    }
    super.setVisible(visible);
  }

  /**
   * ��ʼ������
   */
  private void init() {
    this.setTitle("�г��ı�");
    this.pnlMain.setLayout(null);
    this.lblOffset.setBounds(20, 10, 60, Util.VIEW_HEIGHT);
    this.txtOffset.setBounds(80, 10, 100, Util.INPUT_HEIGHT);
    this.pnlMain.add(this.lblOffset);
    this.pnlMain.add(this.txtOffset);
    this.pnlLineStartEnd.setBounds(10, 40, 85, 65);
    this.pnlLineStartEnd.setBorder(new TitledBorder("�г�λ��"));
    this.pnlLineStartEnd.add(this.radLineStart);
    this.pnlLineStartEnd.add(this.radLineEnd);
    this.pnlMain.add(this.pnlLineStartEnd);
    this.radLineStart.setMnemonic('S');
    this.radLineEnd.setMnemonic('E');
    this.bgpLineStartEnd.add(radLineStart);
    this.bgpLineStartEnd.add(radLineEnd);
    this.radLineStart.setSelected(true);
    this.btnOk.setBounds(110, 45, 85, Util.BUTTON_HEIGHT);
    this.btnCancel.setBounds(110, 80, 85, Util.BUTTON_HEIGHT);
    this.pnlMain.add(this.btnOk);
    this.pnlMain.add(this.btnCancel);
  }

  /**
   * �����¼�������
   */
  private void addListeners() {
    this.radLineStart.addKeyListener(this.keyAdapter);
    this.radLineEnd.addKeyListener(this.keyAdapter);
    this.txtOffset.addKeyListener(this.keyAdapter);
    this.btnOk.addActionListener(this);
    this.btnOk.addKeyListener(this.buttonKeyAdapter);
    this.btnCancel.addActionListener(this);
    this.btnCancel.addKeyListener(this.buttonKeyAdapter);
  }

  /**
   * Ϊ����������¼��Ĵ�������
   */
  public void actionPerformed(ActionEvent e) {
    if (this.btnOk.equals(e.getSource())) {
      this.onEnter();
    } else if (this.btnCancel.equals(e.getSource())) {
      this.onCancel();
    }
  }

  /**
   * �г��ַ�
   */
  private void removeText() {
    int offset = 0;
    try {
      offset = Integer.parseInt(this.txtOffset.getText().trim());
    } catch (NumberFormatException x) {
      x.printStackTrace();
      JOptionPane.showMessageDialog(this, "��ʽ�������������֣�", Util.SOFTWARE,
          JOptionPane.CANCEL_OPTION);
      this.txtOffset.requestFocus();
      this.txtOffset.selectAll();
      return;
    }
    if (offset <= 0) {
      JOptionPane.showMessageDialog(this, "��ֵ�������0��", Util.SOFTWARE,
          JOptionPane.CANCEL_OPTION);
      this.txtOffset.requestFocus();
      this.txtOffset.selectAll();
    } else {
      this.toRemoveText(offset);
      this.dispose();
    }
  }

  /**
   * ������/��β�г�һ����Ŀ���ַ�
   * 
   * @param offset
   *          ������/��β�г��ַ��ĸ���
   */
  private void toRemoveText(int offset) {
    CurrentLines currentLines = new CurrentLines(this.txaSource);
    String strContent = currentLines.getStrContent();
    int startIndex = currentLines.getStartIndex();
    int endIndex = currentLines.getEndIndex();
    String[] arrText = strContent.split("\n", -1); // ����ǰѡ�����ı����д���������ĩβ�Ķദ����
    boolean isLineStart = this.radLineStart.isSelected();
    StringBuilder stbText = new StringBuilder();
    for (int n = 0; n < arrText.length; n++) {
      int strLen = arrText[n].length();
      if (strLen <= offset) {
        arrText[n] = "";
      } else {
        if (isLineStart) {
          arrText[n] = arrText[n].substring(offset);
        } else {
          arrText[n] = arrText[n].substring(0, strLen - offset);
        }
      }
      stbText.append(arrText[n] + "\n");
    }
    this.txaSource.replaceRange(stbText.deleteCharAt(stbText.length() - 1)
        .toString(), startIndex, endIndex);
    endIndex = startIndex + stbText.length() - 1; // �г��ַ��󣬵�ǰѡ����ĩ�е���βƫ����
    if (this.txaSource.getText().length() == endIndex + 1) {
      endIndex++;
    }
    this.txaSource.select(startIndex, endIndex);
  }

  /**
   * Ĭ�ϵ�"ȷ��"��������
   */
  public void onEnter() {
    this.removeText();
  }

  /**
   * Ĭ�ϵ�"ȡ��"��������
   */
  public void onCancel() {
    this.dispose();
  }
}