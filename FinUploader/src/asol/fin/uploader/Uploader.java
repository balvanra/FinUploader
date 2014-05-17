package asol.fin.uploader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import asol.fin.uploader.parser.FinDataParser;
import asol.fin.uploader.parser.Parser;
import asol.fin.uploader.reader.PresentationReader;
import asol.fin.uploader.reader.Reader;
import asol.fin.uploader.writer.Writer;
import asol.fin.uploader.writer.XLSXWriter;

public class Uploader {

	private static Logger L = LogManager.getLogger(Uploader.class.getName());

	public Uploader() {
	}

	public void publishPresentation(Reader reader, Writer writer,
			PresentationInfo info) throws Exception {
		String data = reader.readData();
		Parser parser = new FinDataParser(data);
		Table table = parser.getData();
		writer.write(table, info);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			try {
				L.info("Entering.");

				UploaderProperties prop = new UploaderProperties();
				Uploader u = new Uploader();

				prop.loadProperties();

				DBHelper db = new DBHelper(prop.getDest_db_user(),
						prop.getDest_db_pwd(), prop.getDest_db(),
						prop.getCurrent_user());

				// String sourceFile = args[0];
				// Reader reader = new FileReader(sourceFile);
				PresentationReader reader = new PresentationReader();
				Writer writer = new XLSXWriter(prop.getExport_path());

				ArrayList<PresentationInfo> pl = null;

				if (prop.isRefresh_presentations()) {
					pl = db.fetchPresentations(false);
					for (PresentationInfo presentationInfo : pl) {
						L.debug(presentationInfo);
						db.refreshPresentation(presentationInfo);
					}
				}

				pl = db.fetchPresentations(true);

				for (PresentationInfo presentationInfo : pl) {
					L.info(presentationInfo);
					try {
						reader.setInfo(presentationInfo);
						u.publishPresentation(reader, writer, presentationInfo);
					} catch (Exception e) {
						String fname = "trace_" + presentationInfo.getId()
								+ ".srd";
						L.fatal(String
								.format("Error with presentation: %d %s - tracefile: %s",
										presentationInfo.getId(),
										presentationInfo.getName(), fname), e);
						Files.write(Paths.get(fname),
								presentationInfo.getData(),
								StandardOpenOption.CREATE);
					}
				}

				L.info("Exiting");
			} catch (Exception e) {
				L.error(e.getMessage(), e);
			}
		} catch (Throwable t) {
			L.error(t.getMessage(), t);
		}
	}

}