package javaMail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectDialog extends JDialog {
	
	
	private JTextField username;
	private JPasswordField password;
	
	public ConnectDialog(JFrame frame) {
		super(frame, true);
		
		setTitle("StringConnection");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				cancelAction();
			}
		});
		
		JPanel stringConnection = new JPanel();
		stringConnection.setBorder(BorderFactory.createTitledBorder("Connection Strings"));
		
		GridBagLayout layout = new GridBagLayout();
		stringConnection.setLayout(layout);
		
		GridBagConstraints gbc = new GridBagConstraints();

		JLabel usernameLab = new JLabel("Username: ");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(5, 5, 0, 0);
		stringConnection.add(usernameLab, gbc);
		
		username = new JTextField(15);
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(5, 5, 0, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		stringConnection.add(username, gbc);
		
		JLabel passwordLab = new JLabel("Password: ");
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(5, 5, 5, 0);
		stringConnection.add(passwordLab, gbc);
		
		password = new JPasswordField();
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		stringConnection.add(password, gbc);
		
		
		JPanel buttonsPanel = new JPanel();
		
		JButton connect = new JButton("Connect");	
		connect.addActionListener(ae -> actionConnect());
		
		buttonsPanel.add(connect);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(ae -> cancelAction());
		
		buttonsPanel.add(cancel);
			
		setLayout(new BorderLayout());
		add(stringConnection, BorderLayout.CENTER);
		add(buttonsPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(frame);
	}
	
	public String getUsername() {
		return username.getText();
	}
	
	public String getUserPassword() {
		return new String(password.getPassword());
	}
	
	
	public void cancelAction() {
		System.exit(0);
	}
	
	private void actionConnect() {
		char[] passwordChars = password.getPassword();
		
		if(username.getText().trim().length() < 1 || passwordChars.toString().length() < 1) {
			JOptionPane.showInputDialog(this, "one or two fields are missing", "Missing Field(s)",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		dispose();
	}
 
}
