package javaMail;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.mail.*;
import javax.mail.internet.InternetAddress;

public class MessageDialog extends JDialog{
	
	private JTextField toField, subjectField;
	private JTextArea contentArea;
	
	private boolean cancelled = false;
	
	public MessageDialog(JFrame parent, MessageType type, Message message) throws Exception {
		super(parent, true);
		
		String subject = "", content = "", to = "";
		
		switch(type) {
		case REPLY -> {
			setTitle("Reply Message");
			
			Address[] address = message.getFrom();
			to = ((InternetAddress) address[0]).getAddress();
		
			subject = "RE: " + message.getSubject();
			
			content = "\n------------" +
						"REPLIED TO MESSAGE" +
					   "------------\n" +
						EmailClient.getMessageContent(message);	
		}
		case FORWARD -> {
			setTitle("Forward Message");
			
			subject = "FW: " + message.getSubject();
			
			content = "\n------------" +
					"FORWARDED MESSAGE" +
					   "------------\n" +
						EmailClient.getMessageContent(message);	
		}
		case NEW -> setTitle("New Message");
		}
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				actionCancel();
			}
		});
		
		JPanel fieldsPanel = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		GridBagLayout layout = new GridBagLayout();
		
		fieldsPanel.setLayout(layout);
		
		JLabel toLabel = new JLabel("To: ");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(5, 5, 0, 0);
		fieldsPanel.add(toLabel, gbc);
		
		toField = new JTextField(to);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(5, 5, 0, 0);
		gbc.weightx = 1;
		fieldsPanel.add(toField, gbc);
		
		JLabel subjectLabel = new JLabel("Subject: ");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(5, 5, 5, 0);
		fieldsPanel.add(subjectLabel, gbc);
		
		subjectField = new JTextField(subject);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER; 
		fieldsPanel.add(subjectField, gbc);
		
		
		contentArea = new JTextArea(content, 10, 50);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(contentArea);
		
		
		JPanel buttonsPanel = new JPanel();
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> actionCancel());
		buttonsPanel.add(cancel);
		
		JButton send = new JButton("Send");
		send.addActionListener(ae -> sendAction());
		buttonsPanel.add(send);
		
		setLayout(new BorderLayout());
		add(fieldsPanel, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(null);
	}
	
	public void sendAction() {
		if(toField.getText().trim().length() < 1 || subjectField.getText().trim().length() < 1 || 
			contentArea.getText().trim().length() < 1) {	
			JOptionPane.showMessageDialog(this, "There are empty fields", "Field(s) missing", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		dispose();
	}
	
	public void actionCancel() {
		cancelled = true;
		
		System.exit(0);
	}
	
	
	public boolean display() {
		setVisible(true);
		
		return !cancelled;
		
	}
	
	public String getTo() {
		return toField.getText();
	}
	
	public String getSubject() {
		return subjectField.getText();
	}
	
	public String getContent() {
		return contentArea.getText();
	}
}
