package com.ttv.at;

import java.awt.Color;
import java.util.Date;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;

public class guiutil {

	static public void txtOutput_append_8black(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.black);
			StyleConstants.setFontSize(txtOutput_style, 8);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}

			// txtOutput.setText(txtOutput.getText() + "\n" + message);
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_8blue(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.blue);
			StyleConstants.setFontSize(txtOutput_style, 8);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_8green(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.green);
			StyleConstants.setFontSize(txtOutput_style, 8);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_8red(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 8);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10black(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.black);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10blue(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.blue);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10blue(String message, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.blue);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);
			
			try {
				txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);

				if (link_after_failed != null && link_after_failed.length() > 0)
					txtOutput_addHyperlink(link_after_failed, " desktop capturing", txtOutput, txtOutput_doc);
			}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10green(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.green);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10red(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10red(String message, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try {
				txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);

				if (link_after_failed != null && link_after_failed.length() > 0)
					txtOutput_addHyperlink(link_after_failed, " desktop capturing", txtOutput, txtOutput_doc);
			}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10red(String message, String link_before_fail, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			txtOutput_addHyperlink(link_before_fail, " -- before failed action", txtOutput, txtOutput_doc);
			txtOutput_addHyperlink(link_after_failed, " -- after failed action", txtOutput, txtOutput_doc);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10magenta(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.magenta);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10magenta(String message, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.magenta);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			try {
				txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);

				if (link_after_failed != null && link_after_failed.length() > 0)
					txtOutput_addHyperlink(link_after_failed, " desktop capturing", txtOutput, txtOutput_doc);
			}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10magenta(String message, String link_before_fail, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.magenta);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, false);

			txtOutput_addHyperlink(link_before_fail, " -- before failed action", txtOutput, txtOutput_doc);
			txtOutput_addHyperlink(link_after_failed, " -- after failed action", txtOutput, txtOutput_doc);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10black_bold(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.black);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}

			// txtOutput.setText(txtOutput.getText() + "\n" + message);
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10blue_bold(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.blue);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10green_bold(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.green);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10magenta_bold(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.magenta);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10magenta_bold(String message, String link_before_fail, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.magenta);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			txtOutput_addHyperlink(link_before_fail, " -- before failed action", txtOutput, txtOutput_doc);
			txtOutput_addHyperlink(link_after_failed, " -- after failed action", txtOutput, txtOutput_doc);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10red_bold(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_10red_bold(String message, String link_before_fail, String link_after_failed, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 10);
			StyleConstants.setBold(txtOutput_style, true);

			txtOutput_addHyperlink(link_before_fail, " -- before failed action", txtOutput, txtOutput_doc);
			txtOutput_addHyperlink(link_after_failed, " -- after failed action", txtOutput, txtOutput_doc);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12black(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.black);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}

			// txtOutput.setText(txtOutput.getText() + "\n" + message);
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12black_3lines(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.black);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n\n\n" + message,txtOutput_style);}
			catch (BadLocationException e){}

			// txtOutput.setText(txtOutput.getText() + "\n" + message);
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12blue(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.blue);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12green(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.green);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message, txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12red(String message, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try { txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}
	static public void txtOutput_append_12magenta_bold_for_log(String msg_text, String log_path, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			if (log_path != null && log_path.length() > 0) {
				txtOutput_addHyperlink(log_path, msg_text, txtOutput, txtOutput_doc, Color.magenta, 12);
				txtOutput_scroll_to_end(msg_text.length(), txtOutput);
			}
		}
	}
	static public void txtOutput_append_12red(String message, String before_failed_image, String after_failed_image, JTextPane txtOutput, Style txtOutput_style, StyledDocument txtOutput_doc) {
		if (txtOutput != null) {
			StyleConstants.setForeground(txtOutput_style, Color.red);
			StyleConstants.setFontSize(txtOutput_style, 12);
			StyleConstants.setBold(txtOutput_style, false);

			try {
				txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + message,txtOutput_style);

				if (before_failed_image != null && before_failed_image.length() > 0)
					txtOutput_addHyperlink(before_failed_image, " -- before failed action", txtOutput, txtOutput_doc);
				if (after_failed_image != null && after_failed_image.length() > 0)
					txtOutput_addHyperlink(after_failed_image, " -- after failed action", txtOutput, txtOutput_doc);
			}
			catch (BadLocationException e){}
			txtOutput_scroll_to_end(message.length(), txtOutput);
		}
	}

	static public void txtOutput_addHyperlink(String url, String text, JTextPane txtOutput, StyledDocument txtOutput_doc) {
		try {
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setUnderline(attrs, true);
			StyleConstants.setForeground(attrs, Color.red);
			attrs.addAttribute(HTML.Attribute.ID, ((Long)((new Date()).getTime())).toString());
			attrs.addAttribute(HTML.Attribute.HREF, url);
			txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + text, attrs);
		}
		catch (BadLocationException e) {
			e.printStackTrace(System.err);
		}
	}

	static public void txtOutput_addHyperlink(String url, String text, JTextPane txtOutput, StyledDocument txtOutput_doc, Color foreGroundColor, int fontSize) {
		try {
			SimpleAttributeSet attrs = new SimpleAttributeSet();
			StyleConstants.setUnderline(attrs, true);
			StyleConstants.setForeground(attrs, foreGroundColor);
			StyleConstants.setFontSize(attrs, fontSize);
			attrs.addAttribute(HTML.Attribute.ID, ((Long)((new Date()).getTime())).toString());
			attrs.addAttribute(HTML.Attribute.HREF, url);
			txtOutput_doc.insertString(txtOutput_doc.getLength(), "\n" + text, attrs);
		}
		catch (BadLocationException e) {
			e.printStackTrace(System.err);
		}
	}
	static public void txtOutput_scroll_to_end(JTextPane txtOutput) {
		txtOutput.setCaretPosition( txtOutput.getDocument().getLength());
	}
	static public void txtOutput_scroll_to_end(int last_line_length, JTextPane txtOutput) {
		txtOutput.setCaretPosition(txtOutput.getDocument().getLength() - last_line_length + 1);
	}

}
