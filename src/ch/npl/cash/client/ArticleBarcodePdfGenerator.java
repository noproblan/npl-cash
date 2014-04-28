package ch.npl.cash.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ArticleBarcodePdfGenerator {
	private List<String> captions, details, codes;
	private String path;
	
	public ArticleBarcodePdfGenerator(String path) {
		this.path = path;
		this.captions = new ArrayList<String>();
		this.details = new ArrayList<String>();
		this.codes = new ArrayList<String>();
	}

	public void addData(String caption, String detail, String code) {
		this.captions.add(caption);
		this.details.add(detail);
		this.codes.add(code);
	}

	public Document generate() {
		Document document = new Document();
		PdfWriter writer;
		
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(this.path));
			document.open();
			PdfContentByte cb = writer.getDirectContent();
		
			// Title
			document.add(new Paragraph("NPL Kitchen Barcodes: DateTime ????", new Font(FontFamily.TIMES_ROMAN, 20)));
			
			// Barcode Table
			int cols = 3;
			PdfPTable framing = new PdfPTable(cols);
			framing.setSpacingBefore(50);
			framing.setComplete(true);
			for(int i = 0; i < this.captions.size(); i++) {
				PdfPTable table = this.createBarcodeTable(cb, captions.get(i), details.get(i), codes.get(i));
				PdfPCell cell = new PdfPCell(table);
				cell.setVerticalAlignment(Element.ALIGN_CENTER);
				framing.addCell(cell);
			}
			for(int i = 0; i < cols - (this.captions.size() % cols); i++) { // fill last row
				framing.addCell("");	
			}
			document.add(framing);

			document.close();
		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}

		return document;
	}
	
	private PdfPTable createBarcodeTable(PdfContentByte cb, String caption, String detail, String code) {
		// Barcode Generation
		Barcode39 codeEAN = new Barcode39();
		codeEAN.setCode(code);
		Image imageEAN = codeEAN.createImageWithBarcode(cb, null, null);
		
		// Table
		PdfPTable table = new PdfPTable(1);
		table.setSpacingBefore(10);
		table.setSpacingAfter(10);
		table.setTotalWidth(80);
        table.setLockedWidth(true);
        PdfPCell cell1 = new PdfPCell(imageEAN);
        PdfPCell cell2 = new PdfPCell(new Paragraph(caption));
        PdfPCell cell3 = new PdfPCell(new Paragraph(detail));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        cell3.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        
        return table;
	}
}
