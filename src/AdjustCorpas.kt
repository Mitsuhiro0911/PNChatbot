import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

fun main(args: Array<String>) {
    for(i in 1 until 130) {
        val br = BufferedReader(FileReader(File("./data/corpas/nucc/data${String.format("%03d", i)}.txt")))
        val bw = BufferedWriter(FileWriter(File("./data/corpas/nucc_adjust/data${String.format("%03d", i)}.txt")))
        val participantList = mutableListOf<String>()
        var str = br.readLine()
        while(str.get(0) == '＠'){
            if(str.substring(1, 4) == "参加者"){
                if(str.substring(4, 7) != "の関係" && str.substring(4, 8) != "間の関係") {
                    participantList.add(str.substring(4, 8))
                }
            }
            bw.write(str)
            bw.newLine()
            str = br.readLine()
            println(str)
        }

        bw.write(str)
        str = br.readLine()

        println(participantList)
        while (str != null) {
            var participantFlag = false
            for(participant in participantList){
                if(str.contains("${participant}：")){
                    bw.newLine()
                    bw.write(str)
                    participantFlag = true
                }
            }
            if(!participantFlag) {
                bw.write(str)
            }
            str = br.readLine()
        }
        bw.close()
    }
}