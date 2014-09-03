package asol.fin.uploader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

	UploaderProperties prop;
	Reader reader;
	Writer writer;

	public Uploader(Reader reader, Writer writer) {
		this.reader = reader;
		this.writer = writer;
	}

	public void publishPresentation(Writer writer, PresentationInfo info)
			throws Exception {
		String data = info.getStringData();
		Parser parser = new FinDataParser(data);
		Table table = parser.getData();
		writer.write(table, info);
	}

	public void setup() throws Exception {
		this.prop = new UploaderProperties();
		prop.loadProperties();
		writer.init(prop.getExport_path());
		reader.setup(prop);
	}

	public void upload() throws Exception {

		if (reader.readData()) {
			for (PresentationInfo presentationInfo : reader) {
				L.info(presentationInfo);
				try {
					publishPresentation(writer, presentationInfo);
				} catch (Exception e) {
					String tracefile = presentationInfo.flushData2TraceFile();
					L.fatal(String.format(
							"Error with presentation: %d %s - tracefile: %s",
							presentationInfo.getId(),
							presentationInfo.getName(), tracefile), e);
				}
			}
		}
	}

	void checkPasswordAndExit() {
		EncryptedPassword cipher = new EncryptedPassword();
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
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			try {
				L.info("Entering.");

				PresentationReader reader = new PresentationReader();
				XLSXWriter writer = new XLSXWriter();

				Uploader u = new Uploader(reader, writer);

				u.setup();
				u.checkPasswordAndExit();
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