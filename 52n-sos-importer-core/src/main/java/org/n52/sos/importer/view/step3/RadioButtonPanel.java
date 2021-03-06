/**
 * Copyright (C) 2012
 * by 52North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.sos.importer.view.step3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.n52.sos.importer.model.table.TableElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * used for all radio buttons in step 3
 * @author Raimund
 *
 */
public abstract class RadioButtonPanel extends SelectionPanel {
	
	private static final long serialVersionUID = 1L;
	
	protected ButtonGroup group = new ButtonGroup();
	
	private static final Logger logger = LoggerFactory.getLogger(RadioButtonPanel.class);
	
	public RadioButtonPanel(final JPanel containerPanel) {
		super(containerPanel);
        setLayout(new GridLayout(0, 1));
	}
	
	/**
	 * creates a radio button with the given name on the panel
	 * @param name
	 */
	protected void addRadioButton(final String name) {
		final JRadioButton radioButton = new JRadioButton(name);
		radioButton.addActionListener(new RemoveChildPanel());
		if (group.getButtonCount() == 0) {
			radioButton.setSelected(true);
		}
		group.add(radioButton);
		add(radioButton);
	}
	
	/**
	 * creates a radio button with the given name on the panel,
	 * when this button is pressed the given selection panel appears
	 * @param name
	 */
	protected void addRadioButton(final String name, final String toolTip, final SelectionPanel childPanel) {
		final JRadioButton radioButton = new JRadioButton(name);
		if (toolTip != null) {
			radioButton.setToolTipText(toolTip);
		}
		addChildPanel(name, childPanel);
		radioButton.addActionListener(new AddChildPanel(childPanel));
		if (group.getButtonCount() == 0) {
			radioButton.setSelected(true);
			setSelectedChildPanel(childPanel);
		}
		group.add(radioButton);
		this.add(radioButton);
	}

	@Override
	public void setSelection(final String s) {
		ButtonModel m = null;
		final Enumeration<AbstractButton> e = group.getElements();
		while(e.hasMoreElements()) {
			final JRadioButton b = (JRadioButton) e.nextElement();
			if (s.equals(b.getText())) {
				m = b.getModel();
				break;
			}
		}	
		group.setSelected(m, true);	
	}
	
	@Override
	public void setDefaultSelection() {
		final JRadioButton firstButton = (JRadioButton) group.getElements().nextElement();
		group.setSelected(firstButton.getModel(), true);
		setSelectedChildPanel(firstButton.getText());
	}

	@Override
	public String getSelection() {
		final Enumeration<AbstractButton> e = group.getElements();
		while(e.hasMoreElements()) {
			final JRadioButton b = (JRadioButton) e.nextElement();
			if (b.isSelected()) {
				return b.getText();
			}
		}
		return null;
	}
	
	@Override
	public void assign(final TableElement tableElement) {}
	
	@Override
	public void unAssign(final TableElement tableElement) {}
	
	/**
	 * action when a radio button with selection panel is pressed
	 * @author Raimund
	 */
	private class AddChildPanel implements ActionListener {
		SelectionPanel childPanel;
		
		public AddChildPanel(final SelectionPanel childPanel) {
			this.childPanel = childPanel;
		}
		
		@Override
		public void actionPerformed(final ActionEvent arg0) {
			if (logger.isTraceEnabled()) {
				logger.trace("actionPerformed(cmd: " + arg0.getActionCommand() + ")");
			}
			if (getSelectedChildPanel() != null) {
				getSelectedChildPanel().removeFromContainerPanel();
			}
			setSelectedChildPanel(childPanel);
			childPanel.addToContainerPanel();		
			
			revalidate();
			patternChanged();
		}	
	}
	
	/**
	 * Action when a radio button without any child panels is pressed
	 * @author Raimund
	 */
	private class RemoveChildPanel implements ActionListener {
		
		@Override
		public void actionPerformed(final ActionEvent e) {
			if (getSelectedChildPanel() != null) {
				getSelectedChildPanel().removeFromContainerPanel();
				revalidate();
				final SelectionPanel childPanel = null;
				setSelectedChildPanel(childPanel);
			}
			patternChanged();
		}		
	}
}
