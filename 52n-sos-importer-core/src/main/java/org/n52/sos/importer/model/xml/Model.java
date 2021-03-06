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
package org.n52.sos.importer.model.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oxf.xmlbeans.parser.XMLBeansParser;
import org.n52.sos.importer.Constants;
import org.n52.sos.importer.model.Step1Model;
import org.n52.sos.importer.model.Step2Model;
import org.n52.sos.importer.model.Step3Model;
import org.n52.sos.importer.model.Step4aModel;
import org.n52.sos.importer.model.Step4bModel;
import org.n52.sos.importer.model.Step5aModel;
import org.n52.sos.importer.model.Step5cModel;
import org.n52.sos.importer.model.Step6aModel;
import org.n52.sos.importer.model.Step6bModel;
import org.n52.sos.importer.model.Step6bSpecialModel;
import org.n52.sos.importer.model.Step6cModel;
import org.n52.sos.importer.model.Step7Model;
import org.n52.sos.importer.model.StepModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sensorweb.sos.importer.x02.DataFileDocument.DataFile;
import org.x52North.sensorweb.sos.importer.x02.LocalFileDocument.LocalFile;
import org.x52North.sensorweb.sos.importer.x02.RemoteFileDocument.RemoteFile;
import org.x52North.sensorweb.sos.importer.x02.SosImportConfigurationDocument;
import org.x52North.sensorweb.sos.importer.x02.SosImportConfigurationDocument.SosImportConfiguration;

/**
 * In this class the XML model for an CSV file is stored for later re-use by
 * another application.
 * 
 * @author e.h.juerrens@52north.org
 * @since 0.2
 * @version
 * 
 */
public class Model {

	private static final Logger logger = LoggerFactory.getLogger(Model.class);

	private final SosImportConfiguration sosImpConf;

	private StepModel[] stepModells = new StepModel[1];

	/**
	 * Create a new and empty model
	 */
	public Model() {
		sosImpConf = SosImportConfiguration.Factory.newInstance();
	}

	/**
	 * Create model based on xml file
	 * 
	 * @param xmlFileWithModel
	 *            the file containing the <code>Model</code>
	 * @throws XmlException
	 *             thrown while parsing the file &rarr; <code>Model</code>
	 *             file is <b>not</b> valid.
	 * @throws IOException
	 *             having any problems while reading file
	 */
	public Model(final File xmlFileWithModel) throws XmlException, IOException {
		final SosImportConfigurationDocument sosImpConfDoc = SosImportConfigurationDocument.Factory
				.parse(xmlFileWithModel);
		sosImpConf = sosImpConfDoc.getSosImportConfiguration();
	}

	/**
	 * Create model based on an existing one
	 * 
	 * @param sosImpConf
	 */
	public Model(final SosImportConfiguration sosImpConf) {
		this.sosImpConf = sosImpConf;
	}

	public String getFileName() {
		if (logger.isTraceEnabled()) {
			logger.trace("getFileName()");
		}
		final DataFile df = sosImpConf.getDataFile();
		String result = null;
		if (df != null) {
			if (df.isSetLocalFile()) {
				final LocalFile lf = df.getLocalFile();
				result = lf.getPath();
				result = result.substring(result.lastIndexOf(File.separatorChar)+1);
			} else if (df.isSetRemoteFile()) {
				final RemoteFile rf = df.getRemoteFile();
				result = rf.getURL();
				result = result.substring(result.lastIndexOf("/")+1);
			}
		}
		return result;
	}

	public boolean registerProvider(final StepModel sm) {
		if (logger.isTraceEnabled()) {
			logger.trace("registerProvider(" + 
					(sm.getClass().getSimpleName()==null?
							sm.getClass():
								sm.getClass().getSimpleName()) +
					")");
		}
		//
		ArrayList<StepModel> sMs;
		//
		sMs = createArrayListFromArray(stepModells);
		final boolean result = sMs.add(sm);
		saveProvidersInArray(sMs);
		//
		return result;
	}

	public boolean removeProvider(final StepModel sm) {
		if (logger.isTraceEnabled()) {
			logger.trace("removeProvider(" +  
					(sm.getClass().getSimpleName()==null? 
							sm.getClass(): 
								sm.getClass().getSimpleName()) + 
								")");
		}
		//
		ArrayList<StepModel> provider;
		//
		provider = createArrayListFromArray(stepModells);
		final boolean result = provider.remove(sm);
		saveProvidersInArray(provider);
		//
		return result;
	}

