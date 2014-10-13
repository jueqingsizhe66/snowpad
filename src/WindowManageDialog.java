/**
 * Copyright (C) 2014 ��ԭ
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.xiboliya.snowpad;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;

/**
 * "���ڹ���"�Ի���
 * 
 * @author ��ԭ
 * 
 */
public class WindowManageDialog extends BaseDialog implements ActionListener {
  private static final long serialVersionUID = 1L;
  private JPanel pnlMain = (JPanel) this.getContentPane();
  private JPanel pnlLeft = new JPanel(new BorderLayout());
  private JPanel pnlRight = new JPanel(null);
  private JTable tabMain = null; // ��ʾ���ݵı������
  private JScrollPane spnMain = null;
  private JButton btnOk = new JButton("����");
  private JButton btnSave = new JButton("����");
  private JButton btnClose = new JButton("�ر�");
  private JButton btnCancel = new JButton("ȡ��");
  private BaseKeyAdapter keyAdapter = new BaseKeyAdapter(this);
  private BaseKeyAdapter buttonKeyAdapter = new BaseKeyAdapter(this, false);
  private Vector<Vector<String>> cells = new Vector<Vector<String>>();
  private Vector<String> cellsTitle = new Vector<String>();
  private BaseDefaultTableModel baseDefaultTableModel = null;
  private JTabbedPane tpnMain = null; // ��ʾ�ı����ѡ����

  /**
   * ���췽��
   * 
   * @param owner
   *          ������ʾ�öԻ���ĸ����
   * @param modal
   *          �Ƿ�Ϊģʽ�Ի���
   * @param txaSource
   *          ��Բ������ı���
   * @param tpnMain
   *          ��ʾ�ı����ѡ����
   */
  public WindowManageDialog(JFrame owner, boolean modal, JTextArea txaSource,
      JTabbedPane tpnMain) {
    super(owner, modal);
    if (txaSource == null) {
      return;
    }
    this.txaSource = txaSource;
    this.tpnMain = tpnMain;
    this.init();
    this.addTable();
    this.refresh();
    this.addListeners();
    this.setSize(520, 275);
    this.setMinimumSize(new Dimension(520, 275)); // ���ñ����ڵ���С�ߴ�
    this.setResizable(true);
    this.setVisible(true);
  }

  /**
   * ��ʼ������
   */
  private void init() {
    this.setTitle("���ڹ���");
    this.pnlMain.add(this.pnlLeft, BorderLayout.CENTER);
    this.pnlMain.add(this.pnlRight, BorderLayout.EAST);
    this.btnOk.setBounds(10, 20, 90, Util.BUTTON_HEIGHT);
    this.btnSave.setBounds(10, 55, 90, Util.BUTTON_HEIGHT);
    this.btnClose.setBounds(10, 90, 90, Util.BUTTON_HEIGHT);
    this.btnCancel.setBounds(10, 200, 90, Util.BUTTON_HEIGHT);
    this.pnlRight.setPreferredSize(new Dimension(110, 275)); // �����������ʳߴ�
    this.pnlRight.add(this.btnOk);
    this.pnlRight.add(this.btnSave);
    this.pnlRight.add(this.btnClose);
    this.pnlRight.add(this.btnCancel);
  }

  /**
   * ����������ӱ�����ͼ
   */
  private void addTable() {
    for (String title : Util.WINDOW_MANAGE_TABLE_TITLE_TEXTS) {
      this.cellsTitle.add(title);
    }
    this.baseDefaultTableModel = new BaseDefaultTableModel(this.cells,
        this.cellsTitle);
    this.tabMain = new JTable(this.baseDefaultTableModel);
    this.spnMain = new JScrollPane(this.tabMain);
    this.pnlLeft.add(this.spnMain, BorderLayout.CENTER);
  }

  /**
   * ��ȡ������
   */
  private void getCells() {
    this.cells.clear();
    Vector<String> cellsLine = null;
    for (int i = 0; i < this.tpnMain.getTabCount(); i++) {
      JViewport viewport = ((JScrollPane) this.tpnMain.getComponentAt(i))
          .getViewport();
      BaseTextArea textArea = (BaseTextArea) viewport.getView();
      String path = textArea.getFile() == null ? "" : textArea.getFile()
          .getParent();
      cellsLine = new Vector<String>();
      cellsLine.add(this.tpnMain.getTitleAt(i));
      cellsLine.add(path);
      cellsLine.add(textArea.getFileExt().toString());
      this.cells.add(cellsLine);
    }
  }

  /**
   * ˢ�±����е�����
   */
  public void refresh() {
    this.getCells();
    this.tabMain.updateUI();
    int index = this.tpnMain.getSelectedIndex();
    this.tabMain.setRowSelectionInterval(index, index); // �Զ�ѡ�е�ǰ������ļ���
  }

  /**
   * ���Ӻͳ�ʼ���¼�������
   */
  private void addListeners() {
    this.btnOk.addActionListener(this);
    this.btnSave.addActionListener(this);
    this.btnClose.addActionListener(this);
    this.btnCancel.addActionListener(this);
    this.btnOk.addKeyListener(this.buttonKeyAdapter);
    this.btnSave.addKeyListener(this.buttonKeyAdapter);
    this.btnClose.addKeyListener(this.buttonKeyAdapter);
    this.btnCancel.addKeyListener(this.buttonKeyAdapter);
    this.tabMain.addKeyListener(this.keyAdapter);
  }

  /**
   * Ϊ����������¼��Ĵ�������
   */
  public void actionPerformed(ActionEvent e) {
    if (this.btnOk.equals(e.getSource())) {
      this.onEnter();
    } else if (this.btnSave.equals(e.getSource())) {
      this.saveFiles();
    } else if (this.btnClose.equals(e.getSource())) {
      this.closeFiles();
    } else if (this.btnCancel.equals(e.getSource())) {
      this.onCancel();
    }
  }

  /**
   * "����"�Ĳ�������
   */
  private void saveFiles() {
    int[] indexs = this.tabMain.getSelectedRows();
    ((SnowPadFrame) this.getOwner()).windowManageToSaveFile(indexs);
    this.refresh();
  }

  /**
   * "�ر�"�Ĳ�������
   */
  private void closeFiles() {
    int[] indexs = this.tabMain.getSelectedRows();
    ((SnowPadFrame) this.getOwner()).windowManageToCloseFile(indexs);
    this.refresh();
  }

  /**
   * Ĭ�ϵ�"ȷ��"��������
   */
  public void onEnter() {
    int index = this.tabMain.getSelectedRow();
    if (index >= 0) {
      this.tpnMain.setSelectedIndex(index);
    }
    this.dispose();
  }

  /**
   * Ĭ�ϵ�"ȡ��"��������
   */
  public void onCancel() {
    this.dispose();
  }

}