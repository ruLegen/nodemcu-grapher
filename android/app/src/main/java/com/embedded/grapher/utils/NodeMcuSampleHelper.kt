package com.embedded.grapher.utils

data class NodeMcuSample(val time:Float, val value:Float, val channel:Int)

class NodeMcuSampleHelper {
    companion object {
        fun decodeSamples(bytes:ByteArray) :List<NodeMcuSample>{
            val decodeResults = mutableListOf<UnpackResult>()
            var samples = emptyList<NodeMcuSample>()
            var decodeIndex = 0
            try {
                while (decodeIndex < bytes.size){
                    val decodeResult = NodeMcuStruct.unpack("<ffi",bytes,decodeIndex);
                    decodeResults.add(decodeResult)
                    decodeIndex  = decodeResult.position
                }
            }catch (ex: Exception){
                print(ex)
            }
            try {
                samples  = decodeResults.map{
                    NodeMcuSample(it.result[0] as Float,it.result[1] as Float,(it.result[2] as Long).toInt())
                }
            }catch (ex:Exception)
            {
                print(ex)
            }
            return  samples
        }
    }
}