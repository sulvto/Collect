package me.qinchao;

import static org.junit.Assert.*;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.junit.Test;

public class Other {
	private static String url="http://www.baidu.com";
	public static void main(String[] args) {
		try{
		aaa();
		}catch(Exception e){
			
		}
	}

	
	   

	  public static void aaa()  throws Exception{
	    JEditorPane editorPane = new JEditorPane();
	    editorPane.setEditable(false);
	    editorPane.setSize(200, 400);
	    editorPane.setPage(url);
	    JScrollPane pane = new JScrollPane(editorPane);
	    JFrame frame = new JFrame("PRINT");
	    frame.setResizable(false);
	    frame.setLocation(500, 400);
	    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    frame.setContentPane(pane);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(500, 500);
	    frame.setVisible(true);


	
	}
}
