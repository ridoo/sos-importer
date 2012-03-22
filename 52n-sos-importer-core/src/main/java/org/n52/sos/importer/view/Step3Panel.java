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
package org.n52.sos.importer.view;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.n52.sos.importer.model.measuredValue.Boolean;
import org.n52.sos.importer.model.measuredValue.Count;
import org.n52.sos.importer.model.measuredValue.Text;
import org.n52.sos.importer.model.resources.FeatureOfInterest;
import org.n52.sos.importer.model.resources.ObservedProperty;
import org.n52.sos.importer.model.resources.Sensor;
import org.n52.sos.importer.model.resources.UnitOfMeasurement;
import org.n52.sos.importer.view.step3.DateAndTimeCombinationPanel;
import org.n52.sos.importer.view.step3.NumericValuePanel;
import org.n52.sos.importer.view.step3.MeasuredValueSelectionPanel;
import org.n52.sos.importer.view.step3.PositionCombinationPanel;
import org.n52.sos.importer.view.step3.RadioButtonPanel;
import org.n52.sos.importer.view.step3.ResourceSelectionPanel;
import org.n52.sos.importer.view.step3.SelectionPanel;
import org.n52.sos.importer.view.utils.ToolTips;

/**
 * consists of the table and a radio button panel for 
 * different types of metadata
 * @author Raimund
 *
 */
public class Step3Panel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel rootPanel = new JPanel();
	private JPanel additionalPanel1 = new JPanel();
	private JPanel additionalPanel2 = new JPanel();
	private TablePanel tablePanel = TablePanel.getInstance();
	private final SelectionPanel radioButtonPanel;
	
	public Step3Panel(int firstLineWithData) {
		super();
		radioButtonPanel = new RootPanel(firstLineWithData);	
		radioButtonPanel.getContainerPanel().add(radioButtonPanel);
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
	    this.add(tablePanel);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.add(rootPanel);
		buttonPanel.add(additionalPanel1);
		buttonPanel.add(additionalPanel2);
		this.add(buttonPanel);
	}
	
	public void clearAdditionalPanels() {
		additionalPanel1.removeAll();
		additionalPanel2.removeAll();
	}
	
	/**
	 * Get the last child panel of <b><code>this</code></b> {@link org.n52.sos.importer.view.step3.SelectionPanel}
	 * The last one should contain the user input.
	 * @return
	 */
	public SelectionPanel getLastChildPanel() {
		SelectionPanel lastChildPanel = radioButtonPanel;
		SelectionPanel nextPanel = radioButtonPanel.getSelectedChildPanel();
		while (nextPanel != null) {
			lastChildPanel = nextPanel;
			nextPanel = nextPanel.getSelectedChildPanel();
		}
		return lastChildPanel;
	}
	
	/**
	 * Stores the current selection in the {@linkplain org.n52.sos.importer.model.ModelStore}
	 * instance.
	 * @param selection list of all selected items, e.g. column type and the corresponding meta data
	 */
	public void store(List<String> selection) {
		radioButtonPanel.store(selection);
	}
	
	public void restore(List<String> selection) {
		radioButtonPanel.restore(selection);
	}
	
	public void restoreDefault() {
		radioButtonPanel.restoreDefault();
	}
	
	private class RootPanel extends RadioButtonPanel {
		
		private static final long serialVersionUID = 1L;
		
		/**
		 * First panel presenting a column with radio buttons to define the
		 * column type
		 * @param firstLineWithData required for the test parsing results
		 */
		public RootPanel(int firstLineWithData) {	
			super(rootPanel);
			addRadioButton("Undefined");
			addRadioButton("Measured Value", ToolTips.get("MeasuredValue"), new MeasuredValuePanel(firstLineWithData));
			addRadioButton("Date & Time", ToolTips.get("DateAndTime"), new DateAndTimePanel(firstLineWithData));
			addRadioButton("Position", ToolTips.get("Position"), new PositionPanel(firstLineWithData));
			addRadioButton("Feature of Interest", ToolTips.get("FeatureOfInterest"), new ResourceSelectionPanel(additionalPanel1, new FeatureOfInterest()));
			addRadioButton("Observed Property", ToolTips.get("ObservedProperty"), new ResourceSelectionPanel(additionalPanel1, new ObservedProperty()));
			addRadioButton("Unit of Measurement", ToolTips.get("UnitOfMeasurement"), new ResourceSelectionPanel(additionalPanel1, new UnitOfMeasurement()));
			addRadioButton("Sensor", ToolTips.get("Sensor"), new ResourceSelectionPanel(additionalPanel1, new Sensor()));
			addRadioButton("Do not export");				
		}

		private class MeasuredValuePanel extends RadioButtonPanel {

			private static final long serialVersionUID = 1L;
			
			/**
			 * JPanel for the definition of the measure value type
			 * @param firstLineWithData required for the test parsing results
			 */
			public MeasuredValuePanel(int firstLineWithData) {	
				super(additionalPanel1);		
				addRadioButton("Numeric Value", ToolTips.get("NumericValue"), new NumericValuePanel(additionalPanel2, firstLineWithData));
				addRadioButton("Count", ToolTips.get("Count"), new MeasuredValueSelectionPanel(additionalPanel2, new Count(),firstLineWithData));
				addRadioButton("Boolean", ToolTips.get("Boolean"), new MeasuredValueSelectionPanel(additionalPanel2, new Boolean(),firstLineWithData));
				addRadioButton("Text", ToolTips.get("Text"), new MeasuredValueSelectionPanel(additionalPanel2, new Text(),firstLineWithData));
			}	
		}
		
		private class DateAndTimePanel extends RadioButtonPanel {

			private static final long serialVersionUID = 1L;
			
			/**
			 * JPanel for the definition of the date time type
			 * @param firstLineWithData required for the test parsing results
			 */
			public DateAndTimePanel(int firstLineWithData) {
				super(additionalPanel1);
				addRadioButton("Combination", null, new DateAndTimeCombinationPanel(additionalPanel2, firstLineWithData));
				addRadioButton("UNIX time");
			}	
		}

		private class PositionPanel extends RadioButtonPanel {

			private static final long serialVersionUID = 1L;
			
			/**
			 * JPanel for the definition of the position type
			 * @param firstLineWithData required for the test parsing results
			 */
			public PositionPanel(int firstLineWithData) {
				super(additionalPanel1);	
				addRadioButton("Combination", null, new PositionCombinationPanel(additionalPanel2, firstLineWithData));
			}
		}	
	}		
}
