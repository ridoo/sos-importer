package org.n52.sos.importer.view.step3;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.n52.sos.importer.Combination;
import org.n52.sos.importer.EditableJComboBoxPanel;
import org.n52.sos.importer.controller.TableController;

public abstract class CombinationPanel extends SelectionPanel {
	//source: 	http://download.oracle.com/javase/tutorial/uiswing/
	// 			examples/components/ComboBoxDemo2Project/src/components/
	// 			ComboBoxDemo2.java
	private static final long serialVersionUID = 1L;

	private JLabel groupLabel = new JLabel("Group: ");
	private JComboBox groupComboBox = new JComboBox(getGroupItems());
	
	private EditableJComboBoxPanel patternComboBox = new EditableJComboBoxPanel(getPatterns(), "Format");
	
    private JLabel exampleLabel = new JLabel("Example: ");
    private final ExampleFormatLabel exampleFormatLabel = new ExampleFormatLabel(getCombination());
	
    private final ParseTestLabel parseTestLabel = new ParseTestLabel(getCombination());
   
	public CombinationPanel(JPanel containerPanel) {	
		super(containerPanel);
		
		setDefaultSelection();
		patternComboBox.addActionListener(new FormatChanged());
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		formatPanel.add(patternComboBox);
							
		JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		groupPanel.add(groupLabel);
		groupPanel.add(groupComboBox);
		
		JPanel examplePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		examplePanel.add(exampleLabel);
		examplePanel.add(exampleFormatLabel);
		
		JPanel dateTimePanel = new JPanel();
		dateTimePanel.setLayout(new BoxLayout(dateTimePanel, BoxLayout.PAGE_AXIS));
		dateTimePanel.add(formatPanel);	
		dateTimePanel.add(examplePanel);
		dateTimePanel.add(groupPanel);
		this.add(dateTimePanel);
		this.add(parseTestLabel);
	}
	
	public abstract Combination getCombination();
	
	public abstract String[] getGroupItems();
	
	public abstract DefaultComboBoxModel getPatterns();
	
	public abstract Object getTestValue();

    @Override
	public void setSelection(String s) {
		patternComboBox.setSelectedItem(s);
    	selectionChanged();
	}
	
	@Override
	public void setDefaultSelection() {
		patternComboBox.setSelectedItem((String)getPatterns().getElementAt(0));
		getCombination().
		setPattern(getSelection());
	}
	
	@Override
	public String getSelection() {
		return (String)patternComboBox.getSelectedItem();
	}
    
    @Override
    protected void selectionChanged() {
    	getCombination().setPattern(getSelection());
    	parseTestLabel.parseValues(TableController.getInstance().getMarkedValues());
    	exampleFormatLabel.reformat(getTestValue());
    }
    
    @Override
    protected void reinit() {
    	parseTestLabel.parseValues(TableController.getInstance().getMarkedValues());
    	exampleFormatLabel.reformat(getTestValue());
    };
  	    
	private class FormatChanged implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
	        selectionChanged();
	    }
	}
}
