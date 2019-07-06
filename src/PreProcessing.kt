import java.io.BufferedReader

class PreProcessing {

    /**
     * data●●●.txtの冒頭のヘッダ部を読み飛ばし、対話内容の１行目を返す。
     */
    fun skipHeader(br: BufferedReader): String?{
        var reverseMessage = br.readLine()
        while(reverseMessage.get(0) == '＠'){
            reverseMessage = br.readLine()
        }
        return reverseMessage
    }
}