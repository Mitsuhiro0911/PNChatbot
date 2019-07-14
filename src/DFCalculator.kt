import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    // TODO:DF値を全出現数で計算している。出現している発言数をカウントするように修正する
    // TODO:CSVやテキストファイルなどへ出力する
    // nuccコーパスのdata001.txt〜data129.txtを解析
    val DFMap = LinkedHashMap<String , Double>()
    for(i in 1 until 130) {
        val br = BufferedReader(FileReader(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        var str = br.readLine()
//        while(str.get(0) == '＠'){
//            str = br.readLine()
//            println(str)
//        }
        val parser = Parser()
        while(str != null) {
            if(str.get(0) == '＠'){
                println(str)
                str = br.readLine()
                continue
            }
            val command = arrayOf(
                "sh", "-c",
                "echo ${str.substring(5, str.length)} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )
            var wordList: ArrayList<String>? = arrayListOf()
            wordList = parser.parse(command, wordList)
//            println(str)
//            println(wordList!!.distinct())
            for(word in wordList!!.distinct()) {
                if (DFMap.contains(word)) {
                    DFMap.put(word, DFMap.getValue(word).plus(1.0))
                } else {
                    DFMap.put(word, 1.0)
                }
            }
            str = br.readLine()
        }
    }
    println(DFMap)
    val bw = BufferedWriter(FileWriter(File("./data/feature/df.txt")))
    bw.write(DFMap.toString().substring(1, DFMap.toString().length - 1).replace(", ", "\n"))
    bw.close()
}