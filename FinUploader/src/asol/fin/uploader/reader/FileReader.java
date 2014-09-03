package asol.fin.uploader.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;

import asol.fin.uploader.DBHelper;
import asol.fin.uploader.PresentationInfo;
import asol.fin.uploader.UploaderProperties;

public class FileReader implements Reader {
	public static Logger L = LogManager.getLogger(FileReader.class.getName());

	String pathData;
	ArrayList<PresentationInfo> pl = new ArrayList<PresentationInfo>();

	public String getPathData() {
		return pathData;
	}

	public void setPathData(String pathData) {
		this.pathData = pathData;
	}

	public FileReader(String pathData) {
		L.trace("pathData:" + pathData);
		this.setPathData(pathData);
	}

	@Override
	public boolean readData() throws Exception {
		L.trace("readData");
		// L.entry();
		// String data = readFile(getPathData(), Charset.defaultCharset());
		// L.trace(data);
		// L.exit();

		PresentationInfo i = new PresentationInfo();
		i.setId(120134);
		i.setAuthor("ddd user");
		i.setName("nazov prezentacie");
		i.setCreated(new Date());
		i.setRefreshed(new Date());
		byte[] byte_array = Files.readAllBytes(Paths.get(getPathData()));
		i.setData(byte_array);
		pl.add(i);
		return true;
	}

	String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	@Override
	public Iterator<PresentationInfo> iterator() {
		return pl.iterator();
	}

	@Override
	public void setup(UploaderProperties prop) {
		// TODO Auto-generated method stub

	}

}
