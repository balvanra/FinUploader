package asol.fin.uploader.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import asol.fin.uploader.DataCell;
import asol.fin.uploader.Label;
import asol.fin.uploader.Table;
import asol.fin.uploader.TablePosition;
import asol.fin.uploader.Uploader;

public class FinDataParser implements Parser {
	String mainData;

	Table t;

	public String getMainData() {
		return mainData;
	}

	public void setMainData(String mainData) {
		this.mainData = mainData;
	}

	public FinDataParser(String mainData) {
		this.setMainData(mainData);
	}

	ArrayList<Label> findAndSortLabels(ArrayList<String> texts,
			TablePosition checkedPosition) {
		ArrayList<Label> labels = new ArrayList<Label>();

		for (String line : texts) {
			Label l = processSingleText(line);
			if (l != null && l.getPosition() == checkedPosition)
				labels.add(l);
		}
		Collections.sort(labels);
		return labels;
	}

	/**
	 * text(band=header .. .. data(
	 * 
	 * @param data
	 * @return
	 */
	ArrayList<String> getTexts(String data) {
		String line;

		ArrayList<String> texts = new ArrayList<String>();

		int index = data.indexOf(prefixText);
		while (index > 0) {
			int nextindex = data.indexOf(prefixText, index + 1);
			if (nextindex < 0) {
				nextindex = data.indexOf(DATA, index + 1);
				line = data.substring(index, nextindex - 1);
				texts.add(line);
				break;
			}
			line = data.substring(index, nextindex - 1);
			texts.add(line);
			index = nextindex;
		}

		return texts;
	}

	Label processSingleText(String line) {
		TablePosition pos;
		L.trace(line);
		if (line.indexOf(TRANSPARENT) > 0) {
			// skip - je to prekryvny label
		} else {
			String text = getValue(line, LABELTEXT, UVODZOVKA);
			L.trace(text);

			String name = getValue(line, NAME, MEDZERA);

			String col = getValue(name, NAZOV, NAME_T);
			String row = "";
			if (col != null) {
				row = "1";
				pos = TablePosition.ROWHEADER;
			} else {
				col = getValue(name, NAME_PREFIXH, NAME_D);
				if (col == null)
					col = "0";
				row = getValue(name, NAME_D, NAME_T);
				if (row == null)
					row = "0";
				pos = TablePosition.COLHEADER;
			}
			L.trace(String.format("%s: r:%s c:%s ", name, row, col));

			return new Label(Integer.parseInt(row), Integer.parseInt(col),
					text, pos);
		}
		return null;
	}

	private String getValue(String data, String starts, String ends) {
		int index = data.indexOf(starts);
		if (index < 0)
			return null;
		index += starts.length();
		int endindex = data.indexOf(ends, index);
		if (endindex < 0)
			return null;
		String value = data.substring(index, endindex);
		if (value.length() > 500)
			L.trace(value.length());
		return value;
	}

	String[] splitData() {
		String data = getValue(mainData, DATA, DATA_END);
		if (data == null) {
			throw new NullPointerException("Data not found");
		}
		L.trace(data);

		// http://regexr.com/
		//String strpattern = "(\\\"[^\\\"]+\\\",)|([\\+\\- 01234567890\\.]+[^\\\"])|(\\w+)";
		String strpattern = "(\\\"[^\\\"]+\\\",)|([\\+\\-01234567890\\.]+)|([\\ ]*null)";
		L.trace(strpattern);
		Pattern p = Pattern.compile(strpattern);
		Matcher m = p.matcher(data);

		L.trace("Matches!");
		ArrayList<String> sl = new ArrayList<String>();
		while (m.find()) {
			String s = m.group();
			s = DataCell.trimAndRemoveSpecial(s);
			sl.add(s);
			L.trace(s);
		}

		String[] sa = new String[sl.size()];
		return sl.toArray(sa);
	}

