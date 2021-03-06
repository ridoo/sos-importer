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
package org.n52.sos.importer.controller;

import java.util.List;

import javax.swing.JPanel;

import org.n52.sos.importer.model.Component;
import org.n52.sos.importer.model.ModelStore;
import org.n52.sos.importer.model.Step6aModel;
import org.n52.sos.importer.model.StepModel;
import org.n52.sos.importer.model.dateAndTime.DateAndTime;
import org.n52.sos.importer.model.measuredValue.MeasuredValue;
import org.n52.sos.importer.view.MissingComponentPanel;
import org.n52.sos.importer.view.Step5Panel;
import org.n52.sos.importer.view.i18n.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lets the user choose date&time for all measured value columns 
 * in case there are not any date&times given in the CSV file
 * @author Raimund
 *
 */
public class Step6aController extends StepController {
	
	private static final Logger logger = LoggerFactory.getLogger(Step6aController.class);
	
	private Step6aModel step6aModel;
	
	private Step5Panel step5Panel;
	
	private DateAndTimeController dateAndTimeController;
	
	private final TableController tableController;

	private final int firstLineWithData;
	
	public Step6aController(final int firstLineWithData) {
		this.firstLineWithData = firstLineWithData;
		tableController = TableController.getInstance();
	}
	
	public Step6aController(final Step6aModel step6aModel,final int firstLineWithData) {
		this(firstLineWithData);
		this.step6aModel = step6aModel;
	}

	@Override
	public void loadSettings() {
		tableController.turnSelectionOff();
		
		final DateAndTime dateAndTime = step6aModel.getDateAndTime();
		dateAndTimeController = new DateAndTimeController(dateAndTime);
		final List<Component> components = step6aModel.getMissingDateAndTimeComponents();
		dateAndTimeController.setMissingComponents(components);
		
		for (final MeasuredValue mv: ModelStore.getInstance().getMeasuredValues()) {
			mv.setDateAndTime(null);
		}
		
		dateAndTimeController.unassignMissingComponentValues();
		
		final String description = step6aModel.getDescription();
		final List<MissingComponentPanel> mcp = dateAndTimeController.getMissingComponentPanels();
		step5Panel = new Step5Panel(description, mcp);
	}

	@Override
	public void saveSettings() {
		dateAndTimeController.assignMissingComponentValues();	
		
		final List<Component> components = dateAndTimeController.getMissingComponents();
		step6aModel.setMissingDateAndTimeComponents(components);

		final DateAndTime dateAndTime = dateAndTimeController.getDateAndTime();
		
		for (final MeasuredValue mv: ModelStore.getInstance().getMeasuredValues()) {
			mv.setDateAndTime(dateAndTime);
		}
		
		tableController.turnSelectionOn();
		
		step5Panel = null;
	}

	@Override
	public void back() {
		tableController.turnSelectionOn();
		
		step5Panel = null;
	}
	
	@Override
	public String getDescription() {
		return Lang.l().step6aDescription();
	}

	@Override
	public JPanel getStepPanel() {
		return step5Panel;
	}

	@Override
	public StepController getNextStepController() {
		return new Step6bController(firstLineWithData);
	}

	@Override
	public boolean isNecessary() {
		final int n = ModelStore.getInstance().getDateAndTimes().size();
		if (n == 0) {
			final DateAndTime dateAndTime = new DateAndTime();
			step6aModel = new Step6aModel(dateAndTime);
			return true;
		}
		
		logger.info("Skip Step 6a since there is at least one Date&Time");
			
		return false;
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public StepController getNext() {
		return null;
	}

	@Override
	public StepModel getModel() {
		return step6aModel;
	}

}
