package asol.fin.uploader.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileReader implements Reader {
	public static Logger L = LogManager.getLogger(FileReader.class.getName());

	String pathData;

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
	public String readData() throws Exception {
		L.trace("readData");
		L.entry();
		String data = readFile(getPathData(), Charset.defaultCharset());
		L.trace(data);
		L.exit();
		return data;
	}

	String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
