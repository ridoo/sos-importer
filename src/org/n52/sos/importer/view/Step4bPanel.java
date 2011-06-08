package org.n52.sos.importer.view;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Step4bPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JLabel markingLabel = new JLabel();
	
	private final JPanel tablePanel = TablePanel.getInstance();
	
	public Step4bPanel(String text) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		markingLabel.setText(text);
		markingLabel.setBackground(Color.yellow);
		markingLabel.setOpaque(true);
		this.add(markingLabel);
		
		this.add(tablePanel);
	}
}