import java.io.BufferedReader
import java.io.File
import java.io.FileReader


fun main(args: Array<String>) {
    // TODO:一人の連続した会話を連結できるようにする
    // TODO:素性ベクトルの計算処理にIDFの尺度を追加する

    // チャットボットへの入力メッセージ
//    val message = "この写真はインスタ映えしますね。"
    val message = "ラーメン食べよう。"
//    val reverseMessage = "私の趣味は写真を取ることです。写真"
    val cal = Calculator()

    // 入力メッセージを形態素解析し、出現する名詞をリストへ格納
    val messageCommand = arrayOf(
        "sh", "-c",
        "echo ${message} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
    )
    val messageWordList = Parser().parseMessage(messageCommand)
    println(messageWordList)

    // nuccコーパスのdata001.txt〜data129.txtを解析
    for(i in 1 until 130) {
        println("data${String.format("%03d", i)}")
        val br = BufferedReader(FileReader(File("./data/corpas/nucc/data${String.format("%03d", i)}.txt")))
        // data●●●.txtの冒頭のヘッダ部を読み飛ばし、対話内容の１行目を取得
        var reverseMessage = PreProcessing().skipHeader(br)
        while (reverseMessage != null) {
//        println(reverseMessage)
            val reverseCommand = arrayOf(
                "sh", "-c",
                "echo ${reverseMessage} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )
            // 返答メッセージを形態素解析し、出現する名詞をリストへ格納。入力メッセージと名詞が１つも被らない場合はnullとなる
            val reverseMessageWordList = Parser().parseReverseMessage(reverseCommand, messageWordList, reverseMessage)
            // 入力メッセージと返答メッセージの名詞が全く重複しない場合、その後の処理は冗長なのでスキップする(処理速度向上目的)
            if (reverseMessageWordList === null) {
                reverseMessage = br.readLine()
                continue
            }
            println(reverseMessage)
            //　入力メッセージの素性ベクトルを取得(TF値により算出)
            val messaseTfMap = Parser().calTf(messageCommand, reverseMessageWordList)
            println(messaseTfMap)

            //　返答メッセージの素性ベクトルを取得(TF値により算出)
            val reverseMessaseTfMap = Parser().calTf(reverseCommand, reverseMessageWordList)
            println(reverseMessaseTfMap)

            // 入力メッセージと返答メッセージのコサイン類似度を計算
            val messageVector = messaseTfMap.values.toDoubleArray()
            val reverseMessageVector = reverseMessaseTfMap.values.toDoubleArray()
            println(cal.calCosSimilarity(messageVector, reverseMessageVector))

            reverseMessage = br.readLine()
            println("返答：${reverseMessage}")
        }
    }
}