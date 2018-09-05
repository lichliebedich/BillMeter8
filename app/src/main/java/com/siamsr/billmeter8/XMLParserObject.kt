package com.siamsr.billmeter8

class XMLParserObject {

    private var precode: String? = null
    private var wwcode: String? = null
    private var mtrrdroute: String? = null
    private var mtrseq: Int = 0
    private var custcode: String? = null
    private var usertype: String? = null
    private var oldtype: String? = null
    private var custstat: String? = null
    private var location: String? = null
    private var custname: String? = null
    private var custaddr: String? = null
    private var mtrmkcode: String? = null
    private var metersize: String? = null
    private var meterno: String? = null
    private var prsmtrstat: String? = null
    private var lstmtrddt: String? = null
    private var lstmtrcnt: Int = 0
    private var revym: String? = null
    private var novat: String? = null
    private var avgwtuse: Int = 0
    private var discntcode: String? = null
    private var invoicecnt: String? = null
    private var invflag: String? = null
    private var debmonth: Int = 0
    private var debamt: Double = 0.toDouble()
    private var remwtusg: Int = 0
    private var noofhouse: Int = 0
    private var pwa_flag: String? = null
    private var meterest: Int = 0
    private var smcnt: Int = 0
    private var mincharge: String? = null
    private var lstwtusg: Int = 0
    private var subdiscn: Int = 0
    private var readflag: String? = null
    private var newread: String? = null
    private var prsmtrcnt: Int = 0
    private var nortrfwt: Double = 0.toDouble()
    private var discntamt: Double = 0.toDouble()
    private var srvfee: Double = 0.toDouble()
    private var vat: Double = 0.toDouble()
    private var tottrfwt: Double = 0.toDouble()
    private var comment: String? = null
    private var CommentDec: String? = null
    private var billflag: String? = null
    private var billsend: String? = null
    private var hln: String? = null
    private var prswtusg: Int = 0
    private var latitude: String? = null
    private var lontitude: String? = null
    private var prsmtrrddt: String? = null
    private var time: String? = null
    private var readcount: Int = 0
    private var printcount: Int = 0
    private var usgcalmthd: String? = null
    private var userid: String? = null
    private var chkdigit: String? = null
    private var controlmtr: String? = null
    private var bigmtrno: String? = null
    private var tbseq: Int = 0
    private val service_flag: String? = null
    private var regisno: String? = null
    private var custbrn: String? = null
    //    private double dupamt;

    //Constant
    //private String wwcode;
    private var wwnamet: String? = null
    private var wwtel: String? = null
    private var ba: String? = null
    private var mvamt1: Double = 0.toDouble()
    private var mvamt2: Double = 0.toDouble()
    private var mvamt3: Double = 0.toDouble()
    private var dispwapro: Double = 0.toDouble()

    //dbst51,52,53,53
    private var wttrfrt: Double = 0.toDouble()
    private var acwttrfamt: Double = 0.toDouble()
    private var lowusgran: Int = 0
    private var highusgran: Int = 0

    //dbst06
    private var discntmean: String? = null
    private var discnttype: String? = null
    private var discntsrvf: String? = null
    private var discntpcen: Double = 0.toDouble()
    private var discntbaht: Double = 0.toDouble()
    private var discntnumr: Int = 0
    private var discntdnom: Int = 0
    private var discntunit: Int = 0

    //msEmployee
    private var empid: String? = null
    private var empname: String? = null
    private var emplastname: String? = null
    private var empmoblie: String? = null
    private var empposition: String? = null


    fun getprecode(): String? {
        return precode
    }

    fun setprecode(precode: String) {
        this.precode = precode
    }

    fun getwwcode(): String? {
        return wwcode
    }

    fun setwwcode(wwcode: String) {
        this.wwcode = wwcode
    }

    fun getwwnamet(): String? {
        return wwnamet
    }

    fun setwwnamet(wwnamet: String) {
        this.wwnamet = wwnamet
    }


    fun getwwtel(): String? {
        return wwtel
    }

    fun setwwtel(wwtel: String) {
        this.wwtel = wwtel
    }

    fun getba(): String? {
        return ba
    }

    fun setba(ba: String) {
        this.ba = ba
    }

