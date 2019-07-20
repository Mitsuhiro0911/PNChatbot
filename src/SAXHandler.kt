import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

internal class SAXHandler : DefaultHandler() {
    // SkipGramベクトル取得対象の<word>を解析するときにtrueになるフラグ
    var targetFlag = false
    // <synonyms>を読み込む時にtrueになるフラグ
    var synonymsFlag = false

    /**
     * 要素解析開始時に実行される。
     * <synonums>の要素を解析するときsynonymsFlagをtrueにする。
     */
    override fun startElement(uri: String?, localName: String?, qName: String, attributes: Attributes?) {
        if (qName == "synonyms") {
            synonymsFlag = true
        }
    }

    /**
     * 要素解析終了時に実行される。
     * synonymsFlagを初期値(false)に戻す。
     */
    override fun endElement(uri: String?, localName: String?, qName: String?) {
        synonymsFlag = false
    }

    /**
     * 要素の中身を出力する。
     */
    override fun characters(ch: CharArray?, start: Int, length: Int) {
        // SkipGramベクトル取得対象の<word>を取得する
        if (String(ch!!, start, length) == "尚絅学院中学校・高等学校") {
            println(String(ch, start, length))
            targetFlag = true
            return
        }

        // SkipGramベクトル取得対象の<synonyms>を取得する
        // SkipGramベクトル取得対象の<word>を取得後　かつ　要素が<synonyms>のとき
        if (targetFlag && synonymsFlag) {
            println(String(ch, start, length))
            targetFlag = false
        }

    }
}