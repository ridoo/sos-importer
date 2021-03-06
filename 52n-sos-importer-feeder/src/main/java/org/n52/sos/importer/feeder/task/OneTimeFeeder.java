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
package org.n52.sos.importer.feeder.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPHTTPClient;
import org.apache.xmlbeans.XmlException;
import org.n52.oxf.OXFException;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.sos.importer.feeder.Configuration;
import org.n52.sos.importer.feeder.DataFile;
import org.n52.sos.importer.feeder.SensorObservationService;
import org.n52.sos.importer.feeder.model.requests.InsertObservation;
import org.n52.sos.importer.feeder.util.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO if failed observations-> store in file
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk J&uuml;rrens</a>
 *
 */
// TODO refactor to abstract class: move getRemoteFile to FTPOneTimeFeeder
public class OneTimeFeeder implements Runnable {

	private final Configuration config;

	private DataFile dataFile;

	private static final Logger LOG = LoggerFactory.getLogger(OneTimeFeeder.class);

	public OneTimeFeeder(final Configuration config) {
		this.config = config;
	}

	public OneTimeFeeder(final Configuration config, final File datafile) {
		this.config = config;
		dataFile = new DataFile(config, datafile);
	}

	private DataFile getRemoteFile(final Configuration config) {
		File dataFile = null;

		// ftp client
		FTPClient client;

		// proxy
		final String pHost = System.getProperty("proxyHost", "proxy");
		int pPort = -1;
		if (System.getProperty("proxyPort") != null) {
			pPort = Integer.parseInt(System.getProperty("proxyPort"));
		}
		final String pUser = System.getProperty("http.proxyUser");
		final String pPassword = System.getProperty("http.proxyPassword");
		if (pHost != null && pPort != -1) {
			LOG.info("Using proxy for FTP connection!");
			if (pUser != null && pPassword != null) {
				client = new FTPHTTPClient(pHost, pPort, pUser, pPassword);
			}
			client = new FTPHTTPClient(pHost, pPort);
		} else {
			LOG.info("Using no proxy for FTP connection!");
			client = new FTPClient();
		}

		// get first file
		final String directory = config.getConfigFile().getAbsolutePath();
		dataFile = FileHelper.createFileInImporterHomeWithUniqueFileName(directory + ".csv");

		// if back button was used: delete old file
		if (dataFile.exists()) {
			dataFile.delete();
		}

		try {
			client.connect(config.getFtpHost());
			final boolean login = client.login(config.getUser(), config.getPassword());
			if (login) {
				LOG.info("FTP: connected...");
				// download file
				final int result = client.cwd(config.getFtpSubdirectory());
				LOG.info("FTP: go into directory...");
				if (result == 250) { // successfully connected
					final FileOutputStream fos = new FileOutputStream(dataFile);
					LOG.info("FTP: download file...");
					client.retrieveFile(config.getFtpFile(), fos);
					fos.flush();
					fos.close();
				} else {
					LOG.info("FTP: cannot go to subdirectory!");
				}
				final boolean logout = client.logout();
				if (!logout) {
					LOG.info("FTP: cannot logout!");
				}
			} else {
				LOG.info("FTP:  cannot login!");
			}

		} catch (final SocketException e) {
			LOG.error("The file you specified cannot be obtained.");
			return null;
		} catch (final IOException e) {
			LOG.error("The file you specified cannot be obtained.");
			return null;
		}

		return new DataFile(config, dataFile);
	}

	@Override
	public void run() {
		LOG.trace("run()");
		LOG.info("Starting feeding data from file via configuration '{}' to SOS instance", config.getFileName());
		// csv / ftp
		if (config.isRemoteFile()) {
			dataFile = getRemoteFile(config);
		} else if (dataFile == null){
			dataFile = new DataFile(config, config.getDataFile());
		}
		if (dataFile == null) {
			LOG.error("No datafile was found!");
		}
		if (dataFile.isAvailable()) {
			try {
				// check SOS
				SensorObservationService sos = null;
				final String sosURL = config.getSosUrl().toString();
				try {
					sos = new SensorObservationService(config);
				} catch (final ExceptionReport er) {
					LOG.error("SOS " + sosURL + " is not available. Please check the configuration!", er);
				} catch (final OXFException oxfe) {
					LOG.error("SOS " + sosURL + " is not available. Please check the configuration!", oxfe);
				}
				if (sos == null || !sos.isAvailable()) {
					LOG.error(String.format("SOS '%s' is not available. Please check the configuration!", sosURL));
				} else if (!sos.isTransactional()){
					LOG.error(String.format("SOS '%s' does not support required transactional operations!", sosURL));
				} else {
					LOG.debug("Create counter file");
					final String directory = dataFile.getFileName();
					File counterFile = null;
					String fileName = null;
					if (config.isRemoteFile()) {
						fileName = directory + "_counter";
					} else {
						fileName = config.getConfigFile().getCanonicalPath() +
								"_" +
								dataFile.getCanonicalPath() +
								"_counter";
					}
					counterFile = FileHelper.createFileInImporterHomeWithUniqueFileName(fileName);
					// read already inserted line count
					if (counterFile.exists()) {
						LOG.debug("Get already read lines");
						final Scanner sc = new Scanner(counterFile);
						final int count = sc.nextInt();
						sos.setLastLine(count);
					}

					// SOS is available and transactional
					// start reading data file line by line starting from flwd
					final List<InsertObservation> failedInserts = sos.importData(dataFile);

					LOG.debug("OneTimeFeeder: save read lines count: {}", sos.getLastLine());
					// override counter file
					final FileWriter counterFileWriter = new FileWriter(counterFile.getAbsoluteFile());
					final PrintWriter out = new PrintWriter(counterFileWriter);
					out.println(sos.getLastLine());
					out.close();

					saveFailedInsertObservations(failedInserts);
					LOG.info("Feeding data from file {} to SOS instance finished.",dataFile.getFileName());
				}
			} catch (final MalformedURLException mue) {
				LOG.error("SOS URL syntax not correct in configuration file '{}'. Exception thrown: {}",
						config.getFileName(),
						mue.getMessage());
				LOG.debug("Exception Stack Trace:", mue);
			} catch (final IOException e) {
				log(e);
			} catch (final OXFException e) {
				log(e);
			} catch (final XmlException e) {
				log(e);
			} catch (final ParseException e) {
				log(e);
			} catch (final IllegalArgumentException e) {
				log(e);
			}
		}
	}

	private void log(final Exception e)
	{
		LOG.error("Exception thrown: {}", e.getMessage());
		LOG.debug("Exception Stack Trace:", e);
	}

	/*
	 * Method should store failed insertObservations in a defined directory and
	 * created configuration for this.
	 */
	private void saveFailedInsertObservations(
			final List<InsertObservation> failedInserts) throws IOException {
		// TODO Auto-generated method stub generated on 25.06.2012 around 11:39:44 by eike
		LOG.trace("saveFailedInsertObservations() <-- NOT YET IMPLEMENTED");
//		// TODO save failed InsertObservations via ObjectOutputStream
//		final String fileName = config.getConfigFile().getCanonicalPath() +
//		"_" +
//		dataFile.getCanonicalPath() +
//		"_failedObservations";
//		// TODO define name of outputfile
	}

}
