import org.dom4j.Node
import org.dom4j.io.SAXReader
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory



fun main(args: Array<String>) {
//    println(SkipGram().getSkipGramVector("使用"))
    SkipGram.targetWordList.add("尚絅学院中学校・高等学校")
    SkipGram.targetWordList.add("使用")
    SkipGram.targetWordList.add("パズドラ")
    println(SkipGram().getSkipGramVector())
}

class SkipGram {
    companion object {
        // 入力メッセージと返答メッセージで出現しうる単語のリスト。この単語を起点としたSkipGramモデルベクトルを作成する。
        var targetWordList = ArrayList<String>()
        // 起点のキワード、類似語、類似度のスコアを保持するリスト
        var skipGramMap = LinkedHashMap<String, LinkedHashMap<String, Double>>()
    }

    fun getSkipGramVector(): LinkedHashMap<String, LinkedHashMap<String, Double>> {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(File("./data/feature/skip_gram_vector.xml"), SAXHandler())

        return skipGramMap
    }
}
