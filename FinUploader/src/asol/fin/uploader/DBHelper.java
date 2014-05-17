package asol.fin.uploader;

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;

public class DBHelper {

	private final static Logger L = LogManager.getLogger(DBHelper.class
			.getName());

	Connection _conn = null;
	String _dbusername;
	String _dbpwd;
	String _jdbcUrl;
	int _current_user;

	String SQL_PRESENTATIONS = "SELECT prezentacia_id,"
			+ "     nazov_prezentacie," + "     popis_prezentacie,"
			+ "     datum_vytvorenia," + "     datum_obcerstvenia,"
			+ "     syntax," + "     zalozil "
			+ "  FROM dl.fin_view_presentation2publish "
			+ " WHERE subscriber_user_id = ?";

	String REFRESH_PRESENTATION = "BEGIN "
			+ "  dl.pkfinprezentacie.pVytvorPrezentaciu(?);" + " END; ";

	public DBHelper(String dbusername, String dbpwd, String jdbcUrl,
			int current_user) {
		_dbusername = dbusername;
		_dbpwd = dbpwd;
		_jdbcUrl = jdbcUrl;
		_current_user = current_user;
	}

	public ArrayList<PresentationInfo> fetchPresentations(boolean fetchBlobs)
			throws Exception {

		Connection c = getConnection();

		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			L.debug("prepareStatement");
			pstmt = c.prepareStatement(SQL_PRESENTATIONS);
			pstmt.setInt(1, _current_user);
			L.debug("executeQuery");
			rs = pstmt.executeQuery();

			ArrayList<PresentationInfo> pl = new ArrayList<PresentationInfo>();
			L.debug(String.format("executed rs %s", rs == null));
			int row = 0;
			while (rs.next()) {
				L.debug(String.format("row: %d", ++row));
				PresentationInfo i = new PresentationInfo();
				i.setId(rs.getInt("prezentacia_id"));
				i.setAuthor(rs.getString("zalozil"));
				i.setName(rs.getString("nazov_prezentacie"));
				i.setCreated(rs.getDate("datum_vytvorenia"));
				i.setRefreshed(rs.getDate("datum_obcerstvenia"));
				if (fetchBlobs) {
					Clob clob = rs.getClob("syntax");
					L.debug("clob.length() " + clob.length());
					InputStream bs = clob.getAsciiStream();
					L.debug("bs.available() " + bs.available());
					byte[] byte_array = IOUtils.toByteArray(bs);
					L.debug("byte_array.length: " + byte_array.length);
					i.setData(byte_array);
				}
				L.debug(i);

				pl.add(i);
			}
			return pl;
		} finally {
			pstmt.close();
			if (!c.isClosed())
				c.close();
		}
	}

	public void refreshPresentation(PresentationInfo info) throws Exception {

		Connection c = getConnection();

		CallableStatement pstmt = null;
		try {
			L.debug("prepareCall");
			pstmt = c.prepareCall(REFRESH_PRESENTATION);
			pstmt.setInt(1, info.getId());
			L.debug("execute");
			boolean b = pstmt.execute();
			L.debug("result " + b);
		} finally {
			pstmt.close();
			if (!c.isClosed())
				c.close();
		}
	}

	private Connection getConnection() throws SQLException {
		if (_conn != null) {
			if (_conn.isClosed()) {
				_conn = DriverManager.getConnection("jdbc:oracle:thin:@"
						+ _jdbcUrl, _dbusername, _dbpwd);
			}
			return _conn;
		} else {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			_conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@" + _jdbcUrl, _dbusername, _dbpwd);
			return _conn;
		}

	}

	@SuppressWarnings("unused")
	private void setIntOrNull(PreparedStatement stmt, int paramNum,
			Integer value) throws SQLException {
		if (value != null)
			stmt.setInt(paramNum, value.intValue());
		else
			stmt.setNull(paramNum, java.sql.Types.NUMERIC);

	}

	@SuppressWarnings("unused")
	private void setDoubleOrNull(PreparedStatement stmt, int paramNum,
			Double value) throws SQLException {
		if (value != null)
			stmt.setDouble(paramNum, value.doubleValue());
		else
			stmt.setNull(paramNum, java.sql.Types.DOUBLE);

	}

	@SuppressWarnings("unused")
	private void setDateOrNull(PreparedStatement stmt, int paramNum,
			java.util.Date value) throws SQLException {
		if (value != null)
			stmt.setTimestamp(paramNum, new Timestamp(value.getTime()));
		else
			stmt.setNull(paramNum, java.sql.Types.DATE);

	}

}
