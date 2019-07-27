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

    // nuccコーパスのdata001.txt〜data129.txtに出現する名詞とそのDF値のマップ
    val dfMap = cal.getDf(messageWordList)
    // nuccコーパスのdata001.txt〜data129.txtの全会話数
    val corpasLineNum = cal.getCorpasLineNum()
    // nuccコーパスのdata001.txt〜data129.txtを解析
    for(i in 1 until 130) {
        println("data${String.format("%03d", i)}")
        val br = BufferedReader(FileReader(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        var corpasMessage = br.readLine()
        while (corpasMessage != null) {
            val messagesInfo = MessagesInfo()
            // 文頭が「＠」で始まる文は解析対象ではないため、スキップする
            if(corpasMessage.get(0) == '＠'){
                corpasMessage = br.readLine()
                continue
            }

            // substringによって、文頭の人物コードをカット
            val reverseCommand = arrayOf(
                "sh", "-c",
                "echo ${corpasMessage.substring(5, corpasMessage.length)} | mecab -d /usr/local/lib/mecab/dic/mecab-ipadic-neologd"
            )

            // 返答メッセージを形態素解析し、出現する名詞をリストへ格納。入力メッセージと名詞が１つも被らない場合はnullとなる
            val reverseMessageWordList = Parser().parseReverseMessage(reverseCommand, messageWordList, corpasMessage)
            // 入力メッセージと返答メッセージの名詞が全く重複しない場合、その後の処理は冗長なのでスキップする(処理速度向上目的)
            if (reverseMessageWordList === null) {
                corpasMessage = br.readLine()
                continue
            }
            messagesInfo.similarMessase = corpasMessage.substring(5, corpasMessage.length)
            //　入力メッセージの素性ベクトルを取得(TF値により算出)
            val messaseTfMap = Parser().calTf(messageCommand, reverseMessageWordList)

            //　返答メッセージの素性ベクトルを取得(TF値により算出)
            val reverseMessaseTfMap = Parser().calTf(reverseCommand, reverseMessageWordList)

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

            // 入力メッセージ及び返答メッセージに出現する単語のSkipGramベクトルを取得用リストへ格納
            for(word in messaseTfIdfMap.keys){
                SkipGram.targetWordList.add(word)
            }

            corpasMessage = br.readLine()

            messagesInfo.messaseTfIdfMap = messaseTfIdfMap
            messagesInfo.similarMessaseTfIdfMap = reverseMessaseTfIdfMap
            messagesInfo.reverseMessage = corpasMessage.substring(5, corpasMessage.length)
            messagesInfoList.add(messagesInfo)
        }
    }

    // SkipGramベクトルを取得
    println(SkipGram.targetWordList)
    val skipGram = SkipGram().getSkipGramVectorSAX()
    // 返答メッセージと類似度のマップ
    var answer = LinkedHashMap<String, Double>()
    for(mi in messagesInfoList) {
        println("入力メッセージのTF-IDF：${mi.messaseTfIdfMap}\n")
        println("類似メッセージ：${mi.similarMessase}")
        println("類似メッセージのTF-IDF：${mi.similarMessaseTfIdfMap}\n")
        println("返答メッセージ候補：${mi.reverseMessage}\n")
        // 入力メッセージと返答メッセージのコサイン類似度を計算
        val messageVector = mi.messaseTfIdfMap.values.toDoubleArray()
        val similarMessageVector = mi.similarMessaseTfIdfMap.values.toDoubleArray()
        val cosSimilarity = cal.calCosSimilarity(messageVector, similarMessageVector)
        println("コサイン類似度(TF-IDF)：${cosSimilarity}\n")

        // 使用するSkipGramベクトルを取得
        val messaseTfIdfSkipGramMap = mi.messaseTfIdfMap.clone() as LinkedHashMap<String, Double>
        val similarMessaseTfIdfSkipGramMap = mi.similarMessaseTfIdfMap.clone() as LinkedHashMap<String, Double>
        for(word in mi.messaseTfIdfMap.keys) {
//            print("${word}：")
//            println(skipGram.get(word))
            // TF-IDF素性ベクトルをSkipGram素性ベクトルで拡張
            if(skipGram.get(word) != null) {
                for(vector in skipGram.get(word)!!){
                    // 入力メッセージ素性ベクトルを拡張。TF-IDF値が0.0以外の単語をSkip-Gramで拡張。
                    if(messaseTfIdfSkipGramMap.get(word) != 0.0) {
                        // 既にSkipGramの素性ワードが存在する場合は処理をスキップし、TF-IDF値が上書きされるのを防止
                        if(!messaseTfIdfSkipGramMap.keys.contains(vector.key)) {
                            messaseTfIdfSkipGramMap.put(vector.key, vector.value)
                        }
                    } else {
                        messaseTfIdfSkipGramMap.put(vector.key, 0.0)
                    }

                    // 類似メッセージ素性ベクトルを拡張。TF-IDF値が0.0以外の単語をSkip-Gramで拡張。
                    if(similarMessaseTfIdfSkipGramMap.get(word) != 0.0) {
                        if(similarMessaseTfIdfSkipGramMap.get(word) != 0.0) {
                            // 既にSkipGramの素性ワードが存在する場合は処理をスキップし、TF-IDF値が上書きされるのを防止
                            similarMessaseTfIdfSkipGramMap.put(vector.key, vector.value)
                        }
                    } else {
                        similarMessaseTfIdfSkipGramMap.put(vector.key, 0.0)
                    }
                }
            }

        }
        println("入力メッセージのTF-IDF&SkipGram：${messaseTfIdfSkipGramMap}")
        println("類似メッセージのTF-IDF&SkipGram：${similarMessaseTfIdfSkipGramMap}")
        // 入力メッセージと返答メッセージのコサイン類似度を計算
        val messageTfIdfSkipVector = messaseTfIdfSkipGramMap.values.toDoubleArray()
        val similarMessageTfIdfSkipVector = similarMessaseTfIdfSkipGramMap.values.toDoubleArray()
        val TfIdfSkipcosSimilarity = cal.calCosSimilarity(messageTfIdfSkipVector, similarMessageTfIdfSkipVector)
        println("コサイン類似度(TF-IDF&SkipGram)：${TfIdfSkipcosSimilarity}\n")
        println("\n-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+\n")
        answer.put(mi.reverseMessage, TfIdfSkipcosSimilarity)
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
    companion object {
        var message = ""
    }
    var messaseTfIdfMap = LinkedHashMap<String, Double>()
    var similarMessase = ""
    var similarMessaseTfIdfMap = LinkedHashMap<String, Double>()
    var reverseMessage = ""

}