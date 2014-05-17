package asol.fin.uploader;

import java.util.ArrayList;
import java.util.Iterator;

public class Table implements Iterable<DataCell> {
	int dimensionRows;
	int dimensionCols;
	ArrayList<DataCell> cells;

	public Table(int dimensionRows, int dimensionCols) {
		setDimensions(dimensionRows, dimensionCols);
		cells = new ArrayList<DataCell>();
	}

	public void setDimensions(int dimensionRows, int dimensionCols) {
		this.dimensionCols = dimensionCols;
		this.dimensionRows = dimensionRows;
	}

	public void addCell(DataCell cell) {
		cells.add(cell);
	}

	public boolean isSizeMatchingDimensions() {
		return getPlannedSize() == cells.size();
	}

	public int getSize() {
		return cells.size();
	}

	private long getPlannedSize() {
		return this.dimensionCols * this.dimensionRows;
	}

	@Override
	public Iterator<DataCell> iterator() {
		return cells.iterator();
	}

	public int getDimensionRows() {
		return dimensionRows;
	}

	public int getDimensionCols() {
		return dimensionCols;
	}

}
