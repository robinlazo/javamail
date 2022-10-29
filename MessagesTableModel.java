package javaMail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.*;

import javax.swing.table.AbstractTableModel;

public class MessagesTableModel extends AbstractTableModel {

	private final String[] columNames = { "Sender", "Subject", "Date" };

	private List<Message> listMessages = new ArrayList<>();

	public MessagesTableModel() {

	}

	public int getRowCount() {
		return listMessages.size();
	}
	
	public void setMessages(Message[] messages) {
		for(int i = messages.length - 1; i >= 0; i--) {
			listMessages.add(messages[i]);
		}
		
		fireTableDataChanged();
	}
	
	public String getColumnName(int col) {
		return columNames[col];
	}
	
	public void deleteMessage(int row) {
		listMessages.remove(row);
		
		fireTableRowsDeleted(row, row);
	}

	public Message getMessage(int row) {
		return listMessages.get(row);
	}
	
	public int getColumnCount() {
		return columNames.length;
	}

	public Object getValueAt(int row, int col) {

		Message message = getMessage(row);

		String result = "";
		
		try {
			switch (col) {
			case 0 -> {
				Address[] senders = message.getFrom();
				result = senders.length > 0 ? senders[0].toString() : "[none]";
			}
			case 1 -> {
				String subject = message.getSubject();
				result = subject.length() > 0 ? subject : "[none]";
			}
			case 2 -> {
				Date date = message.getSentDate();
				result = date == null ? date.toString() : "[none]";
			}

			}
		} catch (Exception e) {
			result = "";
		}
		return result;
	}
}
