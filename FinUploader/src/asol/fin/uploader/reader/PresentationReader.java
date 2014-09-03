package asol.fin.uploader.reader;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import asol.fin.uploader.DBHelper;
import asol.fin.uploader.EncryptedPassword;
import asol.fin.uploader.PresentationInfo;
import asol.fin.uploader.UploaderProperties;

public class PresentationReader implements Reader {
	private static Logger L = LogManager.getLogger(PresentationReader.class.getName());

	ArrayList<PresentationInfo> pl = null;
	private EncryptedPassword cipher;
	private UploaderProperties prop;
	private DBHelper db;

	public PresentationReader() {
	}
	
	public void setup (UploaderProperties prop) {
		this.prop = prop;
		this.cipher = new EncryptedPassword();
		String decryptedPassword = null;

		String encryptedPassword = prop.getDest_db_pwd();
		if (encryptedPassword == null) {
			throw new InvalidParameterException("encryptedPassword not stored in properties file");
		} else {
			decryptedPassword = cipher.getDecryptedPassword(encryptedPassword);

			this.db = new DBHelper(prop.getDest_db_user(), decryptedPassword,
					prop.getDest_db(), prop.getCurrent_user());
		}
	}

	@Override
	public boolean readData() throws Exception {
		if (prop.isRefresh_presentations()) {
			pl = db.fetchPresentations(false);
			for (PresentationInfo presentationInfo : pl) {
				L.debug(presentationInfo);
				db.refreshPresentation(presentationInfo);
			}
		}

		pl = db.fetchPresentations(true);
		
		return true;
	}

	@Override
	public Iterator<PresentationInfo> iterator() {
		return pl.iterator();
	}

}
