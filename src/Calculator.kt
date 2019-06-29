import net.sf.javaml.core.DenseInstance
import net.sf.javaml.distance.CosineSimilarity

class Calculator{
    private val cs = CosineSimilarity()
    /**
     * 2つの商品[x],[y]間のコサイン類似度を計算する
     *
     * @param[x],[y] 商品の素性ベクトル
     * @return 商品同士のコサイン類似度
     */
    fun calCosSimilarity(x: DoubleArray, y: DoubleArray): Double{
        return this.cs.measure(DenseInstance(x), DenseInstance(y))
    }
}