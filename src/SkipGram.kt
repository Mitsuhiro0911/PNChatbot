import org.dom4j.Node
import org.dom4j.io.SAXReader
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory



fun main(args: Array<String>) {
//    println(SkipGram().getSkipGramVector("使用"))
    println(SkipGram().getSkipGramVector())
}

class SkipGram {
    companion object {
        var targetWordList = ArrayList<String>()
        var synonymMap = LinkedHashMap<String, Double>()
    }

    fun getSkipGramVector(): LinkedHashMap<String, Double> {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(File("./data/feature/skip_gram_vector.xml"), SAXHandler())

        return synonymMap
    }
}
