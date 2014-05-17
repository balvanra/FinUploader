package asol.fin.uploader;

public class DataCell {
	String strValue;
	Double numValue;
	int row;
	int col;
	Format format;
	int colorFG;
	int colorBG;
	TablePosition position;

	protected DataCell() {
		super();
	}

	public static DataCell numCell(Double numValue, int row, int col,
			int colorFG, int colorBG, TablePosition position) {

		DataCell c = new DataCell(null, numValue, row, col, Format.DOUBLE,
				colorFG, colorBG, position);
		return c;
	}

	public static DataCell strCell(String strValue, int row, int col,
			int colorFG, int colorBG, TablePosition position) {

		DataCell c = new DataCell(strValue, null, row, col, Format.STRING,
				colorFG, colorBG, position);
		return c;
	}

	protected DataCell(String strValue, Double numValue, int row, int col,
			Format format, int colorFG, int colorBG, TablePosition position) {
		super();
		this.strValue = trimAndRemoveSpecial(strValue);
		this.numValue = numValue;
		this.row = row;
		this.col = col;
		this.format = format;
		this.colorFG = colorFG;
		this.colorBG = colorBG;
		this.position = position;
	}

	public String getStrValue() {
		return strValue;
	}

	public Double getNumValue() {
		return numValue;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public Format getFormat() {
		return format;
	}

	public int getColorFG() {
		return colorFG;
	}

	public int getColorBG() {
		return colorBG;
	}

	public TablePosition getPosition() {
		return position;
	}

	public static String trimAndRemoveSpecial(String str) {
		if (str == null)
			return null;
		String result = str.trim();
		if (result.startsWith("\""))
			result = result.substring(1);
		if (result.endsWith(","))
			result = result.substring(0, result.length() - 1);
		if (result.endsWith("\""))
			result = result.substring(0, result.length() - 1);
		return result;
	}

}