	@Override
	public Table getData() {
		t = new Table(0, 0);

		// labels
		ArrayList<String> texts = getTexts(mainData);

		ArrayList<Label> rowLabels = findAndSortLabels(texts,
				TablePosition.ROWHEADER);

		ArrayList<Label> colLabels = findAndSortLabels(texts,
				TablePosition.COLHEADER);

		int currentRow = 0;
		int colheaders = 0;
		boolean finishedRowheaders = false;
		for (Label collabel : colLabels) {
			if (currentRow < collabel.getRow()) {
				for (Label rowlabel : rowLabels) {
					// neopakovanie nadpisov v dalsich riadkoch
					t.addCell(DataCell.strCell(finishedRowheaders ? ""
							: rowlabel.getText(), rowlabel.getRow(), rowlabel
							.getCol(), 0, 0, rowlabel.getPosition()));
					L.trace(rowlabel);
				}
				finishedRowheaders = true;
				currentRow = collabel.getRow();
			}
			t.addCell(DataCell.strCell(collabel.getText(), collabel.getRow(),
					collabel.getCol(), 0, 0, collabel.getPosition()));
			L.trace(collabel);
			if (collabel.getCol() > colheaders)
				colheaders = collabel.getCol();
		}
		L.trace(currentRow);

		// data
		String d[] = splitData();

		String[] lastLabels = new String[rowLabels.size()];
		for (int i = 0; i < lastLabels.length; i++) {
			lastLabels[i] = "";
		}

		int index = 0;
		int rownum = 0;
		while (index < d.length) {
			int rowindex = 0;
			while (rowindex < rowLabels.size()) {
				L.trace(d[index]);

				// neopakovanie hlaviciek riadkov
				String text = d[index];
				if (text.equals(lastLabels[rowindex])) {
					text = "";
				} else {
					lastLabels[rowindex] = text;
				}
				t.addCell(DataCell
						.strCell(text, 1, 1, 0, 0, TablePosition.DATA));
				index += 4; // nazov1, id1, poradie1, rowff1
				rowindex++;
			}
			index++;
			rowindex = 0;
			while (rowindex < colheaders) {
				L.trace(d[index]);
				try {
					t.addCell(DataCell.numCell(Double.valueOf(d[index])
							.doubleValue(), 1, 1, 0, 0, TablePosition.DATA));
				} catch (Exception e) {
					L.fatal("Error at index:" + index);
					L.fatal(d[index - 3]);
					L.fatal(d[index - 2]);
					L.fatal(d[index - 1]);
					L.fatal(d[index]);
					L.fatal(d[index + 1]);
					L.fatal(d[index + 2]);
					L.fatal(d[index + 3]);
					throw e;
				}
				index += 2; // ref_h1, h1
				rowindex++;
			}
			index--;
			rownum++;
		}
		L.trace(rownum);
		t.setDimensions(rownum + currentRow, rowLabels.size() + colheaders);
		return t;
	}

	@SuppressWarnings("unused")
	private Table getTestData() {
		t = new Table(2, 3);

		t.addCell(DataCell.strCell("ORG", 1, 1, 0, 0, TablePosition.ROWHEADER));
		t.addCell(DataCell.strCell(null, 1, 2, 0, 0, TablePosition.ROWHEADER));
		t.addCell(DataCell
				.strCell("April", 1, 3, 0, 0, TablePosition.COLHEADER));
		t.addCell(DataCell.strCell("A", 2, 1, 0, 0, TablePosition.ROWHEADER));
		t.addCell(DataCell.strCell("B", 2, 2, 0, 0, TablePosition.ROWHEADER));
		t.addCell(DataCell.numCell(Double.valueOf(45.5), 2, 3, 0, 0,
				TablePosition.DATA));

		L.debug(String.format("getData size:%d", t.getSize()));
		return t;
	}

	private static Logger L = LogManager.getLogger(Uploader.class.getName());

	private String prefixText = "text(band=header";
	private String TRANSPARENT = "background.mode=\"1\"";
	private String LABELTEXT = "text=\"";
	private String UVODZOVKA = "\"";
	private String NAME = "name=";
	private String MEDZERA = " ";
	private String NAZOV = "nazov";
	private String DATA = "data(";
	private String DATA_END = ",)";
	private String NAME_T = "_t";
	private String NAME_D = "_d";
	private String NAME_PREFIXH = "h";
}
