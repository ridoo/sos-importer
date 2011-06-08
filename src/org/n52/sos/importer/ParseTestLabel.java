package org.n52.sos.importer;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;

public class ParseTestLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private Parser parser;
	
	public ParseTestLabel(Parser parser) {
		super();
		this.parser = parser;
	}
	
	public void parseValues (List<String> values) {
		int notParseableValues = 0;
		StringBuilder notParseable = new StringBuilder();
		Set<String> notParseableStrings = new HashSet<String>();
		notParseable.append("<html>");

		for (String value: values) {
			try {
				parser.parse(value);
			} catch (Exception e) {
				if (notParseableStrings.add(value))
					notParseable.append(value + "<br>");
				notParseableValues++;
			}
		}
		
		String text = "";
		if (notParseableValues == 0) {
			text = "All values parseable.";
			this.setForeground(Color.blue);
		} else if (notParseableValues == 1) {
			text = "1 value not parseable.";
			this.setForeground(Color.red);
		} else {
			text = notParseableValues + " values not parseable.";
			this.setForeground(Color.red);
		}
		
		this.setText("<html><u>" + text+ "</u></html>");
		
		notParseable.append("</html>");
		this.setToolTipText(notParseable.toString());
	}			
}