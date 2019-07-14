import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    // IDFの計算に利用するため、data001.txt〜data130.txtの全会話数をカウントする変数
    var lineCount = 0
    for(i in 1 until 130) {
        val br = BufferedReader(FileReader(File("./data/corpas/nucc/data${String.format("%03d", i)}.txt")))
        val bwAdjustCorpas = BufferedWriter(FileWriter(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        val participantList = mutableListOf<String>()
        var str = br.readLine()
        while(str.get(0) == '＠'){
            if(str.substring(1, 4) == "参加者"){
                if(str.substring(4, 7) != "の関係" && str.substring(4, 8) != "間の関係") {
                    participantList.add(str.substring(4, 8))
                }
            }
            bwAdjustCorpas.write(str)
            bwAdjustCorpas.newLine()
            str = br.readLine()
            println(str)
        }

        bwAdjustCorpas.write(str)
        lineCount = lineCount.plus(1)
        str = br.readLine()

        println(participantList)
        while (str != null) {
            println(str)
            if(str == "＠ＥＮＤ"){
                bwAdjustCorpas.newLine()
                bwAdjustCorpas.write(str)
                break
            }
            var participantFlag = false
            for(participant in participantList){
                if(str.contains("${participant}：")){
                    bwAdjustCorpas.newLine()
                    bwAdjustCorpas.write(str)
                    lineCount = lineCount.plus(1)
                    participantFlag = true
                }
            }
            if(!participantFlag) {
                bwAdjustCorpas.write(str)
            }
            str = br.readLine()
        }
        bwAdjustCorpas.close()
    }
    println("data001.txt〜data130.txtの全会話数：${lineCount}")
    val bwCorpasLineNum = BufferedWriter(FileWriter(File("./data/feature/corpas_line_num.txt")))
    bwCorpasLineNum.write("${lineCount}")
    bwCorpasLineNum.close()
}