    fun getmvamt1(): Double {
        return mvamt1
    }

    fun setmvamt1(mvamt1: Double) {
        this.mvamt1 = mvamt1
    }

    fun getmvamt2(): Double {
        return mvamt2
    }

    fun setmvamt2(mvamt2: Double) {
        this.mvamt2 = mvamt2
    }

    fun getmvamt3(): Double {
        return mvamt3
    }

    fun setmvamt3(mvamt3: Double) {
        this.mvamt3 = mvamt3
    }

    fun getdispwapro(): Double {
        return dispwapro
    }

    fun setdispwapro(dispwapro: Double) {
        this.dispwapro = dispwapro
    }


    fun getmtrrdroute(): String? {
        return mtrrdroute
    }

    fun setmtrrdroute(mtrrdroute: String) {
        this.mtrrdroute = mtrrdroute
    }

    fun getmtrseq(): Int {
        return mtrseq
    }

    fun setmtrseq(mtrseq: Int) {
        this.mtrseq = mtrseq
    }

    fun getcustcode(): String? {
        return custcode
    }

    fun setcustcode(custcode: String) {
        this.custcode = custcode
    }

    fun getusertype(): String? {
        return usertype
    }

    fun setusertype(usertype: String) {
        this.usertype = usertype
    }

    fun getoldtype(): String? {
        return oldtype
    }

    fun setoldtype(oldtype: String) {
        this.oldtype = oldtype
    }

    fun getcuststat(): String? {
        return custstat
    }

    fun setcuststat(custstat: String) {
        this.custstat = custstat
    }

    fun getlocation(): String? {
        return location
    }

    fun setlocation(location: String) {
        this.location = location
    }

    fun getcustname(): String? {
        return custname
    }

    fun setcustname(custname: String) {
        this.custname = custname
    }

    fun getcustaddr(): String? {
        return custaddr
    }

    fun setcustaddr(custaddr: String) {
        this.custaddr = custaddr
    }

    fun getmtrmkcode(): String? {
        return mtrmkcode
    }

    fun setmtrmkcode(mtrmkcode: String) {
        this.mtrmkcode = mtrmkcode
    }

    fun getmetersize(): String? {
        return metersize
    }

    fun setmetersize(metersize: String) {
        this.metersize = metersize
    }

    fun getmeterno(): String? {
        return meterno
    }

    fun setmeterno(meterno: String) {
        this.meterno = meterno
    }

    fun getprsmtrstat(): String? {
        return prsmtrstat
    }

    fun setprsmtrstat(prsmtrstat: String) {
        this.prsmtrstat = prsmtrstat
    }

    fun getlstmtrddt(): String? {
        return lstmtrddt
    }

    fun setlstmtrddt(lstmtrddt: String) {
        this.lstmtrddt = lstmtrddt
    }

    fun getlstmtrcnt(): Int {
        return lstmtrcnt
    }

    fun setlstmtrcnt(lstmtrcnt: Int) {
        this.lstmtrcnt = lstmtrcnt
    }

    fun getrevym(): String? {
        return revym
    }

    fun setrevym(revym: String) {
        this.revym = revym
    }

    fun getnovat(): String? {
        return novat
    }

    fun setnovat(novat: String) {
        this.novat = novat
    }

    fun getavgwtuse(): Int {
        return avgwtuse
    }

    fun setavgwtuse(avgwtuse: Int) {
        this.avgwtuse = avgwtuse
    }

    fun getdiscntcode(): String? {
        return discntcode
    }

    fun setdiscntcode(discntcode: String) {
        this.discntcode = discntcode
    }

    fun getinvoicecnt(): String? {
        return invoicecnt
    }

    fun setinvoicecnt(invoicecnt: String) {
        this.invoicecnt = invoicecnt
    }

    fun getinvflag(): String? {
        return invflag
    }

    fun setinvflag(invflag: String) {
        this.invflag = invflag
    }

    fun getdebmonth(): Int {
        return debmonth
    }

    fun setdebmonth(debmonth: Int) {
        this.debmonth = debmonth
    }

    fun getdebamt(): Double {
        return debamt
    }

    fun setdebamt(debamt: Double) {
        this.debamt = debamt
    }

    fun getremwtusg(): Int {
        return remwtusg
    }

