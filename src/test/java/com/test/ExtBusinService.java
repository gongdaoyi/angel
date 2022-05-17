///**
// * @author fanlei
// *
// * 2017年11月7日下午3:48:55
// * nbop-busin-counter-service
// */
//package com.test;
//
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.Resource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.ByteArrayHttpMessageConverter;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.gf.nbop.busin.common.db.cache.MemCache;
//import com.gf.nbop.busin.common.db.cache.caches.CacheGfDictionary;
//import com.gf.nbop.busin.common.db.cache.caches.CacheGfFieldtoname;
//import com.gf.nbop.busin.common.db.mysql.Mysqltools;
//import com.gf.nbop.busin.counter.dao.AppropriateDao;
//import com.gf.nbop.busin.counter.dao.BatchtaskManagerDao;
//import com.gf.nbop.busin.counter.dao.BusinAcptformDao;
//import com.gf.nbop.busin.counter.model.Acptform;
//import com.gf.nbop.busin.counter.model.BusinAcptformArg;
//import com.gf.nbop.busin.counter.model.BusinAcptformArgRequest;
//import com.gf.nbop.busin.counter.model.Client;
//import com.gf.nbop.busin.counter.model.ClientInfo;
//import com.gf.nbop.busin.counter.model.ClientPrefer;
//import com.gf.nbop.busin.counter.model.DataPageResponse;
//import com.gf.nbop.busin.counter.model.OrganInfo;
//import com.gf.nbop.busin.counter.model.TytWhiteList;
//import com.gf.nbop.busin.counter.model.form.AgreementsFormNew;
//import com.gf.nbop.busin.counter.model.form.ClientUpdateForm;
//import com.gf.nbop.busin.counter.model.form.CommonResponseForm;
//import com.gf.nbop.busin.counter.model.form.ExtClientTotalOutput;
//import com.gf.nbop.busin.counter.model.form.ExtMulitStockAccountTradeOutput;
//import com.gf.nbop.busin.counter.model.form.ExtMulitStockAccountTradeOutput.FailInfo;
//import com.gf.nbop.busin.counter.model.form.ReturnVisitor;
//import com.gf.nbop.busin.counter.model.mybatis.CsdcStbTransA;
//import com.gf.nbop.busin.counter.model.mybatis.Idinfocheck;
//import com.gf.nbop.busin.counter.service.comm.AbstractServiceForJob;
//import com.gf.nbop.busin.counter.service.mybatis.IdinfocheckService;
//import com.gf.nbop.busin.counter.service.mybatis.LockactiveService;
//import com.gf.nbop.busin.counter.service.mybatis.ShregswitchService;
//import com.gf.nbop.busin.counter.service.mybatis.impl.BusinProofJourServiceImpl;
//import com.gf.nbop.busin.counterx.dao.AcptformMapper;
//import com.gf.nbop.busin.counterx.dao.BusinacptformMapper;
//import com.gf.nbop.busin.counterx.dao.CsdcStbTransAMapper;
//import com.gf.nbop.busin.counterx.dao.TytWhiteListMapper;
//import com.gf.nbop.busin.dict.ProofType;
//import com.gf.nbop.plat.commonutils.exception.BopsException;
//import com.gf.nbop.plat.commonutils.exception.ExtcenterBopsException;
//import com.gf.nbop.plat.commonutils.logging.BopsLogger;
//import com.gf.nbop.plat.commonutils.models.constant.Constant;
//import com.gf.nbop.plat.commonutils.models.constant.GFErrorCode;
//import com.gf.nbop.plat.commonutils.models.constant.GfSysConfigContant;
//import com.gf.nbop.plat.commonutils.models.constant.ServiceUri;
//import com.gf.nbop.plat.commonutils.models.constant.SysdictionaryConst;
//import com.gf.nbop.plat.commonutils.models.mybatis.Fatcainfo;
//import com.gf.nbop.plat.commonutils.models.mybatis.Lockactive;
//import com.gf.nbop.plat.commonutils.models.mybatis.Organtaxinfo;
//import com.gf.nbop.plat.commonutils.models.mybatis.Persontaxinfo;
//import com.gf.nbop.plat.commonutils.models.mybatis.Shregswitch;
//import com.gf.nbop.plat.commonutils.monitor.MonitorUtils;
//import com.gf.nbop.plat.commonutils.rest.client.RestClient;
//import com.gf.nbop.plat.commonutils.rest.response.RestResult;
//import com.gf.nbop.plat.commonutils.tools.CSDCDictMappingUtils;
//import com.gf.nbop.plat.commonutils.tools.CommonTools;
//import com.gf.nbop.plat.commonutils.tools.DateFormatter;
//import com.gf.nbop.plat.commonutils.tools.IdentifyUtils;
//import com.gf.nbop.plat.commonutils.tools.KeyDataEnc;
//import com.gf.nbop.plat.commonutils.tools.MapUtil;
//import com.gf.nbop.plat.commonutils.tools.StringUtils;
//
///**
// *
// */
//@Service
//public class ExtBusinService {
//
//	//BOPS-18474: 新三板权限开通校验改异步，101正常运行后删除
//	@Value("${nbop.async.sprint101:true}")
//	private boolean asyncFlag;
//
//	@Autowired
//	BusinAcptformDao acptDao;
//
//	@Autowired
//	private AcptformMapper acptMapper;
//
//	@Autowired
//	MemCache memCache;
//
//	@Autowired
//	BusinAcceptService businAcptSrv;
//
//	@Autowired
//	BusinAcptformService businAcptformService;
//
//	@Autowired
//	ServiceUri serviceUri;
//
//	@Autowired
//	private BusinAcceptService businAcceptService;
//
//	@Autowired
//	private OuterInterfaceService outerInterfaceService;
//
//	@Autowired
//	private BatchtaskManagerDao batchtaskManagerDao;
//
//	@Autowired
//	private AbstractServiceForJob abstractServiceForJob;
//
//	@Value("${nbop.url.hsclient}")
//	private String hsclient_url;
//
//	@Value("${nbop.extcenter.isRepeatSubmit:0}")
//	private String isRepeatSubmit;
//
//	@Autowired
//	private BatchAppropriateService batchAppropriateService;
//
//	@Autowired
//	private LockactiveService lockactiveService;
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	@Autowired
//	AcptformService acptService;
//
//	@Autowired
//	AcceptQryService acceptQryService;
//
//	@Autowired
//	Mysqltools mysql;
//
//	@Autowired
//	MemCache cache;
//
//	@Autowired
//	CacheGfFieldtoname cachefield;
//
//	@Autowired
//	private AppropriateService appropriateService;
//
//	@Autowired
//	private BigDataInterfaceService bigDataInterfaceService;
//
//	@Autowired
//	private ClientTradeInfoService clientTradeInfoService;
//
//	@Autowired
//	private AppropriateDao appropriateDao;
//
//	@Autowired
//	private ExtUserService extUserService;
//
//	@Autowired
//	private ExtApproService extApproService;
//
//	@Autowired
//	private StockholderService stockholderSertice;
//
//	@Autowired
//	private BusinacptformMapper businacptDao;
//
//	@Value("${nbop.url.counter}")
//	private String counter_url;
//
//	@Autowired
//	CacheGfDictionary cacheGfDictionary;
//
//	@Value("${nbop.ext.taskbusin.sign:1}")
//	private String saveBatchTaskBusinSign;
//
//	@Value("#{'${nbop.ext.batchtask.check:1,2}'.split(',')}")
//	private List<String> batchTaskCheckServiceIds;
//
//	@Value("#{'${nbop.ext.runningtask.skip:1,2}'.split(',')}")
//	private List<String> skipRunningCheckServiceIds;
//
//	@Value("${nbop.url.ufics:http://127.0.0.1}")
//	private String ufics_url;
//
//	@Autowired
//	private PointService pointService;
//
//	@Autowired
//	IdinfocheckService idinfocheckService;
//
//	@Autowired
//	private TytWhiteListMapper listMapper;
//
//	@Autowired
//	private HsInterfaceService hsInterfaceService;
//
//	@Autowired
//	private ShregswitchService shregswitchService;
//
//	@Autowired
//	private ClientService clientService;
//
//	@Autowired
//	private BusinProofJourServiceImpl proofServ;
//
//	@Resource
//    CsdcStbTransAMapper csdcStbTransAMapper;
//
//	private BopsLogger logger = new BopsLogger(ExtBusinService.class);
//
//	/*
//	 *根据客户编号，资产账号，证件类别，证件号码，姓名来模糊查询businacptform
//	 * */
//	public CommonResponseForm<DataPageResponse> getBusinAcptformFuzzy(String clientId, String fullName, String idKind, String idNo, String businOpType,
//			Integer startPage, Integer pageSize, String order){
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//		//客户号为空时那就根据三要素查询
//		if(StringUtils.isBlank(clientId)){
//			//三要素查询时必须送证件类别，不然索引走不到
//			if (StringUtils.isBlank(idKind)){
//				extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_IDKIND_IS_EMPTY);
//				extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_IDKIND_IS_EMPTY_INFO);
//				extCommonResponseForm.setRemark("");
//				extCommonResponseForm.setData(null);
//				return extCommonResponseForm;
//			}else{
//				//三要素查询时必须得有证件号
//				if (StringUtils.isBlank(idNo)){
//					extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_IDNO_IS_EMPTY);
//					extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_IDNO_IS_EMPTY_INFO);
//					extCommonResponseForm.setRemark("");
//					extCommonResponseForm.setData(null);
//					return extCommonResponseForm;
//				}
//			}
//		}
//		DataPageResponse data =  acptDao.getBusinAcptformFuzzy(clientId, " ", null, fullName, idKind, idNo, businOpType, startPage, pageSize, order, "");
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	public RestResult<String> qryBusinAcptformInTransit(String clientId, String fullName, String idKind, String idNo, String businOpType) throws BopsException {
//
//		DataPageResponse result = acptDao.getBusinAcptformFuzzy(clientId, " ", null, fullName, idKind, idNo, businOpType, 1, 1, "position_str desc", "");
//		if (result == null || result.getData().isEmpty()) {
//			logger.info2("{},{},{},{}无{}业务", clientId, fullName, idKind, idNo, businOpType);
//			return new RestResult<>("无办理中业务");
//		} else {
//			logger.info2("{},{},{},{}上一笔{}业务：{}", clientId, fullName, idKind, idNo, businOpType, result.getData().get(0));
//			String originStatus = "," + StringUtils.NVL(result.getData().get(0).get("acptStatus"), "").toString() + ",";
//			if (",7,8,9,19,".contains(originStatus)) {// 受理单已完结
//				return new RestResult<>("上笔业务已办理完");
//			} else if (",1,".contains(originStatus)) {// 进复核流程
//				checkDuplicateAcpt(businOpType, clientId, null, fullName, idKind, idNo, 0); // BOPS-13580：检查在途复核任务，防止复核过程中重复提交
//				return new RestResult<>("上笔业务已办理完");
//			} else if (",0,".contains(originStatus)) {// 受理单未流转
//				return new RestResult<>("上笔业务未开始");
//			} else {
//				throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS, "存在办理中的受理单，请勿重复提交业务数据！");
//			}
//		}
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20171107
//	 * @Description 增加业务受理记录 businacptform
//	 * @Link   BOPS-2729**/
//	public CommonResponseForm<Map<String,Object>> saveBusinAcptform(Integer opBranchNo, String operatorNo, Integer serviceId, String opStation, String opEntrustWay, Integer branchNo, String clientId, String fundAccount
//			, String fullName, String organFlag, String idKind, String idNo, String remark, String businOpType, String terminalType, String joinBusinessId, Integer corpRiskLevel){
//
//		CommonResponseForm<Map<String,Object>> extCommonResponseForm = new CommonResponseForm<Map<String,Object>>();
//		Integer currDate = StringUtils.getYYYYMMDD(new Date());
//		Integer currTime = StringUtils.getMinSec(new Date());
//		Map<String,String> sysarg = memCache.getSysarg();
//		logger.info(sysarg.get("init_date").toString());
//		Integer initDate = Integer.valueOf(sysarg.get("init_date").toString());
//		Map<String,String> map = new HashMap<String,String>();
//		Map<String,Object> retmap = new HashMap<String,Object>();
//
//		try {
//			//BOPS-10512：检查在途任务，防止重复提交
//			checkDuplicateAcpt(businOpType, clientId, fundAccount, fullName, idKind, idNo, branchNo);
//
//			//BOPS-6232：20181227,hanjin,add,新增加joinBusinessId字段的优化
//			/*map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation
//					, opEntrustWay, serviceId, branchNo, 202006, " ", " ", clientId, fundAccount, fullName,
//					 organFlag, idKind, idNo, " ", 0, " ", " ", " ", " ", remark, "saveBusinAcptform/saveBusinAcptform", " ", " ", " ", 0, 0, businOpType, " ",
//					 0, " ", " ", 0, terminalType);*/
//			// 如果busin_op_type为900900非现场销户，则校验送进来的joinbusinessid的唯一性，如果不唯一，报错处理
//			if(businOpType.equals("900900")){
//				Map<String, Object> businacptform = acptDao.qryBusinAcptformByJoinId(joinBusinessId);
//				if(businacptform != null){
//					throw new BopsException(BopsException.ERR_PARAM_INVALID, "传入joinBusinessId值有误，该值已经绑定了业务表单！");
//				}
//			}
//			//add by fanlei 20190704 BOPS-7666
//			if (businOpType.equals("900065")){
//				if (corpRiskLevel.equals(0)){
//					throw new BopsException(BopsException.ERR_PARAM_INVALID, "风险等级为特别保护型客户禁止办理此业务！");
//				}
//			}
//
//			joinBusinessId = StringUtils.NVL(joinBusinessId, "");
//			//BOPS-6712:20190306,hanjin,add,增加入参corpRiskLevel
//			/*map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation
//					, opEntrustWay, serviceId, branchNo, 202006, " ", " ", clientId, fundAccount, fullName,
//					 organFlag, idKind, idNo, " ", 0, " ", " ", " ", " ", remark, "saveBusinAcptform/saveBusinAcptform", " ", " ", " ", 0, 0, businOpType, " ",
//					 0, joinBusinessId, " ", 0, terminalType);*/
//			corpRiskLevel = Integer.valueOf(StringUtils.NVL(corpRiskLevel, "0").toString());
//			map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation
//			, opEntrustWay, serviceId, branchNo, 202006, " ", " ", clientId, fundAccount, fullName,
//			 organFlag, idKind, idNo, " ", corpRiskLevel, " ", " ", " ", " ", remark, "saveBusinAcptform/saveBusinAcptform", " ", " ", " ", 0, 0, businOpType, " ",
//			 0, joinBusinessId, " ", 0, terminalType,"");
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("增加业务受理记录失败");
//			extCommonResponseForm.setData(retmap);
//			return extCommonResponseForm;
//		}
//		retmap.put("acptId", map.get("acptId"));
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("增加业务受理记录成功");
//		extCommonResponseForm.setData(retmap);
//		return extCommonResponseForm;
//	}
//
//	private void checkDuplicateAcpt(String businOpType, String clientId, String fundAccount, String fullName, String idKind, String idNo, int branchNo) throws BopsException {
//
//		String rejectConfigs = memCache.getSysconfigValue(8888, GfSysConfigContant.CNST_GF_SYSCONFIG_DUPLICATE_ACPT_REJECT_STATUS.toString());
//		Map<String, String> rejectMap = new HashMap<String, String>();
//		for (String rejectConfig : rejectConfigs.split(";", 0)) {
//			int poi = rejectConfig.indexOf(":");
//			rejectMap.put(rejectConfig.substring(0, poi), rejectConfig.substring(poi + 1));
//		}
//		String checkStatus = rejectMap.get(rejectMap.containsKey(businOpType) ? businOpType : "!");
//		if ("".equals(checkStatus)) {// 无需检查状态
//			return;
//		}
//
//		Acptform cond = new Acptform();
//		cond.setClientId(clientId);
//		cond.setFundAccount(fundAccount);
//		cond.setFullName(fullName);
//		cond.setIdKind(idKind);
//		cond.setIdNo(idNo);
//		cond.setBranchNo(branchNo);
//		cond.setBusinOpType(businOpType);
//
//		int acptCnt = acptMapper.getTotalByClientBusinAndStatus(cond, checkStatus.split(",", 0));
//		if (acptCnt > 0) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS, GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS_INFO);
//		}
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20171108
//	 * @Description 修改业务受理记录 businacptform
//	 * @Link   BOPS-2729
//	 * @Link   BOPS-7289 add opContent by fanlei 20190515
//	 * **/
//	public CommonResponseForm<String> updateBusinAcptform(Integer opBranchNo, String operatorNo, Integer serviceId, String opStation, String opEntrustWay, Integer branchNo, String acptId, String clientId, String fundAccount
//			, String fullName, String organFlag, String idKind, String idNo, String mobileTel, String opContent, String remark, String acptStatus, String clearFlag
//			, String businOpType, Integer dateClear, String terminalType){
//
//		CommonResponseForm<String> extCommonResponseForm = new CommonResponseForm<String>();
//		Integer currDate = StringUtils.getYYYYMMDD(new Date());
//		Integer currTime = StringUtils.getMinSec(new Date());
//		Map<String,String> sysarg = memCache.getSysarg();
//		logger.info(sysarg.get("init_date").toString());
//		Integer initDate = Integer.valueOf(sysarg.get("init_date").toString());
//		Map<String,String> map = new HashMap<String,String>();
//
//		try {
//			map =  acptDao.setBusinAcptform(2, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation, opEntrustWay, serviceId, branchNo, 202006, " ", acptId, clientId, fundAccount, fullName,
//					 organFlag, idKind, idNo, mobileTel, 0, " ", "", "", opContent, remark, "", "", acptStatus, clearFlag, 0, 0, businOpType, "",
//					 0, "", "", dateClear, terminalType,"");
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("修改业务受理记录失败");
//			extCommonResponseForm.setData("");
//			return extCommonResponseForm;
//		}
//
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("修改业务受理记录成功");
//		extCommonResponseForm.setData(map.get("acptId"));
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20171108
//	 * @Description 根据客受理单号获取受理参数
//	 * @Link   BOPS-2729**/
//	public CommonResponseForm<DataPageResponse> getBusinAcptformArg(String acptId, Integer stageStep, Integer startPage, Integer pageSize){
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//
//		DataPageResponse data =  acptDao.qryBusinAcptformArg(acptId, stageStep, startPage, pageSize, "acpt_id");
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20171108
//	 * @Description 增加业务受理记录 businacptformarg
//	 * @Link   BOPS-2729**/
//	public CommonResponseForm<String> saveBusinAcptformArg(BusinAcptformArgRequest request){
//
//		CommonResponseForm<String> extCommonResponseForm = new CommonResponseForm<String>();
//		Map<String,String> map = new HashMap<String,String>();
//		String acptId = request.getAcptId();
//		if (StringUtils.isBlank(acptId)){
//			extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_IS_EMPTY);
//			extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_IS_EMPTY_INFO);
//			extCommonResponseForm.setRemark("");
//			extCommonResponseForm.setData(null);
//			return extCommonResponseForm;
//		}
//
//		if (null == request.getBusinAcptformArgs() || request.getBusinAcptformArgs().isEmpty()){
//			extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPARG_IS_EMPTY);
//			extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPARG_IS_EMPTY_INFO);
//			extCommonResponseForm.setRemark("");
//			extCommonResponseForm.setData(null);
//			return extCommonResponseForm;
//		}
//
//		try {
//
//			map = businAcptSrv.setBusinAcptformArg(1, acptId, request.getBusinAcptformArgs());
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("增加业务受理记录失败");
//			extCommonResponseForm.setData("");
//			return extCommonResponseForm;
//		}
//
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("增加业务受理记录成功");
//		extCommonResponseForm.setData(map.get("remark"));
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author fanlei
//	 * @Date   20171109
//	 * @Description 统一受理外部接口,生成跑批任务
//	 * @Link   BOPS-2729**/
//	public String saveBatchTaskBusi(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, Integer menuId, Integer branchNo, String auditAction, String acptId) throws BopsException{
//
//		CommonResponseForm<Map<String, Object>> extCommonResponseForm = new CommonResponseForm<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		String retObject = null;
//		Map<String, Object> businMap = businAcptformService.getBusinAcptForm(acptId);
//		if(null == businMap || businMap.isEmpty()){
//			extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS);
//			extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS_INFO);
//			extCommonResponseForm.setRemark("统一受理接口调用失败");
//			extCommonResponseForm.setData(map);
//			throw new BopsException(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS, ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS_INFO);
//			//return extCommonResponseForm;
//		}
//
//		//BOPS-9257:校验同一笔busin_acpt_id，如果已存在复核任务，则不允许再次提交。
//		if ("0".equals(isRepeatSubmit) && StringUtils.isNotBlank(acptId)) {
//			Map<String, Object> acptMap = acptService.getAcptFormByJoinBusin(acptId);
//			if (acptMap != null && acptMap.containsKey("acptStatus") && !"0".equals(StringUtils.NVL(acptMap.get("acptStatus"),"").toString()) && StringUtils.isNotBlank(StringUtils.NVL(acptMap.get("acptStatus"),"").toString())) {
//				extCommonResponseForm.setErrorNo(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS);
//				extCommonResponseForm.setErrorInfo(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS_INFO);
//				extCommonResponseForm.setRemark("统一受理接口调用失败");
//				extCommonResponseForm.setData(map);
//				throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS,
//						GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS_INFO);
//			}
//		}
//
//		String businOpType = businMap.get("businOpType").toString();
//		//add by fanlei 20190401 BOPS-6907 提交的时候将受理记录状态改成1
//		//add by fanlei 20190528 BOPS-BOPS-7384 科创板回访模式这里不能改成1
//		//add by fanlei 20200224 BOPS-9463 非现场新三板权限开通900919
//		//add by liyu 20200731 BOPS-11013  交易系统投资者评定900016和非现场专业投资者认定900921
//		// BOPS-12318:支持按功能配置不同兜底检查
//		if (batchTaskCheckServiceIds.contains(businOpType)) {
//			String checkUri = counter_url + serviceUri.getServUri(serviceUri.SVR_URI_ACCEPT_BATCHTASK_CHECK);
//			Map<String, Object> checkArgs = new HashMap<>();
//			checkArgs.put("serviceId", businOpType);
//			checkArgs.put("acptId", acptId);
//			try {
//				JSONObject checkResult = new RestClient().getForJsonObj(checkUri, checkArgs);
//				logger.debug2("提交跑批任务兜底检查:{}", checkResult);
//			} catch (Exception e) {
//				logger.error("提交跑批任务兜底检查失败  args = {}", checkArgs);
//				if (e instanceof BopsException) {
//					throw (BopsException) e;
//				} else {
//					throw new BopsException("-1", "兜底检查失败", e.getMessage());
//				}
//			}
//		}
//
//		//BOPS-7022 add by fanlei 20190409
//		batchAppropriateService.reSetArchDetailFlag(acptId,"1","【业务提交重置档案状态】",1);
//
//		//mdf by fanlei 20200803 BOPS-11012 非现场专业投资者认定这里更新为1的时候调用新的接口产生复核更新状态前产生复核
//		char config_1000150 = memCache.getSysconfigCharValue(8888, GfSysConfigContant.CNST_GF_SYSCONFIG_INVESTMENT_JUDGE.toString());
//		if(config_1000150 == '1' && businOpType.equals("900921")){
//			serviceId = Integer.valueOf(businOpType);
//			try{
//				JSONObject retjson = mdfBusinAcptform(opBranchNo, operatorNo, serviceId, menuId, opStation, opEntrustWay, branchNo, acptId, businMap.get("clientId").toString(), businMap.get("fundAccount").toString()
//					, businMap.get("fullName").toString(), businMap.get("organFlag").toString(), businMap.get("idKind").toString(), businMap.get("idNo").toString(), "", "", "", "1", ""
//					, businOpType, 0, "");
//				//logger.info("retjson = ".concat(retjson.toJSONString()));
//				return retjson.toJSONString();
//			}catch (BopsException e) {
//				// TODO Auto-generated catch block
//				logger.info(e);
//				extCommonResponseForm.setErrorNo(e.getErrCode());
//				extCommonResponseForm.setErrorInfo(e.getErrMessage());
//				extCommonResponseForm.setRemark("统一受理接口调用失败");
//				extCommonResponseForm.setData(map);
//				throw new BopsException(e.getErrCode(), e.getErrMessage());
//				//return extCommonResponseForm;
//			}
//
//
//		}else if (!businOpType.equals("900900") && !businOpType.equals("900135")/* && !businOpType.equals("900919")*/){//BOPS-16491:非现场新三板开通取消回访
//			String acptStatus = "1";
//			updateBusinAcptform( opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, 0, acptId, "", ""
//					, "", "", "", "", "", "", "", acptStatus, ""
//					, "", 0, "");
//		}
//
//		//add by fanlei 由于servlet中使用的是serviceId复核，要重置 BOPS-2941
//		serviceId = Integer.valueOf(businOpType);
//		try {
//			retObject = businAcptformService.addBatchTaskBusi(opBranchNo, operatorNo,
//					opStation, opEntrustWay, serviceId, menuId, branchNo, auditAction, acptId, businOpType);
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("统一受理接口调用失败");
//			extCommonResponseForm.setData(map);
//			throw new BopsException(e.getErrCode(), e.getErrMessage());
//			//return extCommonResponseForm;
//		}
//
//		extCommonResponseForm.setRemark("统一受理接口调用成功");
//		extCommonResponseForm.setData(map);
//		return retObject;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author fanlei
//	 * @Date   20200901
//	 * @Description 周边受理、办理模式电签一体化强控设置
//	 * @Link   BOPS-11755**/
//	public String saveBatchTaskBusi(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, Integer menuId, Integer branchNo, String auditAction, String acptId, AgreementsFormNew agreements) throws BopsException{
//
//		CommonResponseForm<Map<String, Object>> extCommonResponseForm = new CommonResponseForm<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		String retObject = null;
//		Map<String, Object> businMap = businAcptformService.getBusinAcptForm(acptId);
//		if(null == businMap || businMap.isEmpty()){
//			extCommonResponseForm.setErrorNo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS);
//			extCommonResponseForm.setErrorInfo(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS_INFO);
//			extCommonResponseForm.setRemark("统一受理接口调用失败");
//			extCommonResponseForm.setData(map);
//			throw new BopsException(ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS, ExtcenterBopsException.ERR_NBOP_EXTCENTER_ACPTID_NOT_EXISTS_INFO);
//			//return extCommonResponseForm;
//		}
//
//		//BOPS-9257:校验同一笔busin_acpt_id，如果已存在复核任务，则不允许再次提交。
//		if ("0".equals(isRepeatSubmit) && StringUtils.isNotBlank(acptId)) {
//			Map<String, Object> acptMap = acptService.getAcptFormByJoinBusin(acptId);
//			if (acptMap != null && acptMap.containsKey("acptStatus") && !"0".equals(StringUtils.NVL(acptMap.get("acptStatus"),"").toString()) && StringUtils.isNotBlank(StringUtils.NVL(acptMap.get("acptStatus"),"").toString())) {
//				extCommonResponseForm.setErrorNo(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS);
//				extCommonResponseForm.setErrorInfo(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS_INFO);
//				extCommonResponseForm.setRemark("统一受理接口调用失败");
//				extCommonResponseForm.setData(map);
//				throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS,
//						GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS_INFO);
//			}
//		}
//
//		String businOpType = businMap.get("businOpType").toString();
//		// BOPS-12318:检查客户有无同功能申请处理中
//		if (!skipRunningCheckServiceIds.contains(businOpType)) {
//			DataPageResponse runningResult = businAcptSrv.getBusinAcptformFuzzy((String) businMap.get("clientId"), (String) businMap.get("fundAccount"),
//					(String) businMap.get("fullName"), (String) businMap.get("idKind"), (String) businMap.get("idNo"), businOpType, 1, 1, "", "6");// 6-处理中（即跑批未结束）
//			if (runningResult.getData().size() != 0) {
//				throw new BopsException(GFErrorCode.ERR_BOPS_SYSTEM_BUSY, GFErrorCode.ERR_BOPS_SYSTEM_BUSY_INFO, "未完成单号：" + runningResult.getData().get(0).get("acptId"));
//			}
//		}
//		// BOPS-12318:支持按功能配置不同兜底检查
//		if (batchTaskCheckServiceIds.contains(businOpType)) {
//			String checkUri = counter_url + serviceUri.getServUri(serviceUri.SVR_URI_ACCEPT_BATCHTASK_CHECK);
//			Map<String, Object> checkArgs = new HashMap<>();
//			checkArgs.put("serviceId", businOpType);
//			checkArgs.put("acptId", acptId);
//			try {
//				JSONObject checkResult = new RestClient().getForJsonObj(checkUri, checkArgs);
//				logger.debug2("提交跑批任务兜底检查:{}", checkResult);
//			} catch (Exception e) {
//				logger.error("提交跑批任务兜底检查失败  args = {}", checkArgs);
//				if (e instanceof BopsException) {
//					throw (BopsException) e;
//				} else {
//					throw new BopsException("-1", "兜底检查失败", e.getMessage());
//				}
//			}
//		}
//
//		//BOPS-7022 add by fanlei 20190409
//		batchAppropriateService.reSetArchDetailFlag(acptId,"1","【业务提交重置档案状态】",1);
//
//		// 协议签署
//		if (saveBatchTaskBusinSign.equals("1")){
//			Map<String, String> gfservice = memCache.getGFService(Integer.valueOf(businOpType));
//			String configBusinType = gfservice.get("busin_type");
//			if (StringUtils.isNotBlank(configBusinType)){
//				//gfservice如果配置了协议则进行签署协议
//				String actionInEsign = "0";
//				String clientId = businMap.get("clientId").toString();
//				String idKind = businMap.get("idKind").toString();
//				String idNo = businMap.get("idNo").toString();
//				String clientName = businMap.get("fullName").toString();
//				String fundAccount = businMap.get("fundAccount").toString();
//				String organFlag = businMap.get("organFlag").toString();
//				String opRemark = "";
//				String financeType = "0";
//				if(clientId.contains("DS")){
//					financeType = "7";
//				}
//				Map contextMap = new HashMap<String,Object>();
//				contextMap.put("clientName",clientName);
//				contextMap.put("idKind",idKind);
//				contextMap.put("idNo",idNo);
//
//				Map signMap =  extApproService.newClientSignAgmsWithDate(contextMap, agreements, opBranchNo, operatorNo, opEntrustWay,
//						opStation, branchNo, "", "", "", clientId, fundAccount, "", "", clientName,
//						idKind,  idNo, opRemark, organFlag, financeType, opEntrustWay, actionInEsign, configBusinType);
//				//记录businacptformarg 协议签署信息
//				businAcptformService.insertAcptFormArgInfo(acptId, "agreements", signMap.get("agreements").toString());
//				businAcptformService.insertAcptFormArgInfo(acptId, "agreementsInfo", signMap.get("agreementsInfo").toString());
//				businAcptformService.insertAcptFormArgInfo(acptId, "agreementsExemption", signMap.get("agreementsExemption").toString());
//			}
//		}
//
//
//		//add by fanlei 20190401 BOPS-6907 提交的时候将受理记录状态改成1
//		//add by fanlei 20190528 BOPS-BOPS-7384 科创板回访模式这里不能改成1
//		//add by fanlei 20200224 BOPS-9463 非现场新三板权限开通900919
//		//add by liyu 20200731 BOPS-11013  交易系统投资者评定900016和非现场专业投资者认定900921
//
//		//mdf by fanlei 20200803 BOPS-11012 非现场专业投资者认定这里更新为1的时候调用新的接口产生复核更新状态前产生复核
//		char config_1000150 = memCache.getSysconfigCharValue(8888, GfSysConfigContant.CNST_GF_SYSCONFIG_INVESTMENT_JUDGE.toString());
//		if(config_1000150 == '1' && businOpType.equals("900921")){
//			serviceId = Integer.valueOf(businOpType);
//			try{
//				JSONObject retjson = mdfBusinAcptform(opBranchNo, operatorNo, serviceId, menuId, opStation, opEntrustWay, branchNo, acptId, businMap.get("clientId").toString(), businMap.get("fundAccount").toString()
//					, businMap.get("fullName").toString(), businMap.get("organFlag").toString(), businMap.get("idKind").toString(), businMap.get("idNo").toString(), "", "", "", "1", ""
//					, businOpType, 0, "");
//				//logger.info("retjson = ".concat(retjson.toJSONString()));
//				return retjson.toJSONString();
//			}catch (BopsException e) {
//				// TODO Auto-generated catch block
//				logger.info(e);
//				extCommonResponseForm.setErrorNo(e.getErrCode());
//				extCommonResponseForm.setErrorInfo(e.getErrMessage());
//				extCommonResponseForm.setRemark("统一受理接口调用失败");
//				extCommonResponseForm.setData(map);
//				throw new BopsException(e.getErrCode(), e.getErrMessage());
//				//return extCommonResponseForm;
//			}
//
//
//		}else if (!businOpType.equals("900900") && !businOpType.equals("900135")/* && !businOpType.equals("900919")*/){//BOPS-16491:非现场新三板开通取消回访
//			String acptStatus = "1";
//			updateBusinAcptform( opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, 0, acptId, "", ""
//					, "", "", "", "", "", "", "", acptStatus, ""
//					, "", 0, "");
//		}
//
//		//add by fanlei 由于servlet中使用的是serviceId复核，要重置 BOPS-2941
//		serviceId = Integer.valueOf(businOpType);
//		try {
//			retObject = businAcptformService.addBatchTaskBusi(opBranchNo, operatorNo,
//					opStation, opEntrustWay, serviceId, menuId, branchNo, auditAction, acptId, businOpType);
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("统一受理接口调用失败");
//			extCommonResponseForm.setData(map);
//			throw new BopsException(e.getErrCode(), e.getErrMessage());
//			//return extCommonResponseForm;
//		}
//
//		extCommonResponseForm.setRemark("统一受理接口调用成功");
//		extCommonResponseForm.setData(map);
//		return retObject;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author 范磊
//	 * @Date   20200803
//	 * @Description 调用counter的受理单修改接口支持复核
//	 * @Link   BOPS-11012**/
//	public JSONObject mdfBusinAcptform(Integer opBranchNo, String operatorNo, Integer serviceId, Integer menuId, String opStation, String opEntrustWay, Integer branchNo, String acptId, String clientId, String fundAccount
//			, String fullName, String organFlag, String idKind, String idNo, String mobileTel, String opContent, String remark, String acptStatus, String clearFlag
//			, String businOpType, Integer dateClear, String terminalType) throws BopsException{
//
//		RestClient client = new RestClient();
//		String url = counter_url + serviceUri.getServUri(serviceUri.SVR_URI_ACCEPT_BUSINACPTFORM_MOD);
//
//		Map<String,Object> json = new HashMap();
//		json.put("opBranchNo", opBranchNo);
//		json.put("operatorNo", operatorNo);
//		json.put("serviceId", serviceId);
//		json.put("opStation", opStation);
//		json.put("branchNo", branchNo);
//		json.put("opEntrustWay", opEntrustWay);
//		json.put("acptId", acptId);
//		json.put("clientId", clientId);
//		json.put("idNo", idNo);
//		json.put("fundAccount", fundAccount);
//		json.put("fullName", fullName);
//		json.put("organFlag", organFlag);
//		json.put("idKind", idKind);
//		json.put("mobileTel", mobileTel);
//		json.put("opContent", opContent);
//		json.put("remark", remark);
//		json.put("acptStatus", acptStatus);
//		json.put("clearFlag", clearFlag);
//		json.put("businOpType", businOpType);
//		json.put("dateClear", dateClear);
//		json.put("terminalType", terminalType);
//		json.put("menuId", menuId);
//		return client.postForJsonObj(url, json);
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20180622
//	 * @Description 中登股东账号信息查询
//	 * @Link   BOPS-3760**/
//	public List<Map> qryCsdcStkacctListExt(String clientId, String fundAccount, String clientName, String idKind, String idNo
//			, String acodeAccount, String exchangeType, String holderKind, String stockAccount, boolean isCrdt) throws BopsException{
//
//		RestClient client = new RestClient();
//		String url = hsclient_url + serviceUri.getServUri(serviceUri.SVR_URI_HSCLIENT_CSDCSTOCKHOLDERINFO_GET);
//
//		Map<String,Object> json = new HashMap();
//		json.put("clientId", clientId);
//		json.put("fundAccount", fundAccount);
//		json.put("clientName", clientName);
//		json.put("idKind", idKind);
//		json.put("idNo", idNo);
//		json.put("acodeAccount", acodeAccount);
//		json.put("exchangeType", exchangeType);
//		json.put("holderKind", holderKind);
//		json.put("stockAccount", stockAccount);
//		json.put("isCrdt", isCrdt);
//
//		return client.postJson2ForMapList(url, json, new JSONObject());
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20190807
//	 * @Description 中登股东账号信息查询(String接包的方式)
//	 * @Link   BOPS-8031**/
//	public List<JSONObject> qryCsdcStkacctListExt2(String clientId, String fundAccount, String clientName, String idKind, String idNo
//			, String acodeAccount, String exchangeType, String holderKind, String stockAccount, boolean isCrdt) throws BopsException{
//
//		RestClient client = new RestClient();
//		String url = hsclient_url + serviceUri.getServUri(serviceUri.SVR_URI_HSCLIENT_CSDCSTOCKHOLDERINFO_GET);
//
//		Map<String,Object> json = new HashMap();
//		json.put("clientId", clientId);
//		json.put("fundAccount", fundAccount);
//		json.put("clientName", clientName);
//		json.put("idKind", idKind);
//		json.put("idNo", idNo);
//		json.put("acodeAccount", acodeAccount);
//		json.put("exchangeType", exchangeType);
//		json.put("holderKind", holderKind);
//		json.put("stockAccount", stockAccount);
//		json.put("isCrdt", isCrdt);
//
//		ResponseEntity<String> testResult = client.postForEntity(url, String.class, new JSONObject().toJSONString(), json);
//		return JSONObject.parseArray(testResult.getBody(), JSONObject.class);
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20180710
//	 * @Description 腾讯创业板转签
//	 * @Link   BOPS-4026**/
//	public CommonResponseForm<JSONObject> regStockSecondExt(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String terminalType, Integer branchNo, String clientId, String stockAccount, String businOpType, String secRelationName, String secRelationPhone, String socialralType
//			, String gemTrainFlag,String eyewitness, String idiograph,String subRiskAddress) throws BopsException{
//
//		gemTrainFlag = StringUtils.NVL(gemTrainFlag, "");
//		eyewitness = StringUtils.NVL(eyewitness, "");
//		idiograph = StringUtils.NVL(idiograph, "");
//		subRiskAddress = StringUtils.NVL(subRiskAddress, "");
//		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("businOpType", businOpType);
//		args.put("operatorNo", operatorNo);
//		args.put("opBranchNo", opBranchNo.toString());
//		args.put("opEntrustWay", opEntrustWay);
//		args.put("opStation", opStation);
//		args.put("branchNo", branchNo.toString());
//		args.put("actionUrl", "/regStockSecondExt");
//		args.put("terminalType", terminalType);
//		args.put("clientId", clientId);
//		args.put("stockAccount", stockAccount);
//		args.put("secRelationName",secRelationName);
//		args.put("secRelationPhone", secRelationPhone);
//		args.put("socialralType", socialralType);
//		args.put("gemTrainFlag", gemTrainFlag);
//		args.put("eyewitness", eyewitness);
//		args.put("idiograph", idiograph);
//		args.put("subRiskAddress", subRiskAddress);
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		JSONObject jsonObject = new JSONObject();
//		Boolean isModSecondContact = true;
//		Boolean isRegStockSecond = true;
//		Map<String, Object> param = new HashMap<>();
//		param.put("clientId", clientId);
//		param.put("secRelationName", secRelationName);
//		param.put("secRelationPhone", secRelationPhone);
//		param.put("socialralType", socialralType);
//		RestClient restClient = new RestClient();
//		String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_SECONDCONTACT_MOD);
//		try {
//			jsonObject = restClient.postForJsonObj(uri, param);
//		} catch (BopsException e) {
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("第二联系人信息修改失败");
//			extCommonResponseForm.setData(jsonObject);
//			isModSecondContact = false;
//
//		} catch (Exception e1) {
//			extCommonResponseForm.setErrorNo("500");
//			extCommonResponseForm.setErrorInfo(e1.getMessage());
//			extCommonResponseForm.setRemark("第二联系人信息修改失败");
//			extCommonResponseForm.setData(jsonObject);
//			isModSecondContact = false;
//
//		}
//
//		if(!isModSecondContact){
//			args.put("acptStatus", "8");
//			args.put("remark", "第二联系人信息修改失败");
//			businAcceptService.saveBusinAcptFormAndArg(args);
//			return extCommonResponseForm;
//		}
//
//		//2.再进行创业板转签
//		param.clear();
//		param.put("clientId", clientId);
//		param.put("stockAccount", stockAccount);
//		param.put("gemTrainFlag", gemTrainFlag);
//		param.put("eyewitness", eyewitness);
//		param.put("idiograph", idiograph);
//		param.put("subRiskAddress", subRiskAddress);
//		uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_GEMACCINFO_READD);
//		try {
//			jsonObject = restClient.postForJsonObj(uri, param);
//		} catch (BopsException e) {
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("创业板转签失败");
//			extCommonResponseForm.setData(jsonObject);
//			isRegStockSecond = false;
//
//		} catch (Exception e1) {
//			extCommonResponseForm.setErrorNo("500");
//			extCommonResponseForm.setErrorInfo(e1.getMessage());
//			extCommonResponseForm.setRemark("创业板转签失败");
//			extCommonResponseForm.setData(jsonObject);
//			isRegStockSecond = false;
//
//		}
//
//		if(!isRegStockSecond){
//			args.put("acptStatus", "8");
//			args.put("remark", "创业板转签失败");
//			businAcceptService.saveBusinAcptFormAndArg(args);
//			return extCommonResponseForm;
//		}
//
//		//记录businacptform，businacptformarg
//		businAcceptService.saveBusinAcptFormAndArg(args);
//		extCommonResponseForm.setRemark("创业板转签成功");
//		extCommonResponseForm.setData(jsonObject);
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20180710
//	 * @Description 创业板登记检查
//	 * @Link   BOPS-4026**/
//	public JSONObject checkStockSecondExt(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String terminalType, Integer branchNo, String clientId, String stockAccount) throws BopsException{
//
//		Map<String, String> retMap = new HashMap<String, String>();
//		JSONObject jsonObject = new JSONObject();
//		Map<String, Object> param = new HashMap<>();
//		param.put("clientId", clientId);
//		param.put("stockAccount", stockAccount);
//
//		RestClient restClient = new RestClient();
//		String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_STOCKSECOND_CHECK);
//		try {
//			jsonObject = restClient.postForJsonObj(uri, param);
//		} catch (BopsException e) {
//			throw new BopsException(e.getErrCode(), e.getErrMessage());
//
//		} catch (Exception e1) {
//			throw new BopsException("500", e1.getMessage());
//		}
//		return jsonObject;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20181128
//	 * @Description 周边查询办理流水
//	 * @Link   BOPS-5796**/
//	public CommonResponseForm<DataPageResponse> qryAcptTaskBusinExt(String clientId, String idNo, String enBusinOpType,
//			Integer beginDate, Integer endDate, Integer startPage, Integer pageSize, String order){
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//
//		DataPageResponse data = null;
//		try {
//			data = acptDao.qryAcptTaskBusinExt(clientId, idNo, enBusinOpType, beginDate, endDate, startPage, pageSize, order);
//		} catch (BopsException e) {
//			logger.info(e.getErrMessage());
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("周边查询办理流水失败");
//			return extCommonResponseForm;
//		}
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20181129
//	 * @Description 周边查询定时任务
//	 * @Link   BOPS-5796**/
//	public CommonResponseForm<DataPageResponse> qryBatchtaskBusinExt(String clientId, String idNo, String acptId, String enBusinOpType,
//			Integer beginDate, Integer endDate, Integer startPage, Integer pageSize, String order){
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//
//		DataPageResponse data = null;
//		try {
//			data = acptDao.qryBatchtaskBusinExt(clientId, idNo, acptId, enBusinOpType, beginDate, endDate, startPage, pageSize, order);
//		} catch (BopsException e) {
//			logger.info(e.getErrMessage());
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("周边查询定时任务失败");
//			return extCommonResponseForm;
//		}
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//
//	/**
//	 * @Author wangxp
//	 * @Date 20200401
//	 * @Description 周边查询定时任务
//	 * @Link BOPS-9846
//	 **/
//	public CommonResponseForm<DataPageResponse> qryBatchtaskBusinByAcptIdExt(String acptId, Integer startPage,
//			Integer pageSize, String order) {
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//
//		DataPageResponse data = null;
//		try {
//			data = acptDao.qryBatchtaskBusinByAcptIdExt(acptId, startPage, pageSize, order);
//		} catch (BopsException e) {
//			logger.info(e.getErrMessage());
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("周边查询定时任务失败");
//			return extCommonResponseForm;
//		}
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//
//
//
//	/**
//	 * @Author fanlei
//	 * @Date   20181129
//	 * @Description 周边查询定时任务详情
//	 * @Link   BOPS-5796**/
//	public CommonResponseForm<DataPageResponse> qryBatchSubTaskBusinExt(String acptId, Integer startPage, Integer pageSize, String order){
//
//		CommonResponseForm<DataPageResponse> extCommonResponseForm = new CommonResponseForm<DataPageResponse>();
//
//		DataPageResponse data = null;
//		try {
//			data = acptDao.qryBatchSubTaskBusinExt(acptId, startPage, pageSize, order);
//		} catch (BopsException e) {
//			logger.info(e.getErrMessage());
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("周边查询定时任务详情失败");
//			return extCommonResponseForm;
//		}
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20181129
//	 * @Description 周边查询定时任务详情
//	 * @Link   BOPS-5796**/
//	public CommonResponseForm<JSONObject> qryClientAccountsExt(String clientId, String assetProp, String actionStr, String isEcif, String ecifCustNo, String openType, String openFare, String checkStr){
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		RestClient client =  new RestClient();
//		String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CLIENTACCOUNTS_QRY);
//		Map<String,Object> paramMap = new  HashMap<String,Object>();
//		paramMap.put("client_id", clientId);
//		paramMap.put("asset_prop", assetProp);
//		paramMap.put("action_str", actionStr);
//		paramMap.put("is_ecif", isEcif);
//		paramMap.put("ecif_cust_no", ecifCustNo);
//		paramMap.put("is_open_type", openType);
//		paramMap.put("is_open_fare", openFare);
//		paramMap.put("is_check_str", checkStr);
//		paramMap.put("is_all", "1");
//
//		JSONObject data = null;
//
//		try {
//			data = client.getForJsonObj(url, paramMap);
//
//			//add by fanlei 20190109 BOPS-6347 - START
//			JSONObject eligJson = outerInterfaceService.qryClientEligInfo(clientId, "1");
//			String checkStrTotal = "";
//
//			//是否存在非现场销户的证券账户
//			//mdf by fanlei 20201208 BOPS-13736 循环一遍stockholder 判断股票质押权限
//			String configCancelStr = memCache.getSysconfigValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_STOCKACCOUNTCANCEL_STR.toString());
//			JSONArray fundAccountList = data.getJSONArray("fundAccount");
//			Boolean isType = false;
//			JSONArray holderList = data.getJSONArray("stockHolder");
//			if(null != holderList && holderList.size() > 0 ){
//				for(int i = 0; i < holderList.size(); i++){
//					JSONObject holderJson = (JSONObject) JSON.toJSON(holderList.get(i));
//					if(holderJson.containsKey("open_type")
//							&& (holderJson.getString("open_type").equals("1") || holderJson.getString("open_type").equals("2"))){
//						isType = true;
//						break;
//					}
//				}
//				for(int i = 0; i < holderList.size(); i++){
//					JSONObject holderJson = (JSONObject) JSON.toJSON(holderList.get(i));
//					//add by fanlei 20201208 BOPS-13736
//					if (configCancelStr.contains(",54,")){
//						//mdf by fanlei 20201216 需要判断holderCheckStr是否为空，因为周边入参isCheckStr如果是0这里不返回check_str
//						String holderCheckStr = holderJson.getString("check_str");
//						if (StringUtils.isNotBlank(holderCheckStr)){
//							char[] strStatus = holderCheckStr.toCharArray();
//							if (strStatus[53] == '1'){
//								for(int j = 0; j < fundAccountList.size(); j++){
//									JSONObject fundaccJosn = (JSONObject) JSON.toJSON(fundAccountList.get(j));
//									if (holderJson.getString("fund_account").equals(fundaccJosn.getString("fund_account"))){
//
//										if (fundaccJosn.getString("client_rights").contains("o")){
//											//检查白名单
//											if (stockholderSertice.isExistRytWhiteList(clientId, holderJson.getString("stock_account"))){
//												//存在白名单并且资产账户存在o-融资申购
//												strStatus[53] = '0';
//												String tcheckStr = Arrays.toString(strStatus).replaceAll("[\\[\\]\\s,]", "");
//												holderJson.put("check_str", tcheckStr);
//												String mayBeCancel = StringUtils.checkMayBeCancel(tcheckStr, configCancelStr);
//												holderJson.put("may_be_cancel", mayBeCancel);
//											}
//										}
//										break;
//									}
//								}
//							}
//						}
//					}
//					//BOPS-17584:20210818,hanjin,add,禁止股转转板过入账户进行销户
//					if (configCancelStr.contains(",198,")){
//						String holderCheckStr = holderJson.getString("check_str");
//						if (StringUtils.isNotBlank(holderCheckStr)){
//							char[] strStatus = holderCheckStr.toCharArray();
//							if (strStatus[197] == '0'){
//								int lastDate = csdcStbTransAMapper.getNewestInitDate();
//								List<CsdcStbTransA> csdcTransInAcctlist = csdcStbTransAMapper.qryForList(lastDate, "", "", "", holderJson.getString("stock_account"));
//								if(!csdcTransInAcctlist.isEmpty() && csdcTransInAcctlist.size() > 0) {
//									//当前股东卡在股转过入账户中，进行销户
//									strStatus[197] = '1';
//									String tcheckStr = Arrays.toString(strStatus).replaceAll("[\\[\\]\\s,]", "");
//									holderJson.put("check_str", tcheckStr);
//									String mayBeCancel = StringUtils.checkMayBeCancel(tcheckStr, configCancelStr);
//									holderJson.put("may_be_cancel", mayBeCancel);
//								}
//							}
//						}
//					}
//				}
//			}
//			if(isType){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			//状态是否正常
//			String clientStatus = eligJson.getJSONObject("client").getString("client_status");
//			if(clientStatus.equals(SysdictionaryConst.CNST_CLIENT_STATUS_NORMAL)){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			//证件过期
//			Map<String,String> sysarg = memCache.getSysarg();
//			Integer initDate = Integer.valueOf(sysarg.get("init_date").toString());
//			Integer idEnddate = Integer.valueOf(StringUtils.NVL(eligJson.getJSONObject("client").getString("id_enddate"),"0"));
//			if (initDate <= idEnddate){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			//职业学历
//			String degreeCode = eligJson.getJSONObject("clientinfo").getString("degree_code");
//			String professionCode = eligJson.getJSONObject("clientinfo").getString("profession_code");
//			if(StringUtils.isNotBlank(degreeCode) && StringUtils.isNotBlank(professionCode)){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			//个人税收居民身份申明是否签署
//			//个人税收居民身份申明是否签署查gfuser.gfuser.persontaxinfo mdf by fanlei 20190111
//			String organFlag = data.getJSONObject("client").getString("organ_flag");
//			Boolean isTax = false;
//			JSONObject taxJson = outerInterfaceService.qryTaxinfoByKey(clientId, organFlag);
//			if(null != taxJson){
//				if(taxJson.getJSONObject("data") != null){
//					if(taxJson.getJSONObject("data").getString("taxStateFlag").equals("1")){
//						isTax = true;
//					}
//				}
//			}
//
//			if(isTax){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			//add by fanlei 20190128 BOPS-6440 增加第六位个人客户纳税身份声明是否签署
//			if(outerInterfaceService.isFatcainfo(clientId)){
//				checkStrTotal = checkStrTotal.concat("1");
//			}else{
//				checkStrTotal = checkStrTotal.concat("0");
//			}
//
//			data.put("checkStrTotal", checkStrTotal);
//			//add by fanlei 20190109 BOPS-6347 - END
//
//		} catch (BopsException e) {
//			logger.info(e.getErrMessage());
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("客户股东账户信息树形查询失败");
//			return extCommonResponseForm;
//		}catch (Exception e1){
//			logger.error("客户股东账户信息树形查询失败", e1);//BOPS-14035：日志优化
//			extCommonResponseForm.setData(data);
//			extCommonResponseForm.setErrorNo("500");
//			extCommonResponseForm.setErrorInfo(e1.getMessage());
//			extCommonResponseForm.setRemark("客户股东账户信息树形查询失败");
//			// return extCommonResponseForm;//BOPS-14035：假成功问题；BOPS-14108：避免周边影响
//		}
//		extCommonResponseForm.setData(data);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20181227
//	 * @Description 金钥匙挽留客户
//	 * @Link   BOPS-6233**/
//	public CommonResponseForm<String> JYSDetainment(String joinBusinessId, String erpId, String remark, String isSuccess) throws BopsException
//	{
//		if(StringUtils.isBlank(joinBusinessId) || StringUtils.isBlank(erpId)){
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "joinBusinessId或erpId值输入非法，不能为空！");
//		}
//		CommonResponseForm<String> extCommonResponseForm = new CommonResponseForm<String>();
//		String opRemark = "";
//		Map<String, Object> acptform = acptDao.qryBusinAcptformByJoinId(joinBusinessId);
//		if(acptform != null){
//			if(!acptform.get("acptStatus").equals("13")){
//				throw new BopsException(BopsException.ERR_PARAM_INVALID, String.format("joinBusinessId[%s]的状态[%s]非法！",joinBusinessId, acptform.get("acptStatus")));
//			}
//			Integer opBranchNo = Integer.valueOf(acptform.get("opBranchNo").toString());
//			String operatorNo = acptform.get("operatorNo").toString();
//			String opStation = acptform.get("opStation").toString();
//			String opEntrustWay = acptform.get("terminalType").toString();
//			Integer branchNo = Integer.valueOf(acptform.get("branchNo").toString());
//			String acptId = acptform.get("acptId").toString();
//			if(isSuccess.equals("0")){
//				opRemark = saveBatchTaskBusi(opBranchNo, operatorNo, opStation, opEntrustWay,
//						900900, 202006, branchNo, " ", acptId);
//				//更新备注,增加ERP编号信息
//				 /*CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, 1000067, opStation, opEntrustWay, branchNo, acptId, clientId, fundAccount
//							, fullName, organFlag, idKind, idNo, mobileTel, String.format("[%s]:erp_id=[%s]", remark,erpId), acptStatus, clearFlag
//							, businOpType, dateClear, terminalType);*/
//				CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, 1000067, opStation, opEntrustWay, branchNo, acptId, "", ""
//						, "", "", "", "", "", "", String.format("[%s]:erp_id=[%s]", remark,erpId), "15", ""
//						, "", 0, "");
//				logger.info(String.format("acpt_id=[%s]挽留失败，更新备注中erp编号信息;[%s]", acptId, retStringForm.getData()));
//			}else if(isSuccess.equals("1")){
//				CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, 1000067, opStation, opEntrustWay, branchNo, acptId, "", ""
//						, "", "", "", "", "", "", String.format("[%s]:erp_id=[%s]", remark,erpId), "14", ""
//						, "", 0, "");
//				opRemark = String.format("acpt_id=[%s]挽留成功，更新acpt_status状态", retStringForm.getData());
//
//				//BOPS-6418:20190123,hanjin,add,如果挽留成功，解除资产账户的限制“I-禁止银行存入”、“J-禁止柜台存入”，根据备注信息的特征码“非现场销户，录入待挽留信息，增加资产账号”
//				//BOPS-6494:20190215,hanjin,mod,解决解除限制的问题
//				String fundAccount = StringUtils.NVL(acptform.get("fundAccount"),"").toString().trim();
//				Map<String, Object> paramJson = new HashMap<String, Object>();
//				JSONObject retLast = new JSONObject();
//			    paramJson.put("fundAccount", fundAccount);
//			    paramJson.put("tzm", "非现场销户，录入待挽留信息，增加资产账号");
//				paramJson.put("remark", String.format("非现场销户，挽留成功，解除资产账号[%s]的限制", fundAccount));
//
//				/*add by fanlei 20190228 HOTFIX 1.1.1.190301*/
//				paramJson.put("op_entrust_way", opEntrustWay);
//				paramJson.put("op_branch_no", opBranchNo);
//				paramJson.put("operator_no", operatorNo);
//				paramJson.put("op_station", opStation);
//
//				String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_DELETEFUNDACCTRESTRICT);
//				try {
//					logger.info("paramJson = "+paramJson.toString());
//					retLast = new RestClient().postForJsonObj(uri, paramJson);
//					logger.info("非现场销户，挽留成功，解除资产账号限制处理" + retLast.toJSONString());
//				} catch (BopsException e) {
//					throw e;
//				} catch (Exception e) {
//					throw new BopsException("-1", e.getMessage());
//				}
//			}else{
//				throw new BopsException(BopsException.ERR_PARAM_INVALID, "isSuccess值输入非法！");
//			}
//		}
//		else{
//			opRemark = String.format("joinBusinessId = [%s] 无可处理数据", joinBusinessId);
//		}
//
//		extCommonResponseForm.setData(opRemark);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark(opRemark);
//
//		return extCommonResponseForm;
//	}
//
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20181227
//	 * @Description 开户费扣收
//	 * @note   该方法作废，后续使用新方法
//	 * @Link   BOPS-6231
//	 **/
//	@Deprecated
//	public CommonResponseForm<JSONObject> stkOpenfareTax(String clientId, String fundAccount, String enExchangeType, String enHolderKind, String enStockAccount, Integer totalFare, String remark
//			,String opEntrustWay, Integer opBranchNo, String operatorNo, String opStation ) throws BopsException
//	{
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		String[] exchangeTypeList = enExchangeType.split(",");
//		String[] holderKindList = enHolderKind.split(",");
//		String[] stockAccountList = enStockAccount.split(",");
//		if((exchangeTypeList.length != holderKindList.length) || (exchangeTypeList.length != stockAccountList.length)){
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "enExchangeType、enHolderKind、enStockAccount不能一一匹配！");
//		}
//		Integer totalfare = 0;
//		StringBuilder stkinforemark = new StringBuilder();
//		ArrayList<Map<String, Object>> listfareMap = new ArrayList<Map<String, Object>>();
//		for(int i=0; i<exchangeTypeList.length; i++){
//			String exchangeType = exchangeTypeList[i];
//			String holderKind = holderKindList[i];
//			String stockAccount = stockAccountList[i];
//			Integer currAcctFare = 0;
//			JSONObject clientinfo = outerInterfaceService.qryClientInfo(clientId);
//			String fixfareStr = memCache.getSysconfigValue(Integer.valueOf(StringUtils.NVL(clientinfo.get("branch_no"),"8888").toString())
//					,GfSysConfigContant.CNST_GF_SYSCONFIG_OPENFARE_STR.toString());
//			logger.info(String.format("传入的股东账号[%s]资产账号[%s]市场类别[%s]在营业部[%s]对应的费用配置串为[%s]", stockAccount, fundAccount, exchangeType
//					, clientinfo.get("branch_no").toString(), fixfareStr));
//			// fixfareStr格式形如：10:80;20:80;
//			if(!fixfareStr.contains(exchangeType+holderKind+":")){
//				throw new BopsException(BopsException.ERR_PARAM_INVALID, "开户费扣收配置异常，请检查！");
//			}
//			String[] fareentryList = fixfareStr.split(";");
//			for(int j=0;j<fareentryList.length;j++){
//				String fareentry = fareentryList[j];
//				String[] objects = fareentry.split(":");
//				if(objects[0].equals(exchangeType+holderKind)){
//					stkinforemark.append(String.format("股东账号[%s]资产账号[%s]市场类别[%s]收取的费用[%s];",stockAccount, fundAccount, exchangeType, objects[1]));
//					currAcctFare = Integer.valueOf(objects[1]);
//					//BOPS-6560:20190220,hanjin,mod,修正多个证券账户销户，正常更新开户流水表的开户费用问题
//					Map<String, Object> fareMap = new HashMap<String, Object>();
//
//					/*add by fanlei 20190228 HOTFIX 1.1.1.190301*/
//					fareMap.put("op_entrust_way", opEntrustWay);
//					fareMap.put("op_branch_no", opBranchNo);
//					fareMap.put("operator_no", operatorNo);
//					fareMap.put("op_station", opStation);
//
//					fareMap.put("openFare", currAcctFare);
//					fareMap.put("fundAccount", fundAccount);
//					fareMap.put("exchangeType", exchangeType);
//					fareMap.put("stockAccount", stockAccount);
//					//校验股东账户信息是否合法、开户费用是否已经收取
//					String qryurl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_INNER_GETBYACCTATTRIB);
//					Map<String,Object> qrymap = new  HashMap<String,Object>();
//					qrymap.put("fundAccount", fundAccount);
//					qrymap.put("exchangeType", exchangeType);
//					qrymap.put("stockAccount", stockAccount);
//					JSONObject jsonObject = new RestClient().getForJsonObj(qryurl, qrymap);
//					if(jsonObject.get("data") == null){
//						throw new BopsException(BopsException.ERR_PARAM_INVALID
//								, String.format("传入的股东账号[%s]资产账号[%s]市场类别[%s]在开户流水中不存在记录！", stockAccount, fundAccount, exchangeType));
//					}else{
//						JSONObject data = (JSONObject)jsonObject.get("data");
//						if(Double.valueOf(data.get("openFare").toString()) > 0){
//							throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_OPENSTKFARE_GET
//									, String.format("[%s]传入的股东账号[%s]资产账号[%s]市场类别[%s]，请检查！",GFErrorCode.ERR_NBOP_BUSIN_OPENSTKFARE_GET_INFO, stockAccount, fundAccount, exchangeType));
//						}
//					}
//					listfareMap.add(fareMap);
//					break;
//				}
//			}
//			totalfare = totalfare + currAcctFare;
//		}
//		logger.info("totalfare =" + totalfare);
//		logger.info("listfareMap = " + listfareMap);
//
//		//BOPS-6297:20190104,hanjin,add,增加对前端送入的总扣除费用校验
//		if(totalfare.intValue() != totalFare.intValue()){
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "传入的总扣除费用与后台计算待扣除费用不一致，请重新输入！");
//		}
//
//		//费用扣除
//		RestClient client =  new RestClient();
//		String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_GFFUND_CDEPUTY_FARE);
//		Map<String,Object> paramMap = new  HashMap<String,Object>();
//		paramMap.put("action_in", "1");
//		paramMap.put("client_id", clientId);
//		paramMap.put("fund_account", fundAccount);
//		paramMap.put("money_type", "0");
//		paramMap.put("occur_balance", totalfare);
//		paramMap.put("asset_prop", "0");
//		paramMap.put("remark", remark + ";" + stkinforemark);
//		JSONObject jsonObject = client.postForJsonObj(url, paramMap);
//
//		//根据fund_account,exchange_type,stock_account更新开户费用open_fare信息
//		String url2 = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_INNER_UPDATEOPENFARE);
//		JSONObject paramjson = new JSONObject();
//		paramjson.put("webopenaccountjours", listfareMap);
//		JSONObject jsonObject2= new RestClient().postJson2Json(url2, new HashMap<>(), paramjson);
//
//		extCommonResponseForm.setData(jsonObject);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("开户费扣收成功！");
//
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author yuyan
//	 * @Date 20210203
//	 * @Description 开户费扣收(基于stkOpenfareTax重构)
//	 * @Link BOPS-14554
//	 **/
//	public CommonResponseForm<JSONObject> stkOpenfareTax(String clientId, String fundAccount, String enExchangeType, String enHolderKind, String enStockAccount,
//			BigDecimal totalFare, String pointId, BigDecimal totalDeductFare, String remark, String opEntrustWay, Integer opBranchNo, String operatorNo, String opStation)
//			throws BopsException {
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		String[] exchangeTypeList = enExchangeType.split(",");
//		String[] holderKindList = enHolderKind.split(",");
//		String[] stockAccountList = enStockAccount.split(",");
//		if((exchangeTypeList.length != holderKindList.length) || (exchangeTypeList.length != stockAccountList.length)){
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "enExchangeType、enHolderKind、enStockAccount不能一一匹配！");
//		}
//		BigDecimal[] payByPoint = new BigDecimal[stockAccountList.length];
//		BigDecimal[] payByCash = new BigDecimal[stockAccountList.length];
//
//		JSONObject clientinfo = outerInterfaceService.qryClientInfo(clientId);
//		Map<String, BigDecimal> branchFareMap = getBranchOpenFare(StringUtils.NVL(clientinfo.getString("branch_no"), 8888));
//
//		String[] pointParams = memCache.getSysconfigValue(8888, GfSysConfigContant.CNST_GF_SYSCONFIG_POINT_PARAM.toString()).split(",");
//
//		BigDecimal totalPoint = BigDecimal.ZERO;
//		if (totalDeductFare.compareTo(BigDecimal.ZERO) > 0) {// 使用积分抵扣
//			totalPoint = totalDeductFare.multiply(new BigDecimal(pointParams[0]));
//		}
//		BigDecimal pointFareToDistribute = BigDecimal.ZERO.add(totalDeductFare);
//
//		BigDecimal checkTotalFare = BigDecimal.ZERO;
//		StringBuilder stkinforemark = new StringBuilder();
//		for(int i=0; i<exchangeTypeList.length; i++){
//			String exchangeType = exchangeTypeList[i];
//			String holderKind = holderKindList[i];
//			String stockAccount = stockAccountList[i];
//			BigDecimal branchOpenFare = BigDecimal.ZERO;
//
//			if (!branchFareMap.containsKey(exchangeType + holderKind)) {
//				throw new BopsException(BopsException.ERR_PARAM_INVALID, "开户费扣收配置异常，请检查！");
//			}
//			branchOpenFare = branchFareMap.get(exchangeType + holderKind);
//
//			// 校验股东账户信息是否合法、开户费用是否已经收取
//			String qryurl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_INNER_GETBYACCTATTRIB);
//			Map<String, Object> qrymap = new HashMap<String, Object>();
//			qrymap.put("fundAccount", fundAccount);
//			qrymap.put("exchangeType", exchangeType);
//			qrymap.put("stockAccount", stockAccount);
//			JSONObject jsonObject = new RestClient().getForJsonObj(qryurl, qrymap);
//			if (jsonObject.get("data") == null) {
//				throw new BopsException(BopsException.ERR_PARAM_INVALID, String.format("传入的股东账号[%s]资产账号[%s]市场类别[%s]在开户流水中不存在记录！", stockAccount, fundAccount, exchangeType));
//			} else {
//				JSONObject data = (JSONObject) jsonObject.get("data");
//				BigDecimal payedFare = data.getBigDecimal("openFare");
//				BigDecimal deductFare = data.getBigDecimal("deductFare");
//				if (payedFare.add(deductFare).compareTo(branchOpenFare) >= 0) {// 费用已收齐
//					payByPoint[i] = BigDecimal.ZERO;
//					payByCash[i] = BigDecimal.ZERO;
//					continue;
//				}
//				BigDecimal toPay = branchOpenFare.subtract(payedFare).subtract(deductFare);
//				checkTotalFare = checkTotalFare.add(toPay);
//				if (pointFareToDistribute.compareTo(BigDecimal.ZERO) > 0) {
//					if (pointFareToDistribute.compareTo(toPay) >= 0) {
//						payByPoint[i] = toPay;
//						payByCash[i] = BigDecimal.ZERO;
//						pointFareToDistribute = pointFareToDistribute.subtract(toPay);
//					} else {
//						payByPoint[i] = BigDecimal.ZERO.add(pointFareToDistribute);
//						payByCash[i] = toPay.subtract(pointFareToDistribute);
//						pointFareToDistribute = BigDecimal.ZERO;
//					}
//					if (payByPoint[i].compareTo(new BigDecimal(pointParams[1])) < 0) {
//						throw new BopsException(GFErrorCode.ERR_BOPS_PARAM_ILLEGAL, "payByPoint < limit", payByPoint[i]);
//					}
//				} else {
//					payByPoint[i] = BigDecimal.ZERO;
//					payByCash[i] = toPay;
//				}
//				if (payByCash[i].compareTo(BigDecimal.ZERO) > 0) {
//					stkinforemark.append("股东账号[").append(stockAccount).append("]资产账号[").append(fundAccount).append("]市场类别[").append(exchangeType).append("]收取的费用[")
//							.append(payByCash[i]).append(']').append(';');
//				}
//			}
//		}
//		logger.info2("totalFare = {}, distributeFare = {}", checkTotalFare, pointFareToDistribute);
//		// BOPS-6297:20190104,hanjin,add,增加对前端送入的总扣除费用校验
//		if (totalFare.compareTo(checkTotalFare) != 0) {
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "传入的总扣除费用与后台计算待扣除费用不一致，请重新输入！");
//		}
//		if (pointFareToDistribute.compareTo(BigDecimal.ZERO) > 0) {
//			throw new BopsException(BopsException.ERR_PARAM_INVALID, "积分抵扣金额过大！");
//		}
//
//		// 积分扣除
//		if (totalDeductFare.compareTo(BigDecimal.ZERO) > 0) {
//			pointService.withdraw(pointId, clientId, totalPoint, StringUtils.NVL(remark, "抵扣开户费用"));
//			// 更新积分抵扣的开户流水（先更新防止扣款时异常无更新）
//			updateOpenJour(opEntrustWay, opBranchNo, operatorNo, opStation, fundAccount, stockAccountList, exchangeTypeList, null, payByPoint, pointId);
//			extCommonResponseForm.setRemark("开户费积分抵扣成功！");
//		}
//
//		if (totalDeductFare.compareTo(totalFare) < 0) {// 不够全部抵扣
//			// 费用扣除
//			RestClient client = new RestClient();
//			String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_GFFUND_CDEPUTY_FARE);
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("action_in", "1");
//			paramMap.put("client_id", clientId);
//			paramMap.put("fund_account", fundAccount);
//			paramMap.put("money_type", "0");
//			paramMap.put("occur_balance", totalFare.subtract(totalDeductFare));
//			paramMap.put("asset_prop", "0");
//			paramMap.put("remark", remark + ";" + stkinforemark);
//			JSONObject jsonObject = client.postForJsonObj(url, paramMap);
//			logger.debug2("扣款更新返回：{}", jsonObject);
//
//			// 更新开户流水付款情况
//			updateOpenJour(opEntrustWay, opBranchNo, operatorNo, opStation, fundAccount, stockAccountList, exchangeTypeList, payByCash, null, null);
//
//			extCommonResponseForm.setData(jsonObject);
//			extCommonResponseForm.setRemark("开户费扣收成功！");
//		}
//
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//
//		return extCommonResponseForm;
//	}
//
//	private void updateOpenJour(String opEntrustWay, Integer opBranchNo, String operatorNo, String opStation, String fundAccount, String[] stockAccountList,
//			String[] exchangeTypeList, BigDecimal[] payByCash, BigDecimal[] payByPoint, String pointId) throws BopsException {
//
//		String updateOpenJourUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_INNER_UPDATEOPENFARE); // 根据fund_account,exchange_type,stock_account更新开户费用open_fare信息
//
//		StringBuilder remarkBuilder = new StringBuilder();
//		if (payByPoint != null) {
//			remarkBuilder.append(StringUtils.getNowStr()).append("积分事件uuid[").append(pointId).append(']').append("使用积分抵扣费用");
//		}
//
//		ArrayList<Map<String, Object>> jourLists = new ArrayList<Map<String, Object>>();
//		for (int i = 0; i < stockAccountList.length; i++) {
//			Map<String, Object> fareMap = new HashMap<String, Object>();
//			/* add by fanlei 20190228 HOTFIX 1.1.1.190301 */
//			fareMap.put("op_entrust_way", opEntrustWay);
//			fareMap.put("op_branch_no", opBranchNo);
//			fareMap.put("operator_no", operatorNo);
//			fareMap.put("op_station", opStation);
//
//			fareMap.put("fundAccount", fundAccount);
//			fareMap.put("exchangeType", exchangeTypeList[i]);
//			fareMap.put("stockAccount", stockAccountList[i]);
//			if (payByCash != null && payByCash[i].compareTo(BigDecimal.ZERO) > 0) {
//				fareMap.put("openFare", payByCash[i]);
//				jourLists.add(fareMap);
//			}
//			if (payByPoint != null && payByPoint[i].compareTo(BigDecimal.ZERO) > 0) {
//				fareMap.put("deductFare", payByPoint[i]);
//				fareMap.put("remark", remarkBuilder.toString() + payByPoint[i].toPlainString() + ",");
//				jourLists.add(fareMap);
//			}
//		}
//		JSONObject paramjson = new JSONObject();
//		paramjson.put("webopenaccountjours", jourLists);
//		JSONObject result= new RestClient().postJson2Json(updateOpenJourUrl, new HashMap<>(), paramjson);
//		logger.debug2("开户流水更新返回：{}", result);
//	}
//
//	/**
//	 * 获取营业部的开户费用设置
//	 * @return:Map<exchangeKind,openFare>
//	 */
//	private Map<String, BigDecimal> getBranchOpenFare(int branchNo) throws BopsException {
//		Map<String, BigDecimal> fareMap = new HashMap<>();
//
//		String fixfareStr = memCache.getSysconfigValue(branchNo, GfSysConfigContant.CNST_GF_SYSCONFIG_OPENFARE_STR.toString());
//		logger.info2("在营业部[{}]对应的费用配置串为[{}]", branchNo, fixfareStr);
//		// fixfareStr格式形如：10:80;20:80;
//		for (String fareEntry : fixfareStr.split(";")) {
//			String[] objects = fareEntry.split(":");
//			fareMap.put(objects[0], new BigDecimal(objects[1]));
//		}
//
//		return fareMap;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20190107
//	 * @Description 非现场销户受理提交
//	 * @Link   BOPS-6317**/
//	public CommonResponseForm<Map<String,Object>> cancelAcctSubmitExt(String clientId, String acptId
//			,String opEntrustWay, Integer opBranchNo, String operatorNo, String opStation) throws BopsException
//	{
//		CommonResponseForm<Map<String,Object>> extCommonResponseForm = new CommonResponseForm<Map<String,Object>>();
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setData(null);
//		extCommonResponseForm.setRemark(String.format("当期acpt_id[%s]对应无业务数据，无需更新状态为待挽留", acptId));
//		DataPageResponse datapage = acptDao.getBusinAcptformFuzzy2("", clientId, "", "", "", "", "900900", "", "", 1, 1,"a.acpt_id desc");
//		List<Map<String,Object>> data = datapage.getData();
//		if(!data.isEmpty()){
//			Map<String,Object> lastbusin = data.get(0);
//			BopsException bopsexp = new BopsException();
//			if(lastbusin.get("acptStatus").equals("13")){//待挽留
//				bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//				bopsexp.setErrMessage(String.format("已存在acpt_id[%s]的待挽留业务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//				bopsexp.setRestData(lastbusin);
//				throw bopsexp;
//			}else if(lastbusin.get("acptStatus").equals("14")){//挽留成功
//				//允许
//			}else if(lastbusin.get("acptStatus").equals("9")){//复核拒绝
//				//允许
//			}else if(lastbusin.get("acptStatus").equals("15") || lastbusin.get("acptStatus").equals("6")){//挽留失败+处理中
//				//查询当前业务对应的跑批任务状态
//				DataPageResponse currTaskPage = batchtaskManagerDao.getCurrBatchtask("", "", "","900900", "",
//						"", "", clientId, lastbusin.get("acptId").toString(),1, 1 ,"a.acpt_id");
//				List<Map<String, Object>> currTaskList = currTaskPage.getData();
//				if(!currTaskList.isEmpty()){
//					Map<String, Object> currTask = currTaskList.get(0);
//					if(currTask.get("execStatus").toString().equals("3")){
//						//跑批任务处理失败，对于T+1场景不允许，其他则允许
//						DataPageResponse currSubTaskPage = batchtaskManagerDao.getBatchSubtask("", "", "900900", "",
//								"", clientId, lastbusin.get("acptId").toString(),1, 10 ,"a.acpt_id");
//						List<Map<String, Object>> currSubTaskList = currSubTaskPage.getData();
//						for(int i=0;i<currSubTaskList.size();i++){
//							Map<String, Object> currSubTask = currSubTaskList.get(i);
//							if(currSubTask.get("remark").toString().contains("当日有撤销指定交易，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("当日撤销指定处理成功，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("当日有资金操作或结息，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("该账号当天有资金业务流水不能销户")
//									//BOPS-6926:20190328,hanjin,add
//									|| currSubTask.get("remark").toString().contains("当前时间不允许此类证券交易")
//									|| currSubTask.get("remark").toString().contains("当前时间不允许委托")
//									|| currSubTask.get("remark").toString().contains("系统开关4102不允许当天有撤销指定的沪A帐户销户")
//									|| currSubTask.get("remark").toString().contains("存管当日开户禁止销户")
//									//BOPS-9371:20200106,hanjin,add,增加股东卡销户失败的场景判断，对应的错误号1000010
//									|| currSubTask.get("remark").toString().contains(GFErrorCode.ERR_NBOP_BUSIN_TPLUS1_OTHERERROR_INFO)){
//								//复核T+1的场景，不允许
//								bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//								bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且存在当日不能销户的跑批子任务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//								bopsexp.setRestData(lastbusin);
//								throw bopsexp;
//							}
//						}
//					}else if(currTask.get("execStatus").toString().equals("2")){
//						//跑批任务处理成功，允许
//					}else{
//						//正在处理中或其他状态，不允许
//						bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//						bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且跑批任务状态[%s]不正常，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), currTask.get("execStatus").toString(), acptId));
//						bopsexp.setRestData(lastbusin);
//						throw bopsexp;
//					}
//				}else{
//					//不存在跑批任务，不允许
//					bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//					bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且该业务暂未生成跑批任务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//					bopsexp.setRestData(lastbusin);
//					throw bopsexp;
//				}
//			}else{
//				//允许
//			}
//			extCommonResponseForm.setData(data.get(0));
//		}
//
//		//更新当前传入的acptId的状态为13
//		DataPageResponse datapage2 = acptDao.getBusinAcptformFuzzy2(acptId, clientId, "", "", "", "", "900900", "", "", 1, 1,"a.acpt_id desc");
//		List<Map<String, Object>> datapage2list = datapage2.getData();
//		if(!datapage2list.isEmpty()){
//			CommonResponseForm<String> retStringForm = updateBusinAcptform(0, "", 1000069, "", "", 0, acptId, "", ""
//					, "", "", "", "", "", "", String.format("录入待挽留业务信息acpt_id[%s]", acptId), "13", ""
//					, "", 0, "");
//			extCommonResponseForm.setRemark(String.format("录入待挽留业务信息acpt_id[%s],更新状态为[%s]", acptId, "13"));
//
//			//BOPS-6418:20190123,hanjin,add,如果录入待挽留信息，增加资产账户的限制“I-禁止银行存入”、“J-禁止柜台存入”
//			Map<String,Object> businacptform = acptDao.qryBusinAcptformById (acptId);
//			//BOPS-6747:20190311,hanjin,mod,仅对彻底销户的才增加资产账号的I/J限制
//			Map<String, Map<String, Object>> retMap = abstractServiceForJob.loadAcptformInfo(acptId, 0);
//			Map<String, Object> acptformargMap = retMap.get(AbstractServiceForJob.ACPT_FORM_ARG);
//			if(StringUtils.NVL(acptformargMap.get("isCompleteCancel"), "").toString().equals("1")){
//				String fundAccount = StringUtils.NVL(businacptform.get("fundAccount"),"").toString().trim();
//				Map<String, Object> paramJson = new HashMap<String, Object>();
//				JSONObject retLast = new JSONObject();
//			    paramJson.put("fund_account", fundAccount);
//				paramJson.put("remark", String.format("非现场销户，录入待挽留信息，增加资产账号[%s]的限制", fundAccount));
//				paramJson.put("valid_date", "30001231");
//
//				/*add by fanlei 20190228 HOTFIX 1.1.1.190301*/
//				paramJson.put("op_entrust_way", opEntrustWay);
//				paramJson.put("op_branch_no", opBranchNo);
//				paramJson.put("operator_no", operatorNo);
//				paramJson.put("op_station", opStation);
//
//				String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_ADDFUNDACCTRESTRICT);
//				//添加“I-禁止银行存入”的限制
//				paramJson.put("single_restriction", "I");
//				try {
//					logger.info("paramJson = "+paramJson.toString());
//					retLast = new RestClient().postForJsonObj(uri, paramJson);
//					logger.info("非现场销户，录入待挽留信息，增加资产账号限制处理" + retLast.toJSONString());
//				} catch (BopsException e) {
//					throw e;
//				} catch (Exception e) {
//					throw new BopsException("-1", e.getMessage());
//				}
//				//添加“J-禁止柜台存入”的限制
//				paramJson.put("single_restriction", "J");
//				try {
//					logger.info("paramJson = "+paramJson.toString());
//					retLast = new RestClient().postForJsonObj(uri, paramJson);
//					logger.info("非现场销户，录入待挽留信息，增加资产账号限制处理" + retLast.toJSONString());
//				} catch (BopsException e) {
//					throw e;
//				} catch (Exception e) {
//					throw new BopsException("-1", e.getMessage());
//				}
//			}
//		}
//
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20190108
//	 * @Description 非现场销户受理预提交
//	 * @Link   BOPS-6317**/
//	public CommonResponseForm<Map<String,Object>> preCancelAcctSubmitExt(String clientId, String acptId) throws BopsException
//	{
//		CommonResponseForm<Map<String,Object>> extCommonResponseForm = new CommonResponseForm<Map<String,Object>>();
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("检查通过！");
//		extCommonResponseForm.setData(null);
//		DataPageResponse datapage = acptDao.getBusinAcptformFuzzy2("", clientId, "", "", "", "", "900900", "", "", 1, 1,"a.acpt_id desc");
//		List<Map<String,Object>> data = datapage.getData();
//		if(!data.isEmpty()){
//			Map<String,Object> lastbusin = data.get(0);
//			BopsException bopsexp = new BopsException();
//			if(lastbusin.get("acptStatus").equals("13")){//待挽留
//				bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//				bopsexp.setErrMessage(String.format("已存在acpt_id[%s]的待挽留业务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//				bopsexp.setRestData(lastbusin);
//				throw bopsexp;
//			}else if(lastbusin.get("acptStatus").equals("14")){//挽留成功
//				//允许
//			}else if(lastbusin.get("acptStatus").equals("9")){//复核拒绝
//				//允许
//			}else if(lastbusin.get("acptStatus").equals("15") || lastbusin.get("acptStatus").equals("6")){//挽留失败+处理中
//				//查询当前业务对应的跑批任务状态
//				DataPageResponse currTaskPage = batchtaskManagerDao.getCurrBatchtask("", "", "","900900", "",
//						"", "", clientId, lastbusin.get("acptId").toString(),1, 1 ,"a.acpt_id");
//				List<Map<String, Object>> currTaskList = currTaskPage.getData();
//				if(!currTaskList.isEmpty()){
//					Map<String, Object> currTask = currTaskList.get(0);
//					logger.info("currTask = " + currTask.toString());
//					if(currTask.get("execStatus").toString().equals("3")){
//						//跑批任务处理失败，对于T+1场景不允许，其他则允许
//						DataPageResponse currSubTaskPage = batchtaskManagerDao.getBatchSubtask("", "", "900900", "",
//								"", clientId, lastbusin.get("acptId").toString(),1, 10 ,"a.acpt_id");
//						List<Map<String, Object>> currSubTaskList = currSubTaskPage.getData();
//						for(int i=0;i<currSubTaskList.size();i++){
//							Map<String, Object> currSubTask = currSubTaskList.get(i);
//							if(currSubTask.get("remark").toString().contains("当日有撤销指定交易，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("当日撤销指定处理成功，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("当日有资金操作或结息，当日不能销户")
//									|| currSubTask.get("remark").toString().contains("该账号当天有资金业务流水不能销户")
//									//BOPS-6926:20190328,hanjin,add
//									|| currSubTask.get("remark").toString().contains("当前时间不允许此类证券交易")
//									|| currSubTask.get("remark").toString().contains("当前时间不允许委托")
//									|| currSubTask.get("remark").toString().contains("系统开关4102不允许当天有撤销指定的沪A帐户销户")
//									|| currSubTask.get("remark").toString().contains("存管当日开户禁止销户")
//									//BOPS-9371:20200106,hanjin,add,增加股东卡销户失败的场景判断，对应的错误号1000010
//									|| currSubTask.get("remark").toString().contains(GFErrorCode.ERR_NBOP_BUSIN_TPLUS1_OTHERERROR_INFO)){
//								//复核T+1的场景，不允许
//								bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//								bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且存在当日不能销户的跑批子任务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//								bopsexp.setRestData(lastbusin);
//								throw bopsexp;
//							}
//						}
//					}else if(currTask.get("execStatus").toString().equals("2")){
//						//跑批任务处理成功，允许
//					}else{
//						//正在处理中或其他状态，不允许
//						bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//						bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且跑批任务状态[%s]不正常，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), currTask.get("execStatus").toString(), acptId));
//						bopsexp.setRestData(lastbusin);
//						throw bopsexp;
//					}
//				}else{
//					//不存在跑批任务，不允许
//					bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//					bopsexp.setErrMessage(String.format("已经存在挽留失败acpt_id[%s]的业务，且该业务暂未生成跑批任务，请勿重复提交当前acpt_id[%s]", lastbusin.get("acptId").toString(), acptId));
//					bopsexp.setRestData(lastbusin);
//					throw bopsexp;
//				}
//			}else{
//				//允许
//			}
//			extCommonResponseForm.setData(data.get(0));
//		}
//
//		return extCommonResponseForm;
//	}
//
//	public JSONObject setFatcaInfoExt(String opStation, String opEntrustWay, Integer opBranchNo, String operatorNo, Integer actionIn, Fatcainfo fatcainfo) throws BopsException{
//
//		String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_SETFATCAINFO);
//
//		JSONObject fatcainfoJson = (JSONObject) JSON.toJSON(fatcainfo);
//		HashMap<String, Object> fatcainfoMap = JSON.parseObject(fatcainfoJson.toJSONString(),new HashMap<String, Object>().getClass());
//
//		Map<String, Object> uriParam = new HashMap<String, Object>();
//		uriParam.put("opStation", opStation);
//		uriParam.put("opEntrustWay", opEntrustWay);
//		uriParam.put("opBranchNo", opBranchNo);
//		uriParam.put("operatorNo", operatorNo);
//		uriParam.put("actionIn", actionIn);
//		logger.info("uriParam = " + uriParam.toString());
//
//		RestClient restTemplate = new RestClient.RestClientBuilder().clearMessageConverters()
//					.addHttpMessageConverter(new ByteArrayHttpMessageConverter()).clearDefaultHttpHeaders().build();
//		HttpHeaders headers = new HttpHeaders();
//		MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
//		headers.setContentType(type);
//		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<Map<String, Object>>(fatcainfoMap,headers);
//		ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.POST, headers, fatcainfoMap, JSONObject.class, uriParam);
//
//		if (responseEntity.getStatusCode() == HttpStatus.OK) {
//			logger.info("周边调用个人纳税信息设置成功");
//			return responseEntity.getBody();
//		}
//		else{
//			logger.info("周边调用个人纳税信息设置失败");
//			return null;
//		}
//	}
//
//	//BOPS-7424:20190530,hanjin,mod,修改不同场景下的错误号、错误信息
//	public JSONObject checkActiveAccountExt(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String terminalType, Integer branchNo, String clientId, String idKind, String idNo, String clientName) throws BopsException{
//		//1.判断提交客户信息是否存在待激活的客户编号
//		Map<String, Object> uriParam = new HashMap<String, Object>();
//		uriParam.put("idNo", idNo);
//		uriParam.put("idKind", idKind);
//		uriParam.put("isTotal", "1");
//		String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CLIENTFROMID_GET);
//		ArrayList<Map> clientids = new RestClient().getForMapList(url, uriParam);
//		//BOPS-7465:20190612,hanjin,mod,优化客户编号的校验
//		boolean cidIsFinded = false;
//		for(int i=0;i<clientids.size();i++){
//			Map clientid = clientids.get(i);
//			logger.info("clientid = " + clientid.toString());
//			if((clientid.get("client_status").toString().equals("C") || clientid.get("client_status").toString().equals("G"))
//					&& clientid.get("client_id").toString().equals(clientId)
//					&& clientid.get("client_name").toString().equals(clientName)){
//				//存在符合的客户信息
//				cidIsFinded = true;
//				break;
//			}
//		}
//		if(!cidIsFinded){
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CLIENT_INFO_NOTMATCH ,String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CLIENT_INFO_NOTMATCH_INFO , idKind, idNo, clientId, clientName));
//		}
//
//		//2.判断是否存在待激活的股东账号(110-第一位股东账户，第二位基金账户，第三位银行账户)
//		JSONObject clientaccounts = qryClientAccountsExt(clientId, "", "110", "0", "0", "0", "0", "0").getData();
//		logger.info("clientaccounts = " + clientaccounts.toJSONString());
//		JSONArray fundAccounts = clientaccounts.getJSONArray("fundAccount");
//		JSONArray stockHolders = clientaccounts.getJSONArray("stockHolder");
//		JSONArray secumHolders = clientaccounts.getJSONArray("secumHolder");
//		int noCancelFundacct = 0;
//		if(null != fundAccounts && fundAccounts.size() > 0 ){
//			for(int i = 0; i < fundAccounts.size(); i++){
//				JSONObject fundAccountJson = (JSONObject) JSON.toJSON(fundAccounts.get(i));
//				if(!fundAccountJson.getString("fundacct_status").equals("C") && !fundAccountJson.getString("fundacct_status").equals("G")){
//					noCancelFundacct++;
//				}
//				//2.0 BOPS-7435:20190610,hanjin,add,禁止非销户状态的辅助资产账号进行业务办理
//				if(fundAccountJson.getString("main_flag").equals("0") && !fundAccountJson.getString("fundacct_status").equals("3")){
//					throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_ASSISTFUNDACCT_NOLOGOUT_EXISTS, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_ASSISTFUNDACCT_NOLOGOUT_EXISTS_INFO,clientId));
//				}
//			}
//		}
//		//2.1如果客户有多个非休眠的资产账户,则报错
//		if(noCancelFundacct >= 2){
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_NOCANCEL_FUNDACCT_OVERNUM, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_NOCANCEL_FUNDACCT_OVERNUM_INFO,clientId));
//		}
//		//2.2如果客户不存在股东账号,则报错
//		//BOPS-7120:20190507,wangxp,mod,支持有金融账户或者股东账户，则不报错
////		if(null != stockHolders && stockHolders.size() == 0 ){
////			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_MATCHED_CLIENT_NOTEXISTS, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_MATCHED_CLIENT_NOTEXISTS_INFO + "[clientId=%s],[idKind=%s],[idNo=%s],[clientName=%s],不存在证券账号",clientId, idKind, idNo, clientName));
////		}
////		//2.3如果客户不存在多金融账号,则报错
////		if(null != secumHolders && secumHolders.size() == 0 ){
////			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_MATCHED_CLIENT_NOTEXISTS, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_MATCHED_CLIENT_NOTEXISTS_INFO + "[clientId=%s],[idKind=%s],[idNo=%s],[clientName=%s],不存在多金融账号",clientId, idKind, idNo, clientName));
////		}
////
//		if(null != stockHolders && stockHolders.size() == 0 && null != secumHolders && secumHolders.size() == 0 )
//		{
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_HOLDERACCOUNT_NOTEXISTS, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_HOLDERACCOUNT_NOTEXISTS_INFO, clientId));
//		}
//
//		//3.校验中登的股东账户是否满足条件
//		//3.1中登股东账户不能为空
//		//BOPS-8031:20190807,hanjin,mod
//		List<JSONObject> csdcStkaccts = qryCsdcStkacctListExt2(clientId, "", clientName, idKind, idNo, "", "", "", "", false);
//		//BOPS-7424:20190530,hanjin,add,增加对之前BOPS-7120任务的优化，只有本地股东卡有值，才需要保证中登卡必须存在数据
//		//if(csdcStkaccts == null || csdcStkaccts.size() == 0){
//		if((csdcStkaccts == null || csdcStkaccts.size() == 0) && (null != stockHolders && stockHolders.size() > 0) ){
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_NOTEXISTS, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_NOTEXISTS_INFO, clientId, idKind, idNo, clientName));
//		}
//		//3.2客户本地的证券账户需要都在中登股东账户列表中
//		List<JSONObject> csdcMatchStkacct = new ArrayList<JSONObject>();
//		for(int i=0;i<stockHolders.size();i++){
//			JSONObject stockholder = (JSONObject) JSON.toJSON(stockHolders.get(i));
//			boolean isFinded = false;
//			//BOPS-7752:20190711,hanjin,mod,counter-sonar扫描优化
//			if(csdcStkaccts != null){
//				for(int j=0;j<csdcStkaccts.size();j++){
//					JSONObject csdcStkacct = (JSONObject) JSON.toJSON(csdcStkaccts.get(j));
//					logger.info("csdcStkacct = " + csdcStkacct.toJSONString());
//					if(csdcStkacct.get("csdc_stock_account").toString().equals(stockholder.get("stock_account").toString())){
//						csdcMatchStkacct.add(csdcStkacct);
//						isFinded = true;
//						//BOPS-7335:20190521,hanjin,add,客户号下的股东账号在中登不允许为非正常状态且非休眠状态
//						if(!csdcStkacct.get("csdc_holder_status").equals("00") && !csdcStkacct.get("csdc_holder_status").equals("03")){    //00-正常、01-挂失、02-冻结、03-休眠、04-注销、05-禁买、06-禁卖
//							throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_CSDC_STOCKHOLDER_STATUS_NOALLOWED_ACTIVE, String.format(GFErrorCode.ERR_NBOP_BUSIN_CSDC_STOCKHOLDER_STATUS_NOALLOWED_ACTIVE_INFO + "[%s]", csdcStkacct.get("csdc_stock_account").toString()));
//						}
//						break;
//					}
//				}
//			}
//			if(!isFinded){
//				throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_NOTMATCH, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_NOTMATCH_INFO, clientId, idKind, idNo, clientName));
//			}
//		}
//		//3.3客户本地的证券账户在中登股东账户列表中,是否已经存在状态正常的户数<=3
//		for(int i=0;i<stockHolders.size();i++){
//			JSONObject stockholder = (JSONObject) JSON.toJSON(stockHolders.get(i));
//			String csdcHolderKind = CSDCDictMappingUtils.getCSDCHolderKind(stockholder.get("exchange_type").toString()
//					, stockholder.get("holder_kind").toString()
//					, stockholder.get("asset_prop").toString().equals("7")?true : false);
//			int commonKindStkacct = 0;
//			boolean isCurrCsdcStatusNormal = false;
//			//BOPS-7752:20190711,hanjin,mod,counter-sonar扫描优化
//			if(csdcStkaccts != null){
//				for(int j=0;j<csdcStkaccts.size();j++){
//					JSONObject csdcStkacct = (JSONObject) JSON.toJSON(csdcStkaccts.get(j));
//					logger.info("csdcStkacct = " + csdcStkacct.toJSONString());
//					if(csdcStkacct.get("csdc_holder_kind").toString().equals(csdcHolderKind) && csdcStkacct.get("csdc_holder_status").equals("00")){
//						commonKindStkacct++;
//						//BOPS-7709:20190708,hanjin,add,同一个市场证券账户，对于中登状态是正常的，但柜台状态是内部休眠的，即使客户中登的正常账户已经超过三个， 也不用拦截激活柜台休眠状态。
//						if(csdcStkacct.get("csdc_stock_account").toString().equals(stockholder.get("stock_account").toString())){
//							isCurrCsdcStatusNormal = true;
//						}
//					}
//				}
//			}
//			//BOPS-7709:20190708,hanjin,mod,只有中登非正常状态，才校验同市场正常账号不能超过三个
//			if(commonKindStkacct >= 3 && !isCurrCsdcStatusNormal){
//				throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_OVERNUM, String.format(GFErrorCode.ERR_NBOP_EXTCENTER_ACTIVE_CSDCSTKACCT_OVERNUM_INFO, clientId, idKind, idNo, clientName, stockholder.get("stock_account").toString(), commonKindStkacct));
//			}
//		}
//
//		//BOPS-7970:20190806,hanjin,mod,解决特转账户与普通账户在本地证券账户中同时存在，中登返回的账户信息包含相同数据的问题
//		HashSet h = new HashSet(csdcMatchStkacct);
//		csdcMatchStkacct.clear();
//		csdcMatchStkacct.addAll(h);
//		clientaccounts.put("csdcMatchStkacct", csdcMatchStkacct);
//
//		//BOPS-8409:20190909,hanjin,add,周边休眠激活接口优化，增加对休眠激活前客户的状态返回(如有)
//		List<Lockactive> lockactiveList = lockactiveService.getUnhandleClient(clientId);
//		if(!lockactiveList.isEmpty()) {
//			clientaccounts.put("lockactivePreStatus", lockactiveList.get(0).getClientStatus());
//		}else {
//			clientaccounts.put("lockactivePreStatus", "");
//		}
//		return clientaccounts;
//	}
//
//	public List<Map<String, Object>> qryBusinAcptformList(String clientId, String businOpType){
//		ClientUpdateForm qryForm = new ClientUpdateForm();
//		qryForm.setClientId(clientId);
//		qryForm.setBusinOpType(businOpType);
//		return  acptDao.qryBusinAcptformList(qryForm);
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20190515
//	 * @Description 科创板回访提交接口
//	 * @Link   BOPS-7289**/
//	public CommonResponseForm<JSONObject> KCBReturnVisit(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String acptId, String acptStatus, ReturnVisitor opContent) throws BopsException{
//
//		char config_1000089 = memCache.getSysconfigCharValue(8888, GfSysConfigContant.CNST_GF_SYSCONFIG_KCBSZHFJZ_CHAR.toString());
//		if (config_1000089 != '1'){
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_CONFIG_NOT_EFFECT, "科创板回访机制未启用，禁止调用此接口！");
//		}
//
//		Map<String,Object> businacptform = acptDao.qryBusinAcptformById(acptId);
//		if (businacptform == null || businacptform.isEmpty()){
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "受理记录不存在！acptId = ".concat(acptId));
//		}
//		String oriStatus = businacptform.get("acptStatus").toString();
//
//		if (!acptStatus.equals("16") && !acptStatus.equals("17") && !acptStatus.equals("18") && !acptStatus.equals("19")){
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("此接口只允许回访状态16：待回访 ，17：回访中，18：回访成功，19：回访失败，入参 回访状态错误！acptStatus = ".concat(acptStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "此接口只允许回访状态16：待回访 ，17：回访中，18：回访成功，19：回访失败，入参 回访状态错误！acptStatus = ".concat(acptStatus));
//		}
//
//		/*16  待回访  17  回访中  18  回访成功  19  回访失败 */
//		if (acptStatus.equals("16") && !oriStatus.equals("17")){
//			//如果要改为待回访那么原状态必须是回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为待回访那么原状态必须是回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为待回访那么原状态必须是回访中，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("17") && !oriStatus.equals("16")){
//			//如果要改为回访中那么原状态必须是待回访
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访中那么原状态必须是待回访，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访中那么原状态必须是待回访，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("18") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访成功那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访成功那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访成功那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("19") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访失败那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访失败那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访失败那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//		}
//
//		//更新受理状态
//		String opString = JSONObject.toJSONString(opContent);
//		Integer branchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//		CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
//				, "", "", "", "", "", opString, "", acptStatus, ""
//				, "", 0, "");
//
//		String opRemark = "";
//		JSONObject retJson =  new JSONObject();
//		retJson.put("remark", retStringForm.getRemark());
//		if (acptStatus.equals("18")){
//			if (!businacptform.get("opEntrustWay").equals(SysdictionaryConst.CNST_ENTRUST_WAY_NBOP)){
//				/*更新受理状态为7然后调用权限开通接口
//				 * 放在KCBRegExtBatchVisit里面进行状态更新
//				 updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
//							, "", "", "", "", "", "", "", "7", ""
//							, "", 0, "");*/
//				 retJson = KCBRegExtBatchVisit(opBranchNo, operatorNo, opStation, opEntrustWay, serviceId, branchNo, acptId);
//			}else{
//				//柜面办理的则调用提交接口生成复核任务
//				//mdf by fanlei 20190624 这里还是需要用原来的办理人员信息进行提交 BOPS-7606
//				Integer oriOpBranchNo = Integer.valueOf(businacptform.get("opBranchNo").toString());
//				String oriOperatorNo = businacptform.get("operatorNo").toString();
//				String oriOpStation = businacptform.get("opStation").toString();
//				String oriOpEntrustWay = businacptform.get("opEntrustWay").toString();
//				Integer oriBranchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//				Integer menuId = Integer.valueOf(businacptform.get("menuId").toString());
//
//				opRemark = saveBatchTaskBusi(oriOpBranchNo, oriOperatorNo, oriOpStation, oriOpEntrustWay,
//						900135, menuId, oriBranchNo, " ", acptId);
//				retJson.put("remark", "柜面业务已提交，需要进行后台复核！");
//			}
//		}
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		extCommonResponseForm.setData(retJson);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark(opRemark);
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20190328
//	 * @Description 科创板批量权限开通
//	 * @link BOPS-7123 增加config入参回访模式
//	 * @Link   BOPS-6784**/
//	public JSONObject KCBRegExtBatchVisit(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, Integer branchNo, String acptId) throws BopsException {
//
//		Map<String, Object> args = new HashMap<String, Object>();
//		Map<String, Map<String, Object>> retMap = abstractServiceForJob.loadAcptformInfo(acptId, 0);
//		Map<String, Object> acptformargMap = retMap.get(AbstractServiceForJob.ACPT_FORM_ARG);
//
//		//mdf by fanlei 20190527 BOPS-BOPS-7384
//		//List<String> successList = new ArrayList<String>();
//		//List<Map<String, String>> failList = new ArrayList<Map<String, String>>();
//		JSONArray successList = new JSONArray();
//		JSONArray failList = new JSONArray();
//		String openList = StringUtils.NVL(acptformargMap.get("openList"),"").toString();
//		String clientId = StringUtils.NVL(acptformargMap.get("clientId"),"").toString();
//		JSONArray jsonArray = JSONArray.parseArray(openList);
//		JSONObject retLast = new JSONObject();
//
//		logger.debug("KCBRegExtBatchVisit START...");
//		if(null != jsonArray && !jsonArray.isEmpty()){
//			String uri = new String();
//			uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_KCB_REG);
//			RestClient restClient = new RestClient();
//
//			for(int i = 0; i < jsonArray.size(); i++ ){
//				Map<String, Object> tArgs = new HashMap<String, Object>();
//				tArgs = (Map<String, Object>) jsonArray.getJSONObject(i);
//				String assetProp = StringUtils.NVL(tArgs.get("assetProp"), "").toString();
//
//				if(assetProp.equals(SysdictionaryConst.CNST_ASSET_PROP_XYZH)){
//					logger.debug("KCBRegExtBatchVisit 有信用账户需要检查普通账户开通情况...");
//					ArrayList<Map<String,String>> holderList = outerInterfaceService.qryStockholderInfo(clientId);
//					Boolean openRights = false;
//					for(int j = 0; j < holderList.size(); j++){
//						Map<String, String> tempMap = holderList.get(j);
//						if(tempMap.get("asset_prop").equals(SysdictionaryConst.CNST_ASSET_PROP_PTZH)
//								&& tempMap.get("holder_rights").contains(SysdictionaryConst.CNST_HOLDER_RIGHTS_KCB)){
//							openRights = true;
//							break;
//						}
//					}
//					if(!openRights){
//						//errorInfo.append("普通账户未开通科创板权限，信用账户禁止开通！股东账户： ").append(stockAccount).append("！");
//						Map<String, String> tMap = new HashMap<String, String>();
//						String stockAccount = StringUtils.NVL(tArgs.get("stockAccount"), "").toString();
//						tMap.put("stockAccount", stockAccount);
//						tMap.put("reason", "普通账户未开通科创板权限，信用账户禁止开通！股东账户： ".concat(stockAccount).concat("！"));
//						failList.add(tMap);
//						retLast.put("remark", "普通账户未开通科创板权限，信用账户禁止开通！");
//						args.put("acptStatus", "8");	//业务失败
//						args.put("remark", "普通账户未开通科创板权限，信用账户禁止开通！");	//业务失败
//						continue;
//					}
//				}
//
//				try {
//					retLast = restClient.postForJsonObj(uri, tArgs);
//					logger.debug("周边科创板批量权限开通：" + retLast.toJSONString());
//					String stockAccount = StringUtils.NVL(tArgs.get("stockAccount"), "").toString();
//					successList.add(stockAccount);
//					args.put("acptStatus", "7");	//业务成功
//					args.put("remark", "周边科创板批量权限开通成功");
//					retLast.put("remark", "周边科创板批量权限开通成功！");
//				} catch (BopsException e) {
//
//					args.put("acptStatus", "8");	//业务失败
//					args.put("remark", "周边科创板批量权限开通失败");
//					retLast.put("remark", "周边科创板批量权限开通失败！");
//					logger.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
//
//					Map<String, String> tMap = new HashMap<String, String>();
//					String stockAccount = StringUtils.NVL(tArgs.get("stockAccount"), "").toString();
//					tMap.put("stockAccount", stockAccount);
//					tMap.put("reason", e.getErrMessage());
//					failList.add(tMap);
//				} catch (Exception e) {
//
//					args.put("acptStatus", "8");	//业务失败
//					args.put("remark", "周边科创板批量权限开通失败");
//					retLast.put("remark", "周边科创板批量权限开通失败！");
//					logger.error(e);
//					Map<String, String> tMap = new HashMap<String, String>();
//					String stockAccount = StringUtils.NVL(tArgs.get("stockAccount"), "").toString();
//					tMap.put("stockAccount", stockAccount);
//					tMap.put("reason", e.toString());
//					failList.add(tMap);
//				}
//
//			}
//
//			if (args.containsKey("acptStatus")){
//				Map<String,String> sysarg = memCache.getSysarg();
//				Integer dateClear = Integer.valueOf(sysarg.get("init_date").toString());
//
//				updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
//						, "", "", "", "", "", "", args.get("remark").toString(), args.get("acptStatus").toString(), "1"
//						, "", dateClear, "");
//
//				//add by fanlei 增加将开通账户列表更新到businacptformarg里面去 20190522
//				List<BusinAcptformArg> argList = new ArrayList<BusinAcptformArg>();
//
//				BusinAcptformArg businAcptformArg = new BusinAcptformArg();
//				businAcptformArg.setClearFlag("0");
//				businAcptformArg.setDateClear(0);
//				businAcptformArg.setParamName("openFailList");
//				businAcptformArg.setParamScale(0);
//				businAcptformArg.setParamType("S");
//				businAcptformArg.setParamValue(failList.toJSONString());
//				businAcptformArg.setParamWidth(200);
//				businAcptformArg.setStageStep(1);
//				argList.add(businAcptformArg);
//
//				BusinAcptformArg businAcptformArg2 = new BusinAcptformArg();
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.put("stockAccount", successList);
//				businAcptformArg2.setClearFlag("0");
//				businAcptformArg2.setDateClear(0);
//				businAcptformArg2.setParamName("successList");
//				businAcptformArg2.setParamScale(0);
//				businAcptformArg2.setParamType("S");
//				businAcptformArg2.setParamValue(jsonObject.toJSONString());
//				businAcptformArg2.setParamWidth(200);
//				businAcptformArg2.setStageStep(1);
//				argList.add(businAcptformArg2);
//
//				acptDao.insertBusinAcptformArgExt(acptId, argList);
//			}
//
//
//		}else{
//			retLast.put("remark", "受理参数表中没有可开通的信息！");
//		}
//
//		retLast.put("failList", failList);
//		retLast.put("successList", successList);
//		return retLast;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20190328
//	 * @Description 科创板批量权限开通
//	 * @link BOPS-7123 增加config入参回访模式
//	 * @Link   BOPS-6784**/
//	public CommonResponseForm<JSONObject> qryBusinacptformById( String acptId) throws BopsException {
//
//		Map businMap = acptDao.qryBusinAcptformById2(acptId);
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		JSONObject jsonObject = new JSONObject();
//
//		if(businMap != null && !businMap.isEmpty()){
//			logger.debug("qryBusinacptformById businMap = " + businMap.toString());
//			jsonObject = new JSONObject(businMap);
//			logger.debug("qryBusinacptformById jsonObject = " + jsonObject.toString());
//			extCommonResponseForm.setData(jsonObject);
//		}else{
//			extCommonResponseForm.setData(jsonObject);
//		}
//
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20171107
//	 * @Description 增加业务受理记录 businacptform
//	 * @Link   BOPS-2729**/
//	public CommonResponseForm<Map<String,Object>> saveBusinAcptformAndArg(Integer opBranchNo, String operatorNo, Integer serviceId, String opStation, String opEntrustWay, Integer branchNo, String clientId, String fundAccount
//			, String fullName, String organFlag, String idKind, String idNo, String remark, String businOpType, String terminalType, String joinBusinessId, Integer corpRiskLevel,
//			BusinAcptformArgRequest request, String acodeAccount, boolean checkExists) {
//
//		CommonResponseForm<Map<String,Object>> extCommonResponseForm = new CommonResponseForm<Map<String,Object>>();
//		Integer currDate = StringUtils.getYYYYMMDD(new Date());
//		Integer currTime = StringUtils.getMinSec(new Date());
//		Map<String,String> sysarg = memCache.getSysarg();
//		logger.info(sysarg.get("init_date").toString());
//		Integer initDate = Integer.valueOf(sysarg.get("init_date").toString());
//		Map<String,String> map = new HashMap<String,String>();
//		Map<String,Object> retmap = new HashMap<String,Object>();
//
//		try {
//			//如果有存在的受理单则去取就行，没有再创建
//			DataPageResponse data =  acptDao.getBusinAcptformFuzzy(clientId, " ", branchNo, fullName, idKind, idNo, businOpType, 1, 1, "position_str desc","");
//			if (!checkExists || data == null || data.getData().isEmpty()) {//BOPS-18646:(移动双录)无跑批无处理，工单状态一直为6，需支持提交新单
//				if (businOpType.equals("900900")){
//					Map<String, Object> businacptform = acptDao.qryBusinAcptformByJoinId(joinBusinessId);
//					if(businacptform != null){
//						throw new BopsException(BopsException.ERR_PARAM_INVALID_INFO, "传入joinBusinessId值有误，该值已经绑定了业务表单！");
//					}
//				}
//
//				map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation
//				, opEntrustWay, serviceId, branchNo, 202006, " ", " ", clientId, fundAccount, fullName,
//				 organFlag, idKind, idNo, " ", corpRiskLevel, " ", " ", " ", " ", remark, "extcenter/saveBusinAcptformAndArg", " ", " ", " ", 0, 0, businOpType, " ",
//				 0, joinBusinessId, " ", 0, terminalType, acodeAccount);
//
//				acptDao.insertBusinAcptformArgExt(map.get("acptId"), request.getBusinAcptformArgs());
//			}else{
//
//				/* mdf by fanlei 20201027 BOPS-12798
//				 * 如果是办理完成，或者失败，复核决绝，回访拒绝的则可以重新创建
//				 * 如果是状态0可以修改
//				 * 如果是状态1,6等中间状态则报错返回不能修改或者创建受理单
//				 * */
//				Map<String,Object> dataMap = data.getData().get(0);
//				String originStatus = "," + StringUtils.NVL(dataMap.get("acptStatus"), "").toString() + ",";
//				if (",7,8,9,19,".contains(originStatus)) {
//					map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation, opEntrustWay, serviceId, branchNo, 202006, " ",
//							" ", clientId, fundAccount, fullName, organFlag, idKind, idNo, " ", corpRiskLevel, " ", " ", " ", " ", remark, "extcenter/saveBusinAcptformAndArg", " ",
//							" ", " ", 0, 0, businOpType, " ", 0, joinBusinessId, " ", 0, terminalType, acodeAccount);
//					acptDao.insertBusinAcptformArgExt(map.get("acptId"), request.getBusinAcptformArgs());
//				} else if (",1,".contains(originStatus)) {// 受理单复核中
//					checkDuplicateAcpt(businOpType, clientId, null, fullName, idKind, idNo, 0); // BOPS-13580：检查在途复核任务，防止复核过程中重复提交
//					map = acptDao.setBusinAcptform(1, initDate, currDate, currTime, opBranchNo, operatorNo, " ", " ", opStation, opEntrustWay, serviceId, branchNo, 202006, " ",
//							" ", clientId, fundAccount, fullName, organFlag, idKind, idNo, " ", corpRiskLevel, " ", " ", " ", " ", remark, "extcenter/saveBusinAcptformAndArg", " ",
//							" ", " ", 0, 0, businOpType, " ", 0, joinBusinessId, " ", 0, terminalType, acodeAccount);
//					acptDao.insertBusinAcptformArgExt(map.get("acptId"), request.getBusinAcptformArgs());
//				} else if (",0,".contains(originStatus)) {
//					acptDao.insertBusinAcptformArgExt(dataMap.get("acptId").toString(), request.getBusinAcptformArgs());
//					map.put("acptId", (dataMap.get("acptId").toString()));
//				} else {
//					throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_ACPTTASK_EXISTS, "存在办理中的受理单，请勿重复提交业务数据！");
//				}
//			}
//
//		} catch (BopsException e) {
//			// TODO Auto-generated catch block
//			logger.info(e);
//			extCommonResponseForm.setErrorNo(e.getErrCode());
//			extCommonResponseForm.setErrorInfo(e.getErrMessage());
//			extCommonResponseForm.setRemark("增加业务受理记录失败");
//			extCommonResponseForm.setData(retmap);
//			return extCommonResponseForm;
//		}
//		retmap.put("acptId", map.get("acptId"));
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("增加业务受理记录成功");
//		extCommonResponseForm.setData(retmap);
//		return extCommonResponseForm;
//	}
//
//	 /* @Author hanjin
//	 * @Date   20190521
//	 * @Description 周边手机号码核验<手机号码与身份证明文件对应关系核查>
//	 * @Link   BOPS-7336
//	 * BOPS-7840,20190719,hanjin,mod,支持直接送入客户营业部号信息
//	 * **/
//	public CommonResponseForm<JSONObject> csdcMobileCheckExt(String opStation, String opEntrustWay, Integer opBranchNo, String operatorNo,
//			String clientId, String mobileTel, String idNo, Integer actionIn, Integer branchNo, String businType, String clientName) throws BopsException {
//		//BOPS-7840:20190719,hanjin,mod,支持直接送入客户营业部号信息，存在新开户的业务场景，可能不清楚当时客户的客户编号
//		//获取客户的营业部编号
//		/*JSONObject clientinfo = outerInterfaceService.qryClientInfo(clientId);
//		String branchNo = clientinfo.get("branch_no").toString();*/
//		String cbranchNo = "0";
//		businType = StringUtils.NVL(businType, "03");
//		//BOPS-19514 20211221 yisy mod,判断业务类型为02时，客户名称必须送值
//		if("02".equals(businType) && StringUtils.isEmpty(StringUtils.NVL(clientName, ""))) {
//			throw new BopsException("-1", "业务类别为02时，客户名称不能为空");
//		}
//		//BOPS-19516 20211221 yisy mod,判断业务类别对应的证件类型送值
//		String idKind = "0";
//		//当业务类别为“03”时，必填，中登仅能填写“01”（居民身份证），柜台0-身份证，其他情况该字段无效
//		if(!"03".equals(businType)) {
//			idKind = "";
//		}
//		if(clientId.isEmpty()){
//			cbranchNo = branchNo.toString();
//		}else{
//			JSONObject clientinfo = outerInterfaceService.qryClientInfo(clientId);
//			cbranchNo = clientinfo.get("branch_no").toString();
//		}
//		actionIn = Integer.valueOf(StringUtils.NVL(actionIn, 0).toString());
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//	    paramJson.put("operatorNo", operatorNo);
//	    paramJson.put("opBranchNo", opBranchNo);
//	    paramJson.put("opStation", opStation);
//	    paramJson.put("opEntrustWay", opEntrustWay);
//	    //paramJson.put("branchNo", branchNo);
//	    paramJson.put("branchNo", cbranchNo);
//		paramJson.put("clientId", clientId);
//	    paramJson.put("idKind", idKind);   //当业务类别为“03”时，必填，中登仅能填写“01”（居民身份证），柜台0-身份证
//	    paramJson.put("idNo", idNo);
//	    paramJson.put("businType", businType);
//	    paramJson.put("clientName", StringUtils.NVL(clientName, ""));
//	    paramJson.put("mobileTel", mobileTel);
//	    paramJson.put("actionIn", actionIn);
//
//	    String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CSDCMOBILECHECK);
//    	JSONObject retLast = new RestClient().postForJsonObj(uri, paramJson);
//
//    	logger.info("retLast = " + retLast.toJSONString());
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("");
//    	return extCommonResponseForm;
//	}
//
//	/* @Author hanjin
//	 * @Date   20190906
//	 * @Description 周边用户权限获取<查找含特定权限的用户信息>
//	 * @Link   BOPS-8348
//	 * **/
//	public CommonResponseForm<List<Map<String,Object>>> qryUserByRoleIdExt(Integer roleId) throws BopsException {
//
//		String sqlStr = "select a.operator_no,a.op_branch_no,a.operator_name,a.registe_date,a.oper_status, "
//				+ " a.email_address,b.branch_no,b.operator_rights"
//				+ " from gfuser.operators a, gfuser.userright b "
//				+ " where a.operator_no = b.user_id "
//				+ " and substr(oper_roles,?,1) = '1'";
//
//		CommonResponseForm<List<Map<String,Object>>> extCommonResponseForm = new CommonResponseForm<List<Map<String,Object>>>();
//		extCommonResponseForm.setData(jdbcTemplate.queryForList(sqlStr, roleId));
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("周边用户权限获取成功<查找含特定权限的用户信息>");
//    	return extCommonResponseForm;
//	}
//
//	/**
//	 * 新三板权限开通校验（异步处理）
//	 *
//	 * @author: GongDaoYi
//	 * @date: 2022/02/25
//	 * @jira: BOPS-18474
//	 */
//	public CommonResponseForm<JSONObject> checkSTNClientRegExtSync(String clientId) throws BopsException {
//		// BOPS-18533 高龄客户校验
//		this.checkAge(clientId);
//
//		// 新三板权限开通的适当性信息
//		Map appAccountInfo = appropriateService.qryAppropriateInfo("0008", clientId);
//		// 返回信息1: 允许开立的股东卡
//		ArrayList enAccounts = (ArrayList)appAccountInfo.get("en_account");
//		// 返回信息2: 客户信息
//		Map client = (Map)appAccountInfo.get("client");
//		if (CollectionUtils.isEmpty(appAccountInfo)) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT, GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT_INFO);
//		}
//
//		JSONObject resultJson = new JSONObject();
//		CommonResponseForm<JSONObject> resultFrom = new CommonResponseForm<>();
//		resultFrom.setData(resultJson);
//		resultFrom.setErrorNo("0");
//		resultFrom.setErrorInfo("");
//		resultFrom.setRemark("周边新三板权限开通校验成功");
//		return resultFrom;
//	}
//
//	/**
//	 * @Author hanjin
//	 * @Date   20200205
//	 * @Description 新三板权限开通校验
//	 * @Link   BOPS-9461*
//	 * */
//	public CommonResponseForm<JSONObject> checkSTNclientRegExt(String clientId) throws BopsException {
//		// BOPS-18533 高龄客户校验
//		this.checkAge(clientId);
//
//		JSONObject retLast = new JSONObject();
//
//		//新三板权限开通的适当性信息
//		Map approAccountInfo = appropriateService.qryAppropriateInfo("0008", clientId);
//		ArrayList enAccounts = (ArrayList)approAccountInfo.get("en_account");  //返回信息1：允许开立的股东卡
//		Map client = (Map)approAccountInfo.get("client");  //返回信息2，客户信息
//		if(enAccounts.isEmpty() || enAccounts.size()==0) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT, GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT_INFO);
//		}
//
//		String enSstReportType = "";
//
//		JSONObject appropriatealgoBase = new JSONObject();
//		if(enAccounts.size()>0 && !enAccounts.isEmpty()) {
//			HashMap contextMap = new HashMap();
//			contextMap.put("clientId", clientId);
//			contextMap.put("stockAccount", ((Map)(enAccounts.get(0))).get("stock_account").toString());
//			Map approAccountInfoBase = appropriateService.qryAppropriateInfo("0008", clientId); //新三板一类合格投资者
//			ArrayList enAppropriateNo = (ArrayList)approAccountInfoBase.get("en_appropriate_no"); //当前新三板合格投资者适合的策略集合列表
//			for(int i=0; i<enAppropriateNo.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo.get(i).toString()));
//				appropriatealgoBase = appropriateService.qryAppropriatealgo(contextMap, Integer.valueOf(enAppropriateNo.get(i).toString()), clientId);
//			}
//		}
//
//		Map approAccountInfo_0008 = appropriateService.qryAppropriateInfo("0008", clientId); //新三板一类合格投资者
//		ArrayList enAppropriateNo_0008 = (ArrayList)approAccountInfo_0008.get("en_appropriate_no"); //当前新三板合格投资者适合的策略集合列表
//
//		Map approAccountInfo_0037 = appropriateService.qryAppropriateInfo("0037", clientId); //新三板二类合格投资者
//		ArrayList enAppropriateNo_0037 = (ArrayList)approAccountInfo_0037.get("en_appropriate_no"); //当前新三板合格投资者适合的策略集合列表
//
//		Map approAccountInfo_0038 = appropriateService.qryAppropriateInfo("0038", clientId); //新三板三类合格投资者
//		ArrayList enAppropriateNo_0038 = (ArrayList)approAccountInfo_0038.get("en_appropriate_no"); //当前新三板合格投资者适合的策略集合列表
//
//		//BOPS-9612:20200304,hanjin,add,增加股东卡的传送，以便正确识别客户是否是受限投资者
//		for(int k=0;k<enAccounts.size();k++) {
//			Map enAccountEntry = new HashMap();
//			HashMap contextMap = new HashMap();
//			Map<String,JSONObject> appropriatealgos = new HashMap<String, JSONObject>();  //返回信息3，客户选择开立对应合格投资者类型的策略集合整体信息
//			enAccountEntry = (Map)enAccounts.get(k);
//			String stockAccount = enAccountEntry.get("stock_account").toString();
//			contextMap.put("clientId", clientId);
//			contextMap.put("stockAccount", stockAccount);
//			enSstReportType = "";
//
//			for(int i=0; i<enAppropriateNo_0008.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo_0008.get(i).toString()));
//				JSONObject appropriatealgo = appropriateService.qryAppropriatealgo2(contextMap, Integer.valueOf(enAppropriateNo_0008.get(i).toString()), (ArrayList<JSONObject>)(appropriatealgoBase.get("data")), clientId);
//				appropriatealgos.put("class1", appropriatealgo);
//				if((boolean)(appropriatealgo.getBoolean("total_logic_value"))) {
//					enSstReportType = enSstReportType.isEmpty()? "3" : enSstReportType + ",3";
//				}
//			}
//
//			for(int i=0; i<enAppropriateNo_0037.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo_0037.get(i).toString()));
//				JSONObject appropriatealgo = appropriateService.qryAppropriatealgo2(contextMap, Integer.valueOf(enAppropriateNo_0037.get(i).toString()), (ArrayList<JSONObject>)(appropriatealgoBase.get("data")), clientId);
//				appropriatealgos.put("class2", appropriatealgo);
//				if((boolean)(appropriatealgo.getBoolean("total_logic_value"))) {
//					enSstReportType = enSstReportType.isEmpty()? "4" : enSstReportType + ",4";
//				}
//			}
//
//			for(int i=0; i<enAppropriateNo_0038.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo_0038.get(i).toString()));
//				JSONObject appropriatealgo = appropriateService.qryAppropriatealgo2(contextMap, Integer.valueOf(enAppropriateNo_0038.get(i).toString()), (ArrayList<JSONObject>)(appropriatealgoBase.get("data")), clientId);
//				appropriatealgos.put("class3", appropriatealgo);
//				if((boolean)(appropriatealgo.getBoolean("total_logic_value"))) {
//					enSstReportType = enSstReportType.isEmpty()? "5" : enSstReportType + ",5";
//				}
//			}
//
//			enAccountEntry.put("enSstReportType", enSstReportType);
//			enAccountEntry.put("appropriatealgos", appropriatealgos);
//		}
//		retLast.put("errorNo", "0");
//		retLast.put("errorInfo", "");
//		retLast.put("enAccounts", enAccounts);
//		retLast.put("client", client);
//		retLast.put("remark", "周边新三板权限开通校验接口调用成功");
//
//		//BOPS-10103:20200507,hanjin,add,增加新三板权限开通校验中资产统计日期
//		JSONObject bigdata = bigDataInterfaceService.qryOptAssetAmountFromBigData(clientId);
//		ArrayList<JSONObject> retDataBodyRep = (ArrayList<JSONObject>)bigdata.get("response_body");
//		String busiDate = "0";
//		if(!retDataBodyRep.isEmpty() && retDataBodyRep.size()>0){
//			Map<String, Object> cliententry = retDataBodyRep.get(0);
//			busiDate = StringUtils.NVL(cliententry.get("busi_date"), "0").toString();
//		}
//		retLast.put("busiDate", busiDate);
//
//		// BOPS-10103:20200507,hanjin,add,返回当前自然日最近交易日T-1日日期：比如自然日为周五，返回周四日期；自然日为周日，返回周五日期；自然日为周一，返回上周五日期。（假设周一至周五均为交易日）
//		Integer currDate = StringUtils.getYYYYMMDD(new Date());
//		JSONObject dateObj = outerInterfaceService.getNextTradeDate("1", "-1", currDate.toString());//返回当前自然日最近的T-1交易日日期
//		retLast.put("preTradeDate", dateObj.getString("next_trade_date"));
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("周边新三板权限开通校验成功");
//    	return extCommonResponseForm;
//	}
//
//	/**
//	 * 非现场北交所股票交易权限开通校验
//	 * BOPS-18169
//	 * 徐凤年
//	 */
//	public CommonResponseForm<JSONObject> checkBJSclientRegExt(String clientId) throws BopsException {
//		// BOPS-18533 高龄客户校验
//		this.checkAge(clientId);
//
//		JSONObject retLast = new JSONObject();
//
//		Map approAccountInfo = appropriateService.qryAppropriateInfo("0053", clientId);
//		ArrayList enAccounts = (ArrayList) approAccountInfo.get("en_account");
//		Map client = (Map) approAccountInfo.get("client");
//		if (CollectionUtils.isEmpty(enAccounts)) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT, GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT_INFO);
//		}
//
//		String enSstReportType = "";
//		JSONObject appropriatealgoBase = new JSONObject();
//
//		if (enAccounts.size() > 0 && !enAccounts.isEmpty()) {
//			HashMap contextMap = new HashMap();
//			contextMap.put("clientId", clientId);
//			contextMap.put("stockAccount", ((Map) (enAccounts.get(0))).get("stock_account").toString());
//			Map approAccountInfoBase = appropriateService.qryAppropriateInfo("0053", clientId);
//			ArrayList enAppropriateNo = (ArrayList) approAccountInfoBase.get("en_appropriate_no");
//			for (int i = 0; i < enAppropriateNo.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo.get(i).toString()));
//				appropriatealgoBase = appropriateService.qryAppropriatealgo(contextMap, Integer.valueOf(enAppropriateNo.get(i).toString()), clientId);
//			}
//		}
//
//		Map approAccountInfo_0053 = appropriateService.qryAppropriateInfo("0053", clientId);
//		ArrayList enAppropriateNo_0053 = (ArrayList) approAccountInfo_0053.get("en_appropriate_no");
//
//		//BOPS-9612:20200304,hanjin,add,增加股东卡的传送，以便正确识别客户是否是受限投资者
//		for (int k = 0; k < enAccounts.size(); k++) {
//			Map enAccountEntry = new HashMap();
//			HashMap contextMap = new HashMap();
//			//返回信息3，客户选择开立对应合格投资者类型的策略集合整体信息
//			Map<String, JSONObject> appropriatealgos = new HashMap<>();
//			enAccountEntry = (Map) enAccounts.get(k);
//			String stockAccount = enAccountEntry.get("stock_account").toString();
//			contextMap.put("clientId", clientId);
//			contextMap.put("stockAccount", stockAccount);
//			enSstReportType = "";
//
//			for (int i = 0; i < enAppropriateNo_0053.size(); i++) {
//				contextMap.put("appropriateNo", Integer.valueOf(enAppropriateNo_0053.get(i).toString()));
//				JSONObject appropriatealgo = appropriateService.qryAppropriatealgo2(contextMap, Integer.valueOf(enAppropriateNo_0053.get(i).toString()),
//						(ArrayList<JSONObject>) (appropriatealgoBase.get("data")), clientId);
//				appropriatealgos.put("class1", appropriatealgo);
//				if (appropriatealgo.getBoolean("total_logic_value")) {
//					String bjsReportType = memCache.getSysconfigValue(8888, "1000210");
//					enSstReportType = enSstReportType.isEmpty() ? bjsReportType : enSstReportType + "," + bjsReportType;
//				}
//			}
//
//			enAccountEntry.put("enSstReportType", enSstReportType);
//			enAccountEntry.put("appropriatealgos", appropriatealgos);
//		}
//		retLast.put("errorNo", "0");
//		retLast.put("errorInfo", "");
//		retLast.put("enAccounts", enAccounts);
//		retLast.put("client", client);
//		retLast.put("remark", "非现场北交所股票交易权限开通校验成功");
//
//		JSONObject bigdata = bigDataInterfaceService.qryOptAssetAmountFromBigData(clientId);
//		ArrayList<JSONObject> retDataBodyRep = (ArrayList<JSONObject>) bigdata.get("response_body");
//		String busiDate = "0";
//		if (!retDataBodyRep.isEmpty() && retDataBodyRep.size() > 0) {
//			Map<String, Object> cliententry = retDataBodyRep.get(0);
//			busiDate = StringUtils.NVL(cliententry.get("busi_date"), "0").toString();
//		}
//		retLast.put("busiDate", busiDate);
//
//		// BOPS-10103:20200507,hanjin,add,返回当前自然日最近交易日T-1日日期：比如自然日为周五，返回周四日期；自然日为周日，返回周五日期；自然日为周一，返回上周五日期。（假设周一至周五均为交易日）
//		Integer currDate = StringUtils.getYYYYMMDD(new Date());
//		//返回当前自然日最近的T-1交易日日期
//		JSONObject dateObj = outerInterfaceService.getNextTradeDate("1", "-1", currDate.toString());
//		retLast.put("preTradeDate", dateObj.getString("next_trade_date"));
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("非现场北交所股票交易权限开通校验成功");
//		return extCommonResponseForm;
//	}
//
//	private void checkAge(String clientId) throws BopsException {
//		JSONObject clientMsg = outerInterfaceService.qryClientEligInfo(clientId, "0");
//		JSONObject clientJson = clientMsg.getJSONObject("client");
//		JSONObject clientInfoJson = new JSONObject();
//
//		String organFlag = clientJson.getString("organ_flag");
//		if ("0".equals(organFlag)) {
//			clientInfoJson = clientMsg.getJSONObject("clientinfo");
//		}
//
//		Integer age = 0;
//		String birthday = clientInfoJson.getString("birthday");
//		if (birthday != null && birthday.length() == 8) {
//			age = StringUtils.getAgeByBirthday(birthday);
//		} else {
//			String idKind = clientJson.getString("id_kind");
//			if ("0".equals(idKind)) {
//				String idNo = clientJson.getString("id_no");
//				age = StringUtils.getAgeByBirthday(IdentifyUtils.getIdCardBirthDay(idNo));
//			}
//		}
//		if (age == 0) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_BRITHDAY_NOTVALIDED, GFErrorCode.ERR_NBOP_EXTCENTER_BRITHDAY_NOTVALIDED_INFO);
//		}
//
//		Integer maxAge = memCache.getSysconfigIntValue(8888, String.valueOf(GfSysConfigContant.CNST_GF_SYSCONFIG_BJS_XSB_AGE_NOTALLOW));
//		if (age >= maxAge) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED, GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED_INFO);
//		}
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20200206
//	 * @Description 新三板回访接口
//	 * @Link   BOPS-9463**/
//	public CommonResponseForm<JSONObject> STNRegReturnVisit(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String acptId, String acptStatus, ReturnVisitor opContent) throws BopsException{
//
//		Map<String,Object> businacptform = acptDao.qryBusinAcptformById(acptId);
//		if (businacptform == null || businacptform.isEmpty()){
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "受理记录不存在！acptId = ".concat(acptId));
//		}
//		String oriStatus = businacptform.get("acptStatus").toString();
//
//		if (!acptStatus.equals("16") && !acptStatus.equals("17") && !acptStatus.equals("18") && !acptStatus.equals("19")){
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("此接口只允许回访状态16：待回访 ，17：回访中，18：回访成功，19：回访失败，入参 回访状态错误！acptStatus = ".concat(acptStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//		}
//
//		/*16  待回访  17  回访中  18  回访成功  19  回访失败 */
//		if (acptStatus.equals("16") && !oriStatus.equals("17")){
//			//如果要改为待回访那么原状态必须是回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为待回访那么原状态必须是回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//		}
//
//		if (acptStatus.equals("17") && !oriStatus.equals("16")){
//			//如果要改为回访中那么原状态必须是待回访
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访中那么原状态必须是待回访，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//		}
//
//		if (acptStatus.equals("18") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访成功那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访成功那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//		}
//
//		if (acptStatus.equals("19") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访失败那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访失败那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//		}
////		//BOPS-10288:20200519,wangxp,mod,更新状态挪到最后执行
////		//更新受理状态
////		String opString = JSONObject.toJSONString(opContent);
////		Integer branchNo = Integer.valueOf(businacptform.get("branchNo").toString());
////		CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
////				, "", "", "", "", "", opString, "", acptStatus, ""
////				, "", 0, "");
////
//		String opRemark = "";
//		JSONObject retJson =  new JSONObject();
////		retJson.put("remark", retStringForm.getRemark());
//		if (acptStatus.equals("18")){
//
//			/*新三板开通线上&柜面办理的则调用提交接口生成复核任务
//			     这里还是需要用原来的办理人员信息进行提交
//			 900919
//			*/
//			Integer oriOpBranchNo = Integer.valueOf(businacptform.get("opBranchNo").toString());
//			String oriOperatorNo = businacptform.get("operatorNo").toString();
//			String oriOpStation = businacptform.get("opStation").toString();
//			String oriOpEntrustWay = businacptform.get("opEntrustWay").toString();
//			Integer oriBranchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//			Integer menuId = Integer.valueOf(businacptform.get("menuId").toString());
//
//			opRemark = saveBatchTaskBusi(oriOpBranchNo, oriOperatorNo, oriOpStation, oriOpEntrustWay,
//					900919, menuId, oriBranchNo, " ", acptId);
//			retJson.put("remark", "业务已提交，需要进行后台复核！");
//		}
//
//		//BOPS-10288:20200519,wangxp,mod,更新状态挪到最后执行,防止生成跑批任务失败，而状态又提前更新，导致无法重新回访
//		//更新受理状态
//		String opString = JSONObject.toJSONString(opContent);
//		Integer branchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//		CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
//				, "", "", "", "", "", opString, "", acptStatus, ""
//				, "", 0, "");
//		//BOPS-10288:20200519,wangxp,mod,更新状态挪到最后执行
//		if (!acptStatus.equals("18")) {
//			retJson.put("remark", retStringForm.getRemark());
//		}
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		extCommonResponseForm.setData(retJson);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark(opRemark);
//		return extCommonResponseForm;
//	}
//
//	/**
//	 * @Author fanlei
//	 * @Date   20200312
//	 * @Description 客户办理流水查询支持多笔记录返回
//	 * @Link   BOPS-9679**/
//	public List<Map<String, Object>> qryAcptTaskExtNew(Integer serviceId, String opStation ,String opEntrustWay
//			,Integer opBranchNo, String operatorNo, String clientId, String enBusinOpType, Integer beginDate, Integer endDate, String enAcptStatus)
//			throws BopsException {
//
//		String order = "acpt_id desc";
//		ArrayList<Object> argsList = new ArrayList<Object>();
//		String sqlStr = "select a.* from gfbusin.acptform a where 1=1";
//		sqlStr = mysql.sqlCatStr(sqlStr, "a.client_id", clientId, argsList);
//		sqlStr = mysql.sqlCatInstr(sqlStr, "a.busin_op_type", enBusinOpType, argsList);
//		if(null != enAcptStatus && !"".equals(enAcptStatus)) {
//			sqlStr = mysql.sqlCatInstr(sqlStr, "a.acpt_status", enAcptStatus, argsList);
//		}
//		sqlStr = mysql.sqlCatIntBEt(sqlStr, "a.init_date", beginDate, argsList);
//		sqlStr = mysql.sqlCatIntLEt(sqlStr, "a.init_date", endDate, argsList);
//
//		sqlStr = sqlStr.concat(" union select b.* from gfsettle.acptform b where 1=1");
//		sqlStr = mysql.sqlCatStr(sqlStr, "b.client_id", clientId, argsList);
//		sqlStr = mysql.sqlCatInstr(sqlStr, "b.busin_op_type", enBusinOpType, argsList);
//		if(null != enAcptStatus && !"".equals(enAcptStatus)) {
//			sqlStr = mysql.sqlCatInstr(sqlStr, "b.acpt_status", enAcptStatus, argsList);
//		}
//		sqlStr = mysql.sqlCatIntBEt(sqlStr, "b.init_date", beginDate, argsList);
//		sqlStr = mysql.sqlCatIntLEt(sqlStr, "b.init_date", endDate, argsList);
//		sqlStr = sqlStr + " order by "+ order ;
//
//		List<Map<String, Object>> list = mysql.execQueryCamel2(sqlStr,
//				"branch_no,op_branch_no,op_entrust_way,organ_flag,corp_risk_level,acpt_status,busin_op_type,id_kind",
//				argsList);
//		//20170105,wangxp,mod,增加subData返回
//		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
//		for (Map<String, Object> iList : list)
//		{
//			Map subDataMap= acceptQryService.businParamsAllGet(iList.get("acptId").toString());
//			//20170919,wangxp,mod,如果subDataMap为空，则为残次数据，继续下调记录 begin
//			if (subDataMap.isEmpty())
//			{
//				continue;
//			}
//			//20170920,hanjin,add,办理流水展示的审核人字段信息需要取自acptform中的lastProcUid，不能为businacptform的lastProcUid
//			subDataMap.put("lastProcUid", StringUtils.NVL(iList.get("lastProcUid")," ").toString());
//			//20170919,wangxp,mod,如果subDataMap为空，则为残次数据，继续下调记录 end
//			iList.put("subData", subDataMap);
//			iList.put("historyInfo", acceptQryService.getAuditStep(iList.get("acptId").toString(),"2,3"));
//			//20170124,wangxp add ,增加审核流水的返回
//			//iList.put("auditJour", qryAuditJour(iList.get("joinBusinessId").toString()));
//			// mod,hanjin,170811,办理流水返回数据格式改造
//			List<Map<String, String>> formshowJson = new ArrayList<Map<String, String>>();
//			String innerBusinOpType = subDataMap.get("businOpType").toString();
//
//			List<Map<String, String>> settingInfo = cache.getBusinparamorders(innerBusinOpType, 0, "");
//			JSONArray settingInfoJson = (JSONArray) JSONObject.toJSON(settingInfo);
//			MapUtil.sort(settingInfoJson, "orderNo", true);
//			//BOPS-2898:20171207,hanjin,mod,支持营业部级别的字典翻译
//			Integer branchNo = Integer.valueOf(StringUtils.NVL(subDataMap.get("branchNo"),"8888").toString());
//			for (int k = 0; k < settingInfoJson.size(); k++) {
//				Map<String, String> setting = (Map) settingInfoJson.get(k);
//				String paramNameTemp = StringUtils.NVL(setting.get("paramName"), "").toString();
//				if(subDataMap.get(paramNameTemp) != null)
//				{
//					String paramStyleTemp = StringUtils.NVL(setting.get("paramStyle"), "").toString();
//					String paramCtrlstrTemp = StringUtils.NVL(setting.get("paramCtrlstr"), "").toString();
//					String attentionTemp = StringUtils.NVL(setting.get("attention"), "").toString();
//					String remarkTemp = StringUtils.NVL(setting.get("remark"), "").toString();
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("paramName", paramNameTemp);
//					map.put("paramValue", StringUtils.NVL(subDataMap.get(paramNameTemp), "").toString());
//
//					String paramNameTemp2 = StringUtils.camelToUnderline(paramNameTemp);
//					// 20170721,wangxp,add,增加字典翻译
//					//BOPS-2898:20171207,hanjin,mod,支持营业部级别的字典翻译
//					Map<String, Object> retTrans = mysql.transFieldDic(paramNameTemp2, branchNo,
//							StringUtils.NVL(subDataMap.get(paramNameTemp), "").toString());
//					map.put("paramValueDict", retTrans == null ? ""
//							: retTrans.get(StringUtils.underLineToCamel(paramNameTemp2) + "_dict").toString());
//					//20170724,wangxp,mod,增加中文字段名称返回
//					Map tempMap = cachefield.getFieldtoname(paramNameTemp2);
//					map.put("paramNameDict", tempMap == null?"" :tempMap.get("entryName").toString());
//					map.put("paramStyle", paramStyleTemp);
//					map.put("paramCtrlstr", paramCtrlstrTemp);
//					map.put("attention", attentionTemp);
//					map.put("remark", remarkTemp);
//					formshowJson.add(map);
//				}
//			}
//			iList.put("formshow", formshowJson);
//
//			retList.add(iList);
//		}
//
//		return retList;
//	}
//
//	/**
//	 * @throws ParseException
//	 * @throws NumberFormatException
//	 * @Author hanjin
//	 * @Date   20200331
//	 * @Description 专业投资者认定校验
//	 * @Link   BOPS-9812*
//	 * */
//	public CommonResponseForm<Map<String, Object>> checkClientpreferExt(String clientId, String applyProfType) throws BopsException, NumberFormatException, ParseException {
//		Map<String, Object> retLast = new HashMap<String, Object>();
//
//		if(StringUtils.isNotBlank(applyProfType.trim()))
//		{
//			if(!applyProfType.equals("A4") && !applyProfType.equals("B4")) {
//				throw new BopsException(GFErrorCode.ERR_BOPS_PARAM_ILLEGAL, GFErrorCode.ERR_BOPS_PARAM_ILLEGAL_INFO + String.format("[applyProfType = %s]", applyProfType));
//			}
//		}
//
//		Map sysarg = memCache.getSysarg();
//		Integer initDate = Integer.parseInt(sysarg.get("init_date").toString());
//
//		//格式形如：A1:B1,C1,D1;A2:B2,C2,D2;
//		Map<String, Object> profTypeMap = new HashMap<String, Object>();//格式形如{"A1":{"asset":"B1","month":"C1","paperScore":"D1"},"A2":{"asset":"B2","month":"C2","paperScore":"D2"}}
//		String config_1000127 = memCache.getSysconfigStrValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_OFFSITE_CHECK_CLIENTPREFER.toString());
//		//BOPS-11011:20200803,hanjin,add,增加专业投资者评定2020新模式
//		char config_1000150 = memCache.getSysconfigCharValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_PROFCLIENT_OPEN_MODE.toString());
//
//		retLast.put("configStr", config_1000127);
//		String[] profTypes = config_1000127.split(";");
//		for(int i=0; i<profTypes.length; i++) {
//			String profType = profTypes[i];
//			String[] profTypeStr = profType.split(":");
//			String profTypesubStr1 = profTypeStr[0];   //A1、A2
//			String profTypeSubStr2 = profTypeStr[1];   //B1,C1,D1、B2,C2,D2
//			String[] eligEntrys = profTypeSubStr2.split(",");
//			Map eligEntryMap = new HashMap();
//			eligEntryMap.put("asset", eligEntrys[0]);
//			eligEntryMap.put("day", eligEntrys[1]);
//			eligEntryMap.put("paperScore", eligEntrys[2]);
//			profTypeMap.put(profTypesubStr1,eligEntryMap);
//		}
//		logger.info("profTypeMap = " + profTypeMap.toString());
//
//		if(StringUtils.isNotBlank(applyProfType)) {
//			if(profTypeMap.get(applyProfType) == null) {
//				throw new BopsException(GFErrorCode.ERR_BOPS_PARAM_ILLEGAL, GFErrorCode.ERR_BOPS_PARAM_ILLEGAL_INFO + String.format("[applyProfType = %s][checkClientPreferStr = %s]", applyProfType, config_1000127));
//			}
//			Map<String, Object> profTypeMapCopy = new HashMap<String, Object>();
//			profTypeMapCopy.putAll(profTypeMap);
//			profTypeMap.clear();
//			profTypeMap.put(applyProfType, profTypeMapCopy.get(applyProfType));
//		}
//		logger.info("profTypeMapNeedCheck = " + profTypeMap.toString());
//
//
//		for(String key : profTypeMap.keySet()){
//			Map<String, String> profTypeCheckMap = (Map<String, String>)profTypeMap.get(key);
//			Map<String, String> retLastEntry = new HashMap<String, String>();
//			retLastEntry.put("paperScore", "0.0");
//			retLastEntry.put("asset", "0.0");
//			retLastEntry.put("day", "0");
//			retLastEntry.put("isPass", "1");
//			retLast.put(key, retLastEntry);
//			for(String key2 : profTypeCheckMap.keySet()){
//				if(key2.equals("asset")) { //>=value才行
//					//调用资产判断的接口
//					String checkvalue = profTypeCheckMap.get(key2);
//					Map assetMap = bigDataInterfaceService.qryAvgAssetFromBigData(clientId);
//					String clientasset = "0";
//					//BOPS-11011:20200803,hanjin,mod,非现场专业投资者评定2020新模式,如果是个人户，则二十日日均资产变为T-1日的时点资产,同时增加netAsset20avgStib、netAssetTotalStib字段的返回
//					if(config_1000150 == '0') {
//						clientasset = assetMap.get("net_asset_20avg_stib").toString();
//						retLastEntry.put("netAsset20avgStib", clientasset);
//					}else {
//						clientasset = assetMap.get("net_asset_total_stib").toString();
//						retLastEntry.put("netAssetTotalStib", clientasset);
//					}
//					boolean isSubPass = (assetMap!=null && Double.valueOf(clientasset).compareTo(Double.valueOf(checkvalue))>=0)? true : false;
//					retLastEntry.put("asset", clientasset);
//					if(!isSubPass) {
//						retLastEntry.put("isPass", "0");
//						//BOPS-11011:20200810,hanjin,mod,对于不满足资产、交易经验的情况，继续检查，不再直接退出，老模式继续保持直接退出
//						if(config_1000150 == '0') {
//							break;
//						}
//					}
//
//				}
//				if(key2.equals("day")) {
//					//调用首次交易日期接口
//					String checkvalue = profTypeCheckMap.get(key2);
//					JSONObject retjson = clientTradeInfoService.qryFirstTradeDate(clientId, "");
//					String first_exchdate = StringUtils.NVL(retjson.get("first_exchdate"),"0").toString().trim().isEmpty() ? "0" : StringUtils.NVL(retjson.get("first_exchdate").toString(),"0");
//					//BOPS-10469:20200601,hanjin,add,兼容首次交易日期特殊值处理，对非法日期格式进行保护
//					if("0".equals(first_exchdate) || "".equals(first_exchdate) || "-1".equals(first_exchdate) || first_exchdate.length()!=8) {
//						retLastEntry.put("day", first_exchdate);
//						retLastEntry.put("isPass", "0");
//						//BOPS-11011:20200810,hanjin,mod,对于不满足资产、交易经验的情况，继续检查，不再直接退出，老模式继续保持直接退出
//						if(config_1000150 == '0') {
//							break;
//						}
//					}else {
//						Integer plusDate = DateFormatter.plusMonths(Integer.valueOf(first_exchdate),Integer.parseInt(checkvalue));
//						boolean isSubPass = initDate.compareTo(plusDate)>=0 ? true : false;
//						retLastEntry.put("day", first_exchdate);
//						if(!isSubPass) {
//							retLastEntry.put("isPass", "0");
//							//BOPS-11011:20200810,hanjin,mod,对于不满足资产、交易经验的情况，继续检查，不再直接退出，老模式继续保持直接退出
//							if(config_1000150 == '0') {
//								break;
//							}
//						}
//					}
//				}
//				if(key2.equals("paperScore")) {
//					//调用首次交易日期接口
//					String checkvalue = profTypeCheckMap.get(key2);
//					ArrayList<Object> argsList = new ArrayList<>();
//					String sqlStr = "select c.* from ( ";
//					sqlStr = sqlStr + "select a.* from gfbusin.eligtestjour a where 1=1";
//					sqlStr = mysql.sqlCatStr(sqlStr, "a.client_id", clientId, argsList);
//					sqlStr = mysql.sqlCatStr(sqlStr, "a.paper_type", "-", argsList);
//					sqlStr = sqlStr + ") c";
//					List<Map<String,Object>> list = mysql.execQueryCamel2(sqlStr, "op_branch_no,branch_no,id_kind,paper_type,organ_flag",argsList);
//					Double paperScore = 0.0;
//					if (list != null && !list.isEmpty()) {
//						Map<String, Object> lastMap = list.get(list.size() - 1);
//						paperScore = Double.valueOf(StringUtils.NVL(lastMap.get("paperScore"),"0").toString());
//					}
//					boolean isSubPass = (key.equals("B4") && paperScore.compareTo(Double.valueOf(checkvalue))<0) ? false : true; //默认问卷测评通过
//					retLastEntry.put("paperScore", paperScore.toString());
//					if(!isSubPass) {
//						retLastEntry.put("isPass", "0");
//						//BOPS-11011:20200810,hanjin,mod,对于不满足资产、交易经验的情况，继续检查，不再直接退出，老模式继续保持直接退出
//						if(config_1000150 == '0') {
//							break;
//						}
//					}
//				}
//			}
//		}
//
//		CommonResponseForm<Map<String, Object>> extCommonResponseForm = new CommonResponseForm<Map<String, Object>>();
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("周边专业投资者校验接口调用成功");
//    	return extCommonResponseForm;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @throws ParseException
//	 * @throws NumberFormatException
//	 * @Author hanjin
//	 * @Date   20200429
//	 * @Description 创业板权限开通校验
//	 * @Link   BOPS-10134
//	 * */
//	public JSONObject checkGemPermessionExt(String clientId) throws BopsException, NumberFormatException, ParseException {
//
//		JSONObject clientprefer = extUserService.qryClientPreferExt(clientId);
//		JSONObject clientinfo = outerInterfaceService.qryClientInfo(clientId);
//		String organFlag = StringUtils.NVL(clientinfo.get("organ_flag"),"").toString();
//
//		Map sysarg = memCache.getSysarg();
//		Integer initDate = Integer.parseInt(sysarg.get("init_date").toString());
//		char char_1000135 = memCache.getSysconfigCharValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_GEM_PERMISSION_MANA_MODE_OPEN.toString());
//
//		int stkholderNum = 0;
//		boolean isDirectOpen = false;
//		List<Map<String, String>> stkholders = new ArrayList<Map<String, String>>();
//
//		JSONObject retJSON = new JSONObject();
//		retJSON.put("clientId", clientId);
//		retJSON.put("organFlag", organFlag);
//		retJSON.put("stks", new ArrayList<Map<String, String>>());
//		retJSON.put("age", 0);
//		retJSON.put("firstExchdate", "");
//		retJSON.put("avgAsset", 0);
//		//BOPS-10180:20200514,hanjin,mod,修改返回的signType默认值
//		//retJSON.put("signType", "");
//		retJSON.put("signType", " ");
//		retJSON.put("signDate", "");
//		retJSON.put("passType", "0");
//		retJSON.put("riskSubType","");
//		retJSON.put("isRepeat","0");//BOPS-10181:20200501,hanjin,add,增加重试标志位（当前时间可能中登/缓存信息都没查到，建议客户后续重试）
//		retJSON.put("isOnlyOpenCrdt","0");//BOPS-10241:20200507,hanjin,add,增加输出是否允许客户单独勾选只开立信用证券账户创业板权限的判断
//		retJSON.put("isExistJRights","0");//BOPS-10180:20200512,hanjin,add,增加输出是否之前已经存在j权限
//
//		String config_1000133 = memCache.getSysconfigStrValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_GEM_PERMISSION_CHECK_STR.toString());
//		String[] config_1000133Lists = config_1000133.split(";");
//		String assetThreshold = config_1000133Lists[0];
//		String experienceAgeThreshold = config_1000133Lists[1];
//
//		String[] experienceAgeThresholdLists = experienceAgeThreshold.split(",");
//		String experienceThreshold = experienceAgeThresholdLists[0];
//		String ageThreshold = experienceAgeThresholdLists[1];
//
//		//1.需存在深圳市场股东卡，且股东状态0-正常，且不存在W-创业板新权限，且非1-基金账户，且只有0/7资产属性才能继续往下办理
//		//BOPS-10241:20200507,hanjin,add,增加输出是否允许客户单独勾选只开立信用证券账户创业板权限的判断
//		boolean isExistUnopenCrdtAcct = false;
//		boolean isExistOpenNormalAcct = false;
//		//BOPS-10180:20200512,hanjin,add,增加j权限的判断
//		boolean isExistJRights = false;
//		ArrayList<Map<String,String>> stkholderList = outerInterfaceService.qryStockholderInfo(clientId);
//		for(int j = 0; j < stkholderList.size(); j++){
//			Map<String, String> stkholder = stkholderList.get(j);
//			if(stkholder.get("exchange_type").equals(Constant.EXCHANGE_TYPE_SZ)
//					&& stkholder.get("holder_status").toString().equals("0")
//					&& !stkholder.get("holder_rights").contains("W")
//					&& !stkholder.get("holder_kind").toString().equals("1")
//					&& (stkholder.get("asset_prop").toString().equals("0") || stkholder.get("asset_prop").toString().equals("7"))) {
//				stkholders.add(stkholder);
//				stkholderNum++;
//			}
//			//BOPS-10241:20200507,hanjin,add,存在普通证券账户已经开通创业板权限
//			if(stkholder.get("exchange_type").equals(Constant.EXCHANGE_TYPE_SZ)
//					&& stkholder.get("holder_status").toString().equals("0")
//					&& !stkholder.get("holder_rights").contains("W")
//					&& !stkholder.get("holder_kind").toString().equals("1")
//					&& stkholder.get("asset_prop").toString().equals(SysdictionaryConst.CNST_ASSET_PROP_XYZH)) {
//				isExistUnopenCrdtAcct = true;
//			}
//			if(stkholder.get("exchange_type").equals(Constant.EXCHANGE_TYPE_SZ)
//					&& stkholder.get("holder_status").toString().equals("0")
//					&& stkholder.get("holder_rights").contains("W")
//					&& !stkholder.get("holder_kind").toString().equals("1")
//					&& stkholder.get("asset_prop").toString().equals(SysdictionaryConst.CNST_ASSET_PROP_PTZH)) {
//				isExistOpenNormalAcct = true;
//			}
//		}
//
//		//BOPS-10180:20200515,hanjin,add,增加j权限的判断
//		//对于我司存在j权限的，不校验资产和交易经验，允许直接办理(20200514,hanjin,add,只判断普通证券账户是否存在j权限，判断3要素前提的j权限逻辑)
//		String fullName = StringUtils.NVL(clientinfo.get("full_name"),"").toString();
//		String idKind = StringUtils.NVL(clientinfo.get("id_kind"),"").toString();
//		String idNo = StringUtils.NVL(clientinfo.get("id_no"),"").toString();
//		//优化SQL效率，联查client表，使得可以走到索引的内容
//		String sqlStr = "select a.* from hs_asset.stockholder a,hs_asset.client b where b.id_no='" + idNo + "' and b.id_kind='" + idKind
//				+ "' and b.full_name='" + fullName + "' and b.client_id = a.client_id";
//		ArrayList<Map> stks = outerInterfaceService.qrySQL(sqlStr);
//		if(null != stks && !stks.isEmpty()){
//			for(int i=0;i<stks.size();i++){
//				Map<String, Object> stk = (Map)stks.get(i);
//				if(stk.get("exchange_type").equals(Constant.EXCHANGE_TYPE_SZ)
//						&& stk.get("holder_status").toString().equals("0")
//						&& stk.get("holder_rights").toString().contains("j")
//						&& !stk.get("holder_kind").toString().equals("1")
//						&& (stk.get("asset_prop").toString().equals(SysdictionaryConst.CNST_ASSET_PROP_PTZH))){
//					isExistJRights = true;
//					isDirectOpen = true;
//					retJSON.put("isExistJRights","1");
//					retJSON.put("signType", "2");  //2-转签
//				}
//			}
//		}
//
//		retJSON.put("stks", stkholders);
//		if(stkholderNum == 0) {
//			retJSON.put("errorCode", GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT);
//			retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_BUSIN_NO_SATISFY_ACCOUNT_INFO);
//			return retJSON;
//		}
//
//		//BOPS-10241:20200507,hanjin,add,存在普通证券账户已经开通创业板权限
//		if(isExistUnopenCrdtAcct && isExistOpenNormalAcct) {
//			retJSON.put("isOnlyOpenCrdt","1");
//		}
//
//		//2.A1/A2/A3不允许业务办理
//		//BOPS-10180:20200512,hanjin,mod,放开A1~A3客户的业务办理
//		/*if(clientprefer.get("prof_flag").equals("1") && Integer.valueOf(clientprefer.get("prof_end_date").toString()).compareTo(initDate) >= 0
//				&& (StringUtils.NVL(clientprefer.get("profa_sub_type"),"").equals("1") || StringUtils.NVL(clientprefer.get("profa_sub_type"),"").equals("2") || StringUtils.NVL(clientprefer.get("profa_sub_type"),"").equals("3"))) {
//			logger.info("clientprefer = " + clientprefer.toJSONString());
//			retJSON.put("errorCode", GFErrorCode.ERR_NBOP_BUSIN_CLIENTPREFER_A_NOT_ALLOWED);
//			retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_BUSIN_CLIENTPREFER_A_NOT_ALLOWED_INFO
//					+ String.format("当前客户专业投资者为[A%s]", clientprefer.get("profa_sub_type").toString()));
//			return retJSON;
//		}*/
//
//		//3.1对于机构户，无需校验资产及交易日期，可后续直接开通
//		if(!organFlag.equals(Constant.ORGAN_FLAG_PERSON)
//				//|| (clientprefer.get("prof_flag").equals("1") && Integer.valueOf(clientprefer.get("prof_end_date").toString()).compareTo(initDate) >= 0 && (clientprefer.get("profa_sub_type").equals("4")))
//				//|| (clientprefer.get("prof_flag").equals("1") && Integer.valueOf(clientprefer.get("prof_end_date").toString()).compareTo(initDate) >= 0 &&(clientprefer.get("prof_type").equals("B")))
//				){
//			isDirectOpen = true;
//		}
//
//		//3.2纯信用账户无需校验资产及交易日期
//		//BOPS-10208:20200502,hanjin,mod,修改股东卡信息的取值逻辑
//		if((stkholderNum == 1)
//				&& stkholders.get(0).get("asset_prop").toString().equals(SysdictionaryConst.CNST_ASSET_PROP_XYZH)) {
//			isDirectOpen = true;
//			retJSON.put("signType", "2");  //2-转签
//		}
//
//		//3.3 如果办理模式下，增加在途业务的检查
//		if(char_1000135 == '1') {
//			DataPageResponse datapage = acptDao.getBusinAcptformFuzzy2("", clientId, "", "", "", "", "900922", "", "", 1, 1,"a.acpt_id desc");
//			List<Map<String,Object>> data = datapage.getData();
//			if(!data.isEmpty()){
//				Map<String,Object> lastbusin = data.get(0);
//				BopsException bopsexp = new BopsException();
//				if(lastbusin.get("acptStatus").equals("1")){//已存在提交的数据
//					bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//					bopsexp.setErrMessage(String.format("已存在acpt_id[%s]的业务，请勿重复提交当前client_id[%s]", lastbusin.get("acptId").toString(), clientId));
//					bopsexp.setRestData(lastbusin);
//					throw bopsexp;
//				}else if(lastbusin.get("acptStatus").equals("6")){//生成跑批+处理中
//					//查询当前业务对应的跑批任务状态
//					DataPageResponse currTaskPage = batchtaskManagerDao.getCurrBatchtask("", "", "","900922", "",
//							"", "", clientId, lastbusin.get("acptId").toString(),1, 1 ,"a.acpt_id");
//					List<Map<String, Object>> currTaskList = currTaskPage.getData();
//					if(!currTaskList.isEmpty()){
//						Map<String, Object> currTask = currTaskList.get(0);
//						if(!currTask.get("execStatus").toString().equals("3")
//								&& !currTask.get("execStatus").toString().equals("2")){
//							//正在处理中或其他状态，不允许
//							bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//							bopsexp.setErrMessage(String.format("已经存在acpt_id[%s]的业务，且跑批任务状态[%s]不正常，请勿重复提交", lastbusin.get("acptId").toString(), currTask.get("execStatus").toString()));
//							bopsexp.setRestData(lastbusin);
//							throw bopsexp;
//						}
//					}else{
//						//不存在跑批任务，不允许
//						bopsexp.setErrCode(BopsException.ERR_PARAM_INVALID);
//						bopsexp.setErrMessage(String.format("已经存在acpt_id[%s]的业务，且该业务暂未生成跑批任务，请勿重复提交", lastbusin.get("acptId").toString()));
//						bopsexp.setRestData(lastbusin);
//						throw bopsexp;
//					}
//				}
//			}
//		}
//
//		//4.校验个人户(不满足直接开通权限的客户)
//		if(!isDirectOpen && organFlag.equals(Constant.ORGAN_FLAG_PERSON)) {
//			//获取中登签约类型,首次交易日期,签署日期
//			String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CLIENT_EARLY_GEM_INFO);
//			Map<String,Object> paramMap = new  HashMap<String,Object>();
//			paramMap.put("clientId", clientId);
//			JSONObject clienteligreg = new RestClient().getForJsonObj(url, paramMap);
//			logger.info("clienteligreg = " + clienteligreg.toJSONString());
//
//			String riskSubType = StringUtils.NVL(clienteligreg.get("risk_sub_type"),"").toString();//如果中登/本地没有命中，返回Null,做了保护就是""
//			String firstExchdate = StringUtils.NVL(clienteligreg.get("first_exchdate"),"-1").toString();//如果中登/本地没有命中,将返回-1
//			String subRiskDate = StringUtils.NVL(clienteligreg.get("sub_risk_date"),"-1").toString();//如果中登/本地没有命中,将返回-1
//			retJSON.put("firstExchdate", firstExchdate);
//			retJSON.put("signDate", subRiskDate);
//			retJSON.put("riskSubType", riskSubType);
//
//			//BOPS-15650:20210414,hanjin,add,提前查询客户的资产信息
//			JSONObject bigdata = bigDataInterfaceService.qryOptAssetAmountFromBigData(clientId);
//			ArrayList<JSONObject> retDataBodyRep = (ArrayList<JSONObject>)bigdata.get("response_body");
//			BigDecimal avgAsset = new BigDecimal(0);
//			if(!retDataBodyRep.isEmpty() && retDataBodyRep.size()>0){
//				Map<String, Object> cliententry = retDataBodyRep.get(0);
//				avgAsset = new BigDecimal(StringUtils.NVL(cliententry.get("net_asset_20avg_stib"), "0").toString());
//			}
//			retJSON.put("avgAsset", avgAsset);
//			String errorCode = "0";
//			String errorInfo = "";
//
//			//首签需要校验年龄、资产、交易经验
//			if(StringUtils.isBlank(riskSubType)) {
//				retJSON.put("signType", "1");  //1-新签
//				Integer age = qryClientAge(clientId);
//				retJSON.put("age", age);
//				if(age >= Integer.valueOf(ageThreshold)) {
//					/*retJSON.put("errorCode", GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED);
//					retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED_INFO
//							+ String.format("[年龄阈值=%s][客户当前年龄=%s]", ageThreshold, age));
//					return retJSON;*/
//					errorCode = GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED;
//					errorInfo = errorInfo.isEmpty() ? GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED_INFO + String.format("[年龄阈值=%s][客户当前年龄=%s]", ageThreshold, age)
//							: errorInfo + ";" + GFErrorCode.ERR_NBOP_EXTCENTER_AGE_NOTALLOWED_INFO + String.format("[年龄阈值=%s][客户当前年龄=%s]", ageThreshold, age);
//				}
//
//				if(firstExchdate.equals("0") || firstExchdate.equals("-1") || initDate.compareTo(DateFormatter.plusMonths(Integer.valueOf(firstExchdate),Integer.parseInt(experienceThreshold))) < 0){
//					if(firstExchdate.equals("-1")) {
//						retJSON.put("isRepeat","1");
//					}
//					/*retJSON.put("errorCode", GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR);
//					retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO
//							+ String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate));
//					return retJSON;*/
//					errorCode = GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR;
//					errorInfo = errorInfo.isEmpty() ? GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO + String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate)
//							: errorInfo + ";" + GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO + String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate);
//				}
//
//				/*JSONObject bigdata = bigDataInterfaceService.qryOptAssetAmountFromBigData(clientId);
//				ArrayList<JSONObject> retDataBodyRep = (ArrayList<JSONObject>)bigdata.get("response_body");
//				BigDecimal avgAsset = new BigDecimal(0);
//				if(!retDataBodyRep.isEmpty() && retDataBodyRep.size()>0){
//					Map<String, Object> cliententry = retDataBodyRep.get(0);
//					avgAsset = new BigDecimal(StringUtils.NVL(cliententry.get("net_asset_20avg_stib"), "0").toString());
//				}
//				retJSON.put("avgAsset", avgAsset);*/
//				if(avgAsset.compareTo(new BigDecimal(assetThreshold)) < 0){
//					/*retJSON.put("errorCode", GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR);
//					retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO
//							+ String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString()));
//					return retJSON;*/
//					errorCode = GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR;
//					errorInfo = errorInfo.isEmpty() ? GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO + String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString())
//							: errorInfo + ";" + GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO + String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString());
//				}
//			}else {
//				retJSON.put("signType", "2");  //2-转签
//				// 转签1：如果签约日期在20200428之前，不校验交易日期+日均资产
//				// 转签2：如果签约日期在20200428之后，需要校验交易日期+日均资产
//				if(Integer.valueOf(subRiskDate).compareTo(Integer.valueOf("20200428")) >= 0) {
//					if(firstExchdate.equals("0") || firstExchdate.equals("-1") || initDate.compareTo(DateFormatter.plusMonths(Integer.valueOf(firstExchdate),Integer.parseInt(experienceThreshold))) < 0){
//						if(firstExchdate.equals("-1")) {
//							retJSON.put("isRepeat","1");
//						}
//						/*retJSON.put("errorCode", GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR);
//						retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO
//								+ String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate));
//						return retJSON;*/
//						errorCode = GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR;
//						errorInfo = errorInfo.isEmpty() ? GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO + String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate)
//								: errorInfo + ";" + GFErrorCode.ERR_NBOP_EXTCENTER_FIRSTEXCHDATE_ERROR_INFO + String.format("[交易经验要求=%s个月][当前日期=%s][客户首次交易日期=%s]", experienceThreshold, initDate, firstExchdate);
//					}
//
//					/*JSONObject bigdata = bigDataInterfaceService.qryOptAssetAmountFromBigData(clientId);
//					ArrayList<JSONObject> retDataBodyRep = (ArrayList<JSONObject>)bigdata.get("response_body");
//					BigDecimal avgAsset = new BigDecimal(0);
//					if(!retDataBodyRep.isEmpty() && retDataBodyRep.size()>0){
//						Map<String, Object> cliententry = retDataBodyRep.get(0);
//						avgAsset = new BigDecimal(StringUtils.NVL(cliententry.get("net_asset_20avg_stib"), "0").toString());
//					}
//					retJSON.put("avgAsset", avgAsset);*/
//					if(avgAsset.compareTo(new BigDecimal(assetThreshold)) < 0){
//						/*retJSON.put("errorCode", GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR);
//						retJSON.put("errorInfo", GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO
//								+ String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString()));
//						return retJSON;*/
//						errorCode = GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR;
//						errorInfo = errorInfo.isEmpty() ? GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO + String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString())
//								: errorInfo + ";" + GFErrorCode.ERR_NBOP_EXTCENTER_CLIENTASSET_ERROR_INFO + String.format("[资产要求=%s][当前客户资产=%s]", assetThreshold, avgAsset.toString());
//					}
//				}
//			}
//			if(!errorCode.equals("0") || !errorInfo.isEmpty()) {
//				retJSON.put("errorCode", errorCode);
//				retJSON.put("errorInfo", errorInfo);
//				return retJSON;
//			}
//		}
//
//		retJSON.put("errorNo", "0");
//		retJSON.put("errorInfo", "");
//		retJSON.put("passType", "1");
//		return retJSON;
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20200429
//	 * @Description 创业板权限开通
//	 * @Link   BOPS-10134
//	 * */
//	public JSONObject openGemPermessionExt(Integer opBranchNo, String operatorNo, String opEntrustWay, String opStation,
//			Integer branchNo, String passwordType, String password, String userToken, String clientId, String organFlag,
//			List<Map<String, String>> openList, List<Map<String, String>> failList, String enFundAccount, String enStockAccount, String enSignType, String exchangeType, String terminalType,
//			String remark, String firstExchdate, String avgAsset, String age, Map signMap, JSONObject currJson, String signDate, String riskSubType, String isExistJRights) throws BopsException {
//
//		Map<String, Object> args = new HashMap<String, Object>();
//		args.put("op_entrust_way", opEntrustWay);
//		args.put("op_branch_no", opBranchNo);
//		args.put("operator_no", operatorNo);
//		args.put("op_station", opStation);
//		args.put("clientId", clientId);
//		args.put("branchNo", branchNo.toString());
//		args.put("exchangeType", exchangeType);
//		args.put("remark", remark);
//		args.put("successList", "");
//		JSONArray failListJson = new JSONArray();
//		if (failList != null && failList.size() > 0){
//			for (int f = 0; f < failList.size(); f++){
//				Map<String, String> map = failList.get(f);
//				failListJson.add(map);
//			}
//		}
//		JSONArray openListJson = new JSONArray();
//		if (openList != null && openList.size() > 0){
//			for (int f = 0; f < openList.size(); f++){
//				Map<String, String> map = openList.get(f);
//				openListJson.add(map);
//			}
//		}
//		args.put("failList", failListJson.toJSONString());
//		args.put("openList", openListJson.toJSONString());
//
//		JSONObject retLast = new JSONObject();
//		List<String> successList = new ArrayList<String>();
//
//		int openNormalCount = 0; //当前已经开通成功的普通股东卡个数
//		boolean isExistNormal = false;//当前存在需要开立的普通股东卡
//		char char_1000135 = memCache.getSysconfigCharValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_GEM_PERMISSION_MANA_MODE_OPEN.toString());
//		if(char_1000135 != '1') {
//			logger.debug("Service openGemPermessionExt start");
//			for(int i = 0; i < openList.size(); i++){
//				Map<String, Object> tArgs = new HashMap<String, Object>();
//				tArgs.put("op_entrust_way", opEntrustWay);
//				tArgs.put("op_branch_no", opBranchNo);
//				tArgs.put("operator_no", operatorNo);
//				tArgs.put("op_station", opStation);
//				tArgs.put("clientId", clientId);
//				tArgs.put("branchNo", branchNo.toString());
//				tArgs.put("exchangeType", exchangeType);
//				tArgs.put("remark", remark);
//				Map<String, String> openMap = openList.get(i);
//				String fundAccount = openMap.get("fundAccount");
//				String stockAccount = openMap.get("stockAccount");
//				tArgs.put("fundAccount", fundAccount);
//				tArgs.put("stockAccount", stockAccount);
//				tArgs.put("asset_prop", openMap.get("assetProp"));
//				tArgs.put("gemTrainFlag", "1");
//				tArgs.put("is_first_sign", "1");
//				if(openMap.get("assetProp").equals(SysdictionaryConst.CNST_ASSET_PROP_PTZH)) {
//					isExistNormal = true;
//				}
//				try {
//					//BOPS-10196:20200501,hanjin,mod,对于开立列表中存在普通账户，且成功开立普通账户个数为0，这时如果开立信用证券账户，不允许往下做
//					if(openMap.get("assetProp").equals(SysdictionaryConst.CNST_ASSET_PROP_XYZH) && isExistNormal && openNormalCount == 0) {
//						args.put("acptStatus", "8");	//业务失败
//						args.put("remark", "周边创业板批量权限开通失败");
//						Map<String, String> tMap = new HashMap<String, String>();
//						tMap.put("stockAccount", stockAccount);
//						//tMap.put("reason", "开立列表中存在普通账户，且成功开立普通账户个数为0，这时开立信用证券账户，不允许往下做");
//						tMap.put("reason", String.format("尚未开通普通账户创业板权限，信用账户%s不具备创业板权限开通条件",stockAccount));
//						failList.add(tMap);
//						continue;
//					}
//					String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_REGSTOCKINFO);
//					retLast = new RestClient().postForJsonObj(uri, tArgs);
//					logger.debug("周边创业板批量权限开通：" + retLast.toJSONString());
//					successList.add(stockAccount);
//					args.put("acptStatus", "7");	//业务成功
//					if(openMap.get("assetProp").equals(SysdictionaryConst.CNST_ASSET_PROP_PTZH)) {
//						openNormalCount ++;    //如果当前存在普通股东卡开立权限成功的，增加一次
//					}
//				} catch (BopsException e) {
//					args.put("acptStatus", "8");	//业务失败
//					args.put("remark", "周边创业板批量权限开通失败");
//					logger.error(e.getMessage() + Arrays.toString(e.getStackTrace()));
//					Map<String, String> tMap = new HashMap<String, String>();
//					tMap.put("stockAccount", stockAccount);
//					tMap.put("reason", e.getErrMessage());
//					failList.add(tMap);
//				} catch (Exception e) {
//					args.put("acptStatus", "8");	//业务失败
//					args.put("remark", "周边创业板批量权限开通失败");
//					logger.error(e);
//					Map<String, String> tMap = new HashMap<String, String>();
//					tMap.put("stockAccount", stockAccount);
//					tMap.put("reason", e.toString());
//					failList.add(tMap);
//				}
//			}
//			retLast.put("failList", failList);
//			retLast.put("successList", successList);
//			retLast.put("isRealEffect", "1");
//			JSONArray failListJson2 = new JSONArray();
//			if (failList != null && failList.size() > 0){
//				for (int f = 0; f < failList.size(); f++){
//					Map<String, String> map = failList.get(f);
//					failListJson2.add(map);
//				}
//			}
//			args.put("successList", successList.toString());
//			args.put("failList", failListJson2.toJSONString());
//		}
//
//		if(char_1000135 == '1') {
//			args.put("acptStatus", "1");//如果办理模式，提交后状态更新为'1'
//		}
//
//		logger.debug("Service openGemPermessionExt ready save businacptform");
//		//记录一笔acptform
//		//记一下businacptform, businacpformarg
//		args.put("enFundAccount", enFundAccount);
//		args.put("enStockAccount", enStockAccount);
//		args.put("enSignType", enSignType);
//		args.put("businOpType", "900922");
//		args.put("operatorNo", operatorNo);
//		args.put("opBranchNo", opBranchNo.toString());
//		args.put("opEntrustWay", opEntrustWay);
//		args.put("opStation", opStation);
//		args.put("branchNo", branchNo.toString());
//		args.put("actionUrl", "/openGemPermessionExt");
//		args.put("terminalType", terminalType);
//		args.put("firstExchdate", firstExchdate);
//		args.put("asset", avgAsset);
//		args.put("age", age);
//		args.put("currBusinRiskLevel", currJson.get("curr_busin_risk_level"));
//		args.put("currBusinInvestKind", currJson.get("curr_busin_invest_kind"));
//		args.put("currBusinInvestTerm", currJson.get("curr_busin_invest_term"));
//		args.put("currCorpRiskLevel", currJson.get("curr_corp_risk_level"));
//		args.put("currInvestKind", currJson.get("curr_invest_kind"));
//		args.put("currInvestTerm", currJson.get("curr_invest_term"));
//		args.put("currProfType", currJson.get("curr_prof_type"));
//		args.put("isPass", currJson.get("isPass"));
//		args.put("opRemark", StringUtils.NVL(remark, "NBOP周边创业板权限开通"));
//		args.put("agreements", signMap.get("agreements"));
//		args.put("agreementsInfo", signMap.get("agreementsInfo"));
//		args.put("agreementsExemption", signMap.get("agreementsExemption"));
//		//BOPS-10223:20200507,hanjin,add,增加适当性字段的记录
//		args.put("signDate", signDate);
//		args.put("riskSubType", riskSubType);
//		args.put("isExistJRights", isExistJRights);
//
//		//BOPS-10301:20200512,hanjin,mod,调整为使用可归历史的方法调用
//		//Map<String, String> mapAcpt = businAcceptService.saveBusinAcptFormAndArgNotClear(args);
//		//Map<String, String> mapAcpt = businAcceptService.saveBusinAcptFormAndArg(args);
//		//BOPS-10436:20200525,hanjin,mod,对于跑批模式调整为不归历史（由日终初始化控制处理）、对于直接开通权限模式调整为直接归历史的处理
//		Map<String, String> mapAcpt = new HashMap<String, String>();
//		if(char_1000135 == '1') {
//			mapAcpt = businAcceptService.saveBusinAcptFormAndArgNotClear(args);
//			retLast.put("acptId", mapAcpt.get("acptId"));
//		}else {
//			mapAcpt = businAcceptService.saveBusinAcptFormAndArg(args);
//			retLast.put("acptId", mapAcpt.get("acptId"));
//		}
//		//businAcceptService.saveBusinAcptFormAndArg(args);
//
//		if(char_1000135 == '1') {
//			String opRemark = this.saveBatchTaskBusi(opBranchNo, operatorNo, opStation, opEntrustWay,
//					900922, 202006, branchNo, " ", mapAcpt.get("acptId"));//此处跑批默认将acptStatus状态更新为'6'
//			retLast.put("successList", new ArrayList<Map<String, String>>());
//			retLast.put("failList", failList);
//			retLast.put("error_no", "0");
//			retLast.put("error_info", "已生成跑批任务，请稍后查询处理进度！" + opRemark);
//			retLast.put("sub_risk_date", "0");
//			retLast.put("right_open_date", "0");
//			retLast.put("risk_sub_type", "");
//			retLast.put("isRealEffect", "0");
//		}
//
//		MonitorUtils.increment("openGemPermessionExt.approp.count");
//		logger.debug("Service openGemPermessionExt  save businacptform over ready return");
//		return retLast;
//	}
//
//	public Integer qryClientAge(String clientId) throws BopsException
//	{
//		Map clientEligInfo = (Map)outerInterfaceService.qryClientEligInfo(clientId,"0");
//		Map clientMap = (Map)clientEligInfo.get("client");
//		Map clientinfoMap = new HashMap();
//		if(clientMap.get("organ_flag").toString().equals(Constant.ORGAN_FLAG_PERSON)){
//			clientinfoMap = (Map)clientEligInfo.get("clientinfo");
//		}else {
//			return 0;
//		}
//
//		// 个人户，需要获取客户其他信息，年龄等信息后续适当性将用得到
//		if((clientinfoMap.get("birthday") != null) && (clientinfoMap.get("birthday").toString().length() == 8)){
//			String birthday = clientinfoMap.get("birthday").toString();
//			return StringUtils.getAgeByBirthday(birthday);
//		}
//		else{
//			if(clientMap.get("id_kind").equals("0")){
//				String idNo = clientMap.get("id_no").toString();
//				String birthday = IdentifyUtils.getIdCardBirthDay(idNo);
//				return StringUtils.getAgeByBirthday(birthday);
//			}
//			else{
//				throw new BopsException(BopsException.ERR_NBOP_BUSIN_CLIENT_NEEDUPDATEIDINFO,BopsException.ERR_NBOP_BUSIN_CLIENT_NEEDUPDATEIDINFO_INFO);
//			}
//		}
//	}
//
//	private void callCancelGem(String opStation, String opEntrustWay, String operatorNo, Integer opBranchNo, String terminalType, String clientId, Integer branchNo,
//			String fullName, String idKind, String idNo, String financeType, String exchangeType, Map<String, String> todo, String cancelRight, String remark)
//			throws BopsException {
//
//		Map<String, Object> reqParam = new HashMap<String, Object>();
//		reqParam.put("op_entrust_way", opEntrustWay);
//		reqParam.put("op_branch_no", opBranchNo);
//		reqParam.put("operator_no", operatorNo);
//		reqParam.put("op_station", opStation);
//		reqParam.put("client_id", clientId);
//		reqParam.put("branch_no", branchNo);
//		reqParam.put("fund_account", todo.get("fundAccount"));
//		reqParam.put("stock_account", todo.get("stockAccount"));
//		reqParam.put("exchange_type", exchangeType);
//		reqParam.put("del_one", cancelRight);// 创业板权限
//		reqParam.put("op_remark", remark);
//
//		// 注销会联动销普通、信用。NBOP设计要求分开不联动。故用权限删除
//		String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_STOCKHOLDER_RIGHTSONE_DELETE);
//
//		try {
//			JSONObject respObj = new RestClient().postForJsonObj(uri, reqParam);
//			logger.info2("创业板注销：{}", respObj);
//		} catch (Exception e) {
//			if (e instanceof BopsException) {
//				throw e;
//			} else {
//				logger.logException(e);
//				throw new BopsException("-1", e.getMessage());
//			}
//		}
//	}
//
//	/**
//	 * @Author yuy
//	 * @Date 20200619
//	 * @Description 创业板权限注销
//	 * @Link BOPS-10674
//	 **/
//	public ExtMulitStockAccountTradeOutput cancelGem(String opStation, String opEntrustWay, String operatorNo, Integer opBranchNo, String terminalType, String clientId,
//			Integer branchNo, String fullName, String idKind, String idNo, String financeType, String exchangeType, List<Map<String, String>> todoList, String remark, Map signMap)
//			throws BopsException {
//
//		ExtMulitStockAccountTradeOutput output = new ExtMulitStockAccountTradeOutput();
//		output.setFailList(new ArrayList<ExtMulitStockAccountTradeOutput.FailInfo>());
//		output.setSuccList(new ArrayList<String>());
//
//		for (int dealPoi = 0; dealPoi < todoList.size(); dealPoi++) {
//			Map<String, String> todo = todoList.get(dealPoi);
//			try {
//				if (todo.get("holderRights").contains("j")) {// 老创业板权限
//					callCancelGem(opStation, opEntrustWay, operatorNo, opBranchNo, terminalType, clientId, branchNo, fullName, idKind, idNo, financeType, exchangeType, todo, "j",
//							"[nbop创业板准入制权限取消]");
//				}
//				if (todo.get("holderRights").contains("W")) {// 新创业板权限
//					callCancelGem(opStation, opEntrustWay, operatorNo, opBranchNo, terminalType, clientId, branchNo, fullName, idKind, idNo, financeType, exchangeType, todo, "W",
//							"[nbop创业板注册制权限取消]");// 有批量根据变更流水的remark拉数，修改请注意
//				}
//				output.getSuccList().add(todo.get("stockAccount"));
//			} catch (BopsException e) {
//				output.setErrorNo(e.getErrCode());
//				output.setErrorInfo(e.getErrMessage());
//				FailInfo failInfo = output.new FailInfo();
//				failInfo.setStockAccount(todo.get("stockAccount"));
//				failInfo.setReason(output.getErrorInfo());
//				output.getFailList().add(failInfo);
//				if (todo.get("assetProp").equals("7")) {// 信用户销失败，剩下的都不销
//					for (dealPoi = dealPoi + 1; dealPoi < todoList.size(); dealPoi++) {
//						todo = todoList.get(dealPoi);
//						output.setErrorNo("-1");
//						output.setErrorInfo("信用账户注销失败，普通账户不可注销！");
//						failInfo = output.new FailInfo();
//						failInfo.setStockAccount(todo.get("stockAccount"));
//						failInfo.setReason(output.getErrorInfo());
//						output.getFailList().add(failInfo);
//					}
//					break;// 信用户销失败，剩下的都不销
//				}
//			}
//		}
//
//		Map<String, Object> acptform = new HashMap<String, Object>();
//		acptform.put("businOpType", "900923");
//		acptform.put("actionUrl", "/cancelGem");
//		acptform.put("terminalType", terminalType);
//		acptform.put("operatorNo", operatorNo);
//		acptform.put("opBranchNo", opBranchNo);
//		acptform.put("opEntrustWay", opEntrustWay);
//		acptform.put("opStation", opStation);
//		acptform.put("agreements", signMap.get("agreements"));
//		acptform.put("agreementsInfo", signMap.get("agreementsInfo"));
//		acptform.put("agreementsExemption", signMap.get("agreementsExemption"));
//		acptform.put("clientId", clientId);
//		acptform.put("branchNo", branchNo);
//		acptform.put("fullName", fullName);
//		acptform.put("idKind", idKind);
//		acptform.put("idNo", idNo);
//		acptform.put("stockAccountInfo", JSON.toJSONString(todoList));
//		acptform.put("exchangeType", exchangeType);
//		acptform.put("successList", JSON.toJSONString(output.getSuccList()));
//		acptform.put("failList", JSON.toJSONString(output.getFailList()));
//		if (output.getFailList().size() == 0) {
//			acptform.put("acptStatus", "7"); // 业务成功
//			acptform.put("remark", StringUtils.NVL(remark, "创业板权限注销"));
//			output.setRemark("创业板权限注销成功");
//		} else {
//			acptform.put("acptStatus", "8"); // 业务失败
//			acptform.put("remark", "创业板权限注销失败");
//		}
//		businAcceptService.saveBusinAcptFormAndArg(acptform);
//
//		MonitorUtils.increment("cancelGem.approp.count");
//		return output;
//	}
//
//	/**
//	 * @Author liyu
//	 * @Date   20200731
//	 * @Description 专业投资者评定回访提交接口
//	 * @Link   BOPS-11013**/
//	public CommonResponseForm<JSONObject> investorJudgeVisit(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			Integer serviceId, String acptId, String acptStatus, ReturnVisitor opContent) throws BopsException{
//
//		Map<String,Object> businacptform = acptDao.qryBusinAcptformById(acptId);
//		if (businacptform == null || businacptform.isEmpty()){
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "受理记录不存在！acptId = ".concat(acptId));
//		}
//		String oriStatus = businacptform.get("acptStatus").toString();
//
//		if (!acptStatus.equals("16") && !acptStatus.equals("17") && !acptStatus.equals("18") && !acptStatus.equals("19")){
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("此接口只允许回访状态16：待回访 ，17：回访中，18：回访成功，19：回访失败，入参 回访状态错误！acptStatus = ".concat(acptStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "此接口只允许回访状态16：待回访 ，17：回访中，18：回访成功，19：回访失败，入参 回访状态错误！acptStatus = ".concat(acptStatus));
//		}
//
//		/*16  待回访  17  回访中  18  回访成功  19  回访失败 */
//		if (acptStatus.equals("16") && !oriStatus.equals("17")){
//			//如果要改为待回访那么原状态必须是回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为待回访那么原状态必须是回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为待回访那么原状态必须是回访中，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("17") && !oriStatus.equals("16")){
//			//如果要改为回访中那么原状态必须是待回访
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访中那么原状态必须是待回访，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访中那么原状态必须是待回访，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("18") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访成功那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访成功那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访成功那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//		}
//
//		if (acptStatus.equals("19") && (!oriStatus.equals("16") && !oriStatus.equals("17"))){
//			//如果要改为回访失败那么原状态必须是待回访或者回访中
//			BopsException exception = new BopsException();
//			exception.setErrCode(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR);
//			exception.setErrMessage("如果要改为回访失败那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//			JSONObject jsonObject = new JSONObject();
//			jsonObject.put("currentStatus", oriStatus);
//			exception.setRestData(jsonObject);
//			throw exception;
//			//throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_ACPTSTATUS_ERROR, "如果要改为回访失败那么原状态必须是待回访或者回访中，原状态为： ".concat(oriStatus));
//		}
//
//		String retObject = "";
//		JSONObject retJson =  new JSONObject();
//		CommonResponseForm<Map<String, Object>> extCommonResponseForm = new CommonResponseForm<Map<String, Object>>();
//		Map<String, Object> map = new HashMap<String, Object>();
//		if (acptStatus.equals("18")){
//			Integer oriOpBranchNo = Integer.valueOf(businacptform.get("opBranchNo").toString());
//			String oriOperatorNo = businacptform.get("operatorNo").toString();
//			String oriOpStation = businacptform.get("opStation").toString();
//			String oriOpEntrustWay = businacptform.get("opEntrustWay").toString();
//			Integer oriBranchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//			Integer menuId = Integer.valueOf(businacptform.get("menuId").toString());
//			Integer businOpType = Integer.valueOf(businacptform.get("businOpType").toString());
//
//			//opRemark = saveBatchTaskBusi(oriOpBranchNo, oriOperatorNo, oriOpStation, oriOpEntrustWay,
//			//		businOpType, menuId, oriBranchNo, " ", acptId);
//			//add by fanlei 由于servlet中使用的是serviceId复核，要重置 BOPS-2941
//			serviceId = Integer.valueOf(businOpType);
//			try {
//				retObject = businAcptformService.addBatchTaskBusi(opBranchNo, operatorNo,
//						opStation, opEntrustWay, serviceId, menuId, oriBranchNo, " ", acptId, String.valueOf(businOpType));
//			} catch (BopsException e) {
//				// TODO Auto-generated catch block
//				logger.info(e);
//				extCommonResponseForm.setErrorNo(e.getErrCode());
//				extCommonResponseForm.setErrorInfo(e.getErrMessage());
//				extCommonResponseForm.setRemark("统一受理接口调用失败");
//				extCommonResponseForm.setData(map);
//				throw new BopsException(e.getErrCode(), e.getErrMessage());
//				//return extCommonResponseForm;
//			}
//			saveVisitToProof(acptId, String.valueOf(businOpType), opContent);
//			//retJson.put("remark", "柜面业务已提交，需要进行后台复核！");
//		}
//
//
//		//更新受理状态
//		String opString = JSONObject.toJSONString(opContent);
//		Integer branchNo = Integer.valueOf(businacptform.get("branchNo").toString());
//		CommonResponseForm<String> retStringForm = updateBusinAcptform(opBranchNo, operatorNo, serviceId, opStation, opEntrustWay, branchNo, acptId, "", ""
//				, "", "", "", "", "", opString, "", acptStatus, ""
//				, "", 0, "");
//
//		//BOPS-10288:20200519,wangxp,mod,更新状态挪到最后执行
//		if (!acptStatus.equals("18")) {
//			retJson.put("remark", retStringForm.getRemark());
//		}
//
//		CommonResponseForm<JSONObject> extCommonResponseFormTmp = new CommonResponseForm<JSONObject>();
//		extCommonResponseFormTmp.setData(retJson);
//		extCommonResponseFormTmp.setErrorNo("0");
//		extCommonResponseFormTmp.setErrorInfo("");
//		extCommonResponseFormTmp.setRemark(retObject);
//		return extCommonResponseFormTmp;
//	}
//
//	/**
//	 * @Author yuy
//	 * @Date 20200819
//	 * @Description 客户资料查询
//	 * @Link BOPS-10152
//	 **/
//	public RestResult<ExtClientTotalOutput> qryClientTotal(String clientId, boolean qryTax) throws BopsException {
//
//		ExtClientTotalOutput output = new ExtClientTotalOutput();
//
//		String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CLIENTTOTALINFO_GET);
//
//		Map<String, Object> reqParam = new HashMap<>();
//		reqParam.put("clientId", clientId);
//		reqParam.put("actionIn", "1");
//
//		JSONObject totalInfo = null;
//		try {
//			totalInfo = new RestClient().getForJsonObj(uri, reqParam);
//			logger.debug2("客户资料查询返回：{}", totalInfo);
//		} catch (Exception e) {
//			logger.error("客户资料查询失败 args = {}", reqParam);
//			if (e instanceof BopsException) {
//				throw e;
//			}
//			logger.logException(e);
//			throw new BopsException("-1", e.getMessage());
//		}
//
//		Client clientBean = JSON.toJavaObject(totalInfo.getJSONObject("client"), Client.class);
//// BOPS-16758 : 增加对非个人户的信息查询支持
////		if (!"0".equals(clientBean.getOrganFlag())) {
////			throw new BopsException("-1", "非个人户不支持查询");
////		}
//		output.setClient(clientBean);
//
//		ClientPrefer preferBean = JSON.toJavaObject(totalInfo.getJSONObject("clientprefer"), ClientPrefer.class);
//		output.setClientPrefer(preferBean);
//
//		if ("0".equals(clientBean.getOrganFlag())) {
//			ClientInfo infoBean = JSON.toJavaObject(totalInfo.getJSONObject("clientinfo"), ClientInfo.class);
//			output.setClientInfo(infoBean);
//		} else {
//			OrganInfo organBean = JSON.toJavaObject(totalInfo.getJSONObject("organInfo"), OrganInfo.class);
//			output.setOrganInfo(organBean);
//		}
//
//		output.setClientInfoX(new HashMap<String, String>());
//		JSONArray infoX = totalInfo.getJSONArray("clientinfox");
//		for (int i = 0; infoX != null && i < infoX.size(); i++) {
//			JSONObject extInfo = infoX.getJSONObject(i);
//			String key = StringUtils.underLineToCamel(extInfo.getString("field_name"));
//			output.getClientInfoX().put(key, extInfo.getString("field_value"));
//		}
//
//		if (qryTax) {// BOPS-15814:支持查税收信息
//			JSONObject taxInfo = outerInterfaceService.qryTaxinfoByKey(clientBean.getClientId(), clientBean.getOrganFlag());
//			if ("0".equals(clientBean.getOrganFlag())) {
//				Persontaxinfo tax = JSON.toJavaObject(taxInfo.getJSONObject("data"), Persontaxinfo.class);
//				output.setPersonTax(tax);
//			} else {
//				Organtaxinfo tax = JSON.toJavaObject(taxInfo.getJSONObject("data"), Organtaxinfo.class);
//				output.setOrganTax(tax);
//			}
//		}
//
//		return new RestResult<ExtClientTotalOutput>(output);
//	}
//
//	/**
//	 * @Author liyu
//	 * @Date   20200921
//	 * @Description 根据客户号获取待回访受理单
//	 * @Link   BOPS-12283**/
//	public List<Map<String,Object>> qryBusinacptformByClientId(String clientId) throws BopsException {
//
//		List<Map<String,Object>> retList  = acptDao.qryBusinacptformByClientId(clientId);
//		List<Map<String,Object>> retListDstList = new ArrayList<>();
//
//		//临柜专业投资者回访需要特殊处理
//		for (int i = 0; i < retList.size(); i++) {
//			Map<String, Object> acptForm = retList.get(i);
//			String businOpType = acptForm.get("businOpType").toString();
//			if(!businOpType.equals("900016") && !businOpType.equals("900921")) {
//				//科创板回访
//				if(businOpType.equals("900135")) {
//					String acptId = acptForm.get("acptId").toString();
//					List<Map<String, Object>> businFormArg = businAcptformService.getBusinAcptFormArg(acptId,
//							"jgjStr");// 先查出满足条件的记录，然后根据acptId获取相关信息
//
//					String paramStr = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get(0).get("paramValue"),"").toString();
//					JSONObject paramJson = JSON.parseObject(paramStr);
//
//					String riskLevel;
//					if (!StringUtils.isBlank(paramJson.getString("string4"))) {
//						riskLevel = cacheGfDictionary.getSysdictionaryByBranch("2505", Constant.SYS_BRANCH_NO,
//								paramJson.getString("string4")).get("dict_prompt");
//					} else {
//						riskLevel = paramJson.getString("string4");
//					}
//
//					acptForm.put("isFocusClient",  paramJson.getIntValue("kind"));
//					acptForm.put("riskLevel", riskLevel);
//					acptForm.put("custInvestKind", paramJson.getString("string5"));
//					acptForm.put("custInvestTerm", paramJson.getString("string6"));// : "客户投资期限",
//					acptForm.put("matchResult", "1".equals(paramJson.getString("string7")) ? "匹配" : "不匹配");// "适当性匹配结果"
//					acptForm.put("unMatchCause", paramJson.getString("string8"));// : "不匹配原因"
//					acptForm.put("accountRight", paramJson.getString("string3"));// : "开通权限是信用or普通账户",
//
//				}
//
//				//新三板板回访
//				if(businOpType.equals("900919")) {
//					String acptId = acptForm.get("acptId").toString();
//					Map<String, Object> businFormArg = businAcptformService.getBusinAcptformArg(acptId,"enSstReportType","currEnInvestTerm","currEnInvestKind","isPass","matchResult","currCorpRiskLevel");
//					String enSstReportType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("enSstReportType"),"").toString();
//					String currEnInvestTerm = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currEnInvestTerm"),"").toString();
//					String currEnInvestKind = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currEnInvestKind"),"").toString();
//					String isPass = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("isPass"),"").toString();
//                    String investKind= new String();
//
//                    String matchResult = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("matchResult"),"").toString();
//					StringBuilder stringBuilder = new StringBuilder();
//					//拼接三要素不匹配原因
//					if(matchResult.length()>=1){
//						if(matchResult.substring(0, 1).equals("0")){
//							stringBuilder.append("风险等级匹配、");
//						}else{
//							stringBuilder.append("风险等级不匹配、");
//						}
//					}
//
//					if(matchResult.length()>=2){
//						if(matchResult.substring(1, 2).equals("0")){
//							stringBuilder.append("投资期限匹配、");
//						}else{
//							stringBuilder.append("投资期限不匹配、");
//						}
//					}
//
//					if(matchResult.length()>=3){
//						if(matchResult.substring(2, 3).equals("0")){
//							stringBuilder.append("投资品种匹配");
//						}else{
//							stringBuilder.append("投资品种不匹配");
//						}
//
//					}
//
//					if("3".equals(enSstReportType)){
//						investKind = "一类";
//					}else if("4".equals(enSstReportType)){
//						investKind = "二类";
//					}else if("5".equals(enSstReportType)){
//						investKind = "三类";
//					}
//
//					String riskLevel;
//					if (!StringUtils.isBlank(com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currCorpRiskLevel"),"").toString())) {
//						riskLevel = cacheGfDictionary.getSysdictionaryByBranch("2505", Constant.SYS_BRANCH_NO,
//								com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currCorpRiskLevel"),"").toString()).get("dict_prompt");
//					} else {
//						riskLevel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currCorpRiskLevel"),"").toString();
//					}
//
//					acptForm.put("investKind", investKind);
//					acptForm.put("riskLevel", riskLevel);// : "客户风险等级",
//					acptForm.put("custInvestKind", currEnInvestKind);// : "客户投资品种",
//					acptForm.put("custInvestTerm", currEnInvestTerm);// : "客户投资期限",
//					acptForm.put("matchResult", "1".equals(isPass) ? "匹配" : "不匹配");// "适当性匹配结果"
//					if("1".equals(isPass)){
//						//如果匹配，不匹配的原因就置空
//						acptForm.put("unMatchCause", "");// : "不匹配原因"
//					}else{
//						acptForm.put("unMatchCause", stringBuilder.toString());// : "不匹配原因"
//					}
//
//				}
//
//				retListDstList.add(acptForm);
//				continue;
//			}else {
//				//专业投资者回访
//				String acptId = acptForm.get("acptId").toString();
//				Map<String, Object> businFormArg = null ;
//
//				String organFlag = acptForm.get("organFlag").toString();
//				String mobileTel = new String();
//				String clientName = acptForm.get("fullName").toString();
//				String relationName = new String();
//
//				if(businOpType.equals("900016")) {
//					businFormArg = businAcptformService.getBusinAcptformArg(acptId,"relationName","requestType","operatorType","currMobileTel");
//					relationName = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("relationName"),"").toString();
//					mobileTel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("currMobileTel"),"").toString();
//
//				}else {
//					businFormArg = businAcptformService.getBusinAcptformArg(acptId,"requestType","operatorType","relationPhone");
//					mobileTel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("relationPhone"),"").toString();
//				}
//
//				String requestType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("requestType"),"").toString();
//				String operatorType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("operatorType"),"").toString();
//
//				String clientType = new String();
//				if(organFlag.equals("0")) {
//					clientType = "个人";
//				}else {
//					clientType = "机构";
//				}
//
//				String requestTypeMent = new String();
//				String operatorTypeMent = new String();
//
//				if(requestType.equals("0") || requestType.equals("1") || requestType.equals("2") || requestType.equals("4")) {
//					continue;
//				}else if (requestType.equals("3")) {
//					//临柜普通认定为A4类专业投资者，机构不需要回访
//					if(businOpType.equals("900016") && (!organFlag.equals("0"))) {
//						continue;
//					}
//					requestTypeMent = "普通认定A4类投资者";
//				}else if (requestType.equals("5")){
//					//临柜B类认定为A4类专业投资者，机构不需要回访
//					if(businOpType.equals("900016") && (!organFlag.equals("0"))) {
//						continue;
//					}
//					requestTypeMent = "B类评定A4类投资者";
//				}
//
//				if(operatorType.equals("0") || operatorType.equals("3")) {
//						continue;
//				}else if (operatorType.equals("1")) {
//					operatorTypeMent = "普通投资者转B类专业投资者";
//				}
//
//				acptForm.put("clientType",  clientType);
//				acptForm.put("mobileTel", mobileTel);
//				if(businOpType.equals("900016")) {
//					acptForm.put("clientName", relationName);// 联系人
//				}else {
//					acptForm.put("clientName", clientName);// 联系人
//				}
//
//				acptForm.put("requestTypeMent", requestTypeMent);// 申请类型
//				acptForm.put("operatorTypeMent", operatorTypeMent);// 操作类型
//				retListDstList.add(acptForm);
//				continue;
//			}
//
//		}
//
//       return retListDstList;
//	}
//
//	/**
//	 * @Author liyu
//	 * @Date   20200921
//	 * @Description 根据客户号获取受理单状态
//	 * @Link   BOPS-12283**/
//	public List<Map<String,Object>> qryBusinacptformStatusByClientId(String clientId) throws BopsException {
//
//		List<Map<String,Object>> retList  = acptDao.qryBusinacptformStatusByClientId(clientId);
//		List<Map<String,Object>> retListDstList = new ArrayList<>();
//
//		//临柜专业投资者回访需要特殊处理
//		for (int i = 0; i < retList.size(); i++) {
//			Map<String, Object> acptForm = retList.get(i);
//			String businOpType = acptForm.get("businOpType").toString();
//			if(!businOpType.equals("900016") && !businOpType.equals("900921")) {
//				//科创板回访
//				if(businOpType.equals("900135")) {
//					String acptId = acptForm.get("acptId").toString();
//					List<Map<String, Object>> businFormArg = businAcptformService.getBusinAcptFormArg2(acptId,
//							"jgjStr");// 先查出满足条件的记录，然后根据acptId获取相关信息
//					if(businFormArg.size()>0) {
//						String paramStr = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get(0).get("paramValue"),"").toString();
//						JSONObject paramJson = JSON.parseObject(paramStr);
//
//						String riskLevel;
//						if (!StringUtils.isBlank(paramJson.getString("string4"))) {
//							riskLevel = cacheGfDictionary.getSysdictionaryByBranch("2505", Constant.SYS_BRANCH_NO,
//									paramJson.getString("string4")).get("dict_prompt");
//						} else {
//							riskLevel = paramJson.getString("string4");
//						}
//
//						acptForm.put("isFocusClient",  paramJson.getIntValue("kind"));
//						acptForm.put("riskLevel", riskLevel);
//						acptForm.put("custInvestKind", paramJson.getString("string5"));
//						acptForm.put("custInvestTerm", paramJson.getString("string6"));// : "客户投资期限",
//						acptForm.put("matchResult", "1".equals(paramJson.getString("string7")) ? "匹配" : "不匹配");// "适当性匹配结果"
//						acptForm.put("unMatchCause", paramJson.getString("string8"));// : "不匹配原因"
//						acptForm.put("accountRight", paramJson.getString("string3"));// : "开通权限是信用or普通账户",
//					}
//
//				}
//
//				//新三板板回访
//				if(businOpType.equals("900919")) {
//					String acptId = acptForm.get("acptId").toString();
//					StringBuilder stringBuilder = new StringBuilder();
//					String investKind= new String();
//					String enSstReportType= new String();
//					String currEnInvestTerm= new String();
//					String currEnInvestKind= new String();
//					String isPass= new String();
//					String matchResult= new String();
//					String currCorpRiskLevel= new String();
//
//					List<Map<String, Object>> businFormArg1 = businAcptformService.getBusinAcptFormArg2(acptId,"enSstReportType");
//					if(businFormArg1.size()>0) {
//						enSstReportType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg1.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg2 = businAcptformService.getBusinAcptFormArg2(acptId,"currEnInvestTerm");
//					if(businFormArg2.size()>0) {
//						currEnInvestTerm = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg2.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg3 = businAcptformService.getBusinAcptFormArg2(acptId,"currEnInvestKind");
//					if(businFormArg3.size()>0) {
//						currEnInvestKind = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg3.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg4 = businAcptformService.getBusinAcptFormArg2(acptId,"currCorpRiskLevel");
//					if(businFormArg4.size()>0) {
//						currCorpRiskLevel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg4.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg5 = businAcptformService.getBusinAcptFormArg2(acptId,"isPass");
//					if(businFormArg5.size()>0) {
//						isPass = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg5.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg6 = businAcptformService.getBusinAcptFormArg2(acptId,"matchResult");
//					if(businFormArg6.size()>0) {
//						matchResult = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg6.get(0).get("paramValue"),"").toString();
//					}
//
//					//拼接三要素不匹配原因
//					if(matchResult.length()>=1){
//						if(matchResult.substring(0, 1).equals("0")){
//							stringBuilder.append("风险等级匹配、");
//						}else{
//							stringBuilder.append("风险等级不匹配、");
//						}
//					}
//
//					if(matchResult.length()>=2){
//						if(matchResult.substring(1, 2).equals("0")){
//							stringBuilder.append("投资期限匹配、");
//						}else{
//							stringBuilder.append("投资期限不匹配、");
//						}
//					}
//
//					if(matchResult.length()>=3){
//						if(matchResult.substring(2, 3).equals("0")){
//							stringBuilder.append("投资品种匹配");
//						}else{
//							stringBuilder.append("投资品种不匹配");
//						}
//
//					}
//
//					if("3".equals(enSstReportType)){
//						investKind = "一类";
//					}else if("4".equals(enSstReportType)){
//						investKind = "二类";
//					}else if("5".equals(enSstReportType)){
//						investKind = "三类";
//					}
//
//					String riskLevel;
//					if (!StringUtils.isBlank(currCorpRiskLevel)) {
//						riskLevel = cacheGfDictionary.getSysdictionaryByBranch("2505", Constant.SYS_BRANCH_NO, currCorpRiskLevel).get("dict_prompt");
//					} else {
//						riskLevel = currCorpRiskLevel;
//					}
//
//					acptForm.put("investKind", investKind);
//					acptForm.put("riskLevel", riskLevel);// : "客户风险等级",
//					acptForm.put("custInvestKind", currEnInvestKind);// : "客户投资品种",
//					acptForm.put("custInvestTerm", currEnInvestTerm);// : "客户投资期限",
//					acptForm.put("matchResult", "1".equals(isPass) ? "匹配" : "不匹配");// "适当性匹配结果"
//					if("1".equals(isPass)){
//						//如果匹配，不匹配的原因就置空
//						acptForm.put("unMatchCause", "");// : "不匹配原因"
//					}else{
//						acptForm.put("unMatchCause", stringBuilder.toString());// : "不匹配原因"
//					}
//
//				}
//
//				retListDstList.add(acptForm);
//				continue;
//			}else {
//				//专业投资者回访
//				String acptId = acptForm.get("acptId").toString();
//				String organFlag = acptForm.get("organFlag").toString();
//				String mobileTel = new String();
//				String clientName = acptForm.get("fullName").toString();
//				String relationName = new String();
//				String requestType = new String();
//				String operatorType = new String();
//
//				if(businOpType.equals("900016")) {
//					List<Map<String, Object>> businFormArg1 = businAcptformService.getBusinAcptFormArg2(acptId,"relationName");
//					if(businFormArg1.size()>0) {
//						relationName = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg1.get(0).get("paramValue"),"").toString();
//					}
//
//					List<Map<String, Object>> businFormArg2 = businAcptformService.getBusinAcptFormArg2(acptId,"currMobileTel");
//					if(businFormArg2.size()>0) {
//						mobileTel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg2.get(0).get("paramValue"),"").toString();
//					}
//
//				}else {
//					List<Map<String, Object>> businFormArg1 = businAcptformService.getBusinAcptFormArg2(acptId,"relationPhone");
//					if(businFormArg1.size()>0) {
//						mobileTel = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg1.get(0).get("paramValue"),"").toString();
//					}
//				}
//
//				List<Map<String, Object>> businFormArg3 = businAcptformService.getBusinAcptFormArg2(acptId,"requestType");
//				if(businFormArg3.size()>0) {
//					requestType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg3.get(0).get("paramValue"),"").toString();
//				}
//
//				List<Map<String, Object>> businFormArg4 = businAcptformService.getBusinAcptFormArg2(acptId,"operatorType");
//				if(businFormArg4.size()>0) {
//					operatorType = com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg4.get(0).get("paramValue"),"").toString();
//				}
//
//				String clientType = new String();
//				if(organFlag.equals("0")) {
//					clientType = "个人";
//				}else {
//					clientType = "机构";
//				}
//
//				String requestTypeMent = new String();
//				String operatorTypeMent = new String();
//
//				if(requestType.equals("0") || requestType.equals("1") || requestType.equals("2") || requestType.equals("4")) {
//					continue;
//				}else if (requestType.equals("3")) {
//					//临柜普通认定为A4类专业投资者，机构不需要回访
//					if(businOpType.equals("900016") && (!organFlag.equals("0"))) {
//						continue;
//					}
//					requestTypeMent = "普通认定A4类投资者";
//				}else if (requestType.equals("5")){
//					//临柜B类认定为A4类专业投资者，机构不需要回访
//					if(businOpType.equals("900016") && (!organFlag.equals("0"))) {
//						continue;
//					}
//					requestTypeMent = "B类评定A4类投资者";
//				}
//
//				if(operatorType.equals("0") || operatorType.equals("3")) {
//						continue;
//				}else if (operatorType.equals("1")) {
//					operatorTypeMent = "普通投资者转B类专业投资者";
//				}
//
//				acptForm.put("clientType",  clientType);
//				acptForm.put("mobileTel", mobileTel);
//				if(businOpType.equals("900016")) {
//					acptForm.put("clientName", relationName);// 联系人
//				}else {
//					acptForm.put("clientName", clientName);// 联系人
//				}
//
//				acptForm.put("requestTypeMent", requestTypeMent);// 申请类型
//				acptForm.put("operatorTypeMent", operatorTypeMent);// 操作类型
//				retListDstList.add(acptForm);
//				continue;
//			}
//		}
//		return retListDstList;
//	}
//
//	/**
//	 * @Author liyu
//	 * @Date 20201207
//	 * @Description 证券理财账户信息查询
//	 * @Link BOPS-13739
//	 **/
//	public JSONArray qrySecumHolder(String prodtaNo, String operatorNo, String opBranchNo, String opEntrustWay, String opStation, String idNo, String idKind, String clientName) throws BopsException {
//
//		String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_QRY_CLIENT);
//		Map<String, Object> retMap = new HashMap<>();
//		Map<String, Object> reqParam = new HashMap<>();
//
//		retMap.put("idKind", idKind);
//		retMap.put("idNo", idNo);
//		retMap.put("clientName", clientName);
//
//		JSONArray retListDstList = new JSONArray();
//		JSONArray list = new JSONArray();
//
//		try {
//	    	 list = new RestClient().getForJsonObj(uri, retMap).getJSONArray("data");
//		} catch (BopsException e) {
//			logger.error("客户资料查询失败 args = {}", retMap);
//			logger.logException(e);
//			throw new BopsException("-1", e.getMessage());
//		}
//		if(list != null && list.size()>0) {
//			for (int i = 0; i < list.size(); i++){
//				Map<String, String> mapData = (Map)list.get(i);
//				reqParam.put("clientId", mapData.get("client_id"));
//				reqParam.put("prodtaNo", prodtaNo);
//
//			   String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_QRY_SECUM_HOLDER);
//			   try {
//					JSONArray output = new RestClient().getForJsonObj(url, reqParam).getJSONArray("data");
//					retListDstList.addAll(output);
//				} catch (Exception e) {
//					logger.error("理财账号信息查询失败:{}", reqParam);
//					if (e instanceof BopsException) {
//						throw e;
//					} else {
//						throw new BopsException("-1", e.getMessage());
//					}
//				}
//			}
//
//		}
//
//		return retListDstList;
//
//	}
//
//
//	/**
//	 * @Author liyu
//	 * @Date   20201221
//	 * @Description 根据客户编号和TA代码获取基金账户预约开户结果
//	 * @Link   BOPS-14118**/
//	public List<Map<String,Object>> qryBookSecumholder(String clientId,  String prodtaNo) throws BopsException {
//
//		List<Map<String,Object>> retList  = businacptDao.qryBusinacptformByClientAndBusinOpType(clientId, "900154");
//		Map<String, Object> acptForm = new HashMap<>();
//		List<Map<String,Object>> retListDstList = new ArrayList<>();
//
//		if(StringUtils.isEmpty(prodtaNo)) {
//			for (int i = 0; i < retList.size(); i++) {
//				acptForm = retList.get(i);
//				String acptId = acptForm.get("acpt_id").toString();
//				Map<String, Object> businFormArg = businAcptformService.getBusinAcptformArg(acptId,"prodtaNo");
//				acptForm.put("prodta_no", com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("prodtaNo"),"").toString());
//				retListDstList.add(acptForm);
//			}
//
//		}else {
//			for (int i = 0; i < retList.size(); i++) {
//				acptForm = retList.get(i);
//				String acptId = acptForm.get("acpt_id").toString();
//				Map<String, Object> businFormArg = businAcptformService.getBusinAcptformArg(acptId,"prodtaNo");
//				if(com.gf.nbop.plat.commonutils.tools.StringUtils.NVL(businFormArg.get("prodtaNo"),"").toString().equals(prodtaNo)){
//					acptForm.put("prodta_no", prodtaNo);
//					retListDstList.add(acptForm);
//					break;
//				}
//			}
//		}
//
//		return retListDstList;
//
//	}
//
// /**
//	 * 公安核查信息查询
//	 */
//	public Idinfocheck qryIdInfoCheck(String clientId, String idKind, String idNo, String clientName) throws BopsException {
//
//		Idinfocheck idinfocheck = new Idinfocheck();
//		idinfocheck.setClientId(clientId);
//		idinfocheck.setIdKind(idKind);
//		idinfocheck.setIdNo(idNo);
//		idinfocheck.setClientName(clientName);
//		idinfocheck.setIdBegindate(0);
//		idinfocheck.setIdEnddate(0);
//		idinfocheck.setBirthday(0);
//		idinfocheck.setUpdateDate(0);
//		idinfocheck.setUpdateTime(0);
//		idinfocheck.setCheckStatus("0");
//		idinfocheck.setClientGender("");
//		idinfocheck.setIdNation("");
//		idinfocheck.setIdAddress("");
//		idinfocheck.setIssuedDepart("");
//		idinfocheck.setUpFlag("");
//		idinfocheck.setRemark("");
//		idinfocheck.setPositionStrLong("");
//		idinfocheck.setIdAuthType("");
//		idinfocheck.setOpRemark("");
//		idinfocheck.setErrorInfo("");
//
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.YEAR, -1);
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//	    String lastYearDate = formatter.format(cal.getTime());
//
//	    Idinfocheck idinfocheckDst = idinfocheckService.qryIdinfoCheck(clientId, idKind, idNo, clientName, Integer.valueOf(lastYearDate));
//	    if(idinfocheckDst == null) {
//	    	//插入记录
//	    	JSONObject jsonClientInfo = outerInterfaceService.qryClientInfo(clientId);
//	    	idinfocheck.setBranchNo(Integer.valueOf(jsonClientInfo.getString("branch_no")));
//
//	    	idinfocheckService.addIdinfocheck(idinfocheck);
//
//	    	throw new BopsException("-1", "公安核查信息记录不存在，请后续再查询");
//	    }else {
//	    	String checkStatus = idinfocheckDst.getCheckStatus();
//	    	if(!checkStatus.equals("0")) {
//	    		return idinfocheckDst;
//	    	}else {
//	    		throw new BopsException("-1", "公安核查信息记录未进行核查，请后续再查询");
//	    	}
//	    }
//	}
//
//	/**
//	 * @Author liyu
//	 * @Date 20210222
//	 * @Description 获取投易通客户的免密密码token信息
//	 * @Link BOPS-14951
//	 **/
//	public Map<String,Object> getUserTokenForFreeSecretExt(String clientId, String fundAccount, String password,
//		String isPwdEncode, String operatorNo, String opBranchNo, String opEntrustWay, String opStation) throws BopsException {
//		Map<String, Object> tokenMap = new HashMap<>();
//		String passwordCheck = KeyDataEnc.keyDecode(password);
//		String passwordDecode = CommonTools.operPwdEncode(passwordCheck);
//
//		//和后台gfbusin.tytwhitelist后台数据进行密码比对
//		TytWhiteList whiteList = listMapper.getByPK(fundAccount);
//		if (whiteList == null) {
//			throw new BopsException(GFErrorCode.ERR_BOPS_DB_DATA_NOTEXISTS, GFErrorCode.ERR_BOPS_DB_DATA_NOTEXISTS_INFO, "投易通免密白名单");
//		}
//
//		String passwordNew = whiteList.getPassword();
//
//		if(passwordDecode.equals(passwordNew)) {
//			//判断客户是零售客户还是机构客户
//			JSONObject fundAccountJosn = new JSONObject();
//			fundAccountJosn = hsInterfaceService.getFundAccount("1", "", "", "", fundAccount);
//			String sysnodeId = fundAccountJosn.getString("sysnode_id");
//			logger.info("系统节点号为", sysnodeId);
//
//			if(sysnodeId.equals("22")) {  //机构柜台
//				String url = ufics_url + serviceUri.getServUri(serviceUri.SVR_URI_UFICS_USER_TOKEN_GET);
//				Map<String, Object> retMap = new HashMap<>();
//				Map<String, Object> reqParam = new HashMap<>();
//				// mdf by liangyp BOPS-18854 20211108 if isPwdEncode = 1，支持密文调用
//				retMap.put("isPwdEncode", isPwdEncode);
//				retMap.put("password", passwordCheck);
//				retMap.put("passwordType", "2");
//				retMap.put("clientId", clientId);
//				retMap.put("identityType", "");
//				retMap.put("operatorNo", operatorNo);
//				retMap.put("opBranchNo", opBranchNo);
//				retMap.put("opEntrustWay", opEntrustWay);
//		        retMap.put("opStation", opStation);
//
//				try {
//					RestClient restClient = new RestClient();
//					JSONObject restResult = restClient.getForJsonObj(url, retMap);
//					String userToken = restResult.getString("user_token");
//					tokenMap.put("userToken", userToken);
//					tokenMap.put("sysnodeId", sysnodeId);
//
//				} catch (BopsException e) {
//					logger.error("用户token查询失败 args = {}", retMap);
//					logger.logException(e);
//					throw new BopsException("-1", e.getMessage());
//				}
//
//			}else {  //零售柜台
//				String url = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_USER_TOKEN_GET);
//				Map<String, Object> retMap = new HashMap<>();
//				Map<String, Object> reqParam = new HashMap<>();
//				// mdf by liangyp BOPS-18854 20211108 if isPwdEncode = 1，支持密文调用
//				retMap.put("isPwdEncode", isPwdEncode);
//				retMap.put("password", passwordCheck);
//				retMap.put("passwordType", "2");
//				retMap.put("clientId", clientId);
//				retMap.put("identityType", "");
//				retMap.put("operatorNo", operatorNo);
//				retMap.put("opBranchNo", opBranchNo);
//				retMap.put("opEntrustWay", opEntrustWay);
//		        retMap.put("opStation", opStation);
//
//				try {
//					RestClient restClient = new RestClient();
//					JSONObject restResult = restClient.getForJsonObj(url, retMap);
//					String userToken = restResult.getString("user_token");
//					tokenMap.put("userToken", userToken);
//					tokenMap.put("sysnodeId", sysnodeId);
//
//				} catch (BopsException e) {
//					logger.error("用户token查询失败 args = {}", retMap);
//					logger.logException(e);
//					throw new BopsException("-1", e.getMessage());
//				}
//			}
//
//
//
//
//		}else{
//			throw new BopsException("99", "密码校验错误");
//		}
//
//		return tokenMap;
//
//	}
//
//	/**
//	 * @throws BopsException
//	 * @Author hanjin
//	 * @Date   20210308
//	 * @Description 上海交易席位切换登记
//	 * 				shseatOpttype(0-普通转VIP、1-VIP转普通)
//	 * @Link   BOPS-15164
//	 * */
//	public CommonResponseForm<HashMap<String,Object>> shregswitchExt(Integer opBranchNo, String operatorNo, String opStation, String opEntrustWay,
//			String clientId, String fundAccount, String exchangeType, String stockAccount, String shseatOpttype) throws BopsException{
//
//		JSONObject clientJson = outerInterfaceService.qryClientInfo(clientId);
//		if(null == clientJson || !"0".equals(clientJson.getString("client_status"))) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_CLIENT_NOTEXISTS_OR_STATUSERROR
//					, GFErrorCode.ERR_NBOP_BUSIN_CLIENT_NOTEXISTS_OR_STATUSERROR_INFO + String.format("[client_status=%s]", (clientJson==null ? "" : clientJson.getString("client_status"))));
//		}
//
//		JSONObject fundacctJson = outerInterfaceService.qryFundAccountByfundacct(fundAccount);
//		if (null == fundacctJson || !"0".equals(fundacctJson.getString("fundacct_status"))){
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_FUNDACCOUNT_NOTEXISTS_OR_STATUSERROR
//					, GFErrorCode.ERR_NBOP_BUSIN_FUNDACCOUNT_NOTEXISTS_OR_STATUSERROR_INFO + String.format("[fundacct_status=%s]", (fundacctJson==null ? "" : fundacctJson.getString("fundacct_status"))));
//		}
//
//		JSONObject stkacctJson = outerInterfaceService.qryStockHolder(clientId, fundAccount, exchangeType, stockAccount);
//		if(null == stkacctJson || !"0".equals(stkacctJson.getString("holder_status"))) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_STOCKHOLDER_NOTEXISTS_OR_STATUSERROR
//					, GFErrorCode.ERR_NBOP_BUSIN_STOCKHOLDER_NOTEXISTS_OR_STATUSERROR_INFO + String.format("[holder_status=%s]", (stkacctJson==null ? "" : stkacctJson.getString("holder_status"))));
//		}
//
//		String str_1000181 = memCache.getSysconfigStrValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_SEAT_VIPSWITCH_ENABLELIST.toString());//易淘金铂金会员业务当前客户可切换的VIP席位
//		String str_1000182 = memCache.getSysconfigStrValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_SEAT_VIP_LIST.toString());//易淘金铂金会员业务中属于VIP席位的列表
//		String str_1000183 = memCache.getSysconfigStrValue(8888,GfSysConfigContant.CNST_GF_SYSCONFIG_SEAT_CONNECTLIST.toString());//易淘金铂金会员业务中涉及小联通圈的席位信息
//		String currSeatNo = stkacctJson.getString("seat_no");
//		Integer branchNo = stkacctJson.getInteger("branch_no");
//
//		String str_1000181Comma = "," + str_1000181 + ",";
//		String str_1000182Comma = "," + str_1000182 + ",";
//		String str_1000183Comma = "," + str_1000183 + ",";
//		String currSeatNoComma = "," + currSeatNo + ",";
//
//		//校验当前席位是否在小联通圈
//		if(str_1000183Comma.contains(currSeatNoComma)) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_IN_SEAT_CONNECTLIST, GFErrorCode.ERR_NBOP_BUSIN_IN_SEAT_CONNECTLIST_INFO + String.format("[seat_no=%s]", currSeatNo));
//		}
//
//		Shregswitch shregswitchOrg = shregswitchService.getByKey(branchNo, stockAccount, fundAccount, exchangeType);
//		if(null != shregswitchOrg && shregswitchOrg.getDealStatus().equals(SysdictionaryConst.CNST_DEALSTATUS_UNDONE)) {
//			throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_SHREGSWITCH_EXISTS, GFErrorCode.ERR_NBOP_BUSIN_SHREGSWITCH_EXISTS_INFO
//					+ String.format("；未处理完毕的登记信息[dealStatus=%s][branchNo=%s][stockAccount=%s][fundAccount=%s][exchangeType=%s]"
//							, SysdictionaryConst.CNST_DEALSTATUS_UNDONE, branchNo, stockAccount, fundAccount, exchangeType));
//		}
//
//		Shregswitch shregswitchnew = new Shregswitch();
//		shregswitchnew.setBranchNo(branchNo);
//		shregswitchnew.setClientId(clientId);
//		shregswitchnew.setFullName(clientJson.getString("full_name"));
//		shregswitchnew.setFundAccount(fundAccount);
//		shregswitchnew.setExchangeType(exchangeType);
//		shregswitchnew.setStockAccount(stockAccount);
//		shregswitchnew.setShseatOpttype(shseatOpttype);
//		//shregswitchnew.setSrcSeatNo(currSeatNo);
//		//shregswitchnew.setDestSeatNo(str_1000181.split(",")[0]);
//		shregswitchnew.setRegisterDate(StringUtils.getYYYYMMDD(new Date()));
//		shregswitchnew.setDealStatus(SysdictionaryConst.CNST_DEALSTATUS_UNDONE);
//		shregswitchnew.setDealDate(0);
//		shregswitchnew.setRemark("");
//
//		//shseatOpttype(0-普通转VIP、1-VIP转普通)
//		if(SysdictionaryConst.CNST_SHSEAT_OPTTYPE_NORMAL_TO_VIP.equals(shseatOpttype)) {
//			//检验当前席位号是否已经是vip席位号，如是则不允许办理
//			if(str_1000182Comma.contains(currSeatNoComma)) {
//				throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_IN_SEAT_VIPLIST, GFErrorCode.ERR_NBOP_BUSIN_IN_SEAT_VIPLIST_INFO + String.format("[seatNo=%s]", currSeatNo));
//			}
//			shregswitchnew.setSrcSeatNo(currSeatNo);
//			shregswitchnew.setDestSeatNo(str_1000181.split(",")[0]);
//		}else if(SysdictionaryConst.CNST_SHSEAT_OPTTYPE_VIP_TO_NORMAL.equals(shseatOpttype)) {
//			//检验当前席位号是否VIP席位号，如不是则不允许办理
//			if(!str_1000182Comma.contains(currSeatNoComma)) {
//				throw new BopsException(GFErrorCode.ERR_NBOP_BUSIN_NOTIN_SEAT_VIPLIST, GFErrorCode.ERR_NBOP_BUSIN_NOTIN_SEAT_VIPLIST_INFO + String.format("[seatNo=%s]", currSeatNo));
//			}
//
//			//找到之前登记的原始普通->VIP（处理状态为"成功"）的记录
//			if(null != shregswitchOrg && shregswitchOrg.getDealStatus().equals(SysdictionaryConst.CNST_DEALSTATUS_DONE)
//					&& shregswitchOrg.getDestSeatNo().equals(currSeatNo) && shregswitchOrg.getShseatOpttype().equals(SysdictionaryConst.CNST_SHSEAT_OPTTYPE_NORMAL_TO_VIP)) {
//				shregswitchnew.setSrcSeatNo(currSeatNo);
//				shregswitchnew.setDestSeatNo(shregswitchOrg.getSrcSeatNo());
//			}else {
//				//找不到之前登记的原始普通席位信息/当前席位不是上次转换登记后的VIP席位！则取营业部默认的普通席位
//				Map<String, Object> seatargs = new HashMap<String, Object>();
//				String qryUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_HSSEATS_GET);
//				seatargs.put("branchNo", branchNo);
//				seatargs.put("exchangeType", exchangeType);
//				seatargs.put("assetProp", Constant.ASSET_PROP_GENERAL);
//				ArrayList<Map> seats = new RestClient().getForMapList(qryUrl, seatargs);
//				String defaultSeat = "";
//				for(int i=0;i<seats.size();i++) {
//					Map seat = seats.get(i);
//					logger.info("seat = " + seat.toString());
//					if(seat.get("default_mark").toString().equals("1") && seat.get("seatvip_flag").toString().equals("0")){
//						defaultSeat = seat.get("seat_no").toString();
//						logger.info("defaultSeatInfo = " + defaultSeat.toString());
//						break;
//					}
//				}
//				if(StringUtils.isBlank(defaultSeat)) {
//					throw new BopsException(GFErrorCode.ERR_BOPS_PARAM_ILLEGAL,
//							"找不到之前登记的原始普通席位信息/当前席位不是上次转换登记后的VIP席位！且取不到营业部默认的普通席位" + String.format("[branchNo=%s,exchangeType=%s,assetProp=%s]", branchNo, exchangeType, Constant.ASSET_PROP_GENERAL));
//				}
//				shregswitchnew.setSrcSeatNo(currSeatNo);
//				shregswitchnew.setDestSeatNo(defaultSeat);
//			}
//		}else {
//			throw new BopsException(GFErrorCode.ERR_BOPS_PARAM_ILLEGAL, GFErrorCode.ERR_BOPS_PARAM_ILLEGAL_INFO + String.format("[shseatOpttype=%s]", shseatOpttype));
//		}
//
//		//BOPS-15777:20210413,hanjin,mod,该接口不再做信息的登记，只有客户在跑批任务处理时才登记信息
//		//判断以前是否已经存在相应的记录
//		/*if(null != shregswitchOrg) {
//			shregswitchService.setShregswitch(opBranchNo, operatorNo, opStation, opEntrustWay
//				, SysdictionaryConst.CNST_ACTIONIN_UPDATE, shregswitchnew);
//		}else {
//			shregswitchService.setShregswitch(opBranchNo, operatorNo, opStation, opEntrustWay
//					, SysdictionaryConst.CNST_ACTIONIN_ADD, shregswitchnew);
//		}*/
//
//		CommonResponseForm<HashMap<String,Object>> extCommonResponseForm = new CommonResponseForm<HashMap<String,Object>>();
//		HashMap<String, Object> data = new HashMap<String, Object>();
//		data.putAll(MapUtil.toMap(shregswitchnew));
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("上海交易席位切换登记成功！");
//		extCommonResponseForm.setData(data);
//		return extCommonResponseForm;
//	}
//
//	public CommonResponseForm<JSONObject> addOffare2(String opBranchNo, String operatorNo, String opStation, String opEntrustWay, String branchNo,
//			String fareType, String exchangeType, String enStockType, String enEntrustBs, String enEntrustWay, String fareKind, String stockCode,
//			String balanceRatio, String parRatio, String minFare, String maxFare, String dispartCount, String rebateFlag, String rebateRatio, String segmentFlag, String minRatio, String entrustType,
//			String entrustProp, JSONArray Offare2segArray) throws BopsException {
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//		JSONObject retLast = new JSONObject();
//		String httpAddUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_ALL_ADD); //费用记录增加
//
//		if(segmentFlag.equals("0")) { //不分段
//			paramJson.put("operatorNo", operatorNo);
//		    paramJson.put("opBranchNo", opBranchNo);
//		    paramJson.put("opStation", opStation);
//		    paramJson.put("opEntrustWay", opEntrustWay);
//		    paramJson.put("branchNo", branchNo);
//
//			paramJson.put("fare_type", fareType);
//			paramJson.put("fare_kind", fareKind);
//			paramJson.put("exchange_type", exchangeType);
//			paramJson.put("stock_code", stockCode);
//			paramJson.put("en_stock_type", enStockType);
//			paramJson.put("en_entrust_bs", enEntrustBs);
//			paramJson.put("en_entrust_way", enEntrustWay);
//			paramJson.put("balance_ratio", balanceRatio);
//			paramJson.put("par_ratio", parRatio);
//			paramJson.put("min_fare", minFare);
//			paramJson.put("max_fare", maxFare);
//			paramJson.put("dispart_count", dispartCount);
//			paramJson.put("rebate_flag", rebateFlag);
//			paramJson.put("rebate_ratio", rebateRatio);
//			paramJson.put("segment_flag", segmentFlag);
//			paramJson.put("segment_kind", 0);
//			paramJson.put("min_ratio", minRatio);
//			paramJson.put("entrust_type", entrustType);
//			paramJson.put("entrust_prop", entrustProp);
//			paramJson.put("charge_no", "3");
//
//	    	 try {
//	    		 retLast = new RestClient().postForJsonObj(httpAddUrl, paramJson);
//			 } catch (BopsException e) {
//				// TODO: handle exception
//				if(e.getErrCode().equals("-10")) {
//					logger.info(e);
//				}else {
//					throw e;
//				}
//			}
//	    	logger.info("retLast = " + retLast.toJSONString());
//		}else {
//			Map<String, List<Map<String, String>>> segmentKindMap = qryOffare2seg();
//			addOffare2All(Offare2segArray, segmentKindMap, stockCode, fareType, fareKind, exchangeType, enStockType, enEntrustBs, enEntrustWay, balanceRatio, parRatio, minFare, maxFare,
//					dispartCount, rebateFlag, rebateRatio, segmentFlag, minRatio, entrustType, entrustProp);  //按分段的增加费用
//		}
//
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("场内基金二级费用设置成功");
//    	return extCommonResponseForm;
//	}
//
//	private Integer qrySysDictMax() throws BopsException{
//		//获取3009字典子项4000以上的最大值
//		 Integer iMaxsubentry = 4000;
//		 Map<String, Object> argsSysDictGet = new HashMap<String, Object>();
//		 RestClient restClient = new RestClient();
//		 String httpSysDictGetUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_SYSDICTIONARY_GET); //数据字典获取
//
//		 argsSysDictGet.put("dict_entry", "3009");
//		 JSONArray listSysDictGet = restClient.getForJsonObj(httpSysDictGetUrl, argsSysDictGet).getJSONArray("data");
//
//		 for(int i=0; i<listSysDictGet.size(); i++) {
//			 Map<String, String> mapSysDictGet = (Map)listSysDictGet.get(i);
//			 Integer isubentry = Integer.valueOf(mapSysDictGet.get("subentry"));
//			 if(isubentry<4000 || isubentry == 5701 || isubentry == 7615) {
//				 continue;
//			 }else {
//				 if(isubentry>iMaxsubentry) {
//					 iMaxsubentry = isubentry;
//				 }
//			 }
//		 }
//		return iMaxsubentry;
//	}
//
//	private Map<String, List<Map<String, String>>> qryOffare2seg() throws BopsException{
//		Map<String, List<Map<String, String>>> retData = new HashMap<String, List<Map<String, String>>>();
//		RestClient restClient = new RestClient();
//
//		String httpSegGetUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_SEG_GET); //分段记录查询
//		Map<String, Object> argsSegGet = new HashMap<String, Object>();
//		argsSegGet.put("segment_kind", "-1");
//
//		JSONArray listSegGet = restClient.getForJsonObj(httpSegGetUrl, argsSegGet).getJSONArray("data");
//
//		if(listSegGet != null && listSegGet.size()>0) {
//			for(int i=0; i<listSegGet.size(); i++) {
//				Map<String, String> mapSegGet = (Map)listSegGet.get(i);
//				String segmentKind = mapSegGet.get("segment_kind");
//
//				List<Map<String, String>> segmentKindList = new ArrayList<>();
//				if(retData.containsKey(segmentKind)) {
//					List<Map<String, String>> segmentKindExistsList = retData.get(segmentKind);
//					segmentKindExistsList.add(mapSegGet);
//					retData.put(segmentKind, segmentKindExistsList);
//
//				}else {
//					segmentKindList.add(mapSegGet);
//					retData.put(segmentKind, segmentKindList);
//				}
//
//			}
//		}
//
//		return retData;
//	}
//
//
//	//比较产品代码串和费用代码串是否相等
//    private Boolean compareFundAndSeg(JSONArray Offare2segArray,  List<Map<String, String>> segmentKindListDst) throws BopsException{
//    	Boolean bIsNeedSeg = false;
//
//		 if(Offare2segArray.size() != segmentKindListDst.size()) {  //条数不相等
//			 bIsNeedSeg = true;
//		 }else {  //条数相等
//			 for(int i=0; i<Offare2segArray.size(); i++) {
//				 JSONObject fundCodeMap = Offare2segArray.getJSONObject(i);
//				 Map<String, String> segmentKindMap = segmentKindListDst.get(i);
//
//				 BigDecimal amountLowerLimit = new BigDecimal(fundCodeMap.get("aimValue").toString());
//				 BigDecimal aimValue = new BigDecimal(segmentKindMap.get("aim_value"));
//
//				 BigDecimal rateFee = new BigDecimal(fundCodeMap.get("balanceRatio").toString());
//				 BigDecimal balanceRatio = new BigDecimal(segmentKindMap.get("balance_ratio"));
//
//				 BigDecimal maxFee =  new BigDecimal(fundCodeMap.get("maxFare").toString());
//				 BigDecimal maxFare = new BigDecimal(segmentKindMap.get("max_fare"));
//
//				 BigDecimal minFee =  new BigDecimal(fundCodeMap.get("minFare").toString());
//				 BigDecimal minFare = new BigDecimal(segmentKindMap.get("min_fare"));
//
//				 if(minFee.compareTo(BigDecimal.ZERO) > 0 && minFee.compareTo(maxFee) == 0) {  //固定费用
//					 if(amountLowerLimit.compareTo(aimValue) == 0 && minFee.compareTo(minFare) == 0 && maxFee.compareTo(maxFare) == 0 ) {
//						 bIsNeedSeg = false;
//						 continue;
//					 }else{
//						 bIsNeedSeg = true;
//				    	 break;
//					 }
//				 }else {  //费率
//					 if(amountLowerLimit.compareTo(aimValue) == 0 && rateFee.compareTo(balanceRatio) == 0) {
//						 bIsNeedSeg = false;
//						 continue;
//					 }else{
//						 bIsNeedSeg = true;
//				    	 break;
//					 }
//
//				 }
//		  }
//	   }
//    	return bIsNeedSeg;
//    }
//
//  //按分段的增加费用
//  	private void addOffare2All(JSONArray Offare2segArray, Map<String, List<Map<String, String>>> segmentKindData, String stockCode, String fareType, String fareKind, String exchangeType, String enStockType,
//  			String enEntrustBs, String enEntrustWay, String balanceRatio, String parRatio, String minFare, String maxFare,
//			String dispartCount, String rebateFlag, String rebateRatio, String segmentFlag, String minRatio, String entrustType, String entrustProp) throws BopsException{
//  		RestClient restClient = new RestClient();
//  		String httpAddUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_ALL_ADD); //费用记录增加
//  		String httpSegGetUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_SEG_GET); //分段记录查询
//  		String httpSegAddUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_SEG_ADD); //分段记录增加
//  		String httpSysDictGetUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_SYSDICTIONARY_GET); //数据字典获取
//  		String httpSysDictAddUrl = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_SYSDICTIONARY_ADD); //数据字典增加
//
//  		String uficsDictAddUrl = ufics_url + serviceUri.getServUri(ServiceUri.SVR_URI_UFICS_SYSDICTIONARY_ADD); //数据字典增加
//
//
//		Boolean bIsNeedAddSeg = false;
//		Boolean bIsExistSeg = false;
//
//		String segmentKindInsert = new String();
//
//		 for(String segmentKind : segmentKindData.keySet()) {
//			 List<Map<String, String>> segmentKindList = segmentKindData.get(segmentKind);
//			 bIsNeedAddSeg = compareFundAndSeg(Offare2segArray, segmentKindList);
//			 if(!bIsNeedAddSeg) {
//				 segmentKindInsert = segmentKind;
//				 break;
//			 }
//		 }
//
//		 if(bIsNeedAddSeg) {  //分段记录不存在，需要插入
//			 Map<String, Object> argsSysDictAdd = new HashMap<String, Object>();
//			 Map<String, Object> argsSysDictGet = new HashMap<String, Object>();
//			 Integer iMaxsubentry = qrySysDictMax();
//
//			 if(iMaxsubentry == 5700 || iMaxsubentry == 7614) {
//				 iMaxsubentry = iMaxsubentry+1;
//			 }
//
//			//判断分段记录是否存在,不存在就插入
//			 Map<String, Object> argsSegGet = new HashMap<String, Object>();
//			 argsSegGet.put("segment_kind", String.valueOf((iMaxsubentry+1)));
//			 JSONArray listSegGet = restClient.getForJsonObj(httpSegGetUrl, argsSegGet).getJSONArray("data");
//			//不存在，插入分段记录
//			 if(listSegGet == null || listSegGet.isEmpty()) {
//				 bIsExistSeg = false;
//			 }else {
//				 bIsExistSeg = true;
//			 }
//
//			 for(int i=0; i<Offare2segArray.size(); i++) {
//				 JSONObject fundCodeMap = Offare2segArray.getJSONObject(i);
//				 Map<String, Object> argsSegAdd = new HashMap<String, Object>();
//
//				//不存在，插入分段记录
//				 if(!bIsExistSeg) {
//					 logger.info("插入分段记录");
//					 argsSegAdd.put("segment_kind", String.valueOf((iMaxsubentry+1)));
//					 argsSegAdd.put("seg_order", fundCodeMap.get("segOrder").toString());
//					 argsSegAdd.put("aim_value", new BigDecimal(fundCodeMap.get("aimValue").toString()));
//					 argsSegAdd.put("balance_ratio", new BigDecimal(fundCodeMap.get("balanceRatio").toString()));
//					 argsSegAdd.put("par_ratio", "0");
//					 argsSegAdd.put("min_fare", new BigDecimal(fundCodeMap.get("minFare").toString()));
//					 argsSegAdd.put("max_fare", new BigDecimal(fundCodeMap.get("maxFare").toString()));
//					 argsSegAdd.put("charge_no", "3");
//
//					 try {
//						 restClient.postForJsonObj(httpSegAddUrl, argsSegAdd);
//					 } catch (BopsException e) {
//						// TODO: handle exception
//						if(e.getErrCode().equals("-10")) {
//							logger.info(e);
//						}else {
//							throw e;
//						}
//					}
//
//				 }
//			 }
//
//			 //先查下3009的字典子项是否存在，不存在，则插入子项
//			 argsSysDictGet.put("dict_entry", "3009");
//			 argsSysDictGet.put("subentry", String.valueOf((iMaxsubentry+1)));
//			 JSONArray listSysDictGet = restClient.getForJsonObj(httpSysDictGetUrl, argsSysDictGet).getJSONArray("data");
//			 String dictPrompt = new String();
//
//               //不存在，插入字典子项
//			 if (listSysDictGet == null || listSysDictGet.isEmpty()){
//				 //获取插入的分段记录
//
//				 Map<String, Object> argsSegGetTmp = new HashMap<String, Object>();
//				 argsSegGetTmp.put("segment_kind", String.valueOf((iMaxsubentry+1)));
//				 JSONArray listSegGetTmp = restClient.getForJsonObj(httpSegGetUrl, argsSegGetTmp).getJSONArray("data");
//
//				 if(listSegGetTmp != null || listSegGetTmp.size()>0) {
//					 dictPrompt = String.valueOf((listSegGetTmp.size())) + "档";
//					 StringBuilder strBuilder = new StringBuilder();
//					 StringBuilder strBuilderDst = new StringBuilder();
//
//					 for(int k=0; k<listSegGetTmp.size(); k++) {
//						 String aimValue = listSegGetTmp.getJSONObject(k).getString(("aim_value"));
//						 BigDecimal aimValueB = new BigDecimal(aimValue);
//						 String balanceRatioT = listSegGetTmp.getJSONObject(k).getString(("balance_ratio"));
//						 BigDecimal balanceRatioB = new BigDecimal(balanceRatioT).setScale(8, BigDecimal.ROUND_DOWN);
//
//						 String maxFareT = listSegGetTmp.getJSONObject(k).getString(("max_fare"));
//						 String minFareT = listSegGetTmp.getJSONObject(k).getString(("min_fare"));
//						 BigDecimal maxFareB = new BigDecimal(maxFareT);
//						 BigDecimal minFareB = new BigDecimal(minFareT);
//
//						 strBuilder.append(",").append(aimValueB.divide(new BigDecimal(10000)).toPlainString());
//						 if(balanceRatioB.compareTo(BigDecimal.ZERO) > 0 && minFareB.compareTo(BigDecimal.ZERO) == 0) {
//							 strBuilderDst.append(balanceRatioB.toPlainString()).append(",");
//						 }
//
//						 if(maxFareB.compareTo(BigDecimal.ZERO) > 0 && minFareB.compareTo(BigDecimal.ZERO) > 0 && maxFareB.compareTo(minFareB) == 0) {
//							 strBuilderDst.append(minFareB.toPlainString());
//						 }
//					 }
//
//					 dictPrompt = dictPrompt + strBuilder + "w;" + strBuilderDst;
//
//				 }
//
//				 argsSysDictAdd.put("branch_no", "8888");
//				 argsSysDictAdd.put("dict_entry", "3009");
//				 argsSysDictAdd.put("dict_type", "1");
//				 argsSysDictAdd.put("subentry", String.valueOf((iMaxsubentry+1)));
//				 argsSysDictAdd.put("access_level", "1");
//				 argsSysDictAdd.put("dict_prompt", dictPrompt);
//				 restClient.postForJsonObj(httpSysDictAddUrl, argsSysDictAdd);
//
//				 try{
//					 restClient.postForJsonObj(uficsDictAddUrl, argsSysDictAdd);
//				 }catch (Exception e) {
//					// TODO: handle exception
//					 logger.info("机构柜台新增数据字典；"+e);
//				}
//			 }
//
//			//增加基金费用
//			Map<String, Object> argsAdd = new HashMap<String, Object>();
//			argsAdd.put("fare_type", fareType);
//			argsAdd.put("fare_kind", fareKind);
//			argsAdd.put("exchange_type", exchangeType);
//			argsAdd.put("stock_code", stockCode);
//			argsAdd.put("en_stock_type", enStockType);
//			argsAdd.put("en_entrust_bs", enEntrustBs);
//			argsAdd.put("en_entrust_way", enEntrustWay);
//			argsAdd.put("balance_ratio", balanceRatio);
//			argsAdd.put("par_ratio", parRatio);
//			argsAdd.put("min_fare", minFare);
//			argsAdd.put("max_fare", maxFare);
//			argsAdd.put("dispart_count", dispartCount);
//			argsAdd.put("rebate_flag", rebateFlag);
//			argsAdd.put("rebate_ratio", rebateRatio);
//			argsAdd.put("segment_flag", segmentFlag);
//			argsAdd.put("segment_kind", String.valueOf((iMaxsubentry+1)));
//			argsAdd.put("min_ratio", minRatio);
//			argsAdd.put("entrust_type", entrustType);
//			argsAdd.put("entrust_prop", entrustProp);
//			argsAdd.put("charge_no", "3");
//
//			 try {
//				 restClient.postForJsonObj(httpAddUrl, argsAdd);
//			 } catch (BopsException e) {
//				// TODO: handle exception
//				logger.info("错误码", e.getErrCode());
//				if(e.getErrCode().equals("-10")) {
//					logger.info(e);
//				}else {
//					throw e;
//				}
//			}
//
//		 }else {
//			//增加基金费用
//				Map<String, Object> argsAdd = new HashMap<String, Object>();
//				argsAdd.put("fare_type", fareType);
//				argsAdd.put("fare_kind", fareKind);
//				argsAdd.put("exchange_type", exchangeType);
//				argsAdd.put("stock_code", stockCode);
//				argsAdd.put("en_stock_type", enStockType);
//				argsAdd.put("en_entrust_bs", enEntrustBs);
//				argsAdd.put("en_entrust_way", enEntrustWay);
//				argsAdd.put("balance_ratio", balanceRatio);
//				argsAdd.put("par_ratio", parRatio);
//				argsAdd.put("min_fare", minFare);
//				argsAdd.put("max_fare", maxFare);
//				argsAdd.put("dispart_count", dispartCount);
//				argsAdd.put("rebate_flag", rebateFlag);
//				argsAdd.put("rebate_ratio", rebateRatio);
//				argsAdd.put("segment_flag", segmentFlag);
//				argsAdd.put("segment_kind", segmentKindInsert);
//				argsAdd.put("min_ratio", minRatio);
//				argsAdd.put("entrust_type", entrustType);
//				argsAdd.put("entrust_prop", entrustProp);
//				argsAdd.put("charge_no", "3");
//
//				 try {
//					 restClient.postForJsonObj(httpAddUrl, argsAdd);
//				 } catch (BopsException e) {
//					// TODO: handle exception
//					logger.info("错误码", e.getErrCode());
//					if(e.getErrCode().equals("-10")) {
//						logger.info(e);
//					}else {
//						throw e;
//					}
//				}
//
//		 }
//
//  	}
//
//	/*public CommonResponseForm<JSONObject> addOffare2seg(String opBranchNo, String operatorNo, String opStation, String opEntrustWay, String branchNo,
//			String segmentKind, String segOrder, String aimValue, String balanceRatio, String parRatio, String minFare, String maxFare) throws BopsException {
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//	    paramJson.put("operatorNo", operatorNo);
//	    paramJson.put("opBranchNo", opBranchNo);
//	    paramJson.put("opStation", opStation);
//	    paramJson.put("opEntrustWay", opEntrustWay);
//	    paramJson.put("branchNo", branchNo);
//
//	    paramJson.put("segment_kind", segmentKind);
//		 paramJson.put("seg_order", segOrder);
//		 paramJson.put("aim_value", aimValue);
//		 paramJson.put("balance_ratio", balanceRatio);
//		 paramJson.put("par_ratio", parRatio);
//		 paramJson.put("min_fare", minFare);
//		 paramJson.put("max_fare", maxFare);
//
//	    String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_SEG_ADD);
//    	JSONObject retLast = new RestClient().postForJsonObj(uri, paramJson);
//
//    	logger.info("retLast = " + retLast.toJSONString());
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("场内基金二级费用分段设置");
//    	return extCommonResponseForm;
//	}
//
//	public CommonResponseForm<JSONArray> qryOffare2seg(String segmentKind) throws BopsException {
//
//		if(StringUtils.isEmpty(segmentKind)) {
//			segmentKind = "-1";
//	    }
//
//		CommonResponseForm<JSONArray> extCommonResponseForm = new CommonResponseForm<JSONArray>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//
//		paramJson.put("segment_kind", segmentKind);
//	    String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_OFFARE2_SEG_GET);
//    	JSONArray list = new RestClient().getForJsonObj(uri, paramJson).getJSONArray("data");
//
//		extCommonResponseForm.setData(list);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("场内基金二级费用分段查询");
//    	return extCommonResponseForm;
//	}*/
//
//	public CommonResponseForm<JSONObject> CRJCertificateCheck(String opBranchNo, String operatorNo, String opStation, String opEntrustWay, String branchNo,
//			String clientId, String acodeAccount,  String stockAccount, String fundAccount, String businType, String idKind,
//			String idNo, String idEnddate, String nationality, String clientGender, String birthday, String clientName) throws BopsException {
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//	    paramJson.put("operatorNo", operatorNo);
//	    paramJson.put("opBranchNo", opBranchNo);
//	    paramJson.put("opStation", opStation);
//	    paramJson.put("opEntrustWay", opEntrustWay);
//	    paramJson.put("branchNo", branchNo);
//
//	    paramJson.put("clientId", clientId);
//		paramJson.put("acodeAccount", acodeAccount);
//		paramJson.put("stockAccount", stockAccount);
//		paramJson.put("fundAccount", fundAccount);
//		paramJson.put("businType", businType);
//		paramJson.put("idKind", idKind);
//		paramJson.put("idNo", idNo);
//
//		paramJson.put("idEnddate", idEnddate);
//		paramJson.put("nationality", nationality);
//		paramJson.put("clientGender", clientGender);
//		paramJson.put("birthday", birthday);
//		paramJson.put("fullName", clientName);
//
//	    String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_CRJ_ZJXX_HC);
//    	JSONObject retLast = new RestClient().postForJsonObj(uri, paramJson);
//
//    	logger.info("retLast = " + retLast.toJSONString());
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("出入境证件信息核查成功");
//    	return extCommonResponseForm;
//	}
//
//	public CommonResponseForm<JSONObject> qryBankAccountsByCond(String clientId, String fundAccount, String bankNo, String bankType, String moneyType) throws BopsException {
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//    	JSONObject retLast = outerInterfaceService.qryBankAccountsByCond(clientId, fundAccount, bankNo, moneyType, bankType);
//
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("存管账户信息获取成功");
//    	return extCommonResponseForm;
//	}
//
//
//	/**
//	 * @Author liyu
//	 * @Date   20210412
//	 * @Description 联合开户待回访信息查询
//	 * @Link   BOPS-15655**/
//	public CommonResponseForm<List<Map<String, String>>> qryClientOpenWaitVisit(String positionStr1, String positionStr2) throws BopsException {
//
//		CommonResponseForm<List<Map<String, String>>> extCommonResponseForm = new CommonResponseForm<List<Map<String, String>>>();
//		List<Map<String, String>> retList = clientService.qryClientOpenWaitVisit(positionStr1, positionStr2);
//
//		extCommonResponseForm.setData(retList);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("联合开户待回访信息查询成功");
//
//    	return extCommonResponseForm;
//
//	}
//
//	public CommonResponseForm<ArrayList<JSONObject>> qryCsdcAcodeAcctExt(String clientId,  String fundAccount, String clientName, String idKind, String idNo, String acodeAccount) throws BopsException {
//
//		CommonResponseForm<ArrayList<JSONObject>> extCommonResponseForm = new CommonResponseForm<ArrayList<JSONObject>>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//
//	    paramJson.put("clientId", clientId);
//		paramJson.put("acodeAccount", acodeAccount);
//		paramJson.put("fundAccount", fundAccount);
//		paramJson.put("idKind", idKind);
//		paramJson.put("idNo", idNo);
//		paramJson.put("clientName", clientName);
//
//	    String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_QRY_CSDC_ACODE_ACCT);
//	    ArrayList<JSONObject> retLast = new RestClient().postJson2ForListNew(uri, paramJson, new JSONObject());
//
//		extCommonResponseForm.setData(retLast);
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("一码通账户信息查询成功");
//    	return extCommonResponseForm;
//	}
//
//
//	public CommonResponseForm<JSONObject> fundQuickWithdraw(String opBranchNo, String operatorNo, String opStation, String opEntrustWay, String branchNo,
//			String clientId, String fundAccount, String prodCode, String prodtaNo, String entrustAmount, String isBankTransfer,String moneyType, String bankNo,
//			String passwordType, String password, String userToken) throws BopsException {
//
//		CommonResponseForm<JSONObject> extCommonResponseForm = new CommonResponseForm<JSONObject>();
//		Map<String, Object> paramJson = new HashMap<String, Object>();
//		JSONObject retLast = new JSONObject();
//	    paramJson.put("operatorNo", operatorNo);
//	    paramJson.put("opBranchNo", opBranchNo);
//	    paramJson.put("opStation", opStation);
//	    paramJson.put("opEntrustWay", opEntrustWay);
//	    paramJson.put("branchNo", branchNo);
//
//	    paramJson.put("clientId", clientId);
//		paramJson.put("fundAccount", fundAccount);
//		paramJson.put("prodCode", prodCode);
//		paramJson.put("prodtaNo", prodtaNo);
//		paramJson.put("passwordType", passwordType);
//		paramJson.put("password", password);
//		paramJson.put("entrustAmount", entrustAmount);
//		paramJson.put("isBankTransfer", isBankTransfer);
//		//paramJson.put("userToken", userToken);
//
//
//		if(isBankTransfer.equals("1")) {
//
//			String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_FUND_QUICK_WITHDRAW);
//	    	retLast = new RestClient().postForJsonObj(uri, paramJson);
//	    	logger.info("retLast = " + retLast.toJSONString());
//
//	    	Map data = (Map)retLast.get("data");
//	    	String allotNo = data.get("allot_no").toString();
//	    	String serialNo = data.get("serial_no").toString();
//	    	String initDate = data.get("init_date").toString();
//
//
//	    	SimpleDateFormat format = new SimpleDateFormat("HHmmss");
//			Date date = new Date();
//			String currTime = format.format(date);
//
//			paramJson.put("acptStatus", "0");
//			//记一下businacptform, businacpformarg
//			paramJson.put("businOpType", "900940");
//			paramJson.put("operatorNo", operatorNo);
//			paramJson.put("opBranchNo", opBranchNo.toString());
//			paramJson.put("opEntrustWay", opEntrustWay);
//			paramJson.put("opStation", opStation);
//			paramJson.put("branchNo", branchNo.toString());
//			paramJson.put("allotNo", allotNo);
//			paramJson.put("actionUrl", "/fundQuickWithdraw");
//			paramJson.put("entrustNo", serialNo);
//
//			paramJson.put("moneyType", moneyType);
//			paramJson.put("bankNo", bankNo);
//			paramJson.put("currTime", currTime);
//			paramJson.put("initDate", initDate);
//
//            Map<String, String> mapAcpt = businAcceptService.saveBusinAcptFormAndArgNotClear(paramJson);
//
//			String opRemark = saveBatchTaskBusi(Integer.valueOf(opBranchNo), operatorNo, opStation, opEntrustWay,
//					900940, 202006, Integer.valueOf(branchNo), " ", mapAcpt.get("acptId"));//此处跑批默认将acptStatus状态更新为'6'
//
//		}else {
//			String uri = hsclient_url + serviceUri.getServUri(ServiceUri.SVR_URI_HSCLIENT_FUND_QUICK_WITHDRAW);
//	    	retLast = new RestClient().postForJsonObj(uri, paramJson);
//	    	logger.info("retLast = " + retLast.toJSONString());
//
//	    	Map data = (Map)retLast.get("data");
//	    	String allotNo = data.get("allot_no").toString();
//
//	    	paramJson.put("acptStatus", "7");
//			//记一下businacptform, businacpformarg
//			paramJson.put("businOpType", "900940");
//			paramJson.put("operatorNo", operatorNo);
//			paramJson.put("opBranchNo", opBranchNo.toString());
//			paramJson.put("opEntrustWay", opEntrustWay);
//			paramJson.put("opStation", opStation);
//			paramJson.put("branchNo", branchNo.toString());
//			paramJson.put("allotNo", allotNo);
//			paramJson.put("actionUrl", "/fundQuickWithdraw");
//
//		    businAcceptService.saveBusinAcptFormAndArg(paramJson);
//		}
//
//		extCommonResponseForm.setData(retLast.getJSONObject("data"));
//		extCommonResponseForm.setErrorNo("0");
//		extCommonResponseForm.setErrorInfo("");
//		extCommonResponseForm.setRemark("现金增利快速赎回成功");
//    	return extCommonResponseForm;
//	}
//
//	private void saveVisitToProof(String acptId, String businOpType, ReturnVisitor visitInfo) {
//		try {
//			JSONObject detail = new JSONObject().fluentPut("操作", "回访成功");
//			String visitDate = visitInfo.getUpdateDate();
//			if (StringUtils.isNotBlank(visitDate)) {
//				detail.put("回访时间", visitDate.substring(0, 4) + "-" + visitDate.substring(4, 6) + "-" + visitDate.substring(6));
//			} else {
//				detail.put("时间", StringUtils.dateToStr(new Date()));
//			}
//			if (StringUtils.isNotBlank(visitInfo.getUserName())) {
//				detail.put("回访人姓名", visitInfo.getUserName());
//			}
//			proofServ.saveProof("v", acptId, ProofType.Unit_Visit, businOpType, detail, new JSONArray());
//		} catch (Exception e) {
//			logger.logException(e);// 不影响原有交易功能
//		}
//	}
//}
