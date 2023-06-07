package cn.jaylong.auth.kt.po

/**
 * @author NorthLan
 * @date 2021-05-31
 * @url https://noahlan.com
 */
data class AddressData(
    /**
     * 国家
     */
    var country: String = "",

    /**
     * 省份/地区
     */

    var province: String = "",
    /**
     * City or locality component.
     */
    /**
     * 市/州
     */
    var locality: String = "",

    /**
     * 市辖区/县
     */

    var district: String = "",

    /**
     * 街道地址
     *
     * 乡/镇 楼栋号 门牌号 等
     */

    var streetAddress: String = "",

    /**
     * 邮编号码
     */

    var postalCode: String = "",

    /**
     * State, province, prefecture, or region component.
     */
    var region: String = province + locality + district,

    /**
     * Full mailing address, formatted for display or use on a mailing label.
     * This field MAY contain multiple lines, separated by newlines.
     * Newlines can be represented either as a carriage return/line feed pair ("\r\n") or as a single line feed character ("\n").
     */
    var formatted: String = country + province + locality + streetAddress

)