    fun setremwtusg(remwtusg: Int) {
        this.remwtusg = remwtusg
    }

    fun getnoofhouse(): Int {
        return noofhouse
    }

    fun setnoofhouse(noofhouse: Int) {
        this.noofhouse = noofhouse
    }

    fun getpwaflag(): String? {
        return pwa_flag
    }

    fun setpwaflag(pwa_flag: String) {
        this.pwa_flag = pwa_flag
    }

    fun getmeterest(): Int {
        return meterest
    }

    fun setmeterest(meterest: Int) {
        this.meterest = meterest
    }

    fun getsmcnt(): Int {
        return smcnt
    }

    fun setsmcnt(smcnt: Int) {
        this.smcnt = smcnt
    }

    fun getmincharge(): String? {
        return mincharge
    }

    fun setmincharge(mincharge: String) {
        this.mincharge = mincharge
    }

    fun getlstwtusg(): Int {
        return lstwtusg
    }

    fun setlstwtusg(lstwtusg: Int) {
        this.lstwtusg = lstwtusg
    }

    fun getsubdiscn(): Int {
        return subdiscn
    }

    fun setsubdiscn(subdiscn: Int) {
        this.subdiscn = subdiscn
    }

    fun getreadflag(): String? {
        return readflag
    }

    fun setreadflag(readflag: String) {
        this.readflag = readflag
    }

    fun getnewread(): String? {
        return newread
    }

    fun setnewread(newread: String) {
        this.newread = newread
    }

    fun getprsmtrcnt(): Int {
        return prsmtrcnt
    }

    fun setprsmtrcnt(prsmtrcnt: Int) {
        this.prsmtrcnt = prsmtrcnt
    }

    fun getnortrfwt(): Double {
        return nortrfwt
    }

    fun setnortrfwt(nortrfwt: Double) {
        this.nortrfwt = nortrfwt
    }

    fun getdiscntamt(): Double {
        return discntamt
    }

    fun setdiscntamt(discntamt: Double) {
        this.discntamt = discntamt
    }

    fun getsrvfee(): Double {
        return srvfee
    }

    fun setsrvfee(srvfee: Double) {
        this.srvfee = srvfee
    }

    fun getvat(): Double {
        return vat
    }

    fun setvat(vat: Double) {
        this.vat = vat
    }

    fun gettottrfwt(): Double {
        return tottrfwt
    }

    fun settottrfwt(tottrfwt: Double) {
        this.tottrfwt = tottrfwt
    }

    fun getcomment(): String? {
        return comment
    }

    fun setcomment(comment: String) {
        this.comment = comment
    }

    fun getcommentdec(): String? {
        return CommentDec
    }

    fun setcommentdec(commentdec: String) {
        this.CommentDec = commentdec
    }

    fun getbillflag(): String? {
        return billflag
    }

    fun setbillflag(billflag: String) {
        this.billflag = billflag
    }

    fun getbillsend(): String? {
        return billsend
    }

    fun setbillsend(billsend: String) {
        this.billsend = billsend
    }

    fun gethln(): String? {
        return hln
    }

    fun sethln(hln: String) {
        this.hln = hln
    }

    fun getprswtusg(): Int {
        return prswtusg
    }

    fun setprswtusg(prswtusg: Int) {
        this.prswtusg = prswtusg
    }

    fun getlatitude(): String? {
        return latitude
    }

    fun setlatitude(latitude: String) {
        this.latitude = latitude
    }

    fun getlontitude(): String? {
        return lontitude
    }

    fun setlontitude(lontitude: String) {
        this.lontitude = lontitude
    }

    fun getprsmtrrddt(): String? {
        return prsmtrrddt
    }

    fun setprsmtrrddt(prsmtrrddt: String) {
        this.prsmtrrddt = prsmtrrddt
    }

    fun gettime(): String? {
        return time
    }

    fun settime(time: String) {
        this.time = time
    }

    fun getreadcount(): Int {
        return readcount
    }

    fun setreadcount(readcount: Int) {
        this.readcount = readcount
    }

    fun getprintcount(): Int {
        return printcount
    }

    fun setprintcount(printcount: Int) {
        this.printcount = printcount
    }

    fun getusgcalmthd(): String? {
        return usgcalmthd
    }

