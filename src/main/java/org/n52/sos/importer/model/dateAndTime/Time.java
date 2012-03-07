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
package org.n52.sos.importer.model.dateAndTime;

import org.n52.sos.importer.interfaces.Combination;
import org.n52.sos.importer.interfaces.Component;
import org.n52.sos.importer.interfaces.MissingComponentPanel;
import org.n52.sos.importer.view.dateAndTime.MissingTimePanel;

/**
 * aggregates hour, minute and second
 * @author Raimund
 */
public class Time extends Component {

	private Hour hour;
	
	private Minute minute;
	
	private Second second;

	public void setHour(Hour hour) {
		this.hour = hour;
	}

	public Hour getHour() {
		return hour;
	}

	public void setMinute(Minute minute) {
		this.minute = minute;
	}

	public Minute getMinute() {
		return minute;
	}

	public void setSecond(Second second) {
		this.second = second;
	}

	public Second getSecond() {
		return second;
	}
	
	@Override
	public MissingComponentPanel getMissingComponentPanel(Combination c) {
		return new MissingTimePanel((DateAndTime)c);
	}
}