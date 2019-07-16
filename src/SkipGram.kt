import org.dom4j.Node
import org.dom4j.io.SAXReader

fun main(args: Array<String>) {
    println(SkipGram().getSkipGramVector())
}

class SkipGram {
    fun getSkipGramVector(): Unit {
        // キーワード
        val str = "使用"
        val reader = SAXReader()
        val skipGramVector = reader.read("./data/feature/skip_gram_vector_test.xml")
        // キーワードの類義語情報(SkipGramモデル)を抽出
        val vector: List<Node> = skipGramVector.selectNodes("//word[text()='" + str + "']/ancestor-or-self::*/synonyms")
        println(vector.get(0).text)
        // synonymsタグの要素を整形し、単語とスコアに分割する
        val synonymList = vector.get(0).text.replace("[", "").replace("]", "").split("), ")
        for(synonym in synonymList) {
            println(synonym)
            val synonymWord = synonym.split(", ")[0]
            val synonymScore = synonym.split(", ")[1]
            println(synonymWord.substring(2, synonymWord.length - 1))
            println(synonymScore.replace(")", ""))
            println()
        }

    }
}