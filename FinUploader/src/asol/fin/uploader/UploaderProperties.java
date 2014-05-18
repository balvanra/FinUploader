package asol.fin.uploader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UploaderProperties {
	public final static String CONF_NAME = "finuploader.properties";

	public final static String YES = "Y";
	public final static String NO = "N";

	Properties p = new Properties();

	String dest_db_user;
	String dest_db_pwd;
	String dest_db;

	String export_path;
	int current_user;

	String refresh_presentations;

	public UploaderProperties() {
	}

	private String getStringPropertyThrowException(Properties p, String key,
			boolean required) throws Exception {
		try {
			String s = p.getProperty(key);
			if (s == null) {
				if (required)
					throw new NullPointerException("Nenastavene " + key);
				else
					return null;
			} else
				return s;
		} catch (Exception e) {
			throw new Exception("Chyba pri citani " + key, e);
		}
	}

	private int getIntPropertyThrowException(Properties p, String key)
			throws Exception {
		try {
			return Integer.parseInt(getStringPropertyThrowException(p, key,
					true));
		} catch (Exception e) {
			throw new Exception("Chyba pri citani cisla " + key, e);
		}
	}

	public void loadProperties() throws Exception {
		try {
			p.load(new FileInputStream(CONF_NAME));

			setDest_db(getStringPropertyThrowException(p, "dest_db", true));
			setDest_db_user(getStringPropertyThrowException(p, "dest_db_user",
					true));
			setDest_db_pwd(getStringPropertyThrowException(p, "dest_db_pwd",
					false));
			setExport_path(getStringPropertyThrowException(p, "export_path",
					true));
			setCurrent_user(getIntPropertyThrowException(p, "current_user"));
			setRefresh_presentations(getStringPropertyThrowException(p,
					"refresh_presentations", true));

		} catch (Exception e) {
			throw new Exception("Chyba konfiguracneho suboru " + CONF_NAME, e);
		}
	}

	public void saveProperties() throws Exception {
		if (p == null)
			throw new NullPointerException("Nenacitany subor " + CONF_NAME);

		p.store(new FileOutputStream(CONF_NAME), "comment");
	}

	public String getDest_db_user() {
		return dest_db_user;
	}

	private void setDest_db_user(String dest_db_user) {
		this.dest_db_user = dest_db_user;
	}

	public String getDest_db_pwd() {
		return dest_db_pwd;
	}

	private void setDest_db_pwd(String dest_db_pwd) {
		this.dest_db_pwd = dest_db_pwd;
	}

	public String getDest_db() {
		return dest_db;
	}

	private void setDest_db(String dest_db) {
		this.dest_db = dest_db;
	}

	public String getExport_path() {
		return export_path;
	}

	private void setExport_path(String export_path) {
		this.export_path = export_path;
	}

	public int getCurrent_user() {
		return current_user;
	}

	private void setCurrent_user(int current_user) {
		this.current_user = current_user;
	}

	public String getRefresh_presentations() {
		return refresh_presentations;
	}

	public boolean isRefresh_presentations() {
		return (UploaderProperties.YES.equals(this.getRefresh_presentations()));
	};

	private void setRefresh_presentations(String refresh_presentations) {
		this.refresh_presentations = refresh_presentations;
	}

}
