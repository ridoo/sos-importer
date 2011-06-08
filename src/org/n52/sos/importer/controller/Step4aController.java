package org.n52.sos.importer.controller;

import java.awt.Color;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.n52.sos.importer.bean.MeasuredValue;
import org.n52.sos.importer.bean.Resource;
import org.n52.sos.importer.bean.Store;
import org.n52.sos.importer.view.Step4aPanel;

public class Step4aController {

	private static final Logger logger = Logger.getLogger(Step4aController.class);
	
	private Resource resource;
	
	private TableController tableController;
	
	private Step4aPanel step4aView;
	
	public Step4aController(Resource resource) {
		this.resource = resource;
		tableController = TableController.getInstance();
		tableController.allowMultipleSelection();
		tableController.addMultipleSelectionListener(new SelectionChanged());
		
		String text = "";
		switch(Store.getInstance().getTableOrientation()) {
		case TableController.COLUMNS:
			tableController.allowColumnSelection();
			tableController.colorColumn(Color.yellow, resource.getColumnNumber());
			text = "Mark all measured value columns where this " + resource + 
				" column corresponds to.";
			break;
		case TableController.ROWS: 
			tableController.allowRowSelection();
			tableController.colorRow(Color.yellow, resource.getRowNumber());
			text = "Mark all measured value rows where this " + resource + 
				" row corresponds to.";
			break;
		}
		step4aView = new Step4aPanel(text);
	}

	
	public JPanel getView() {
		return step4aView;
	}
	
	public void next() {
		int[] selectedColumns = tableController.getSelectedColumns();
		
		for (int column: selectedColumns) {
			MeasuredValue mv = Store.getInstance().getMeasuredValueAtColumn(column);
			resource.assign(mv);
		}
		Resource r = Store.getInstance().pollResourceWithoutMeasuredValue();
		if (r != null) new Step4aController(r);
		else new Step4bController(); 	
	}
	
	private class SelectionChanged implements TableController.MultipleSelectionListener {

		@Override
		public void columnSelectionChanged(int[] selectedColumns) {
			for (int column: selectedColumns) {
				MeasuredValue mv = Store.getInstance().getMeasuredValueAtColumn(column);
				if (mv == null) {
					logger.error("This is not a measured value.");
					tableController.deselectColumn(column);
					return;
				}

				if (resource.isAssigned(mv)) {
					logger.error(resource + " already set for this measured value.");
					tableController.deselectColumn(column);
					return;
				}
			}
		}

		@Override
		public void rowSelectionChanged(int[] selectedRows) {
			// TODO Auto-generated method stub
			
		}	
	}	
}