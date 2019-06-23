import java.io.BufferedReader
import java.io.InputStreamReader

fun main(args: Array<String>) {
    // チャットボットへの入力メッセージ
    val message = "この写真はインスタ映えしますね。"

    val command = arrayOf(
        "sh", "-c",
        "echo ${message} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
    )

    Parser().parse(command)

}