import net.sf.javaml.core.DenseInstance
import net.sf.javaml.distance.CosineSimilarity
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class Calculator{
    private val cs = CosineSimilarity()
    /**
     * 2つの商品[x],[y]間のコサイン類似度を計算する
     *
     * @param[x],[y] 商品の素性ベクトル
     * @return 商品同士のコサイン類似度
     */
    fun calCosSimilarity(x: DoubleArray, y: DoubleArray): Double {
        return this.cs.measure(DenseInstance(x), DenseInstance(y))
    }

    fun getDf(messageWordList: ArrayList<String>?): LinkedHashMap<String , Double> {
        val br = BufferedReader(FileReader(File("./data/feature/df.txt")))
        val dfMap = linkedMapOf<String , Double>()
        var dfInfo = br.readLine()
        while(dfInfo != null) {
            val dfElement = dfInfo.split("=")
            dfMap.put(dfElement[0], dfElement[1].toDouble())
            dfInfo = br.readLine()
        }
        // 入力メッセージに出現する名詞がnuccコーパスから生成したDFマップに存在しない場合、マップに追加する
        for(messageWord in messageWordList!!.distinct()) {
            if (!dfMap.contains(messageWord)) {
                dfMap.put(messageWord, 1.0)
            }
        }

        println(dfMap)
        return dfMap
    }

    fun getCorpasLineNum(): Int {
        val br = BufferedReader(FileReader(File("./data/feature/corpas_line_num.txt")))
        return br.readLine().toInt()
    }
}