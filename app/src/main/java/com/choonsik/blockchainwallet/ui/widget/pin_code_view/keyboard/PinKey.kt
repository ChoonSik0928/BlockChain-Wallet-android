package com.choonsik.blockchainwallet.ui.widget.pin_code_view.keyboard

import java.lang.IllegalArgumentException

sealed class PinKey {
    class Num(val value: Int) : PinKey()
    class Alphabet(val value: Char) : PinKey()
    object BackKey : PinKey()
    object EmptyKey : PinKey()


    companion object {
        fun getString(pinKey: PinKey): String {
            when (pinKey) {
                is Num -> {
                    if (pinKey.value in 0..9) {
                        return pinKey.value.toString()
                    } else {
                        throw IllegalArgumentException("pinKey supported only 0 to 9")
                    }
                }

                is Alphabet -> {
                    if (pinKey.value in 'a'..'z' || pinKey.value in 'A'..'Z') {
                        return pinKey.value.toString()
                    } else {
                        throw IllegalArgumentException("pinKey supported only a to Z ")
                    }
                }
                is BackKey -> {
                    return "<-"
                }
                is EmptyKey -> {
                    return ""
                }

                else -> throw IllegalArgumentException()
            }
        }

        fun getKeys(keys: ArrayList<PinKey>): String {
            var value = ""
            keys.forEach {
                value += getString(it)
            }

            return value
        }
    }

    override fun equals(other: Any?): Boolean {
        return getString(other as PinKey) == getString(this)
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}