	public boolean save(final File file) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("save(" + file!=null?file.getName():file + ")");
		}
		// laxValidate or validate?
		if (!laxValidate() ||
				sosImpConf.getCsvMetadata() == null || 
				sosImpConf.getDataFile() == null ||
				sosImpConf.getSosMetadata() == null) {
			return false;
		}
		//
		// check write access to file
		if (file != null) {
			if (!file.exists()) {
				if (logger.isDebugEnabled()) {
					logger.debug("File " + file
							+ " does not exist. Try to create it.");
				}
				if (!file.createNewFile()) {
					logger.error("Could not create file " + file);
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("File " + file + " created");
					}
				}
			}
			if (file.isFile()) {
				if (file.canWrite()) {
					final SosImportConfigurationDocument doc = 
						SosImportConfigurationDocument.Factory.newInstance();
					// insert schema location
					final XmlCursor c = sosImpConf.newCursor();
					c.toFirstChild();
					c.insertNamespace(Constants.XML_SCHEMA_PREFIX,
							Constants.XML_SCHEMA_NAMESPACE);
					c.insertAttributeWithValue(Constants.XML_SCHEMALOCATION_QNAME,
							Constants.XML_SOS_IMPORTER_SCHEMA_LOCATION);
					final XmlOptions xmlOpts = new XmlOptions()
						.setSavePrettyPrint()
						.setSavePrettyPrintIndent(4)
						.setUseDefaultNamespace();
					doc.setSosImportConfiguration(sosImpConf);
					doc.save(file, xmlOpts);
					return true;
				} else {
					logger.error("model not saved: could not write to file: "
							+ file);
				}
			} else {
				logger.error("model not saved: file is not a file: " + file);
			}
		} else {
			logger.error("model not saved: file is null");
		}
		// }
		return false;
	}

	/**
	 * Updates the model. Should be called when one of the providers has
	 * changed.
	 */
	public void updateModel() {
		if (logger.isTraceEnabled()) {
			logger.trace("updateModel()");
		}
		/*
		 * check each provider and update the internal model
		 * using ModelHandler for each StepModel
		 */
		
		if (stepModells != null && stepModells.length > 0) {
			//
			for (final StepModel sm : stepModells) {
				//
				if (sm instanceof Step1Model) {
					//
					final Step1Model s1M = (Step1Model) sm;
					new Step1ModelHandler().handleModel(s1M, sosImpConf);
					//
				} else if (sm instanceof Step2Model) {
					//
					final Step2Model s2M = (Step2Model) sm;
					new Step2ModelHandler().handleModel(s2M, sosImpConf);
					//
				} else if (sm instanceof Step3Model) {
					//
					final Step3Model s3M = (Step3Model) sm;
					new Step3ModelHandler().handleModel(s3M, sosImpConf);
					//
				} else if (sm instanceof Step4aModel) {
					//
					final Step4aModel s4aM = (Step4aModel) sm;
					new Step4aModelHandler().handleModel(s4aM, sosImpConf);
					//
				} else if (sm instanceof Step4bModel) {
					//
					final Step4bModel s4bM = (Step4bModel) sm;
					new Step4bModelHandler().handleModel(s4bM, sosImpConf);
					//
				} else if (sm instanceof Step5aModel) {
					//
					final Step5aModel s5aM = (Step5aModel) sm;
					new Step5aModelHandler().handleModel(s5aM, sosImpConf);
					//
				} else if (sm instanceof Step5cModel) {
					//
					final Step5cModel s5cM = (Step5cModel) sm;
					new Step5cModelHandler().handleModel(s5cM, sosImpConf);
					//
				} else if (sm instanceof Step6aModel) {
					//
					final Step6aModel s6aM = (Step6aModel) sm;
					new Step6aModelHandler().handleModel(s6aM, sosImpConf);
					//
				} else if (sm instanceof Step6bModel) {
					//
					final Step6bModel s6bM = (Step6bModel) sm;
					new Step6bModelHandler().handleModel(s6bM, sosImpConf);
					//
				} else if (sm instanceof Step6bSpecialModel) {
					//
					final Step6bSpecialModel s6bSM = (Step6bSpecialModel) sm;
					new Step6bSpecialModelHandler().handleModel(s6bSM, sosImpConf);
					//
				} else if (sm instanceof Step6cModel) {
					//
					final Step6cModel s6cM = (Step6cModel) sm;
					new Step6cModelHandler().handleModel(s6cM, sosImpConf);
					//
				} else if (sm instanceof Step7Model) {
					//
					final Step7Model s7M = (Step7Model) sm;
					new Step7ModelHandler().handleModel(s7M, sosImpConf);
				}
			}
		}
	}

	/**
	 * Should be called after final step to validate the final model.
	 * 
	 * @return
	 */
	public boolean validate() {
		if (logger.isTraceEnabled()) {
			logger.trace("validate()");
		}
		//
		final SosImportConfigurationDocument doc = SosImportConfigurationDocument.Factory.newInstance();
		doc.setSosImportConfiguration(sosImpConf);
		final boolean modelValid = doc.validate();
		if (!modelValid) {
			logger.error("The model is not valid. Please update your values.");
		}
		return modelValid;
	}
	
	public boolean laxValidate() {
		final SosImportConfigurationDocument doc = SosImportConfigurationDocument.Factory.newInstance();
		doc.setSosImportConfiguration(sosImpConf);
		final Collection<XmlError> exs = XMLBeansParser.validate(doc);
		for (final XmlError xmlError : exs) {
			logger.error("Xml error: ",xmlError);
		}
		return (exs.size() == 0)? true : false;
	}

	/*
	 * Private Helper methods for provider and model handling
	 */
	private ArrayList<StepModel> createArrayListFromArray(final StepModel[] models) {
		if (logger.isTraceEnabled()) {
			logger.trace("\tcreateArrayListFromArray()");
		}
		//
		ArrayList<StepModel> result;
		//
		result = new ArrayList<StepModel>(stepModells.length + 1);
		for (final StepModel stepModel : stepModells) {
			if (stepModel != null) {
				result.add(stepModel);
			}
		}
		result.trimToSize();
		//
		return result;
	}

	private void saveProvidersInArray(final ArrayList<StepModel> aL) {
		if (logger.isTraceEnabled()) {
			logger.trace("\tsaveProvidersInArray()");
		}
		//
		aL.trimToSize();
		stepModells = aL.toArray(new StepModel[aL.size()]);
	}

}
