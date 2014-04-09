package com.seadnamcgillycuddy.cloudnote;	//specifies the package which the application belongs to

import java.awt.*;				//imports the necessary classes	
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

class CloudNote extends JFrame {		//creates the main class
	
	private JTextArea area = new JTextArea(20, 120);	//creates a JTextArea box of size 20 X 120 px.
		private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));	//creates a JFileChooser dialog box
		private String currentFile = "Untitled";	//sets the current filename to "Untitled"
		private boolean changed = false;		//sets the boolean 'changed' to false
		
	public CloudNote() {			//The first constructor
		area.setFont(new Font("Monospaced", Font.PLAIN, 12));	//sets the font to be used in the text editor
		JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); //creates a new JScrollPane and sets its appearance
		add(scroll, BorderLayout.CENTER);	//adds the JScrollPane
		
		JMenuBar JMB = new JMenuBar();	//creates a new JMenuBar
		setJMenuBar(JMB);
		JMenu file = new JMenu("File");	//creates a new File menu
		JMenu edit = new JMenu("Edit");	//creates a new Edit menu
		JMB.add(file);			//adds the File menu to the JMenuBar
		JMB.add(edit);			//adds the File menu to the JMenuBar
		
		file.add(New);			//adds a 'New' option to the File menu
		file.add(Open);			//adds a 'Open' option to the File menu
		file.add(Save);			//adds a 'Save' option to the File menu
		file.add(Quit);			//adds a 'Quit' option to the File menu
		file.add(SaveAs);		//adds a 'Save As' option to the File menu
		file.addSeparator();		//adds a seperator
		
		for (int i = 0; i < 4; i++)	//for loop that increments thorough the file items
			file.getItem(i).setIcon(null);	//and sets each icon to 'null'
		
		edit.add(Cut);			//adds a 'Cut' option to the Edit menu
		edit.add(Copy);			//adds a 'Copy' option to the Edit menu
		edit.add(Paste);		//adds a 'Paste' option to the Edit menu
		
		edit.getItem(0).setText("Cut");		//Sets the text for the items in the Edit menu
		edit.getItem(1).setText("Copy");
		edit.getItem(2).setText("Paste");
		
		JToolBar tool = new JToolBar();	//creates a new JToolBar called tool
		add(tool, BorderLayout.NORTH);	//adds the toolbar to the top of the window
		tool.add(New);			//adds a 'New' option to the toolbar
		tool.add(Open);			//adds a 'Open' option to the toolbar
		tool.add(Save);			//adds a 'Save' option to the toolbar
		tool.addSeparator();		//adds a seperator
		
		JButton cut = tool.add(Cut), cop = tool.add(Copy), pas = tool.add(Paste);
		//creates JButtons for the Cut, Copy and Paste operations 
		
		cut.setText(null);	//sets the text of the 'cut' button to null
		cut.setIcon(new ImageIcon(this.getClass().getResource("cut.gif")));	//sets the image for the 'cut' button
		cop.setText(null);	//sets the text of the 'copy' button to null
		cop.setIcon(new ImageIcon(this.getClass().getResource("copy.gif")));	//sets the image for the 'copy' button
		pas.setText(null);	//sets the text of the 'paste' button to null
		pas.setIcon(new ImageIcon(this.getClass().getResource("paste.gif")));	//sets the image for the 'paste' button
		
		Save.setEnabled(false);		//disables the 'Save' button by default 
		SaveAs.setEnabled(false);	//disables the 'Save As' button by default
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		pack();
		area.addKeyListener(k1);	//adds a key listener to the area
		setTitle(currentFile);		//sets the title to the title of the current file
		setVisible(true);		//sets the area's visiblity (visible)
	}
	
	private KeyListener k1 = new KeyAdapter() {	//creates a new Key Listener to detect user input
		public void keyPressed(KeyEvent e) {	//if a key has been pressed...
			changed = true;			//the boolean 'changed' is set to true
			Save.setEnabled(true);		//the Save button is enabled
			SaveAs.setEnabled(true);	//the Save As button is enabled
		}
	};
	
	//Action 1 (New)
	Action New = new AbstractAction("New", new ImageIcon(this.getClass().getResource("new.gif"))) {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			area.setText("");
			currentFile = "Untitled";
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
			SaveAs.setEnabled(false);
		}
	};
	
	//Action 2 (Open)
	Action Open = new AbstractAction("Open", new ImageIcon(this.getClass().getResource("open.gif"))) {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			if(dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				readInFile(dialog.getSelectedFile().getAbsolutePath());
			}
			SaveAs.setEnabled(true);
		}
	};
	
	Action Save = new AbstractAction("Save", new ImageIcon(this.getClass().getResource("save.gif"))) {
		public void actionPerformed(ActionEvent e) {
			if (!currentFile.equals("Untitled"))
				saveFile(currentFile);
			else
				saveFileAs();
		}
	};
	
	Action SaveAs = new AbstractAction("Save as..."){
		public void actionPerformed(ActionEvent e) {
			saveFileAs();
		}
	};
	
	Action Quit = new AbstractAction("Quit") {
		public void actionPerformed(ActionEvent e) {
			saveOld();
			System.exit(0);
		}
	};
	
	ActionMap m = area.getActionMap();
	Action Cut = m.get(DefaultEditorKit.cutAction);
	Action Copy = m.get(DefaultEditorKit.copyAction);
	Action Paste = m.get(DefaultEditorKit.pasteAction);
	
	private void saveFileAs() {
		if(dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			saveFile(dialog.getSelectedFile().getAbsolutePath());
	}
	
	private void saveOld() {
		if(changed) {
			if(JOptionPane.showConfirmDialog(this, "Would you like to save " + currentFile + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				saveFile(currentFile);
		}
	}
	
	private void readInFile(String fileName) {
		try {
			FileReader r = new FileReader(fileName);
			area.read(r, null);
			r.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
		}
		catch(IOException e) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(this, "Editor can't find the file named " + fileName);
		}
	}
	
	private void saveFile(String fileName){
		try {
			FileWriter w = new FileWriter(fileName);
			area.write(w);
			w.close();
			currentFile = fileName;
			setTitle(currentFile);
			changed = false;
			Save.setEnabled(false);
		}
		catch (IOException e) {
			
		}
	}
	
	public static void main(String[] arg) {
		new CloudNote();
	}
}
