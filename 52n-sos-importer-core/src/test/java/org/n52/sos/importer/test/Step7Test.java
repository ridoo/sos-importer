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
package org.n52.sos.importer.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.n52.sos.importer.controller.DateAndTimeController;
import org.n52.sos.importer.controller.MainController;
import org.n52.sos.importer.controller.Step7Controller;
import org.n52.sos.importer.controller.TableController;
import org.n52.sos.importer.model.ModelStore;
import org.n52.sos.importer.model.dateAndTime.DateAndTime;
import org.n52.sos.importer.model.dateAndTime.Second;
import org.n52.sos.importer.model.dateAndTime.TimeZone;
import org.n52.sos.importer.model.measuredValue.NumericValue;
import org.n52.sos.importer.model.position.EPSGCode;
import org.n52.sos.importer.model.position.Height;
import org.n52.sos.importer.model.position.Latitude;
import org.n52.sos.importer.model.position.Longitude;
import org.n52.sos.importer.model.position.Position;
import org.n52.sos.importer.model.resources.FeatureOfInterest;
import org.n52.sos.importer.model.resources.ObservedProperty;
import org.n52.sos.importer.model.resources.Sensor;
import org.n52.sos.importer.model.resources.UnitOfMeasurement;
import org.n52.sos.importer.model.table.Column;

public class Step7Test {
	public static void main(final String[] args) {
		final Object[][] o = {
				{"01/06/2010", "11:45", "12,12", "23,123"},
				{"01/06/2010", "23:45", "323,123", "432,123"}};
		TableController.getInstance().setContent(o); 
		final int firstLineWithData = 0;
		
		final DateAndTime dtm1 = new DateAndTime();
		dtm1.setGroup("1");
		final DateAndTimeController dtc1 = new DateAndTimeController(dtm1);
		dtc1.assignPattern("dd/MM/yyyy", new Column(0,firstLineWithData));
		ModelStore.getInstance().add(dtm1);
		
		final DateAndTime dtm2 = new DateAndTime();
		dtm2.setGroup("1");
		final DateAndTimeController dtc2 = new DateAndTimeController(dtm1);
		dtc2.assignPattern("HH:mm", new Column(1,firstLineWithData));
		ModelStore.getInstance().add(dtm2);
		
		dtc2.mergeDateAndTimes();
		
		final DateAndTime dtm = ModelStore.getInstance().getDateAndTimes().get(0);
		dtm.setSecond(new Second(0));
		dtm.setTimeZone(new TimeZone(0));

		final Position p = new Position();
		p.setGroup("A");
		final Latitude lat = new Latitude(52.5, "°");
		final Longitude lon = new Longitude(7.5, "°");
		final Height h = new Height(100, "m");
		final EPSGCode epsgCode = new EPSGCode(4236);
		p.setLatitude(lat);
		p.setLongitude(lon);
		p.setHeight(h);
		p.setEPSGCode(epsgCode);
		
		final ObservedProperty op = new ObservedProperty();
		op.setName("Temperature");
		final UnitOfMeasurement uom = new UnitOfMeasurement();
		uom.setName("degC");
		final FeatureOfInterest foi = new FeatureOfInterest();
		foi.setName("Weatherstation Muenster");
		final Sensor sn = new Sensor();
		sn.setName("Thermometer 1");
		
		final Sensor sn2 = new Sensor();
		sn2.setName("Thermometer 2");
		try {
			sn2.setURI(new URI("http://thermo.org/123"));
		} catch (final URISyntaxException e) {
			e.printStackTrace();
		}
		
		final NumericValue nv1 = new NumericValue();
		nv1.setTableElement(new Column(1,firstLineWithData));
		nv1.setDateAndTime(dtm);
		nv1.setObservedProperty(op);
		nv1.setFeatureOfInterest(foi);
		nv1.setSensor(sn);
		nv1.setUnitOfMeasurement(uom);
		
		final NumericValue nv2 = new NumericValue();
		nv2.setTableElement(new Column(2,firstLineWithData));
		nv2.setDateAndTime(dtm);
		nv2.setObservedProperty(op);
		nv2.setFeatureOfInterest(foi);
		nv2.setSensor(sn2);
		nv2.setUnitOfMeasurement(uom);	
		
		foi.setPosition(p);
		
		ModelStore.getInstance().add(nv1);
		ModelStore.getInstance().add(nv2);
		ModelStore.getInstance().add(foi);
		
		final MainController f = MainController.getInstance();
		
		f.setStepController(new Step7Controller());
	}
}
