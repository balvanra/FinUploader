package asol.fin.uploader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

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

	EncryptedPassword cipher;
	UploaderProperties prop;
	DBHelper db;
	PresentationReader reader;
	Writer writer;

	public Uploader() {
	}

	public void publishPresentation(Reader reader, Writer writer,
			PresentationInfo info) throws Exception {
		String data = reader.readData();
		Parser parser = new FinDataParser(data);
		Table table = parser.getData();
		writer.write(table, info);
	}

	public void setup() throws Exception {
		this.prop = new UploaderProperties();
		prop.loadProperties();

		reader = new PresentationReader();
		writer = new XLSXWriter(prop.getExport_path());

		this.cipher = new EncryptedPassword();
		String decryptedPassword = null;


		String encryptedPassword = prop.getDest_db_pwd();
		if (encryptedPassword == null) {
			System.out.println("Zadajte heslo do db: ");
			Scanner in = null;
			String pwd;
			try {
				in = new Scanner(System.in);
				pwd = in.nextLine();
			} finally {
				if (in != null)
					in.close();
			}
			encryptedPassword = cipher.getEncryptedPassword(pwd);
			System.out.println("Zapiste riadok do "
					+ UploaderProperties.CONF_NAME);
			System.out.println(String.format("dest_db_pwd=%s",
					encryptedPassword));
			System.exit(0);
		} else {
			decryptedPassword = cipher.getDecryptedPassword(encryptedPassword);

			db = new DBHelper(prop.getDest_db_user(), decryptedPassword,
					prop.getDest_db(), prop.getCurrent_user());
		}

	}

	public void upload() throws Exception {

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
				publishPresentation(reader, writer, presentationInfo);
			} catch (Exception e) {
				String fname = "trace_" + presentationInfo.getId() + ".srd";
				L.fatal(String.format(
						"Error with presentation: %d %s - tracefile: %s",
						presentationInfo.getId(), presentationInfo.getName(),
						fname), e);
				Files.write(Paths.get(fname), presentationInfo.getData(),
						StandardOpenOption.CREATE);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			try {
				L.info("Entering.");

				Uploader u = new Uploader();

				u.setup();
				u.upload();

				/**
				 * String sourceFile = args[0]; Reader reader = new
				 * FileReader(sourceFile);
				 * 
				 */

				L.info("Exiting");
			} catch (Exception e) {
				L.error(e.getMessage(), e);
			}
		} catch (Throwable t) {
			L.error(t.getMessage(), t);
		}
	}

}