package com.siamsr.billmeter8

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList

class XMLParser {

    internal var CISData: MutableList<XMLParserObject>
    private var cis_ref: XMLParserObject? = null
    private var text: String? = null
    private var text2: String? = null

    val cisData: List<XMLParserObject>
        get() = CISData

    init {
        CISData = ArrayList()
    }

    fun parse(`is`: InputStream): List<XMLParserObject> {
        var factory: XmlPullParserFactory? = null
        var parser: XmlPullParser? = null
        try {
            factory = XmlPullParserFactory.newInstance()
            factory!!.isNamespaceAware = true
            parser = factory.newPullParser()
            parser!!.setInput(`is`, null)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG ->
                        // if <table> create a new instance of post
                        if (tagname.equals("BFRead", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "DATA"
                            cis_ref!!.setprecode(text2!!)
                        } else if (tagname.equals("Constant", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                        } else if (tagname.equals("Dbst51", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D51"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("Dbst52", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D52"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("Dbst53", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D53"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("Dbst54", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D54"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("Dbst06", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D06"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("Dbst42", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "D42"
                            cis_ref!!.setwwcode(text2!!)
                        } else if (tagname.equals("msEmployee", ignoreCase = true)) {
                            cis_ref = XMLParserObject()
                            text2 = "EMP"
                            cis_ref!!.setprecode(text2!!)
                        }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("BFRead", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst51", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst52", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst53", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst54", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst06", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("Dbst42", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("msEmployee", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("wwcode", ignoreCase = true)) {
                        cis_ref!!.setwwcode(text!!)
                    } else if (tagname.equals("mtrrdroute", ignoreCase = true)) {
                        cis_ref!!.setmtrrdroute(text!!)
                    } else if (tagname.equals("mtrseq", ignoreCase = true)) {
                        cis_ref!!.setmtrseq(Integer.parseInt(text))
                    } else if (tagname.equals("custcode", ignoreCase = true)) {
                        cis_ref!!.setcustcode(text!!)
                    } else if (tagname.equals("usertype", ignoreCase = true)) {
                        cis_ref!!.setusertype(text!!)
                    } else if (tagname.equals("oldtype", ignoreCase = true)) {
                        cis_ref!!.setoldtype(text!!)
                    } else if (tagname.equals("custname", ignoreCase = true)) {
                        cis_ref!!.setcustname(text!!)
                    } else if (tagname.equals("custaddr", ignoreCase = true)) {
                        cis_ref!!.setcustaddr(text!!)
                    } else if (tagname.equals("location", ignoreCase = true)) {
                        cis_ref!!.setlocation(text!!)
                    } else if (tagname.equals("mtrmkcode", ignoreCase = true)) {
                        cis_ref!!.setmtrmkcode(text!!)
                    } else if (tagname.equals("metersize", ignoreCase = true)) {
                        cis_ref!!.setmetersize(text!!)
                    } else if (tagname.equals("meterno", ignoreCase = true)) {
                        cis_ref!!.setmeterno(text!!)
                    } else if (tagname.equals("prsmtrstat", ignoreCase = true)) {
                        cis_ref!!.setprsmtrstat(text!!)
                    } else if (tagname.equals("lstmtrddt", ignoreCase = true)) {
                        cis_ref!!.setlstmtrddt(text!!)
                    } else if (tagname.equals("lstmtrcnt", ignoreCase = true)) {
                        cis_ref!!.setlstmtrcnt(Integer.parseInt(text))
                    } else if (tagname.equals("revym", ignoreCase = true)) {
                        cis_ref!!.setrevym(text!!)
                    } else if (tagname.equals("novat", ignoreCase = true)) {
                        cis_ref!!.setnovat(text!!)
                    } else if (tagname.equals("avgwtuse", ignoreCase = true)) {
                        cis_ref!!.setavgwtuse(Integer.parseInt(text))
                    } else if (tagname.equals("discntcode", ignoreCase = true)) {
                        cis_ref!!.setdiscntcode(text!!)
                    } else if (tagname.equals("invoicecnt", ignoreCase = true)) {
                        cis_ref!!.setinvoicecnt(text!!)
                    } else if (tagname.equals("invflag", ignoreCase = true)) {
                        cis_ref!!.setinvflag(text!!)
                    } else if (tagname.equals("debmonth", ignoreCase = true)) {
                        cis_ref!!.setdebmonth(Integer.parseInt(text))
                    } else if (tagname.equals("debamt", ignoreCase = true)) {
                        cis_ref!!.setdebamt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("remwtusg", ignoreCase = true)) {
                        cis_ref!!.setremwtusg(Integer.parseInt(text))
                    } else if (tagname.equals("noofhouse", ignoreCase = true)) {
                        cis_ref!!.setnoofhouse(Integer.parseInt(text))
                    } else if (tagname.equals("pwa_flag", ignoreCase = true)) {
                        cis_ref!!.setpwaflag(text!!)
                    } else if (tagname.equals("meterest", ignoreCase = true)) {
                        cis_ref!!.setmeterest(Integer.parseInt(text))
                    } else if (tagname.equals("smcnt", ignoreCase = true)) {
                        cis_ref!!.setsmcnt(Integer.parseInt(text))
                    } else if (tagname.equals("mincharge", ignoreCase = true)) {
                        cis_ref!!.setmincharge(text!!)
                    } else if (tagname.equals("lstwtusg", ignoreCase = true)) {
                        cis_ref!!.setlstwtusg(Integer.parseInt(text))
                    } else if (tagname.equals("subdiscnt", ignoreCase = true)) {
                        cis_ref!!.setsubdiscn(Integer.parseInt(text))
                    } else if (tagname.equals("readflag", ignoreCase = true)) {
                        cis_ref!!.setreadflag(text!!)
                    } else if (tagname.equals("newread", ignoreCase = true)) {
                        cis_ref!!.setnewread(text!!)
                    } else if (tagname.equals("prsmtrcnt", ignoreCase = true)) {
                        cis_ref!!.setprsmtrcnt(Integer.parseInt(text))
                    } else if (tagname.equals("nortrfwt", ignoreCase = true)) {
                        cis_ref!!.setnortrfwt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("nortrfwt", ignoreCase = true)) {
                        cis_ref!!.setnortrfwt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("discntamt", ignoreCase = true)) {
                        cis_ref!!.setdiscntamt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("srvfee", ignoreCase = true)) {
                        cis_ref!!.setsrvfee(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("vat", ignoreCase = true)) {
                        cis_ref!!.setvat(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("tottrfwt", ignoreCase = true)) {
                        cis_ref!!.settottrfwt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("comment", ignoreCase = true)) {
                        cis_ref!!.setcomment(text!!)
                    } else if (tagname.equals("commentdec", ignoreCase = true)) {
                        cis_ref!!.setcommentdec(text!!)
                    } else if (tagname.equals("billflag", ignoreCase = true)) {
                        cis_ref!!.setbillflag(text!!)
                    } else if (tagname.equals("billsend", ignoreCase = true)) {
                        cis_ref!!.setbillsend(text!!)
                    } else if (tagname.equals("hln", ignoreCase = true)) {
                        cis_ref!!.sethln(text!!)
                    } else if (tagname.equals("prswtusg", ignoreCase = true)) {
                        cis_ref!!.setprswtusg(Integer.parseInt(text))
                    } else if (tagname.equals("latitude", ignoreCase = true)) {
                        cis_ref!!.setlatitude(text!!)
                    } else if (tagname.equals("lontitude", ignoreCase = true)) {
                        cis_ref!!.setlontitude(text!!)
                    } else if (tagname.equals("prsmtrrddt", ignoreCase = true)) {
                        cis_ref!!.setprsmtrrddt(text!!)
                    } else if (tagname.equals("time", ignoreCase = true)) {
                        cis_ref!!.settime(text!!)
                    } else if (tagname.equals("readcount", ignoreCase = true)) {
                        cis_ref!!.setreadcount(Integer.parseInt(text))
                    } else if (tagname.equals("printcount", ignoreCase = true)) {
                        cis_ref!!.setprintcount(Integer.parseInt(text))
                    } else if (tagname.equals("usgcalmthd", ignoreCase = true)) {
                        cis_ref!!.setusgcalmthd(text!!)
                    } else if (tagname.equals("userid", ignoreCase = true)) {
                        cis_ref!!.setuserid(text!!)
                    } else if (tagname.equals("chkdigit", ignoreCase = true)) {
                        cis_ref!!.setchkdigit(text!!)
                    } else if (tagname.equals("controlmtr", ignoreCase = true)) {
                        cis_ref!!.setcontrolmtr(text!!)
                    } else if (tagname.equals("bigmtrno", ignoreCase = true)) {
                        cis_ref!!.setbigmtrno(text!!)
                    } else if (tagname.equals("tbseq", ignoreCase = true)) {
                        cis_ref!!.settbset(Integer.parseInt(text))
                    } else if (tagname.equals("regis_no", ignoreCase = true)) {
                        cis_ref!!.setregisno(text!!)
                    } else if (tagname.equals("cust_brn", ignoreCase = true)) {
                        cis_ref!!.setcustbrn(text!!)
                    } else if (tagname.equals("Constant", ignoreCase = true)) {
                        cis_ref?.let { CISData.add(it) }
                    } else if (tagname.equals("wwnamet", ignoreCase = true)) {
                        cis_ref!!.setprecode("CON")
                        cis_ref!!.setwwnamet(text!!)
                    } else if (tagname.equals("wwtel", ignoreCase = true)) {
                        cis_ref!!.setwwtel(text!!)
                    } else if (tagname.equals("ba", ignoreCase = true)) {
                        cis_ref!!.setba(text!!)
                    } else if (tagname.equals("mvamt1", ignoreCase = true)) {
                        cis_ref!!.setmvamt1(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("mvamt2", ignoreCase = true)) {
                        cis_ref!!.setmvamt2(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("mvamt3", ignoreCase = true)) {
                        cis_ref!!.setmvamt3(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("dispwapro", ignoreCase = true)) {
                        cis_ref!!.setdispwapro(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("wttrfrt", ignoreCase = true)) {
                        cis_ref!!.setwttrfrt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("acwttrfamt", ignoreCase = true)) {
                        cis_ref!!.setacwttrfamt(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("lowusgran", ignoreCase = true)) {
                        cis_ref!!.setlowusgran(Integer.parseInt(text))
                    } else if (tagname.equals("highusgran", ignoreCase = true)) {
                        cis_ref!!.sethighusgran(Integer.parseInt(text))
                    } else if (tagname.equals("discntmean", ignoreCase = true)) {
                        cis_ref!!.setdiscntmean(text!!)
                    } else if (tagname.equals("discnttype", ignoreCase = true)) {
                        cis_ref!!.setdiscnttype(text!!)
                    } else if (tagname.equals("discntsrvf", ignoreCase = true)) {
                        cis_ref!!.setdiscntsrvf(text!!)
                    } else if (tagname.equals("discntpcen", ignoreCase = true)) {
                        cis_ref!!.setdiscntpcen(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("discntbaht", ignoreCase = true)) {
                        cis_ref!!.setdiscntbaht(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("discntnumr", ignoreCase = true)) {
                        cis_ref!!.setdiscntnumr(Integer.parseInt(text))
                    } else if (tagname.equals("discntdnom", ignoreCase = true)) {
                        cis_ref!!.setdiscntdnom(Integer.parseInt(text))
                    } else if (tagname.equals("discntunit", ignoreCase = true)) {
                        cis_ref!!.setdiscntunit(Integer.parseInt(text))
                    } else if (tagname.equals("metersize", ignoreCase = true)) {
                        cis_ref!!.setmetersize(text!!)
                    } else if (tagname.equals("srvfee", ignoreCase = true)) {
                        cis_ref!!.setsrvfee(java.lang.Double.parseDouble(text))
                    } else if (tagname.equals("empid", ignoreCase = true)) {
                        cis_ref!!.setempid(text!!)
                    } else if (tagname.equals("empname", ignoreCase = true)) {
                        cis_ref!!.setempname(text!!)
                    } else if (tagname.equals("emplastname", ignoreCase = true)) {
                        cis_ref!!.setemplastname(text!!)
                    } else if (tagname.equals("empmobile", ignoreCase = true)) {
                        cis_ref!!.setempmoblie(text!!)
                    } else if (tagname.equals("empposition", ignoreCase = true)) {
                        cis_ref!!.setempposition(text!!)
                    }

                    else -> {
                    }
                }
                eventType = parser.next()
            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return CISData
    }

}
