import java.io.BufferedReader
import java.io.File
import java.io.FileReader


fun main(args: Array<String>) {
    // チャットボットへの入力メッセージ
    val message = "この写真はインスタ映えしますね。"
//    val reverseMessage = "私の趣味は写真を取ることです。写真"
    val cal = Calculator()
    val messageCommand = arrayOf(
        "sh", "-c",
        "echo ${message} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
    )
    val messageWordList = Parser().parseMessage(messageCommand)
    println(messageWordList)
    for(i in 1 until 130) {
        println("data${String.format("%03d", i)}")
        val br = BufferedReader(FileReader(File("./data/corpas/nucc/data${String.format("%03d", i)}.txt")))
        var reverseMessage = br.readLine()
        while (reverseMessage != null) {
//        println(reverseMessage)
            val reverseCommand = arrayOf(
                "sh", "-c",
                "echo ${reverseMessage} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )
            val wordList = Parser().parseReverseMessage(reverseCommand, messageWordList, reverseMessage)
            // 発言と返答の名詞が全く重複しない場合、その後の処理は冗長なのでスキップする
            if (wordList === null) {
                reverseMessage = br.readLine()
                continue
            }
            println(reverseMessage)
            val messaseTfMap = Parser().calTf(messageCommand, wordList)
            println(messaseTfMap)


            val reverseMessaseTfMap = Parser().calTf(reverseCommand, wordList)
            println(reverseMessaseTfMap)

            val messageVector = messaseTfMap.values.toDoubleArray()
            val reverseMessageVector = reverseMessaseTfMap.values.toDoubleArray()
            println(cal.calCosSimilarity(messageVector, reverseMessageVector))
            reverseMessage = br.readLine()
            println("返答：${reverseMessage}")
        }
    }
}