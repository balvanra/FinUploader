package asol.fin.uploader;

public class Label implements Comparable<Label> {

	int row;
	int col;
	String text;
	TablePosition position;

	public Label(int row, int col, String text, TablePosition position) {
		super();
		this.row = row;
		this.col = col;
		this.text = text;
		this.position = position;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(Label o) {
		if (this.row == o.row)
			if (this.col > o.col)
				return 1;
			else if (this.col == o.col)
				return 0;
			else if (this.col < o.col)
				return -1;

		if (this.row > o.row)
			return 1;
		else if (this.row < o.row)
			return -1;
		else
			return 0;
	}

	public TablePosition getPosition() {
		return position;
	}

	public void setPosition(TablePosition position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return String.format(" %s c:%d r:%d pos:%s", text, col, row, position);
	}

}