    fun setusgcalmthd(usgcalmthd: String) {
        this.usgcalmthd = usgcalmthd
    }

    fun getuserid(): String? {
        return userid
    }

    fun setuserid(userid: String) {
        this.userid = userid
    }

    fun getchkdigit(): String? {
        return chkdigit
    }

    fun setchkdigit(chkdigit: String) {
        this.chkdigit = chkdigit
    }

    fun getcontrolmtr(): String? {
        return controlmtr
    }

    fun setcontrolmtr(controlmtr: String) {
        this.controlmtr = controlmtr
    }

    fun getbigmtrno(): String? {
        return bigmtrno
    }

    fun setbigmtrno(bigmtrno: String) {
        this.bigmtrno = bigmtrno
    }

    fun gettbseq(): Int {
        return tbseq
    }

    fun settbset(tbseq: Int) {
        this.tbseq = tbseq
    }

    fun getregisno(): String? {
        return regisno
    }

    fun setregisno(regisno: String) {
        this.regisno = regisno
    }

    fun getcustbrn(): String? {
        return custbrn
    }

    fun setcustbrn(custbrn: String) {
        this.custbrn = custbrn
    }

    fun getwttrfrt(): Double {
        return wttrfrt
    }

    fun setwttrfrt(wttrfrt: Double) {
        this.wttrfrt = wttrfrt
    }

    fun getacwttrfamt(): Double {
        return acwttrfamt
    }

    fun setacwttrfamt(acwttrfamt: Double) {
        this.acwttrfamt = acwttrfamt
    }

    fun getlowusgran(): Int {
        return lowusgran
    }

    fun setlowusgran(lowusgran: Int) {
        this.lowusgran = lowusgran
    }

    fun gethighusgran(): Int {
        return highusgran
    }

    fun sethighusgran(highusgran: Int) {
        this.highusgran = highusgran
    }

    fun getdiscntmean(): String? {
        return discntmean
    }

    fun setdiscntmean(discntmean: String) {
        this.discntmean = discntmean
    }

    fun getdiscnttype(): String? {
        return discnttype
    }

    fun setdiscnttype(discnttype: String) {
        this.discnttype = discnttype
    }

    fun getdiscntsrvf(): String? {
        return discntsrvf
    }

    fun setdiscntsrvf(discntsrvf: String) {
        this.discntsrvf = discntsrvf
    }

    fun getdiscntpcen(): Double {
        return discntpcen
    }

    fun setdiscntpcen(discntpcen: Double) {
        this.discntpcen = discntpcen
    }

    fun getdiscntbaht(): Double {
        return discntbaht
    }

    fun setdiscntbaht(discntbaht: Double) {
        this.discntbaht = discntbaht
    }

    fun getdiscntnumr(): Int {
        return discntnumr
    }

    fun setdiscntnumr(discntnumr: Int) {
        this.discntnumr = discntnumr
    }

    fun getdiscntdnom(): Int {
        return discntdnom
    }

    fun setdiscntdnom(discntdnom: Int) {
        this.discntdnom = discntdnom
    }

    fun getdiscntunit(): Int {
        return discntunit
    }

    fun setdiscntunit(discntunit: Int) {
        this.discntunit = discntunit
    }

    fun getempid(): String? {
        return empid
    }

    fun setempid(empid: String) {
        this.empid = empid
    }

    fun getempname(): String? {
        return empname
    }

    fun setempname(empname: String) {
        this.empname = empname
    }

    fun getemplastname(): String? {
        return emplastname
    }

    fun setemplastname(emplastname: String) {
        this.emplastname = emplastname
    }

    fun getempmoblie(): String? {
        return empmoblie
    }

    fun setempmoblie(empmoblie: String) {
        this.empmoblie = empmoblie
    }

    fun getempposition(): String? {
        return empposition
    }

    fun setempposition(empposition: String) {
        this.empposition = empposition
    }

    //
    override fun toString(): String {
        //return "เส้นทาง : " + mtrrdroute +","+ custcode +revym + custcode + custaddr +"\n";
        return "เส้นทาง : $wwcode $wwnamet $discntmean $wttrfrt $acwttrfamt $empid $empname $srvfee\n"
    }

}
