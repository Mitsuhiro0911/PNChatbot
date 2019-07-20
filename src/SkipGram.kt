import org.dom4j.Node
import org.dom4j.io.SAXReader
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory



fun main(args: Array<String>) {
//    println(SkipGram().getSkipGramVector("尚絅学院中学校・高等学校"))
    println(SkipGram().getSkipGramVectorSpeedCompare())
}

class SkipGram {
    fun getSkipGramVector(keyword: String): LinkedHashMap<String, Double> {
        val reader = SAXReader()
        val skipGramVector = reader.read("./data/feature/skip_gram_vector.xml")
        // キーワードの類義語情報(SkipGramモデル)を抽出
        val vector: List<Node> = skipGramVector.selectNodes("//word[text()='" + keyword + "']/ancestor-or-self::*/synonyms")
        println(vector.get(0).text)
        val synonymMap = LinkedHashMap<String, Double>()
        // synonymsタグの要素を整形し、単語とスコアに分割する
        val synonymList = vector.get(0).text.replace("[", "").replace("]", "").split("), ")
        for(synonym in synonymList) {
            println(synonym)
            val synonymWord = synonym.split(", ")[0]
            val synonymScore = synonym.split(", ")[1]
            println(synonymWord.substring(2, synonymWord.length - 1))
            println(synonymScore.replace(")", ""))
            println()
            synonymMap.put(synonymWord.substring(2, synonymWord.length - 1), synonymScore.replace(")", "").toDouble())
        }
        return synonymMap
    }
    fun getSkipGramVectorSpeedCompare(): Unit {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(File("./data/feature/skip_gram_vector.xml"), SAXHandler())
    }
}
