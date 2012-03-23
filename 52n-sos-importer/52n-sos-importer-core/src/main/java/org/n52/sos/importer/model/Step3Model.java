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
package org.n52.sos.importer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

public class Step3Model implements StepModel{
	
	private static final Logger logger = Logger.getLogger(Step3Model.class);
	
	private int markedColumn;
	private List<String> selection;
	private HashMap<Integer, List<String>> columnAssignments;
	
	private final int firstLineWithData;
	private final boolean useHeader;
	
	public Step3Model(int markedColumn,int firstLineWithData, boolean useHeader) {
		this.markedColumn = markedColumn;
		this.firstLineWithData = firstLineWithData;
		this.useHeader = useHeader;
		this.columnAssignments = new HashMap<Integer, List<String>>();
//		selection = new ArrayList<String>();
//		selection.add("Undefined");
	}

	/**
	 * saves the current selection of the radio button panel for the current
	 * marked column
	 * @param selection the selection by the user for the current column
	 */
	public boolean addSelection(List<String> selection) {
		if (logger.isTraceEnabled()) {
			logger.trace("addSelection()");
		}
		this.columnAssignments.put(this.markedColumn, selection);
		List<String> addedValue = this.columnAssignments.get(this.markedColumn);
		if (logger.isDebugEnabled()) {
			logger.debug("Next two values should be equal: ");
			logger.debug("addedValue: " + addedValue);
			logger.debug("selection : " + selection);
			logger.debug("==? " + (addedValue==selection));
			logger.debug("equals? " + addedValue.equals(selection));
		}
		if(addedValue == selection && addedValue.equals(selection)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Returns the stored selection (column assignment) for the given column
	 * @param colIndex index of the column
	 * @return a <code>List&lt;String&gt;</code>
	 */
	public List<String> getSelectionForColumn(int colIndex){
		if (logger.isTraceEnabled()) {
			logger.trace("getSelectionForColumn(colIndex:=" + colIndex +")");
		}
		List<String> value = this.columnAssignments.get(colIndex);
		if (logger.isDebugEnabled()) {
			logger.debug("found selection: " + value);
		}
		return value;
	}
	/**
	 * Returns all selections (column assignments) stored in this model
	 * @return a <code>HashMap&lt;Integer,List&lt;String&gt;&gt;</code>
	 */
	protected HashMap<Integer,List<String>> getAllSelections(){
		if (logger.isTraceEnabled()) {
			logger.trace("getAllSelections()");
		}
		return this.columnAssignments;
	}
/*
	public void setSelection(List<String> selection) {
		if (logger.isTraceEnabled()) {
			logger.trace("setSelection(): " + selection);
		}
		this.selection = selection;
	}
*/
	/**
	 * returns the saved selection of the radio button panel
	 */
//	public List<String> getSelection() { return selection; }
	/*
	 * 	simple getter and setter
	 */
	public int getFirstLineWithData() {	return firstLineWithData; }
	public int getMarkedColumn() { return markedColumn; }
	public void setMarkedColumn(int colIndex) { this.markedColumn = colIndex; }
	public boolean getUseHeader() { return useHeader; }
}
