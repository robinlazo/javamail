package javaMail;

import java.awt.BorderLayout;

import java.awt.Cursor;
import java.awt.event.*;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.*;
import javax.swing.*;
import javax.swing.event.*;

public class EmailClient extends JFrame {
	
	private JSplitPane splitPane;
	
	private MessagesTableModel tableModel;
	
	private JTextArea messageTextArea;
	
	private JTable table;
	
	private Message selectedMessage;
	
	private JButton forwardButton, replyButton, deleteButton;
	
	private String user, password;
	
	private Session senderSession;
	
	private String sendMessageHost = "smtp.gmail.com";
	private String retrieveMessageHost = "pop.gmail.com";
	
	private boolean deleting = false;
	
	public EmailClient() {
		
		setTitle("E-mail Client");
	
		setSize(640, 480);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				actionCancel();
			}
		});
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_C);	
		exitItem.addActionListener((e) -> actionCancel());
		
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);
		
		setJMenuBar(menuBar);
		
		JPanel newMessagePane = new JPanel();
		
		JButton newMessage = new JButton("New Message");
		newMessage.addActionListener((e) -> newMessage());
		newMessagePane.add(newMessage);
		
		JPanel tablePanel = new JPanel();
		tablePanel.setBorder(BorderFactory.createTitledBorder("E-mails"));
		
		tableModel = new MessagesTableModel();
		table = new JTable(tableModel);	
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent le) {
				tableSelectionChanged();
			}
		});	
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		messageTextArea = new JTextArea();
		messageTextArea.setEditable(false);
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), new JScrollPane(messageTextArea));
		
		tablePanel.setLayout(new BorderLayout());
		tablePanel.add(splitPane, BorderLayout.CENTER);
		
		JPanel buttonsPanel = new JPanel();
		
		forwardButton = new JButton("Forward");
		forwardButton.addActionListener((e) -> actionForward());
		forwardButton.setEnabled(false);
		buttonsPanel.add(forwardButton);
		
		replyButton = new JButton("Reply");
		replyButton.addActionListener((e) -> actionReply());
		replyButton.setEnabled(false);
		buttonsPanel.add(replyButton);
		
		deleteButton = new JButton("Delete");
		deleteButton.addActionListener((e) -> actionDelete());
		deleteButton.setEnabled(false);
		buttonsPanel.add(deleteButton);
		
		setLayout(new BorderLayout());
		add(newMessagePane, BorderLayout.NORTH);
		add(tablePanel, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
		
		setLocationRelativeTo(null);
	}
	
	public void tableSelectionChanged() {
		if(!deleting) {
			selectedMessage = tableModel.getMessage(table.getSelectedRow());
			showSelectedMessage();
			updateButtons();
		}
	}
	
	public void updateButtons() {
		if(selectedMessage != null) {
			forwardButton.setEnabled(true);
			replyButton.setEnabled(true);
			deleteButton.setEnabled(true);
		} else {
			forwardButton.setEnabled(false);
			replyButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
	}
	
	public void showSelectedMessage() {
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		try {
			messageTextArea.setText(getMessageContent(selectedMessage));
			messageTextArea.setCaretPosition(0);
		} catch(Exception e) {
			System.out.println(e);
		}
		
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	public static String getMessageContent(Message message) throws Exception {
		Object content = message.getContent();
		
		if(content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			StringBuffer messageContent = new StringBuffer();
			for(int i = 0; i < multipart.getCount(); i++) {
				Part part = multipart.getBodyPart(i);
				
				if(part.isMimeType("text/plain")) {
					messageContent.append(part.getContent().toString());
				}		
			}		
			return messageContent.toString();
		} else {
			return content.toString();
		}
		
	}
	
	public void sendMessage(MessageType type, Message message) {
		MessageDialog dialog = null;
		try {
			dialog = new MessageDialog(this, type, message);
			if(!dialog.display()) return;
		} catch(Exception e) {
			showErrorMessage("Unable to send message", false);
			return;
		}
		
		try {
			Message userMessage = new MimeMessage(senderSession);
			userMessage.setSubject(dialog.getSubject());
			userMessage.setText(dialog.getContent());
			userMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(dialog.getTo()));
			userMessage.setFrom(new InternetAddress(user));	
			
			Transport transport = senderSession.getTransport();
			transport.connect(null, password);
			transport.sendMessage(userMessage, userMessage.getAllRecipients());	
			transport.close();
			
		} catch(Exception e) {
			showErrorMessage("Unable to send message.", false);
		}
		
	}
	
	public void initMessageSenderSession() {
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtps");
		props.put("mail.smtps.host", sendMessageHost);
		props.put("mail.smtps.user", user);
		
		senderSession = Session.getInstance(props);
	}
	
	public void connect() {
		ConnectDialog dialog = new ConnectDialog(this);	
		
		dialog.setVisible(true);
		
		user = dialog.getUsername();
		password = dialog.getUserPassword();
		
		initMessageSenderSession();//init properties of session to send messages
		
		DownloadingDialog downloadDialog = new DownloadingDialog(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				downloadDialog.setVisible(true);
			}
		});
		
		Store store = null;
		try {
			Properties props = new Properties();
			props.put("mail.pop3.host", retrieveMessageHost);
			props.put("mail.pop3.port", "995");
			props.put("mail.pop3.starttls.enable", "true");
			
			Session session = Session.getInstance(props);
			session.setDebug(true);
			store = session.getStore("pop3s");
			
			store.connect(retrieveMessageHost, user, password);
			
		} catch(Exception e) {
			downloadDialog.dispose();
			showErrorMessage("Unable to connect to server", false);
		}
		
		try {
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);
			
			Message[] messages = folder.getMessages();
			
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			
			folder.fetch(messages, profile);
			
			tableModel.setMessages(messages);
			
		} catch(Exception e) {			
			downloadDialog.dispose();
			showErrorMessage("Unable to find messages", false);
		}
		
		downloadDialog.dispose();
	} 
	
	public void showErrorMessage(String message, boolean exit) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
		
		if(exit)
			System.exit(0);
	}
	
	public void newMessage() {
		sendMessage(MessageType.NEW, null);
	}
	
	public void actionDelete() {
		deleting = true;
		
		try {
			selectedMessage.setFlag(Flags.Flag.DELETED, true);
			Folder folder = selectedMessage.getFolder();
			folder.close(true);
			folder.open(Folder.READ_WRITE);
		} catch(Exception e) {
			showErrorMessage("Unable to delete message", false);
		}
		
		tableModel.deleteMessage(table.getSelectedRow());
		
		messageTextArea.setText("");
		deleting = false;
		selectedMessage = null;
		updateButtons();
	}
	
	public void show() {
		super.show();
		
		splitPane.setDividerLocation(.5);
	}
	
	public void actionReply() {
		sendMessage(MessageType.REPLY, selectedMessage);
	}
	
	public void actionForward() {
		sendMessage(MessageType.FORWARD, selectedMessage);
	}
	
	public void actionCancel() {
		System.exit(0);
	}
	
	public static void main(String args[]) {
		
		EmailClient emailClient = new EmailClient();
		emailClient.show();
		
		emailClient.connect();
	}

}
