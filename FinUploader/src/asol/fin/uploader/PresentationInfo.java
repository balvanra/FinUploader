package asol.fin.uploader;

import java.text.DateFormat;
import java.util.Date;

public class PresentationInfo {
	int id;
	String name;
	Date created;
	Date refreshed;
	String author;
	byte[] data;

	public PresentationInfo() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getRefreshed() {
		return refreshed;
	}

	public void setRefreshed(Date refreshed) {
		this.refreshed = refreshed;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public byte[] getData() {
		return data;
	}

	public int getDataLength() {
		return data == null ? 0 : data.length;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return String.format("id:%d name:%s author:%s create:%s refresh:%s size:%d",getId(),
				getName(), getAuthor(),
				DateFormat.getDateInstance().format(getCreated()), DateFormat
						.getDateInstance().format(getRefreshed()),
				getDataLength());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
