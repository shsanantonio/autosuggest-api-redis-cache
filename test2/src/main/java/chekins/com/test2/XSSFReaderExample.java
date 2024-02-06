package chekins.com.test2;


import java.io.InputStream;
import java.util.*;

import chekins.com.test2.model.Location;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
//import org.springframework.data.geo.Point;
import org.springframework.data.geo.Point;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XSSFReaderExample {
    static List<Location> locations = new LinkedList<>();

    public List<Location> readExcelFile(String filename) throws Exception {
        System.out.println("___EXCEL FILE NAME" + filename);
        OPCPackage opcPackage = OPCPackage.open(filename);
        XSSFReader xssfReader = new XSSFReader(opcPackage);
        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
        XMLReader parser = getSheetParser(sharedStringsTable);

        Iterator<InputStream> sheets = xssfReader.getSheetsData();
        while (sheets.hasNext()) {
            System.out.println("Processing sheet:");
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
        System.out.println("____FILE READ COMPLETE");
        System.out.println("Array List size: " + locations.size());
        return locations;
    }

    public XMLReader getSheetParser(SharedStringsTable sharedStringsTable) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader();
        ContentHandler handler = new SheetHandler(sharedStringsTable);
        parser.setContentHandler(handler);
        return parser;
    }

    /** sheet handler class for SAX2 events */
    private static class SheetHandler extends DefaultHandler {
        private SharedStringsTable sharedStringsTable;
        private String contents;
        protected long rowNumber = 0;
        protected String cellId;
        protected Map<String, String> rowValues = new HashMap<>();

        private ArrayList<String> rowElements = new ArrayList<String>();
        private boolean isCellValue;
        private boolean fromSST;

        private SheetHandler(SharedStringsTable sharedStringsTable) {
            this.sharedStringsTable = sharedStringsTable;
        }
        protected static String getColumnId(String attribute) throws SAXException {
            for (int i = 0; i < attribute.length(); i++) {
                if (!Character.isAlphabetic(attribute.charAt(i))) {
                    return attribute.substring(0, i);
                }
            }
            throw new SAXException("Invalid format " + attribute);
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

            contents = "";
            if (name.equals("row")) {
                String rowNumStr = attributes.getValue("r");
                rowNumber = Long.parseLong(rowNumStr);

                if (rowElements.size() >= 8) {
                    Location location = Location.of(
                            rowElements.get(0),
                            rowElements.get(1),
                            rowElements.get(2),
                            rowElements.get(3),
                            rowElements.get(4),
                            rowElements.get(5),
                            rowElements.get(6),
                            new Point(Double.parseDouble(rowElements.get(7)), Double.parseDouble(rowElements.get(8)))
                    );
                    System.out.println(location);
                    locations.add(location);
                    rowElements = new ArrayList<>();
                }
            }else if (name.equals("c")) {
                cellId = getColumnId(attributes.getValue("r"));


                String cellType = attributes.getValue("t");
                if (cellType != null  && cellType.equals("s") /*|| cellType != null && cellType.equals("n")*/) {
                    // cell type s means value will be extracted from SharedStringsTable
                    fromSST = true;
                }
                // element v represents value of Cell
            } else if (name.equals("v")) {
                isCellValue = true;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isCellValue) {
                contents += new String(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name) throws SAXException {
            if (isCellValue && fromSST) {
                int index = Integer.parseInt(contents);
                contents = new XSSFRichTextString(sharedStringsTable.getEntryAt(index)).toString();
                rowElements.add(contents);
                isCellValue = false;
                fromSST = false;

            } else if (isCellValue) {
                rowElements.add(contents);
                isCellValue = false;
            }else if (name.equals("row")) {
                if (rowNumber == 1) {
                    rowElements = new ArrayList<>();
                }
            }

        }

    }
}