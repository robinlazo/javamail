package javaMail;

import javax.swing.*;

public class DownloadingDialog extends JDialog{
	
	public DownloadingDialog(JFrame frame) {
		super(frame, true);
		
		setTitle("Dowloading");
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel panel = new JPanel();	
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.add(new JLabel("Downloading messages..."));
		
		add(panel);
		
		pack();
		setLocationRelativeTo(frame);
	}
}
