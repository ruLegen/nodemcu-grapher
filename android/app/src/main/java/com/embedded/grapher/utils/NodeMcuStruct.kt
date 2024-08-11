package com.embedded.grapher.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.math.min

typealias Inttype = Long
typealias Uinttype = Long

const val MAXINTSIZE = 8
const val LUA_MININTEGER = Int.MIN_VALUE
const val LUA_MAXINTEGER = Int.MAX_VALUE

data class UnpackResult(val result: List<Any>, val position: Int)
class NodeMcuStruct {

    companion object{

    fun isp2(x: Int): Boolean = (x > 0 && (x and (x - 1)) == 0)

    data class Header(
        var endian: ByteOrder = ByteOrder.LITTLE_ENDIAN,
        var align: Int = 1
    )

    fun defaultOptions(h: Header) {
        h.endian = ByteOrder.LITTLE_ENDIAN
        h.align = 1
    }

    fun getNum(fmt: String, defaultValue: Int): Int {
        var pos = 0
        var value = 0
        while (pos < fmt.length && fmt[pos].isDigit()) {
            value = value * 10 + (fmt[pos] - '0')
            pos++
        }
        return if (pos == 0) defaultValue else value
    }

    fun optSize(opt: Char, fmt: String): Int {
        return when (opt) {
            'B', 'b' -> Byte.SIZE_BYTES
            'H', 'h' -> Short.SIZE_BYTES
            'L', 'l' -> Long.SIZE_BYTES
            'T' -> Long.SIZE_BYTES
            'f' -> 4
            'd' -> 8
            'x' -> 1
            'c' -> getNum(fmt, 1)
            'i', 'I' -> {
                val sz = getNum(fmt, Int.SIZE_BYTES)
                if (sz > MAXINTSIZE) throw IllegalArgumentException("Integral size $sz is larger than limit of $MAXINTSIZE")
                sz
            }
            else -> 0
        }
    }

    fun getToAlign(len: Int, h: Header, opt: Char, size: Int): Int {
        if (size == 0 || opt == 'c') return 0
        val alignment = min(size, h.align)
        return (alignment - (len % alignment)) % alignment
    }

    fun putInteger(buffer: ByteBuffer, value: Long, endian: ByteOrder, size: Int) {
        buffer.order(endian)
        when (size) {
            Byte.SIZE_BYTES -> buffer.put(value.toByte())
            Short.SIZE_BYTES -> buffer.putShort(value.toShort())
            Int.SIZE_BYTES -> buffer.putInt(value.toInt())
            Long.SIZE_BYTES -> buffer.putLong(value)
            else -> throw IllegalArgumentException("Unsupported size: $size")
        }
    }

    fun getInteger(buffer: ByteBuffer, endian: ByteOrder, size: Int): Long {
        buffer.order(endian)
        return when (size) {
            Byte.SIZE_BYTES -> buffer.get().toLong()
            Short.SIZE_BYTES -> buffer.getShort().toLong()
            Int.SIZE_BYTES -> buffer.getInt().toLong()
            Long.SIZE_BYTES -> buffer.getLong()
            else -> throw IllegalArgumentException("Unsupported size: $size")
        }
    }

    fun pack(fmt: String, vararg args: Any): ByteArray {
        val buffer = ByteBuffer.allocate(1024)
        val h = Header()
        defaultOptions(h)
        var argIndex = 0
        var fmtIndex = 0

        while (fmtIndex < fmt.length) {
            val opt = fmt[fmtIndex++]
            val size = optSize(opt, fmt.substring(fmtIndex))
            val toAlign = getToAlign(buffer.position(), h, opt, size)
            for (i in 0 until toAlign) {
                buffer.put(0.toByte())
            }

            when (opt) {
                'b', 'B', 'h', 'H', 'l', 'L', 'T', 'i', 'I' -> {
                    val value = (args[argIndex++] as Number).toLong()
                    putInteger(buffer, value, h.endian, size)
                }
                'x' -> buffer.put(0.toByte())
                'f' -> buffer.putFloat((args[argIndex++] as Number).toFloat())
                'd' -> buffer.putDouble((args[argIndex++] as Number).toDouble())
                'c' -> {
                    val str = args[argIndex++] as String
                    buffer.put(str.toByteArray(Charset.defaultCharset()), 0, size)
                }
                's' -> {
                    val str = args[argIndex++] as String
                    buffer.put(str.toByteArray(Charset.defaultCharset()))
                    buffer.put(0.toByte()) // null-terminated
                }
                '>' -> h.endian = ByteOrder.BIG_ENDIAN
                '<' -> h.endian = ByteOrder.LITTLE_ENDIAN
                '!' -> {
                    h.align = getNum(fmt.substring(fmtIndex), 1)
                    if (!isp2(h.align)) throw IllegalArgumentException("Alignment ${h.align} is not a power of 2")
                }
                ' ' -> {} // ignore spaces
                else -> throw IllegalArgumentException("Invalid format option: '$opt'")
            }
        }
        return buffer.array().sliceArray(0 until buffer.position())
    }


    fun unpack(fmt: String, data: ByteArray,offset:Int = 0): UnpackResult {
        val buffer = ByteBuffer.wrap(data)
        buffer.position(offset)
        val h = Header()
        defaultOptions(h)
        val result = mutableListOf<Any>()
        var fmtIndex = 0

        while (fmtIndex < fmt.length) {
            val opt = fmt[fmtIndex++]
            val size = optSize(opt, fmt.substring(fmtIndex))
            val toAlign = getToAlign(buffer.position(), h, opt, size)
            buffer.position(buffer.position() + toAlign)

            when (opt) {
                'b', 'B', 'h', 'H', 'l', 'L', 'T', 'i', 'I' -> {
                    result.add(getInteger(buffer, h.endian, size))
                }
                'x' -> buffer.position(buffer.position() + 1)
                'f' -> result.add(buffer.float)
                'd' -> result.add(buffer.double)
                'c' -> {
                    val bytes = ByteArray(size)
                    buffer.get(bytes)
                    result.add(String(bytes, Charset.defaultCharset()))
                }
                's' -> {
                    val start = buffer.position()
                    while (buffer.get() != 0.toByte());
                    val end = buffer.position() - 1
                    result.add(String(data, start, end - start, Charset.defaultCharset()))
                }
                '>' -> {
                    h.endian = ByteOrder.BIG_ENDIAN
                    buffer.order(h.endian)
                }
                '<' ->{
                    h.endian = ByteOrder.LITTLE_ENDIAN
                    buffer.order(h.endian)
                }
                '!' -> {
                    h.align = getNum(fmt.substring(fmtIndex), 1)
                    if (!isp2(h.align)) throw IllegalArgumentException("Alignment ${h.align} is not a power of 2")
                }
                ' ' -> {} // ignore spaces
                else -> throw IllegalArgumentException("Invalid format option: '$opt'")
            }
        }
        return UnpackResult(result,buffer.position())
    }

    fun size(fmt: String): Int {
        val h = Header()
        defaultOptions(h)
        var pos = 0
        var fmtIndex = 0

        while (fmtIndex < fmt.length) {
            val opt = fmt[fmtIndex++]
            val size = optSize(opt, fmt.substring(fmtIndex))
            pos += getToAlign(pos, h, opt, size)

            if (opt == 's') throw IllegalArgumentException("Option 's' has no fixed size")
            if (opt == 'c' && size == 0) throw IllegalArgumentException("Option 'c0' has no fixed size")

            if (!opt.isLetterOrDigit()) {
                if (opt == '>' || opt == '<' || opt == '!') continue
                throw IllegalArgumentException("Invalid format option: '$opt'")
            }

            pos += size
        }
        return pos
    }
    }
}