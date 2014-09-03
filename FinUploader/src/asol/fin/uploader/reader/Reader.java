package asol.fin.uploader.reader;

import java.util.Iterator;

import asol.fin.uploader.DBHelper;
import asol.fin.uploader.PresentationInfo;
import asol.fin.uploader.UploaderProperties;


public interface Reader extends Iterable<PresentationInfo> {
	void setup (UploaderProperties prop);
	boolean readData() throws Exception;
}
