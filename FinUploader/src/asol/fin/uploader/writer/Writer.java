package asol.fin.uploader.writer;

import asol.fin.uploader.PresentationInfo;
import asol.fin.uploader.Table;

public interface Writer {
	void init (String destination);
	void write(Table table, PresentationInfo info) throws Exception;
}
