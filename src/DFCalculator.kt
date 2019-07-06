import java.io.BufferedReader
import java.io.File
import java.io.FileReader

fun main(args: Array<String>) {
    // TODO:DF値を全出現数で計算している。出現している発言数をカウントするように修正する
    // TODO:CSVやテキストファイルなどへ出力する
    // nuccコーパスのdata001.txt〜data129.txtを解析
    val DFMap = LinkedHashMap<String , Int>()
    for(i in 1 until 130) {
        val br = BufferedReader(FileReader(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        var str = br.readLine()
        while(str.get(0) == '＠'){
            str = br.readLine()
            println(str)
        }
        val parser = Parser()
        while(str != null) {
            val command = arrayOf(
                "sh", "-c",
                "echo ${str.substring(5, str.length)} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )
            var wordList: ArrayList<String>? = arrayListOf()
            wordList = parser.parse(command, wordList)
            println(str)
            println(wordList)
            for(word in wordList!!) {
                if (DFMap.contains(word)) {
                    DFMap.put(word, DFMap.getValue(word).plus(1))
                } else {
                    DFMap.put(word, 0)
                }
            }
            str = br.readLine()
        }
    }
    println(DFMap)
}