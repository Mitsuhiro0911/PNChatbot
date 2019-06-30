import java.io.BufferedReader
import java.io.InputStreamReader

class Parser{
    fun makeWordList(messageCommand: Array<String>, reverseCommand: Array<String>, reverseMessage: String): ArrayList<String>?{
        val wordList = ArrayList<String>()
        val command = mutableListOf<Array<String>>()
        command.add(messageCommand)
        command.add(reverseCommand)
        var skipFlag = false
        // 発言と返答を形態素解析
        for(com in command) {
            // 返答を形態素解析する時、発言の名詞が一つも含まれていない場合処理をスキップする(処理高速化のため)
            if(wordList.size != 0){
                for(word in wordList) {
                    if(reverseMessage.contains(word)) {
                        skipFlag = true
                    }
                }
                if(skipFlag === false) {
                    return null
                }
            }
            // コマンド結果をProcessで受け取る
            val ps = Runtime.getRuntime().exec(com)
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
                        wordList.add(word)
                    }
                }
            }
            // 終了を待つ
            ps.waitFor()
        }
        return wordList
    }

    fun calTf(command: Array<String>, wordList: ArrayList<String>): LinkedHashMap<String, Double>{
        val tfMap = LinkedHashMap<String, Double>()
        for(word in wordList){
            tfMap.put(word, 0.0)
        }
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
                val targetType = targetLine.split("[\t|,]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                val word = targetLine.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                if (targetType == "名詞") {
                    // 同じ単語が出現する毎に出現回数をカウントアップする
                    tfMap.put(word, tfMap.get(word)!!.plus(1.0))
                }
            }
        }
        // 終了を待つ
        ps.waitFor()
        return tfMap
    }
}