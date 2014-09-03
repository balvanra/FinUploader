package asol.fin.uploader.tests;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import asol.fin.uploader.Uploader;
import asol.fin.uploader.reader.FileReader;
import asol.fin.uploader.writer.XLSXWriter;

public class UploaderTest {

	Uploader u;

	@Before
	public void setUp() throws Exception {
		XLSXWriter writer = new XLSXWriter();
		FileReader reader = new FileReader(
				"c:\\Users\\rados_000\\git\\FinUploader\\FinUploader\\txt\\trace_260226516.srd");

		u = new Uploader(reader, writer);
	}

	@Test
	public void testMain() {
		try {
			u.setup();
			u.upload();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Assert.assertTrue(true);
	}

}
