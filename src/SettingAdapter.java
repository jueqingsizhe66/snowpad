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

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ���ڽ����ͱ������������ļ��Ĺ�����
 * 
 * @author ��ԭ
 * 
 */
public final class SettingAdapter {
  private Setting setting = null; // ��������������
  private URI uri = null; // XML�����ļ���URI
  private File file = null; // XML�����ļ�

  /**
   * �������Ĺ��췽��
   * 
   * @param setting
   *          ��������������
   */
  public SettingAdapter(Setting setting) {
    this.setSetting(setting);
    this.initSettingFile();
  }

  /**
   * ��ʼ��XML�����ļ�
   */
  private void initSettingFile() {
    String dir = SettingAdapter.class.getResource("").getPath();
    dir = dir.substring(5, dir.length() - 26);
    dir = new File(dir).getParent();
    dir = dir.replace(Util.FILE_SEPARATOR, "/"); // ����ǰ����ϵͳ���ļ��ָ���ͳһ�滻ΪUnix/Linux����Ա�����Windowsϵͳ�³���URI�﷨��������⡣
    try {
      this.uri = new URI("file:///" + dir + "/" + Util.SETTING_XML); // ʹ��URI�������ļ��������������·���д��ڿո�����������µĴ���
    } catch (URISyntaxException x) {
      x.printStackTrace();
    }
    this.file = new File(this.uri);
  }

  /**
   * ������������������
   * 
   * @param setting
   *          ��������������
   */
  public void setSetting(Setting setting) {
    this.setting = setting;
  }

