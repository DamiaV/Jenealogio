package net.darmo_creations.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.MatteBorder;

import net.darmo_creations.controllers.ExtensionFileFilter;
import net.darmo_creations.controllers.MainController;
import net.darmo_creations.gui.components.DisplayPanel;
import net.darmo_creations.gui.dialog.CardDetailsDialog;
import net.darmo_creations.gui.dialog.TreeCreationDialog;
import net.darmo_creations.gui.dialog.card.CardDialog;
import net.darmo_creations.gui.dialog.link.LinkDialog;
import net.darmo_creations.model.family.Family;
import net.darmo_creations.model.family.FamilyMember;
import net.darmo_creations.model.family.Wedding;
import net.darmo_creations.util.ImageUtil;

public class MainFrame extends JFrame {
  private static final long serialVersionUID = 2426665404072947885L;

  public static final String BASE_TITLE = "Généalogie";

  private JFileChooser fileChooser;
  private TreeCreationDialog treeCreationDialog;
  private CardDialog cardDialog;
  private LinkDialog linkDialog;
  private CardDetailsDialog detailsDialog;

  private JMenu editMenu;
  private JMenuItem saveItem, saveAsItem, addCardItem, addLinkItem, editItem, deleteItem;
  private JButton saveBtn, saveAsBtn, addCardBtn, addLinkBtn, editCardBtn, editLinkBtn, deleteCardBtn, deleteLinkBtn;
  private DisplayPanel displayPnl;

  public MainFrame() {
    MainController controller = new MainController(this);

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(800, 600));

    addWindowListener(controller);

    this.fileChooser = new JFileChooser();
    this.fileChooser.setAcceptAllFileFilterUsed(false);
    this.fileChooser.setMultiSelectionEnabled(false);
    this.fileChooser.setFileFilter(new ExtensionFileFilter("Arbre généalogique", "tree"));
    this.treeCreationDialog = new TreeCreationDialog(this);
    this.cardDialog = new CardDialog(this);
    this.linkDialog = new LinkDialog(this);
    this.detailsDialog = new CardDetailsDialog(this);

    setJMenuBar(initJMenuBar(controller));

    add(getJToolBar(controller), BorderLayout.NORTH);
    this.displayPnl = new DisplayPanel();
    this.displayPnl.addObserver(controller);
    add(new JScrollPane(this.displayPnl), BorderLayout.CENTER);

    controller.init();

