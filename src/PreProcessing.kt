import java.io.BufferedReader
import java.util.regex.Pattern


class PreProcessing {

    /**
     * data●●●.txtの冒頭のヘッダ部を読み飛ばし、対話内容の１行目を返す。
     */
    fun skipHeader(br: BufferedReader): String? {
        var reverseMessage = br.readLine()
        while (reverseMessage.get(0) == '＠') {
            reverseMessage = br.readLine()
        }
        return reverseMessage
    }

    /**
     * 形態素解析結果から不用語を除去(数字だけのもの、記号だけのもの等)
     * @return 不要語と判定した場合falseを返す
     */
    fun cleanNoise(word: String): Boolean {
        var addFlag = true
        // 形態素解析した名詞が数字だけで構成される場合、不用語と判定する
        // 括弧で囲むことにより、groupメソッドの引数で部分を指定した抽出ができるようになる
        val regex = "([0-9]+)"
        val p = Pattern.compile(regex)
        val m = p.matcher(word)
        if (m.find()) {
            addFlag = false
        }

        // 形態素解析した名詞が記号の場合、不用語と判定する
        if(word == "<" || word == ">") {
            addFlag = false
        }
        return addFlag
    }
}