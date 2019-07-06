import java.io.BufferedReader
import java.io.InputStreamReader

class Parser {

    /**
     * 入力メッセージを形態素解析する。
     */
    fun parseMessage(command: Array<String>): ArrayList<String>? {
        var wordList: ArrayList<String>? = arrayListOf()
        return parse(command, wordList)
    }

    /**
     * 返答メッセージを形態素解析する。
     */
    fun parseReverseMessage(command: Array<String>, messageWordList: ArrayList<String>?, reverseMessage: String): ArrayList<String>? {
        var wordList = messageWordList?.clone() as ArrayList<String>?
        var skipFlag = true
        // 返答メッセージが入力メッセージリストの名詞を１つも含まない場合、nullを返す
        for (word in wordList!!) {
            if (reverseMessage.contains(word)) {
                skipFlag = false
            }
        }
        if (skipFlag) {
            return null
        }

        return parse(command, wordList)
    }

    /**
     * TF値をカウントし、元に素性ベクトルを作成
     */
    fun calTf(command: Array<String>, wordList: ArrayList<String>): LinkedHashMap<String, Double> {
        val tfMap = LinkedHashMap<String, Double>()
        for (word in wordList) {
            tfMap.put(word, 0.0)
        }
        var wordCountList: ArrayList<String>? = arrayListOf<String>()
        wordCountList = parse(command, wordCountList)
        for(wordCount in wordCountList!!){
            // 同じ単語が出現する毎に出現回数をカウントアップする
            tfMap.put(wordCount, tfMap.get(wordCount)!!.plus(1.0))
        }
        return tfMap
    }

    /**
     * 形態素解析し、出現した名詞のリストを返す。
     */
    fun parse(command: Array<String>, wordList: ArrayList<String>?): ArrayList<String>?{
        // コマンド結果をProcessで受け取る
        val ps = Runtime.getRuntime().exec(command)
        // 標準出力
        val bReader_i = BufferedReader(InputStreamReader(ps.inputStream, "UTF-8"))
        // 標準出力を1行ずつ受け取る一時オブジェクト
        var targetLine: String?
        // 形態素解析結果を全て解析する
        while (true) {
            // 形態素解析結果を1行ずつ受け取る
            targetLine = bReader_i.readLine()
            // 最終行まで解析が完了したらループを抜ける
            if (targetLine == null) {
                break
            } else if (targetLine == "EOS") {
                continue
            } else {
                // 品詞
                val targetType =
                    targetLine.split("[\t|,]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val word = targetLine.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                if (targetType == "名詞") {
                    wordList?.add(word)
                }
            }
        }
        // 終了を待つ
        ps.waitFor()
        return wordList
    }
}