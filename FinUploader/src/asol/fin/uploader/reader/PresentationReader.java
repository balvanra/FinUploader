package asol.fin.uploader.reader;

import asol.fin.uploader.PresentationInfo;

public class PresentationReader implements Reader {

	PresentationInfo info;

	public PresentationReader() {
	}

	@Override
	public String readData() throws Exception {
		if (info == null) {
			throw new NullPointerException(
					"PresentationReader: Presentation not set.");
		}

		return new String(info.getData());
	}

	public void setInfo(PresentationInfo info) {
		this.info = info;
	}

}
