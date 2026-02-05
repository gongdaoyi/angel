package com.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.google.common.net.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatcherTest {

    private static final Logger log = LoggerFactory.getLogger(MatcherTest.class);

    private static final int ARRAY_LENGTH = 9;
    private static final int MIN_NUMBER = 1;
    private static final int MAX_NUMBER = 99;

    public static void main(String[] args) {
        String regex = "协议编号:(?<protocolNumber>[0-9]+).*?证券资金台账:(?<fundAccount>[0-9]+).*?丙方:(?<partyC>.*?银行)[^协议编号]";

        String str = "工商银行专用协议编号:7016900100000640广发证券股份有限公司第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金第三方存管业务三方协议客户交易结算资金第三方存管业务三方协议鉴于乙方和丙方实施客户交易结算资金银行存管方案,甲、乙、丙三方依据《中华人民共和国证券法》、《中华人民共和国商业银行法》、《中市机民共和国合同法》、《人民币银行结算账户管理办法》、《支付结算办法》、《支付结算业务代理办法》、《银行卡业务业务管理甘台办亿方客户不其他有关法律、法规、规章以及沪、深证券交易所交易规则、登记结算规则的规定,就乙方代理甲구보方证券券交物议为淮官乙方客户交易结算资金,及其他相关事宜达成如下协议,供三方共同道守,如甲乙双方原签协议与本协议不一致甲方声明和签字:的,以本协议为准.兹声明本人／本单位已仔细阅读井理解《广发证券股份有限公司客户交易结算资金第三方存管业务三方协议（工商银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.第一条甲方声明如下:第一章三方声明相应合法的证券投资资格,不存在法律、法规、规章和证券交易所交易规则禁止或限制其投资证券市场的情形.东日1甲方姓名／名称:玉印和约定的要求,及时通022（三）甲方保证其资金来来源合法.证件类型:身俗证证件号码:130302196303160c011（四）乙方已向甲方清楚揭示证券市场投资风险,甲方清楚认识并愿意承担证券市场投资风险.-eli-国（五）甲方同意遵守证券市场有关的法律、法规及证券交易所交易规则.六口4mi闻-1-e法定代表人／授权人姓名和证件号码:（六）羊细阅读本协议所有条款,准确理解其含义,特别是其中有关乙方、丙方的责任条款,并同意本协议所有条款.第二条乙方声明如下:代理人姓名和证件号码:（一）乙方是依法设立的证券经营机构,具有相应的证券经纪业务资格,并经证券监督管理机关核准具有开展网上委托业务的资格（二）乙方具有开展证券经纪业务的必要条件,能够为甲方的证券交易提供相应服务.证券资金台账:37242649（三）乙方遵守证券市市场有关的法律、法规及证券交易和登记结算规则.第三条丙方声明如下:银行结算账号:6222080404001453803海安里24-2-4（二）丙方是体法设立的银行金诊机控有省应交易结路金有管交易体芛贡金辊供相关的服务.通讯地址:河北省秦岛市海治区光理男邮政编码:066000（三）丙方在严格遵循国家有关法律、法规以及规章的前提下,办理甲方的客户交易结算资金存管业务.第二章客户证券资金台账、客户交易结算资金管理账户、客户银行结算账户联系人／联系电话:1372546082EMAIL:第四条客户证券资金台账指甲方在乙方开立的专门用于证券交易用途的账户,与甲方在丙方开立的银行结算账户之间建立银证转账对应关系.乙方通过该账户对甲方的证券买卖交易进行前端控制,进行清算交收和计付利息等.乙方对投资者托管在证券资金台账中的客户交易结算资金安全负责,甲方在办理客户交易结算资金银行存管前,须在乙方开立证券资金台账.签名（公章）生印和（法定代表人或代理人）:第五条客户交易结算资金管理账户指丙方为方在业务管理系统中设立的与其在乙方证券资金台账一-对应的管理账户,用于记录甲方在证券资金台账中的客户交易结算资金的变动明细.该管理账户不构成丙方对甲方的负债义务.丙方为甲方提供查询和对账服务.第六条银行结算账户指甲方在丙方开立的,用于银行资金往来结算的存款账户.乙方不再直接办理甲方的客户交易结算资金存取服务.甲方存入客户交易结算资金,应先将资金应先将资金存入其在丙方的银行结算账户,再通过银证转账交易将资金转到其在乙方的证券资日期:2024年5月6日．中交易结算资金,只能通过银证转账交易转回到其在丙方的银行结算账户.券资金台账时,应提供合法有效的证件、同名证券账户卡,并按乙方要求签署《证券交易协议金第七条甲方在向乙方申请开设证券sDee书》；甲方原资金账户改称保员金燃广以称为n热血热,白燃编明综贤金账亏编码相同,中万如果为乙方的新客户则直接新开立证券乙方:广发证券股份有限公司丙方:中国工商银行股份有限公司资金台账.银行博軍方を子方號丙有海道国造办理家户齐易信部资金销行在作应银供本人合法的有效证件、深沪两市的限东卡.客服电话:95575网址:www.gf.com.cn电话银行:95588网上银行:www.icbc.com.cn资金管理账户之间的对应关系,甲方在正常的交S岡江票次AA业E印东东店亚市的空白六豆±宿资수户4工田账点う同5コ六A4日-药ncn1-同计2注M十月出开-e签章:签章:户校验银行预留印金金台账的资金密码,如果密码遗忘,本人持有效身份证（授权代理的还须提供代理人身份证件、易时间内可以通过乙方柜台修改证券资李Υ电t7年奸FТ书尹流4153-4ne占上理密码重置.儿恫合九2xTT生八月双河时证明,住七万记台刃授权委托书）、证券账户省市证第九条甲方办理深市证券转托管、沪市证券撤销指定须校验在乙方设置的证券交易密码或证券资金密码.第三章证券交易代理第三方存管开第十条甲方可以通过在乙方已申请开通的柜面委托、自助委托、电话委托、网上交易委托等方式下达证券交易委托.LH服务:第十一条乙方为甲方提的委托委托；代理甲方进行证券和资金的清算与交收；代理保管甲方买入或存入的有价证券；代理甲方领取业务专用章接受并忠实执行甲方下达的et深合公影和打高济立陈化樓的杰询共应用市的源龙提供胡质相应的交割单、对账单；担任红利股息；接受甲方对其委托、成交及证乙双方依法约定的其他事项；证券监管部门规定提供的其他服务.м工甲方的客户交易结算资金出交易密话委托系统或网上交易系统办理四学医托生中7学2:-TaшED经办人（签章）办人（签章）:交易委托时,必须提供本人身份证、证券账户卡,代理人委托还需出示代理人身份证、授权齐工-三十種基11辆生一2024.5.代理卡或第十四条当甲方委托未成交或未全部成交时,甲方可以变更其未成交的委托.系统进行柜台委托、自助委托、电话委托、网上交易,必须输入正确的交易密码.出ceYLU务专用章日期:日期:经办机构签章:广发证券GFSECURITIES第十五条甲方应在委托下达后三个交易日内向乙方查询该委托结果,当甲方对该结果有异议时,须在查询当日以书面形式向乙第八章甲、乙、丙三方的责任条款第三十九条乙方因未有效展行甲方证券投责瓷产的管理主体、甲方证券交易代理主体和清算交收义务,所导致的甲方证券牧資方质询.甲方逾期未办理查询或未对有异议的查询结角认该结果.0G用acdtmtoiTwmlY0第十六条甲方选择乙方作为其在上海证券交易所挂牌交易证券甲方在指定交易期间的证品方需为甲方申报上海证券交易所指定读产损失或甲方证券文易无法正業进行的,由乙方承担购偿责任.交易,在指定交易生效后,其证券账户内的证券即同均通过乙方代理.银行职责的情況下,仍然委托丙方存管甲方的客户交易结算资金,导致甲方客户交易结算资金出现存管风险的,乙方应承担相应的長刘光日仕co、enУ证孙宏关力加司有4第十七条甲方如需要撤销在乙方处的指定交易,须本人在正常交易时间内亲自到场,及代理授权文件中明确委托他人办理撤销urTNen账占上店사0T22em指定交易的,可由授权人向乙方提出书面申请代办,并提理委托书.任.第四十一条如因乙方证券投资资产托管问顯或证券资金台账差错导致甲方证券交易无法正常进行或客户交易结算资金无法正常E3LL0stettx．5cT册1A医07LXIxI4erxtm山＋7口供的甘地六十h通上T乙方应在甲方申请的当日为其办理撤销指定交易申请.甲方也可通过乙方提供的其他方式苏理上海证券交易所撤销指定交易.第十八条当甲方证券资金台账出现买卖异常时,乙方有义务子以关注并及时向证券监管部门报告.转账支取的,由乙方根据有关规定承担相应任第四十二条丙方因未履行总分核对职责,未履行存管银行的其他存管职责和义务,导致甲方客户交易结算资金风险的,丙方应第四章资金存取承担相应的赔偿责任.第十九条甲方通过自动柜员机或丙方柜台办理银行结算账户资金存取业务,通过丙方提供的电话银行、网上银行、柜面服务、客户交易结算资金,丙方因不履行存管义务,接受任何形式的客户交易结算资金担係,以及监篬韵就是的其艳音实信止尊项,超出第四十三条芮方需按獺国家法律法规、推高人民法院的司法解释或有关通知以及与乙方管霸的机实协议投路保管甲方的多媒体自助终端等方式（具体服务渠道由丙方安排）办理客户交易结算资金转账业务,但应按照丙方的要求在约手续石大后用流e户性rш4F号（丙方不要求的除外）.甲方（非机构客户）也可以通过乙方结算资金的转相关协议规定的乘造成的差错事故,乙方根据丙方对账数据对客户证券资金台账进行调账丙方鬻予以必要約配合,因是供的电话会托、网工Xлe托力式小理广交易对田Hcrc2EIXRY250nena成千ク华P团47数-←-7マ＊＊1日口账的第四十四条因甲方银证转账造账业务；甲方（机构客户）发起证券交易结算资金证券转银行前需向乙方预深合的玛印和划站用交亚的取正甲方证券交易造成的差错事故,以乙方证券交易数据为准,由乙第四十五条乙方未忠实执行甲方下达的交易委托指令,导致甲方损失的,由乙方承担赔偿责任,乙方如遇到不可抗力的情况除由乙方负责查明原因井调账.1HC加21方式转入其在丙方开立的同名银行结算账户.甲方再通过银行结算账户办理及时办亚的提取和划转.甲方依法享有资金存取自由.甲方办理资金存取业务时,除非甲乙双方另有约定有指令,丙方必须及时办理.第二十条甲方（机构客户）通过丙方柜台办理客户交易结算资金转出时,应填写相应的凭证,加盖在丙方已签约的银行结算账外.户的辆留印务,甲方（机的客户）通过两方的同上银行办理其密户交易结昇金客户交易结算资金转出时,应遵循丙方的网上银行认证规则.甲方（机第四十六条乙、丙双方均应向甲方提供客户交易结算资金的查询对账功能.渠道办理证券公司客户交易结算资金第三方存管业务,应通守丙方电子银行章第四十七条甲方通过丙方网上银中力11组迎内万MLkTs电点银第二十一条若甲方为机构客户,则甲方不能与任何自然人储蓄账户建立客户交易结算资金银证转账对应关系,只能易结算资金银证转账对应关系,只能与甲方在丙程、相关协议,并按照丙方电子银行业务相关交易规则进行办理.方开立的银行结算账户之间办理签约转账.签约银行结算账户的预留印鉴发生变更,其对应的客户交易结算资金管理账户预留印鉴视第四十八条乙方、丙方郑重提醒甲方注意用户号（会员号、注册卡号／登录1D等）和密码的保密,乙方、丙方对使用用户号及同变更.密码所进行的证券交易委托或资金划转操作均视为甲方本人所为.由于甲方未尽到防范风险的义务造成用户号及密码失密或其他非乙方、丙方原因而导致的甲方损失,乙方、丙方不承担责任.第五章清算和交收第四十九条乙方、丙方对甲方的开户资料、委托事项、交易记录等资科负有保密义务,非经法定有权机关或甲方指示,不得向第二十二条甲方场内、场外交易后的清算和交收均由乙方负责.在每个证券交易营业日,乙方根据从证券交易所、登记结算公第三众五平家有西國其通身標信中,資方造理的卡及个人银行借记卡／铝行存拆或租柃客户模行有事户正司收到的交易清算数据,完成与甲方的清算交收,并将甲方当日的交易轧差数和证券资金台账余额发送给丙方.丙方同步调整甲方客户交易结算资金管理账户的明细记录和余额.明文件.第二十三条若甲方对其证券交易资金清算明细或余额有异议,应向乙方查询井核实.若乙方认为有必要,可以要求丙方配合进第五十一条若甲方遗失证券账户卡、身份证,应及时向乙方办理挂失,在挂失生效前已经发生的证券账户损失由甲方承担,甲方遗失个行查询、核实工作.人银行借记卡／银行存折,应及时向丙方办理挂失,在挂失生效前已发生的转账业务所产生的损失及其他损失由甲方承担.第五十二条因地震、台风、水灾、火灾、战争及其他不可抗力因素导致的甲方损失,乙方方、丙方均不承担任何赔偿责任.第六章变更和撤销第五十三条因乙方、丙万不可预测或无法控制的系统故障、设备故障、通讯故障、停电等突发事故,给甲方造成的损失,乙方、第二十四条甲方需要变更银行结算账户户名、证件号码、地址、联系电话等,可到丙方柜台申请办理.甲方在符合相关法律法丙方不承担任何赔偿责任,规及丙定的情况下成功变更户名或证件号码的,还应及时前往乙方柜台办理变更证券资金台账信息手续,在未成功变更第五十四条因互联网上传输原因,交易和转账指令出现中断、停顿、延迟、数据错误等情况以及乙方或丙方不可原测或不可控证券资金台账信息之前,甲方不能办理客户交易结算资金的存取.制因素而造成甲方不能正常交易和转账,乙方、丙方不承担责任,但乙方、丙方保证尽快采取其他方式使交易正常运行,重要得二十五务当東方需要北更证券资金合户名的证件号码地址联系电语证券账户代理人代理人权用代理期限第五十五条甲方办理网上交易前,应开通柜面委托、电话委托、自助委托等其他委托方式,当网络中断、高峰掘挤或网上交易被冻结时,甲方可使用上述委托手段码的,应前往丙方柜台根据相关法律法规及丙方的有关业务规定,申请办理变更手续；在未成功变更银行结算账户信息之前,甲方不第五十六条甲方为进行网上交易所使用的软件必须是乙方提供的或乙方指定站点下载的.甲方使用其他途径获得的软件,由此能办结算资金的存取.产生的后果由甲方自行承担.第二十六条甲方撤销指定交易、办理转托管,需在乙方办理井另行签署有关文件.第五十七条当第五十二条、第五十三条、第五十四条所述事件发生后,乙方、丙方应当及时采取措施防止甲方损失可能的进一第二十七条甲方办理变更存管银行时,需先通过银证转账方式将其在乙方的证券资金台账余额清零.甲方证券资金台账余额清步扩大.零后直至重新成功指定存管银行之前不能再发生证券买卖交易.甲方在转账成功后第二个工作日,到乙方办理撤销现有存管银行手续,第五十八条乙方应本着勤勉尽责的精抻忠实地向甲方提供信息、资料.乙方向甲方提供的各种信息及资料,仅作为投资参考,然后再办理新存管银行的指定手续.甲方应自行承担据此进行投资所产生的风险.第二十八条甲方通过丙方营业方通过丙方营业网点办理变更银证转账对应银行结算账户时,乙、丙双方系统通知对方同步变更相应的签约信息.第五十九条甲、乙、丙三方各自依照相关法律与本协议承担违约责任,任何一方不对其他方的行为承担责任.方运第二十九条甲方撤销其在乙方的证券资金台账时,需先到乙方办理销户手续,乙方确认甲方符合销户条件后,为甲方结息,甲第九章争议的解决＊7-0797--9＊や모후금☑希月ト阳7718J卖,乙L4力民サ共证ナ穴立A瓜示职27仕9万ロ門五01门ロン＊．开住行-1LTF日,生JKHTa11次:方校验甲方身份和资金密码通过后,为甲方办理存管银行撤销手续,乙方系统将甲方申请信息发送丙方,丙方系统校验通过后,变更第六十条因本协议引起的或与本协议有关的任何争议,由协议签订各方协商解决；协商不成的,可以提请中国证券业协会调解；甲方客户交易结算资金管理账户为销户,取消甲方的银证转账服务和客户交易结算资金管理账户对账单服务.调解不成的,协议签订各方一致同意将争议提交有管辖权的人民法院解决.第三十条除非甲方有未履行交易交收义务等违约情形,甲方可随时撤销其在乙方的证券资金台账和在丙方的客户交易结算资金第十章附则管理账户.第三十一条有下列情形之一的,乙方或丙方可要求甲方限期纠正,甲方不能按期纠正或拒不纠正的,经乙方和丙方双方认可后,第六十一条乙方按照有关法律法规及证券交易所的交易规则的规定向甲方收取佣金,代扣代缴甲方有关税费,可终止本协议:第六十二条甲方的交易委托凭证和资金存取凭证是指其在乙方和丙方柜台委托所填写的单据、非柜台委托所形成的乙方和丙方①乙方或丙方发现甲方向其提供的资料、证件严重失实；②甲方有严重损害乙方或丙方合法权益、影响其正常经营秩序的行为；电脑记录资料.③甲方的资金来源不合法；④监管部门认定的其他情形.第六十三条乙方和丙方必须根据法律法规规定的方式和期限保存甲方的交易委托凭证和资金存取凭证等资料.第三十二条乙方和丙方终止本协议,需通知甲方,并说明理由.第六十四条本协议签署后,若有关法律法规、规章制度修订,相关内容及条款按新修订的法律法规、规章制度及行业规章办理.第三十三条甲方在收到终止本协议通知后,应到乙方办理撤销证券资金台账手续.本协议终止,不影响乙方与丙方依法追究甲但本协议其他内容及条款继续有效.方违约责任的权利.第六十五条本协议根据法律法规规章和证券交易所、登记结算公司、中国证监会等的规定如霈修改或增补,修改或增补的内容指令.第三十四条在甲方收到乙方或丙方终止本协议通知至甲方销户手续办理完毕期间,乙方不接受甲方的买入委托指令或资金转账将由乙第六十六条本协议所指的通知方式除上述条款中已有约定外,式通知用を以者电方在士日内不構进巡公告内瓷切成为东协议垣成部分公当在指定报料械HZVA12t046C乙方、丙方经营场所发布之日起生效.第七章委托代理第六十七条本协议有效期自三方签署至第三十条、第三十一杀皈指情形丁杀所指情形发生第三十五条甲方在乙方开设证券资金台账后,可以授权代理人代为办理证券交易委托及相关事项.第査す八管大的らみが的的,管抗有人的な以开条件后生效第三十六条甲方授权他人代为办理前款所述事项时,应当甲方授权他人代为办理前款所述事项时,应当签署有关授权委托书.授权委托书至少应载明下列内容:代理人姓名s＊＊99一日8华璇구Ζ江白及合法有效的证件号码、授权权限、授权期限乙方安冰明尔的其他事项.2．乙方加盖业务专用章,丙方加盖证券代理业务协议专用章服务用章:第三十七条甲方授权委托书的签署地应当在乙方的开户网点所在地,但经国家公证机关公证或我国驻外使领馆认证的授权委托3．乙方经办机构或丙方经办机构的经办人加盖人名章或签字书除外.甲方签署的授权委托书原件应备案.4甲方（投资者）须持本协议至丙方（存管银行）申请办理运资金第三方存管（多银行模式）业务,井上成2甘东ン65I你伽旺南当rd号Kcncr1s第三十八条甲方在授权委托书有效期内变更授权事项或终止授权时,应当及时书面通知乙方,并到其开户的乙方营业网点办理有关取得经丙方确认的业务申请书客户留存联,本协议方能生效.手续.乙方在收到甲方书面通知前,仍执行原授权委托书.";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            int num = 0;
            while (matcher.find(num)) {
                System.out.println("协议编号：" + matcher.group("protocolNumber") + " 资金账号：" + matcher.group("fundAccount") + " 丙方：" + matcher.group("partyC"));
                num = matcher.end();
            }
        }
    }