    pack();
    setLocationRelativeTo(null);
    setExtendedState(MAXIMIZED_BOTH);
  }

  private JMenuBar initJMenuBar(ActionListener listener) {
    JMenuBar menuBar = new JMenuBar();
    JMenuItem i;

    // Menu 'Fichier'
    {
      JMenu fileMenu = new JMenu("Fichier");
      fileMenu.setMnemonic('f');
      fileMenu.add(i = new JMenuItem("Nouvel arbre..."));
      i.setIcon(ImageUtil.NEW_TREE);
      i.setActionCommand("new");
      i.setMnemonic('n');
      i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
      i.addActionListener(listener);
      fileMenu.add(i = new JMenuItem("Ouvrir..."));
      i.setIcon(ImageUtil.OPEN);
      i.setActionCommand("open");
      i.setMnemonic('n');
      i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
      i.addActionListener(listener);
      fileMenu.add(this.saveItem = new JMenuItem("Enregistrer"));
      this.saveItem.setIcon(ImageUtil.SAVE);
      this.saveItem.setActionCommand("save");
      this.saveItem.setMnemonic('e');
      this.saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
      this.saveItem.addActionListener(listener);
      fileMenu.add(this.saveAsItem = new JMenuItem("Enregistrer sous..."));
      this.saveAsItem.setIcon(ImageUtil.SAVE_AS);
      this.saveAsItem.setActionCommand("save-as");
      this.saveAsItem.setMnemonic('s');
      this.saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK));
      this.saveAsItem.addActionListener(listener);
      fileMenu.add(i = new JMenuItem("Quitter"));
      i.setIcon(ImageUtil.EXIT);
      i.setActionCommand("exit");
      i.setMnemonic('Q');
      i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
      i.addActionListener(listener);
      menuBar.add(fileMenu);
    }

    // Menu 'Édition'
    {
      this.editMenu = new JMenu("Édition");
      this.editMenu.setMnemonic('é');
      this.editMenu.add(this.addCardItem = new JMenuItem("Ajouter une fiche..."));
      this.addCardItem.setIcon(ImageUtil.ADD_CARD);
      this.addCardItem.setActionCommand("add-card");
      this.addCardItem.setMnemonic('a');
      this.addCardItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
      this.addCardItem.addActionListener(listener);
      this.editMenu.add(this.addLinkItem = new JMenuItem("Ajouter un lien..."));
      this.addLinkItem.setIcon(ImageUtil.ADD_LINK);
      this.addLinkItem.setActionCommand("add-link");
      this.addLinkItem.setMnemonic('l');
      this.addLinkItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
      this.addLinkItem.addActionListener(listener);
      this.editMenu.add(this.editItem = new JMenuItem());
      this.editItem.setActionCommand("edit");
      this.editItem.setMnemonic('m');
      this.editItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
      this.editItem.addActionListener(listener);
      this.editMenu.add(this.deleteItem = new JMenuItem());
      this.deleteItem.setActionCommand("delete");
      this.deleteItem.setMnemonic('s');
      this.deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
      this.deleteItem.addActionListener(listener);
      menuBar.add(this.editMenu);
    }

    // Menu 'Aide'
    {
      JMenu helpMenu = new JMenu("Aide");
      helpMenu.setMnemonic('i');
      helpMenu.add(i = new JMenuItem("À propos..."));
      i.setIcon(ImageUtil.HELP);
      i.setActionCommand("about");
      i.setMnemonic('à');
      i.addActionListener(listener);
      menuBar.add(helpMenu);
    }

    return menuBar;
  }

  private JToolBar getJToolBar(ActionListener listener) {
    JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
    toolBar.setFloatable(false);
    toolBar.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
    JButton b;

    toolBar.add(b = new JButton(ImageUtil.NEW_TREE_BIG));
    b.setToolTipText("Nouvel arbre... (Ctrl+N)");
    b.setFocusable(false);
    b.setActionCommand("new");
    b.addActionListener(listener);
    toolBar.add(b = new JButton(ImageUtil.OPEN_BIG));
    b.setToolTipText("Ouvrir... (Ctrl+O)");
    b.setFocusable(false);
    b.setActionCommand("open");
    b.addActionListener(listener);
    toolBar.add(this.saveBtn = new JButton(ImageUtil.SAVE_BIG));
    this.saveBtn.setToolTipText("Enregistrer (Ctrl+S)");
    this.saveBtn.setFocusable(false);
    this.saveBtn.setActionCommand("save");
    this.saveBtn.addActionListener(listener);
    toolBar.add(this.saveAsBtn = new JButton(ImageUtil.SAVE_AS_BIG));
    this.saveAsBtn.setToolTipText("Enregistrer sous... (Ctrl+Maj+S)");
    this.saveAsBtn.setFocusable(false);
    this.saveAsBtn.setActionCommand("save-as");
    this.saveAsBtn.addActionListener(listener);
    toolBar.add(this.addCardBtn = new JButton(ImageUtil.ADD_CARD_BIG));
    this.addCardBtn.setToolTipText("Ajouter une fiche... (Ctrl+A)");
    this.addCardBtn.setFocusable(false);
    this.addCardBtn.setActionCommand("add-card");
    this.addCardBtn.addActionListener(listener);
    toolBar.add(this.editCardBtn = new JButton(ImageUtil.EDIT_CARD_BIG));
    this.editCardBtn.setToolTipText("Éditer la fiche... (Ctrl+E)");
    this.editCardBtn.setFocusable(false);
    this.editCardBtn.setActionCommand("edit");
    this.editCardBtn.addActionListener(listener);
    toolBar.add(this.deleteCardBtn = new JButton(ImageUtil.DELETE_CARD_BIG));
    this.deleteCardBtn.setToolTipText("Supprimer la fiche (Supprimer)");
    this.deleteCardBtn.setFocusable(false);
    this.deleteCardBtn.setActionCommand("delete");
    this.deleteCardBtn.addActionListener(listener);
    toolBar.add(this.addLinkBtn = new JButton(ImageUtil.ADD_LINK_BIG));
    this.addLinkBtn.setToolTipText("Ajouter un lien... (Ctrl+L)");
    this.addLinkBtn.setFocusable(false);
    this.addLinkBtn.setActionCommand("add-link");
    this.addLinkBtn.addActionListener(listener);
    toolBar.add(this.editLinkBtn = new JButton(ImageUtil.EDIT_LINK_BIG));
    this.editLinkBtn.setToolTipText("Éditer le lien... (Ctrl+E)");
    this.editLinkBtn.setFocusable(false);
    this.editLinkBtn.setActionCommand("edit");
    this.editLinkBtn.addActionListener(listener);
    toolBar.add(this.deleteLinkBtn = new JButton(ImageUtil.DELETE_LINK_BIG));
    this.deleteLinkBtn.setToolTipText("Supprimer le lien (Supprimer)");
    this.deleteLinkBtn.setFocusable(false);
    this.deleteLinkBtn.setActionCommand("delete");
    this.deleteLinkBtn.addActionListener(listener);

    return toolBar;
  }

  public void updateMenus(boolean fileOpen, boolean cardSelected, boolean linkSelected) {
    this.saveItem.setEnabled(fileOpen);
    this.saveAsItem.setEnabled(fileOpen);
    this.editMenu.setEnabled(fileOpen);
    this.addCardItem.setEnabled(fileOpen);
    this.addLinkItem.setEnabled(fileOpen);
    this.editItem.setEnabled(fileOpen && (cardSelected || linkSelected));
    this.deleteItem.setEnabled(fileOpen && (cardSelected || linkSelected));

    this.saveBtn.setEnabled(fileOpen);
    this.saveAsBtn.setEnabled(fileOpen);
    this.addCardBtn.setEnabled(fileOpen);
    this.addLinkBtn.setEnabled(fileOpen);
    this.editCardBtn.setEnabled(fileOpen && cardSelected);
    this.deleteCardBtn.setEnabled(fileOpen && cardSelected);
    this.editLinkBtn.setEnabled(fileOpen && linkSelected);
    this.deleteLinkBtn.setEnabled(fileOpen && linkSelected);

    if (fileOpen) {
      if (cardSelected && linkSelected)
        throw new IllegalStateException("can't select a card and a link at the same time");
      if (cardSelected) {
        this.editItem.setText("Éditer la fiche...");
        this.editItem.setIcon(ImageUtil.EDIT_CARD);
        this.deleteItem.setText("Supprimer la fiche");
        this.deleteItem.setIcon(ImageUtil.DELETE_CARD);
      }
      else if (linkSelected) {
        this.editItem.setText("Éditer le lien...");
        this.editItem.setIcon(ImageUtil.EDIT_LINK);
        this.deleteItem.setText("Supprimer le lien");
        this.deleteItem.setIcon(ImageUtil.DELETE_LINK);
      }
      else {
        this.editItem.setText("Éditer...");
        this.editItem.setIcon(null);
        this.deleteItem.setText("Supprimer");
        this.deleteItem.setIcon(null);
      }
    }
  }

  public void resetDisplay() {
    this.displayPnl.reset();
  }

  public void refreshDisplay(Family family) {
    this.displayPnl.refresh(family);
  }

  public File showSaveFileChooser() {
    this.fileChooser.showSaveDialog(this);
    return this.fileChooser.getSelectedFile();
  }

  public File showOpenFileChooser() {
    this.fileChooser.showOpenDialog(this);
    return this.fileChooser.getSelectedFile();
  }

  public Optional<String> showCreateTreeDialog() {
    this.treeCreationDialog.setVisible(true);
    return this.treeCreationDialog.getTreeName();
  }

  public Optional<FamilyMember> showAddCardDialog() {
    this.cardDialog.setCard(null);
    this.cardDialog.setVisible(true);
    return this.cardDialog.getCard();
  }

  public Optional<Wedding> showAddLinkDialog(Family family) {
    this.linkDialog.setLink(null, family);
    this.linkDialog.setVisible(true);
    return this.linkDialog.getLink();
  }

  public Optional<FamilyMember> showUpdateCard(FamilyMember member) {
    this.cardDialog.setCard(member);
    this.cardDialog.setVisible(true);
    return this.cardDialog.getCard();
  }

  public Optional<Wedding> showUpdateLink(Wedding wedding, Family family) {
    this.linkDialog.setLink(wedding, family);
    this.linkDialog.setVisible(true);
    return this.linkDialog.getLink();
  }

  public void showDetailsDialog(FamilyMember member, Wedding wedding) {
    this.detailsDialog.setInfo(member, wedding);
    this.detailsDialog.setVisible(true);
  }

  public void showAboutDialog() {
    // TODO
  }

  public void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
  }

  public int showConfirmDialog(String message) {
    return JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
  }
}