  /**
   * ����XML�����ļ��ķ���
   */
  public void parse() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      if (!this.file.exists()) {
        this.createSettingFile();
      }
      Document document = builder.parse(this.file);
      Element root = document.getDocumentElement();
      NodeList nodeList = root.getElementsByTagName("TextArea");
      parseTextArea(nodeList);
      nodeList = root.getElementsByTagName("FindReplace");
      parseFindReplace(nodeList);
      nodeList = root.getElementsByTagName("View");
      parseView(nodeList);
      nodeList = root.getElementsByTagName("Files");
      parseFiles(nodeList);
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  /**
   * TextArea�ڵ�Ľ�������
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void parseTextArea(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        parseTextArea(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        String value = node.getTextContent().trim();
        if (!value.isEmpty()) {
          boolean logic = false;
          if (value.equalsIgnoreCase("true")) {
            logic = true;
          }
          if (key.equalsIgnoreCase("isLineWrap")) {
            this.setting.isLineWrap = logic;
          } else if (key.equalsIgnoreCase("isWrapStyleWord")) {
            this.setting.isWrapStyleWord = logic;
          } else if (key.equalsIgnoreCase("textDrag")) {
            this.setting.textDrag = logic;
          } else if (key.equalsIgnoreCase("autoIndent")) {
            this.setting.autoIndent = logic;
          } else if (key.equalsIgnoreCase("tabReplaceBySpace")) {
            this.setting.tabReplaceBySpace = logic;
          } else if (key.equalsIgnoreCase("tabSize")) {
            int size = Util.DEFAULT_TABSIZE;
            try {
              size = Integer.parseInt(value);
            } catch (NumberFormatException x) {
              x.printStackTrace();
            }
            if (size < Util.MIN_TABSIZE || size > Util.MAX_TABSIZE) {
              size = Util.DEFAULT_TABSIZE;
            }
            this.setting.tabSize = size;
          }
        }
      } else {
        if (node.getNodeName().equalsIgnoreCase("font")) {
          String strName = ((Element) node).getAttribute("name");
          String strStyle = ((Element) node).getAttribute("style");
          String strSize = ((Element) node).getAttribute("size");
          int style = 0;
          int size = 0;
          try {
            style = Integer.parseInt(strStyle);
          } catch (NumberFormatException x) {
            x.printStackTrace();
          }
          if (style < 0 || style > 3) {
            style = Util.TEXT_FONT.getStyle();
          }
          try {
            size = Integer.parseInt(strSize);
          } catch (NumberFormatException x) {
            x.printStackTrace();
          }
          if (size < Util.MIN_FONT_SIZE || size > Util.MAX_FONT_SIZE) {
            size = Util.TEXT_FONT.getSize();
          }
          this.setting.font = new Font(strName, style, size);
        } else if (node.getNodeName().equalsIgnoreCase("colorStyle")) {
          String strColor1 = ((Element) node).getAttribute("color1");
          String strColor2 = ((Element) node).getAttribute("color2");
          String strColor3 = ((Element) node).getAttribute("color3");
          String strColor4 = ((Element) node).getAttribute("color4");
          String strColor5 = ((Element) node).getAttribute("color5");
          try {
            Color color1 = new Color(Integer.parseInt(strColor1, 16), false); // ��һ���ַ���ת��Ϊʮ���������֣��ڶ�������false��ʾ��ʹ��͸����
            Color color2 = new Color(Integer.parseInt(strColor2, 16), false);
            Color color3 = new Color(Integer.parseInt(strColor3, 16), false);
            Color color4 = new Color(Integer.parseInt(strColor4, 16), false);
            Color color5 = new Color(Integer.parseInt(strColor5, 16), false);
            this.setting.colorStyle = new Color[] { color1, color2, color3,
                color4, color5 };
          } catch (NumberFormatException x) {
            x.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * FindReplace�ڵ�Ľ�������
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void parseFindReplace(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        parseFindReplace(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        String value = node.getTextContent().trim();
        if (!value.isEmpty()) {
          boolean logic = false;
          if (value.equalsIgnoreCase("true")) {
            logic = true;
          }
          if (key.equalsIgnoreCase("matchCase")) {
            this.setting.matchCase = logic;
          } else if (key.equalsIgnoreCase("isWrap")) {
            this.setting.isWrap = logic;
          } else if (key.equalsIgnoreCase("findDown")) {
            this.setting.findDown = logic;
          } else if (key.equalsIgnoreCase("searchStyle")) {
            int style = 0;
            try {
              style = Integer.parseInt(value);
            } catch (NumberFormatException x) {
              x.printStackTrace();
            }
            switch (style) {
            case 1:
              this.setting.searchStyle = SearchStyle.TRANSFER;
              break;
            case 2:
              this.setting.searchStyle = SearchStyle.PATTERN;
              break;
            case 0:
            default:
              this.setting.searchStyle = SearchStyle.DEFAULT;
              break;
            }
          }
        }
      }
    }
  }

  /**
   * View�ڵ�Ľ�������
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void parseView(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        parseView(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        String value = node.getTextContent().trim();
        if (!value.isEmpty()) {
          boolean logic = false;
          if (value.equalsIgnoreCase("true")) {
            logic = true;
          }
          if (key.equalsIgnoreCase("viewToolBar")) {
            this.setting.viewToolBar = logic;
          } else if (key.equalsIgnoreCase("viewStateBar")) {
            this.setting.viewStateBar = logic;
          } else if (key.equalsIgnoreCase("viewLineNumber")) {
            this.setting.viewLineNumber = logic;
          } else if (key.equalsIgnoreCase("viewAlwaysOnTop")) {
            this.setting.viewAlwaysOnTop = logic;
          } else if (key.equalsIgnoreCase("viewLockResizable")) {
            this.setting.viewLockResizable = logic;
          } else if (key.equalsIgnoreCase("viewTabPolicy")) {
            this.setting.viewTabPolicy = logic;
          } else if (key.equalsIgnoreCase("viewClickToClose")) {
            this.setting.viewClickToClose = logic;
          } else if (key.equalsIgnoreCase("viewTabIcon")) {
            this.setting.viewTabIcon = logic;
          } else if (key.equalsIgnoreCase("viewLookAndFeel")) {
            int id = -1;
            try {
              id = Integer.parseInt(value);
            } catch (NumberFormatException x) {
              x.printStackTrace();
            }
            if (id >= Util.LOOK_AND_FEEL_INFOS.length) {
              id = -1;
            }
            this.setting.viewLookAndFeel = id;
          }
        }
      }
    }
  }

  /**
   * Files�ڵ�Ľ�������
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void parseFiles(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        parseFiles(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        String value = node.getTextContent().trim();
        if (!value.isEmpty()) {
          if (key.equalsIgnoreCase("file")) {
            int index = this.setting.fileHistoryList.indexOf(value);
            if (index < 0) {
              this.setting.fileHistoryList.add(value);
            }
          }
        }
      }
    }
  }

  /**
   * ���������ñ��浽XML�����ļ��ķ���
   */
  public void save() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      if (!this.file.exists()) {
        this.createSettingFile();
      }
      Document document = builder.parse(this.file);
      Element root = document.getDocumentElement();
      NodeList nodeList = root.getElementsByTagName("TextArea");
      saveTextArea(nodeList);
      nodeList = root.getElementsByTagName("FindReplace");
      saveFindReplace(nodeList);
      nodeList = root.getElementsByTagName("View");
      saveView(nodeList);
      nodeList = root.getElementsByTagName("file");
      saveFiles(nodeList, root, document);
      // ���²������ս�����д�뵽Ӳ���ļ���
      TransformerFactory tff = TransformerFactory.newInstance();
      Transformer tf = tff.newTransformer();
      DOMSource ds = new DOMSource(document);
      StreamResult sr = new StreamResult(this.file);
      tf.transform(ds, sr);
    } catch (Exception x) {
      x.printStackTrace();
    }
  }

  /**
   * TextArea�ڵ�ı��淽��
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void saveTextArea(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        saveTextArea(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        if (key.equalsIgnoreCase("isLineWrap")) {
          node.setTextContent(String.valueOf(this.setting.isLineWrap));
        } else if (key.equalsIgnoreCase("isWrapStyleWord")) {
          node.setTextContent(String.valueOf(this.setting.isWrapStyleWord));
        } else if (key.equalsIgnoreCase("textDrag")) {
          node.setTextContent(String.valueOf(this.setting.textDrag));
        } else if (key.equalsIgnoreCase("autoIndent")) {
          node.setTextContent(String.valueOf(this.setting.autoIndent));
        } else if (key.equalsIgnoreCase("tabReplaceBySpace")) {
          node.setTextContent(String.valueOf(this.setting.tabReplaceBySpace));
        } else if (key.equalsIgnoreCase("tabSize")) {
          node.setTextContent(String.valueOf(this.setting.tabSize));
        }
      } else {
        if (node.getNodeName().equalsIgnoreCase("font")) {
          Element element = (Element) node;
          element.setAttribute("name", this.setting.font.getFamily());
          element.setAttribute("style", String.valueOf(this.setting.font
              .getStyle()));
          element.setAttribute("size", String.valueOf(this.setting.font
              .getSize()));
        } else if (node.getNodeName().equalsIgnoreCase("colorStyle")) {
          if (this.setting.colorStyle != null) {
            Element element = (Element) node;
            element.setAttribute("color1", Integer.toHexString(
                this.setting.colorStyle[0].getRGB()).substring(2)); // ������ɫ��ʮ��������ʱ����Ҫ����ͷ��ʾ͸���ȵ�2λ����ȥ�����Ա������ʱ����int���ݷ�Χ�������ָ�ʽ���������
            element.setAttribute("color2", Integer.toHexString(
                this.setting.colorStyle[1].getRGB()).substring(2));
            element.setAttribute("color3", Integer.toHexString(
                this.setting.colorStyle[2].getRGB()).substring(2));
            element.setAttribute("color4", Integer.toHexString(
                this.setting.colorStyle[3].getRGB()).substring(2));
            element.setAttribute("color5", Integer.toHexString(
                this.setting.colorStyle[4].getRGB()).substring(2));
          }
        }
      }
    }
  }

  /**
   * FindReplace�ڵ�ı��淽��
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void saveFindReplace(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        saveFindReplace(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        if (key.equalsIgnoreCase("matchCase")) {
          node.setTextContent(String.valueOf(this.setting.matchCase));
        } else if (key.equalsIgnoreCase("isWrap")) {
          node.setTextContent(String.valueOf(this.setting.isWrap));
        } else if (key.equalsIgnoreCase("findDown")) {
          node.setTextContent(String.valueOf(this.setting.findDown));
        } else if (key.equalsIgnoreCase("searchStyle")) {
          int style = 0;
          switch (this.setting.searchStyle) {
          case DEFAULT:
          default:
            style = 0;
            break;
          case TRANSFER:
            style = 1;
            break;
          case PATTERN:
            style = 2;
            break;
          }
          node.setTextContent(String.valueOf(style));
        }
      }
    }
  }

  /**
   * View�ڵ�ı��淽��
   * 
   * @param nodeList
   *          �ڵ��б�
   */
  private void saveView(NodeList nodeList) {
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node.hasChildNodes()) {
        NodeList list = node.getChildNodes();
        saveView(list); // �ݹ����
      } else if (node.getNodeType() == Node.TEXT_NODE) {
        String key = node.getParentNode().getNodeName();
        if (key.equalsIgnoreCase("viewToolBar")) {
          node.setTextContent(String.valueOf(this.setting.viewToolBar));
        } else if (key.equalsIgnoreCase("viewStateBar")) {
          node.setTextContent(String.valueOf(this.setting.viewStateBar));
        } else if (key.equalsIgnoreCase("viewLineNumber")) {
          node.setTextContent(String.valueOf(this.setting.viewLineNumber));
        } else if (key.equalsIgnoreCase("viewAlwaysOnTop")) {
          node.setTextContent(String.valueOf(this.setting.viewAlwaysOnTop));
        } else if (key.equalsIgnoreCase("viewLockResizable")) {
          node.setTextContent(String.valueOf(this.setting.viewLockResizable));
        } else if (key.equalsIgnoreCase("viewTabPolicy")) {
          node.setTextContent(String.valueOf(this.setting.viewTabPolicy));
        } else if (key.equalsIgnoreCase("viewClickToClose")) {
          node.setTextContent(String.valueOf(this.setting.viewClickToClose));
        } else if (key.equalsIgnoreCase("viewTabIcon")) {
          node.setTextContent(String.valueOf(this.setting.viewTabIcon));
        } else if (key.equalsIgnoreCase("viewLookAndFeel")) {
          node.setTextContent(String.valueOf(this.setting.viewLookAndFeel));
        }
      }
    }
  }

  /**
   * Files�ڵ�ı��淽��
   * 
   * @param nodeList
   *          �ڵ��б�
   * @param element
   *          ������ǩԪ��
   * @param document
   *          ����XML�ĵ�
   */
  private void saveFiles(NodeList nodeList, Element element, Document document) {
    String str = "\n    ";
    int length = nodeList.getLength();
    for (int i = 0; i < length; i++) {
      Node node = nodeList.item(0);
      node.getParentNode().removeChild(node);
    }
    nodeList = element.getElementsByTagName("Files");
    nodeList.item(0).setTextContent("");
    Element e = null;
    for (String strFile : this.setting.fileHistoryList) {
      nodeList.item(0).appendChild(document.createTextNode(str));
      e = document.createElement("file");
      e.setTextContent(strFile);
      nodeList.item(0).appendChild(e);
    }
    nodeList.item(0).appendChild(document.createTextNode("\n  "));
  }

  /**
   * ����Ĭ�ϵ�XML�����ļ�
   */
  public void createSettingFile() {
    URL url = ClassLoader.getSystemResource("res/" + Util.SETTING_XML);
    InputStreamReader inputStreamReader = null;
    StringBuilder stbTemp = new StringBuilder();
    FileOutputStream fileOutputStream = null;
    char chrBuf[] = new char[Util.BUFFER_LENGTH];
    int len = 0;
    try {
      inputStreamReader = new InputStreamReader(url.openStream(), "UTF-8");
      while ((len = inputStreamReader.read(chrBuf)) != -1) {
        stbTemp.append(chrBuf, 0, len);
      }
      String strText = stbTemp.toString();
      byte byteStr[] = strText.getBytes("UTF-8");
      fileOutputStream = new FileOutputStream(this.file);
      fileOutputStream.write(byteStr);
    } catch (Exception x) {
      x.printStackTrace();
    } finally {
      try {
        inputStreamReader.close();
        fileOutputStream.flush();
        fileOutputStream.close();
      } catch (IOException x) {
        x.printStackTrace();
      }
    }
  }

}