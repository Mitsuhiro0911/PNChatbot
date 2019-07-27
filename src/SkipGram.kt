import org.dom4j.Node
import org.dom4j.io.SAXReader
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory



fun main(args: Array<String>) {
    // DOMによるSkipGramベクトルの取得
//    SkipGram.targetWordList.add("片")
//    SkipGram.targetWordList.add("<")
//    SkipGram.targetWordList.add("する")
//    println(SkipGram().getSkipGramVectorDOM())

    // SAXによるSkipGramベクトルの取得
//    SkipGram.targetWordList.add("ゲーム")
//    println(SkipGram().getSkipGramVectorSAX())
}

class SkipGram {
    companion object {
        // 入力メッセージと返答メッセージで出現しうる単語のリスト。この単語を起点としたSkipGramモデルベクトルを作成する。
        var targetWordList = ArrayList<String>()
        // 起点のキワード、類似語、類似度のスコアを保持するリスト
        var skipGramMap = LinkedHashMap<String, LinkedHashMap<String, Double>>()
    }

    fun getSkipGramVectorDOM(): LinkedHashMap<String, LinkedHashMap<String, Double>> {
        val reader = SAXReader()
        val skipGramVector = reader.read("./data/feature/skip_gram_vector.xml")
        // キーワードの類義語情報(SkipGramモデル)を抽出
        for(keyword in targetWordList) {
            val vector: List<Node> =
                skipGramVector.selectNodes("//word[text()='" + keyword + "']/ancestor-or-self::*/synonyms")
            println(vector.get(0).text)
            val synonymMap = LinkedHashMap<String, Double>()
            // synonymsタグの要素を整形し、単語とスコアに分割する
            val synonymList = vector.get(0).text.replace("[", "").replace("]", "").split("), ")
            for (synonym in synonymList) {
                println(synonym)
                val synonymWord = synonym.split(", ")[0]
                val synonymScore = synonym.split(", ")[1]
                println(synonymWord.substring(2, synonymWord.length - 1))
                println(synonymScore.replace(")", ""))
                println()
                synonymMap.put(
                    synonymWord.substring(2, synonymWord.length - 1),
                    synonymScore.replace(")", "").toDouble()
                )
                SkipGram.skipGramMap.put(keyword, synonymMap)
            }
        }
        return SkipGram.skipGramMap
    }

        fun getSkipGramVectorSAX(): LinkedHashMap<String, LinkedHashMap<String, Double>> {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        parser.parse(File("./data/feature/skip_gram_vector_forSAX.xml"), SAXHandler())

        return skipGramMap
    }
}
