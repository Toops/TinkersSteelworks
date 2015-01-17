package tsteelworks.lib.util;

import mantle.books.ManualReader;
import org.w3c.dom.Document;

/**
 * This class fixes the lack of
 */
public class BookData extends mantle.books.BookData {
	private BookData(Document xmlFile) {
		this.doc = xmlFile;
	}

	public static BookData newInstance(String xmlFile) {
		Document doc = ManualReader.readManual(xmlFile);

		if (doc == null) return null;

		return new BookData(doc);
	}
}