//    public static void main(String[] args) {
//        String regex = "协议编号:(?<protocolNumber>[^,]*?)(第一联|广发证券股份有限公司).*?证券资金台账:(?<fundAccount>[^,]*?)银行结算账号.*?丙方:(?<partyC>[^,]*?)(客服电话|网址).*?广发证券GFSECURITIES";
//        String str = "协议编号:7010900700001713第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金银行存管三方协议书甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金银行存管三方协议书（广发银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份证件号码:440921198801215(32法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:719209银行结算账号:6225680922000183783通讯地址:邮政编码:526040广东省市编州区蓝塘四路联系人／联系电话8号骤江图EMAIL13922294308월101）跳6签名（公章）:优日期:2024.5.7乙方:广发证券股份有限公司丙方:广发银行股份有限公司客服电话:95575网址:www.gf.com.cn电话银行:95508网上银行:www.cgbchina.com.cn签章:签章:务专用章第三方存管业专用章三方存管开业务专用章반wee经办人（签章）:经办人（签章）:日期:20245.7日期:经办机构签章:广发证券GFSECURITIES协议编号:7010900100002581第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金第三方存管业务三方协议甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金第三方存管业务三方协议（工商银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份讥证件号码:440921198801215132法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:7192092银行结算账号:6212263602027551571通讯地址:未省童市流州巴蓝塘回路路邮政编码:526040联系人／联系电话3922294308深江国加EMAIL:训湾9栋1101房签名（公章）:（法定代表人或代理人）:一九穷日期:2024.5.7乙方:广发证券股份有限公司丙方:中国工商银行股份有限公司客服电话:95575网址:www.gf.com.cn网上银行:www.icbc.com.cn汉务电增银行:95608签章:签章:三方存管开业务专用章经办人（签章）经办人（签章）:m你日期:2024.5、日期:经办机构签章:广发证券GFSECURITIES";
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(str);
//        if (matcher.find()) {
//            int num = 0;
//            while (matcher.find(num)) {
//                System.out.println("已匹配：" + matcher.group("protocolNumber"));
//                num = matcher.end();
//            }
//        }
//    }

    // 删除当前文件夹，以及里面所有文件
    public static void deleteDir(String path) {
        File file = new File(path);
        File[] list = file.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                deleteDir(f.getPath());
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    // 分段匹配示例3
    public static void split3Matcher() {
        String regex = "协议编号:(?<protocolNumber>[^,]*?)(第一联|广发证券股份有限公司).*?证券资金台账:(?<fundAccount>[^,]*?)银行结算账号.*?丙方:(?<partyC>[^,]*?)(客服电话|网址).*?广发证券GFSECURITIES";
        String str = "协议编号:7010900700001713第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金银行存管三方协议书甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金银行存管三方协议书（广发银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份证件号码:440921198801215(32法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:719209银行结算账号:6225680922000183783通讯地址:邮政编码:526040广东省市编州区蓝塘四路联系人／联系电话8号骤江图EMAIL13922294308월101）跳6签名（公章）:优日期:2024.5.7乙方:广发证券股份有限公司丙方:广发银行股份有限公司客服电话:95575网址:www.gf.com.cn电话银行:95508网上银行:www.cgbchina.com.cn签章:签章:务专用章第三方存管业专用章三方存管开业务专用章반wee经办人（签章）:经办人（签章）:日期:20245.7日期:经办机构签章:广发证券GFSECURITIES协议编号:7010900100002581第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金第三方存管业务三方协议甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金第三方存管业务三方协议（工商银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份讥证件号码:440921198801215132法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:7192092银行结算账号:6212263602027551571通讯地址:未省童市流州巴蓝塘回路路邮政编码:526040联系人／联系电话3922294308深江国加EMAIL:训湾9栋1101房签名（公章）:（法定代表人或代理人）:一九穷日期:2024.5.7乙方:广发证券股份有限公司丙方:中国工商银行股份有限公司客服电话:95575网址:www.gf.com.cn网上银行:www.icbc.com.cn汉务电增银行:95608签章:签章:三方存管开业务专用章经办人（签章）经办人（签章）:m你日期:2024.5、日期:经办机构签章:广发证券GFSECURITIES";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            System.out.println("成功匹配");
            int num = 0;
            while (matcher.find(num)) {
                System.out.println("已匹配：" + matcher.group("protocolNumber"));
                num = matcher.end();
            }
        }
    }

    // 分段匹配示例2
    public static void split2Matcher() {
        String regex = "协议编号:(?<data>.*?)广发证券GFSECURITIES";
        String str = "协议编号:7010900700001713第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金银行存管三方协议书甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金银行存管三方协议书（广发银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份证件号码:440921198801215(32法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:719209银行结算账号:6225680922000183783通讯地址:邮政编码:526040广东省市编州区蓝塘四路联系人／联系电话8号骤江图EMAIL13922294308월101）跳6签名（公章）:优日期:2024.5.7乙方:广发证券股份有限公司丙方:广发银行股份有限公司客服电话:95575网址:www.gf.com.cn电话银行:95508网上银行:www.cgbchina.com.cn签章:签章:务专用章第三方存管业专用章三方存管开业务专用章반wee经办人（签章）:经办人（签章）:日期:20245.7日期:经办机构签章:广发证券GFSECURITIES协议编号:7010900100002581第一联（白）:乙方（券商）存第二联（红）:甲方（客户）存第三联（蓝）:丙方（银行）存广发证券股份有限公司客户交易结算资金第三方存管业务三方协议甲方声明和签字:兹声明本人／本单位已仔细阅读并理解《广发证券股份有限公司客户交易结算资金第三方存管业务三方协议（工商银行专用）》,完全同意和接受该协议书的全部条款和内容,愿意履行和承担该协议书中约定的权利和义务.甲方姓名／名称:陈金证件类型:身份讥证件号码:440921198801215132法定代表人／授权人姓名和证件号码:代理人姓名和证件号码:证券资金台账:7192092银行结算账号:6212263602027551571通讯地址:未省童市流州巴蓝塘回路路邮政编码:526040联系人／联系电话3922294308深江国加EMAIL:训湾9栋1101房签名（公章）:（法定代表人或代理人）:一九穷日期:2024.5.7乙方:广发证券股份有限公司丙方:中国工商银行股份有限公司客服电话:95575网址:www.gf.com.cn网上银行:www.icbc.com.cn汉务电增银行:95608签章:签章:三方存管开业务专用章经办人（签章）经办人（签章）:m你日期:2024.5、日期:经办机构签章:广发证券GFSECURITIES";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            int num = 0;
            while (matcher.find(num)) {
                System.out.println("已匹配：" + matcher.group("data"));
                num = matcher.end();
            }
        }
    }

    // 分段匹配示例1
    public static void splitMatcher() {
        String regex = "A(?<fundAccount>[^,]*?)B";
        String str = "A奔驰BA宝马BA奥迪B";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            int num = 0;
            while (matcher.find(num)) {
                System.out.println(matcher.group("fundAccount"));
                num = matcher.end();
            }
        }
    }


    // 全匹配
    public static void allMatcher() {
        String regex = "(?<fundAccount>.*)";
        String beTestString = "丙方:中国农业银行股份有限公司网址";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(beTestString);
        if (matcher.find()) {
            System.out.println(matcher.group("fundAccount"));
        }
    }

    public static String replaceChar(String str, int index, char newChar) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(index, newChar);

        return sb.toString();
    }

    /**
     * 字体转换(繁体-简体)
     */
    public static void fontChanger() {
        String original = "生命不息，奮鬥不止";
        String result = ZhConverterUtil.convertToSimple(original);
        System.out.println("繁体转简体：" + result);

        String original2 = "生命不息，奋斗不止";
        String result2 = ZhConverterUtil.convertToTraditional(original2);
        System.out.println("简体转繁体：" + result2);
    }

    public static String generateRandomArray() {
        // 初始化数组
        List<String> array = new ArrayList<>();

        // 随机生成9个数字
        Random random = new Random();
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            int num = random.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER;
            array.add(String.format("%02d", num));
        }

        // 对数组进行排序
        Collections.sort(array);

        // 标记大小
        int smallCount = 0;
        int largeCount = 0;
        for (String num : array) {
            if (Integer.parseInt(num) > 50) {
                largeCount += 1;
            } else {
                smallCount += 1;
            }
        }

        // 找出相差最小的两个数字
        int minDiff = Integer.MAX_VALUE;
        String[] minDiffNums = new String[2];
        for (int i = 0; i < array.size() - 1; i++) {
            int diff = Math.abs(Integer.parseInt(array.get(i)) - Integer.parseInt(array.get(i + 1)));
            if (diff < minDiff) {
                minDiff = diff;
                minDiffNums[0] = array.get(i);
                minDiffNums[1] = array.get(i + 1);
            }
        }

        // 输出结果
        String output = "随机数组: " + String.join(" ", array) +
                " (小:大=" + smallCount + ":" + largeCount +
                ") (相差最小:" + minDiffNums[0] + "和" + minDiffNums[1] + ")";

        return output;
    }

    /**
     * 90天之前的日期
     */
    public static void date() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, 60);
        String endDate = new SimpleDateFormat("yyyy/MM/dd").format(now.getTime());
        System.out.println(endDate);
    }

    /**
     * 获取当前月第一天
     */
    public static String getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 获取某月最小天数
        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最小天数
        calendar.set(Calendar.DAY_OF_MONTH, firstDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        return sdf.format(calendar.getTime());
    }

    /**
     * 获取当前月最后一天
     */
    public static String getLastDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        // 获取某月最大天数
        int lastDay;
        //2月的平年瑞年天数
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month == 2) {
            lastDay = calendar.getLeastMaximum(Calendar.DAY_OF_MONTH);
        } else {
            lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        // 设置日历中月份的最大天数
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        return sdf.format(calendar.getTime());
    }

    /**
     * @param src 目标串
     * @param len 处理后长度
     * @param ch  填充字符
     * @return 处理后串
     */
    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    private static JSONArray testJSON() {

        JSONObject three = new JSONObject();
        three.put("data", "20210102");
        three.put("time", 91050);

        JSONObject four = new JSONObject();
        four.put("data", "20210102");
        four.put("time", 101050);

        JSONArray ary = new JSONArray();
        ary.add(three);
        ary.add(four);


        // 对jsonarray排序
        ary.sort(Comparator.comparing(obj -> ((JSONObject) obj).getInteger("time")));

        return ary;
    }

    /**
     * 检查幂等性
     */
    private static void checkResult() {
        JSONObject one = new JSONObject();
        one.put("111", "aaaa");
        one.put("aaa", "bbbb");

        JSONObject two = new JSONObject();
        two.put("aaa", "bbbb");
        two.put("111", "aaaa");

        JSONObject oneJson = (JSONObject) JSON.parse(JSON.toJSONString(one));
        JSONObject twoJson = (JSONObject) JSON.parse(JSON.toJSONString(two));

        System.out.println(oneJson.toString());
        System.out.println(twoJson.toString());

        if (!oneJson.equals(twoJson)) {
            System.out.println("查询结果不一致");
            return;
        }

        System.out.println("一致");
    }

    private static void checkContains(String str) {
        try {
            String[] split = str.split("/");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NullPointerException("空指针异常");
        } finally {
            System.out.println("str:" + str);
        }
    }

    /**
     * 截取倒数第三个/之后的字符
     * PS：index + 1之后不含斜杠
     */
    private static String parse(String shirley) {
        int index = shirley.lastIndexOf("/");
        index = shirley.lastIndexOf("/", index - 1);
        index = shirley.lastIndexOf("/", index - 1);

        return shirley.substring(index + 1);
    }

    /**
     * JSON转换
     *
     * @param subDataObj {"时间":"2021-08-12 08:50:39","操作":"完成签署广发证券专业投资者风险告知确认书（A4）","客户":"向季歼"}
     * @return {时间:'2021-08-12 08:50:39',操作:'完成签署广发证券专业投资者风险告知确认书（A4）',客户:'向季歼'}
     */
    private static String handleSubData(JSONObject subDataObj) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        List<String> lis = new ArrayList<>();
        for (Map.Entry<String, Object> entry : subDataObj.entrySet()) {
            String property = entry.getKey() + ":'" + entry.getValue() + "'";
            lis.add(property);
        }
        String data = lis.stream().collect(Collectors.joining(","));
        sb.append(data);
        sb.append("}");

        return sb.toString();
    }

    //path为本地文件路劲
    public void play(String path, HttpServletRequest request, HttpServletResponse response) {

        RandomAccessFile targetFile = null;
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            response.reset();
            //获取请求头中Range的值
            String rangeString = request.getHeader(HttpHeaders.RANGE);

            //打开文件
            File file = new File(path);
            if (file.exists()) {
                //使用RandomAccessFile读取文件
                targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();
                long requestSize = (int) fileLength;
                //分段下载视频
                if (StringUtils.hasText(rangeString)) {
                    //从Range中提取需要获取数据的开始和结束位置
                    long requestStart = 0, requestEnd = 0;
                    String[] ranges = rangeString.split("=");
                    if (ranges.length > 1) {
                        String[] rangeDatas = ranges[1].split("-");
                        requestStart = Integer.parseInt(rangeDatas[0]);
                        if (rangeDatas.length > 1) {
                            requestEnd = Integer.parseInt(rangeDatas[1]);
                        }
                    }
                    if (requestEnd != 0 && requestEnd > requestStart) {
                        requestSize = requestEnd - requestStart + 1;
                    }
                    //根据协议设置请求头
                    response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
                    response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
                    if (!StringUtils.hasText(rangeString)) {
                        response.setHeader(HttpHeaders.CONTENT_LENGTH, fileLength + "");
                    } else {
                        long length;
                        if (requestEnd > 0) {
                            length = requestEnd - requestStart + 1;
                            response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + length);
                            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + requestStart + "-" + requestEnd + "/" + fileLength);
                        } else {
                            length = fileLength - requestStart;
                            response.setHeader(HttpHeaders.CONTENT_LENGTH, "" + length);
                            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + requestStart + "-" + (fileLength - 1) + "/"
                                    + fileLength);
                        }
                    }
                    //断点传输下载视频返回206
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    //设置targetFile，从自定义位置开始读取数据
                    targetFile.seek(requestStart);
                } else {
                    //如果Range为空则下载整个视频
                    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.mp4");
                    //设置文件长度
                    response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength));
                }

                //从磁盘读取数据流返回
                byte[] cache = new byte[4096];
                try {
                    while (requestSize > 0) {
                        int len = targetFile.read(cache);
                        if (requestSize < cache.length) {
                            outputStream.write(cache, 0, (int) requestSize);
                        } else {
                            outputStream.write(cache, 0, len);
                            if (len < cache.length) {
                                break;
                            }
                        }
                        requestSize -= cache.length;
                    }
                } catch (IOException e) {
                    // tomcat原话。写操作IO异常几乎总是由于客户端主动关闭连接导致，所以直接吃掉异常打日志
                    //比如使用video播放视频时经常会发送Range为0- 的范围只是为了获取视频大小，之后就中断连接了
                    log.info(e.getMessage());
                }
            } else {
                throw new RuntimeException("文件路劲有误");
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("文件传输错误", e);
            throw new RuntimeException("文件传输错误");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("流释放错误", e);
                }
            }
            if (targetFile != null) {
                try {
                    targetFile.close();
                } catch (IOException e) {
                    log.error("文件流释放错误", e);
                }
            }
        }
    }
}
