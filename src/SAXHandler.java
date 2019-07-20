import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class SAXHandler extends DefaultHandler {
    boolean targetFlag = false;
    boolean synonymsFlag = false;
    public void startDocument() {
        System.out.println("読み込み開始");
    }

    public void endDocument() {
        System.out.println("読み込み終了");
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if(qName.equals("synonyms")) {
            synonymsFlag = true;
        }
    }

    public void endElement(String uri, String localName, String qName) {
        synonymsFlag = false;
    }

    public void characters(char[] ch, int start, int length) {
        if(new String(ch, start, length).equals("尚絅学院中学校・高等学校")) {
            System.out.println(new String(ch, start, length));
            targetFlag = true;
            return;
        }
        if(targetFlag && synonymsFlag){
            System.out.println(new String(ch, start, length));
            targetFlag = false;
        }
    }
}