import java.io.BufferedReader
import java.io.File
import java.io.FileReader


fun main(args: Array<String>) {
    // チャットボットへの入力メッセージ
    val message = "この写真はインスタ映えしますね。"
//    val reverseMessage = "私の趣味は写真を取ることです。写真"
    val cal = Calculator()

    val br = BufferedReader(FileReader(File("./data/corpas/nucc/data001.txt")))
    var reverseMessage = br.readLine()
    while(reverseMessage != null) {
        println(reverseMessage)
        val command = arrayOf(
            "sh", "-c",
            "echo ${message + reverseMessage} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
        )

        val wordList = Parser().makeWordList(command)
        val messageCommand = arrayOf(
            "sh", "-c",
            "echo ${message} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
        )

        val messaseTfMap = Parser().calTf(messageCommand, wordList)
        println(messaseTfMap)
        val reverseCommand = arrayOf(
            "sh", "-c",
            "echo ${reverseMessage} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
        )

        val reverseMessaseTfMap = Parser().calTf(reverseCommand, wordList)
        println(reverseMessaseTfMap)

        val messageVector = messaseTfMap.values.toDoubleArray()
        val reverseMessageVector = reverseMessaseTfMap.values.toDoubleArray()
        println(cal.calCosSimilarity(messageVector, reverseMessageVector))
        reverseMessage = br.readLine()
    }
}