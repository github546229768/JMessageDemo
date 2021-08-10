package com.rl.jmessagedemo.extensions

import java.io.UnsupportedEncodingException

/**

 * @Auther: 杨景

 * @datetime: 2021/8/10

 * @desc:

 */
class ChineseCharToEn {
    companion object {
        private val li_SecPosValue = intArrayOf(
            1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590
        )
        private val lc_FirstLetter = arrayOf(
            "a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"
        )

        /**
         * 取得给定汉字串的首字母串,即声母串
         * @param str 给定汉字串
         * @return 声母串
         */
        fun getAllFirstLetter(str: String?): String {
            if (str == null || str.trim { it <= ' ' }.isEmpty()) {
                return ""
            }
            var desc = ""
            for (i in str.indices) {
                desc += getFirstLetter(str.substring(i, i + 1))
            }
            return desc
        }

        /**
         * 取得给定汉字的首字母,即声母
         * @param src 给定的汉字
         * @return 给定汉字的声母
         */
        private fun getFirstLetter(src: String): String {
            var chinese = src
            if (chinese.trim { it <= ' ' }.isEmpty()) {
                return ""
            }
            chinese = conversionStr(chinese, "GB2312", "ISO8859-1")
            if (chinese.length > 1) // 判断是不是汉字
            {
                var liSectorCode = chinese[0].toInt() // 汉字区码
                var liPositionCode = chinese[1].toInt() // 汉字位码
                liSectorCode -= 160
                liPositionCode -= 160
                val liSecPosCode = liSectorCode * 100 + liPositionCode // 汉字区位码
                if (liSecPosCode in 1601..5589) {
                    for (i in 0..22) {
                        if (liSecPosCode >= li_SecPosValue[i]
                            && liSecPosCode < li_SecPosValue[i + 1]
                        ) {
                            chinese = lc_FirstLetter[i]
                            break
                        }
                    }
                } else  // 非汉字字符,如图形符号或ASCII码
                {
                    chinese = conversionStr(chinese, "ISO8859-1", "GB2312")
                    chinese = chinese.substring(0, 1)
                }
            }
            return chinese
        }

        /**
         * 字符串编码转换
         * @param desc 要转换编码的字符串
         * @param charsetName 原来的编码
         * @param toCharsetName 转换后的编码
         * @return 经过编码转换后的字符串
         */
        private fun conversionStr(
            desc: String,
            charsetName: String,
            toCharsetName: String
        ): String {
            var str = desc
            try {
                str = String(str.toByteArray(charset(charsetName)), charset(toCharsetName))
            } catch (ex: UnsupportedEncodingException) {
            }
            return str
        }

    }

}