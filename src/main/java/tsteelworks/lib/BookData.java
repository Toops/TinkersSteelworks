package tsteelworks.lib;

import mantle.books.ManualReader;

/**
 * This class fixes the lack of
 */
public class BookData extends mantle.books.BookData {
	public BookData(String xmlFile) {
		doc = ManualReader.readManual(xmlFile);
	}
}