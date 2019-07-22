import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.math.log10


fun main(args: Array<String>) {
    // TODO:前処理でノイズ除去
    // TODO:Word2Vecで単語間の関係性把握
    // TODO:ポジティブ・ネガティブ判定

    val cal = Calculator()
    val messagesInfoList = arrayListOf<MessagesInfo>()

    // 入力メッセージ(message.txt)を形態素解析し、出現する名詞をリストへ格納
    val messageCommand = arrayOf(
        "sh", "-c",
        "mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd /Users/Nakamura/IdeaProjects/PNChatbot/data/input/message.txt"
    )
    val messageWordList = Parser().parseMessage(messageCommand)
    println(messageWordList)

    // 返答メッセージと類似度のマップ
    var answer = LinkedHashMap<String, Double>()
    // nuccコーパスのdata001.txt〜data129.txtに出現する名詞とそのDF値のマップ
    val dfMap = cal.getDf(messageWordList)
    // nuccコーパスのdata001.txt〜data129.txtの全会話数
    val corpasLineNum = cal.getCorpasLineNum()
    // nuccコーパスのdata001.txt〜data129.txtを解析
    for(i in 1 until 130) {
        println("data${String.format("%03d", i)}")
        val br = BufferedReader(FileReader(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        var reverseMessage = br.readLine()
        while (reverseMessage != null) {
            val messagesInfo = MessagesInfo()

            // 文頭が「＠」で始まる文は解析対象ではないため、スキップする
            if(reverseMessage.get(0) == '＠'){
                reverseMessage = br.readLine()
                continue
            }
//        println(reverseMessage)
            // substringによって、文頭の人物コードをカット
            val reverseCommand = arrayOf(
                "sh", "-c",
                "echo ${reverseMessage.substring(5, reverseMessage.length)} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )

            // 返答メッセージを形態素解析し、出現する名詞をリストへ格納。入力メッセージと名詞が１つも被らない場合はnullとなる
            val reverseMessageWordList = Parser().parseReverseMessage(reverseCommand, messageWordList, reverseMessage)
            // 入力メッセージと返答メッセージの名詞が全く重複しない場合、その後の処理は冗長なのでスキップする(処理速度向上目的)
            if (reverseMessageWordList === null) {
                reverseMessage = br.readLine()
                continue
            }
            println()
            println("類似した発話：${reverseMessage.substring(5, reverseMessage.length)}")
            //　入力メッセージの素性ベクトルを取得(TF値により算出)
            val messaseTfMap = Parser().calTf(messageCommand, reverseMessageWordList)
            println(messaseTfMap)

            //　返答メッセージの素性ベクトルを取得(TF値により算出)
            val reverseMessaseTfMap = Parser().calTf(reverseCommand, reverseMessageWordList)
            println(reverseMessaseTfMap)

            // 入力メッセージと返答メッセージのTF-IDF値を計算
            var messaseTfIdfMap = messaseTfMap.clone() as LinkedHashMap<String, Double>
            var reverseMessaseTfIdfMap = reverseMessaseTfMap.clone() as LinkedHashMap<String, Double>
            for(word in reverseMessageWordList) {
                val idf = log10(corpasLineNum / dfMap.get(word)!!)
                val messageTfIdf = messaseTfMap.get(word)!! * idf
                messaseTfIdfMap.put(word, messageTfIdf)
                val reverseMessaseTfIdf = reverseMessaseTfMap.get(word)!! * idf
                reverseMessaseTfIdfMap.put(word, reverseMessaseTfIdf)
            }
            println("入力メッセージTFIDF:${messaseTfIdfMap}\n返答メッセージTFIDF:${reverseMessaseTfIdfMap}")

            // 入力メッセージ及び返答メッセージに出現する単語のSkipGramベクトルを取得用リストへ格納
            for(word in messaseTfIdfMap.keys){
                SkipGram.targetWordList.add(word)
            }

            // 入力メッセージと返答メッセージのコサイン類似度を計算
            val messageVector = messaseTfIdfMap.values.toDoubleArray()
            val reverseMessageVector = reverseMessaseTfIdfMap.values.toDoubleArray()
            val cosSimilarity = cal.calCosSimilarity(messageVector, reverseMessageVector)
            println("コサイン類似度：${cosSimilarity}")

            reverseMessage = br.readLine()
            println("返答：${reverseMessage.substring(5, reverseMessage.length)}")
            println()
            answer.put(reverseMessage.substring(5, reverseMessage.length), cosSimilarity)

            messagesInfo.messaseTfIdfMap = messaseTfIdfMap
            messagesInfo.similarMessaseTfIdfMap = reverseMessaseTfIdfMap
            messagesInfo.reverseMessage = reverseMessage
            messagesInfoList.add(messagesInfo)

        }
    }

    // SkipGramベクトルを取得
    println(SkipGram.targetWordList)
    val skipGram = SkipGram().getSkipGramVectorSAX()
    for(mi in messagesInfoList) {
        println("入力メッセージのTF-IDF：${mi.messaseTfIdfMap}")
        println("類似メッセージのTF-IDF：${mi.similarMessaseTfIdfMap}")
        println("返答メッセージ候補：${mi.reverseMessage}")
        // 入力メッセージと返答メッセージのコサイン類似度を計算
        val messageVector = mi.messaseTfIdfMap.values.toDoubleArray()
        val similarMessageVector = mi.similarMessaseTfIdfMap.values.toDoubleArray()
        println("コサイン類似度：${cal.calCosSimilarity(messageVector, similarMessageVector)}")
        // 使用するSkipGramベクトルを取得
        for(word in mi.messaseTfIdfMap.keys) {
            print("${word}：")
            println(skipGram.get(word))
        }
        println()
    }

    // 返答メッセージ候補トップ１０を出力
    val sortedAnswer = answer.toList().sortedByDescending { it.second }.toMap()
    var i = 0
    for(ans in sortedAnswer){
        i = i.plus(1)
        println("${i}位：${ans}")
        if(i > 9) {
            break
        }
    }

}

class MessagesInfo {
    var messaseTfIdfMap = LinkedHashMap<String, Double>()
    var similarMessaseTfIdfMap = LinkedHashMap<String, Double>()
    var reverseMessage = ""

}