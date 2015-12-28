package com.fortunes.faas.network.model.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class XmlEntity {
	
	final static Logger logger = LoggerFactory.getLogger("ROOT");
	
	public static final String SUCCESS_RESPONSE_CODE = "000000";
	public static final String SUCCESS_RESPONSE_MSG = "SUCCESS";
	
	public static final int RESPONSE_MSG_MAX_LENGTH = 80;
	
	
	
	
	
	/**
	 * 
	 * trancode   	交易代码  	固定6	
	* 	tracenum   	交易流水号	固定8	
	* 	trandate   	交易日期  	固定8	
	* 	trantime   	交易时间  	固定6	
	* 	packseqnum 	报文顺序号	固定6	
	* 	recordCount	记录个数  	固定6	
	* 	endFlag    	结束标志  	固定1	   1：未结束，0：结束
	* 	respcode   	响应代码  	固定6	成功000000，否则失败
	* 	respmsg    	响应信息  	80	

	 */
	public static final String ROOT = "root";
	public static final String PRIMARY = "primary";
	public static final String PRIMARY_TRANCODE = "tranCode";
	public static final String PRIMARY_TRACENUM = "traceNum";
	public static final String PRIMARY_TRANDATE = "tranDate";
	public static final String PRIMARY_TRANTIME = "tranDateTime";
	public static final String PRIMARY_PACKSEQNUM = "packseqNum";
	public static final String PRIMARY_RECORDCOUNT = "recordCount";
	public static final String PRIMARY_ENDFLAG = "endFlag";
	public static final String PRIMARY_RESPCODE = "respCode";
	public static final String PRIMARY_RESPMSG = "respMsg";

	public static final String DETAIL = "detail";
	public static final String DETAIL_RECORD = "record";
	
	public static final String NOT_END_FLAG = "1";
	public static final String END_FLAG = "0";
	
	private Document document;
	
	private Primary primary = new Primary();
	private List<Record> recordList;
	
	private InetSocketAddress remoteAddress;
	
	private boolean compactForamt = false;

	private XmlEntity() {
		this.primary = new Primary();
		this.recordList = new ArrayList<Record>();
	}
	
	public Record getUniqueRecord(){
		if(this.getRecordList() != null && this.getRecordList().size() > 0){
			return this.getRecordList().get(0);
		}else{
			return null;
		}
	}
	
	public String getReturnCode(){
		return this.getPrimary().get(PRIMARY_RESPCODE);
	}
	
	public int getTransCode(){
		return Integer.parseInt(this.getPrimary().get(PRIMARY_TRANCODE));
	}
	
	public boolean hasNext(){
		return this.getPrimaryItem(PRIMARY_ENDFLAG).equals(NOT_END_FLAG);
	}
	
	public void setLastFlag(boolean flag){
		this.setPrimaryItem(PRIMARY_ENDFLAG, flag ? END_FLAG : NOT_END_FLAG);
	}
	
	public XmlEntity setResponseCodeAndMsg(String code,String msg){
		return this.setPrimaryItem(XmlEntity.PRIMARY_RESPCODE, code)
				.setPrimaryItem(XmlEntity.PRIMARY_RESPMSG, msg);
	}
	
	public XmlEntity setSuccessResponseCodeAndMsg(){
		return this.setPrimaryItem(XmlEntity.PRIMARY_RESPCODE, SUCCESS_RESPONSE_CODE)
				.setPrimaryItem(XmlEntity.PRIMARY_RESPMSG, SUCCESS_RESPONSE_MSG);
	}
	
	public XmlEntity createDefaultResponse(XmlEntity request){
		return this
			.setPrimaryItem(XmlEntity.PRIMARY_TRANCODE, request.getPrimaryItem(PRIMARY_TRANCODE))
			.setPrimaryItem(XmlEntity.PRIMARY_TRACENUM, request.getPrimaryItem(PRIMARY_TRACENUM))
			.setPrimaryItem(XmlEntity.PRIMARY_TRANDATE, request.getPrimaryItem(PRIMARY_TRANDATE))
			.setPrimaryItem(XmlEntity.PRIMARY_TRANTIME, request.getPrimaryItem(PRIMARY_TRANTIME))
			.setPrimaryItem(XmlEntity.PRIMARY_RECORDCOUNT, "0")
			.setPrimaryItem(XmlEntity.PRIMARY_PACKSEQNUM, request.getPrimaryItem(PRIMARY_PACKSEQNUM))
			.setPrimaryItem(XmlEntity.PRIMARY_ENDFLAG, request.getPrimaryItem(PRIMARY_ENDFLAG))
			.setPrimaryItem(XmlEntity.PRIMARY_RESPCODE, SUCCESS_RESPONSE_CODE)
			.setPrimaryItem(XmlEntity.PRIMARY_RESPMSG, SUCCESS_RESPONSE_MSG);
	}
	
	public XmlEntity createDefaultRequest(int trancode){
		return this
			.setPrimaryItem(XmlEntity.PRIMARY_TRANCODE, trancode+"")
			.setPrimaryItem(XmlEntity.PRIMARY_TRACENUM, "000001")
			.setPrimaryItem(XmlEntity.PRIMARY_TRANDATE, new SimpleDateFormat("yyyyMMdd").format(new Date()))
			.setPrimaryItem(XmlEntity.PRIMARY_TRANTIME, new SimpleDateFormat("HHmmss").format(new Date()))
			.setPrimaryItem(XmlEntity.PRIMARY_RECORDCOUNT, "000000")
			.setPrimaryItem(XmlEntity.PRIMARY_PACKSEQNUM, "000000")
			.setPrimaryItem(XmlEntity.PRIMARY_ENDFLAG, END_FLAG)
			.setPrimaryItem(XmlEntity.PRIMARY_RESPCODE, SUCCESS_RESPONSE_CODE)
			.setPrimaryItem(XmlEntity.PRIMARY_RESPMSG, "");
	}
	
	@SuppressWarnings("unchecked")
	public static XmlEntity parse(String xmlString) {
		XmlEntity entity = new XmlEntity();
		Document document = null;
		try {
			document = DocumentHelper.parseText(xmlString);
		} catch (DocumentException e) {
			logger.info("解析xml文件出错!", e);
		}
		
		Element root = document.getRootElement();

		// 处理PRIMARY
		Element primaryElement = root.element(XmlEntity.PRIMARY);
		if(primaryElement == null){
			logger.info("解析primary元素出错!");
		}
		for(Object primaryItem : primaryElement.elements()){
			Element primaryItemElement = (Element)primaryItem;
			entity.primary.put(primaryItemElement.getName(), primaryItemElement.getTextTrim());
		}

		// 处理DETAIL
		Element deatilElement = root.element(XmlEntity.DETAIL);
		if(deatilElement != null){
			List<Element> recordElemnts = deatilElement
			.elements(XmlEntity.DETAIL_RECORD);
			for (Element recordElemnt : recordElemnts) {
				Record record = new Record(entity);
				for (Object recordItem : recordElemnt.elements()) {
					Element itemElement = (Element) recordItem;
					record.put(itemElement.getName(), itemElement.getTextTrim());
				}
				entity.recordList.add(record);
			}
		}
		

		return entity;
	}
	
	public Document build(String encoding) {
		if(document == null){
			document = DocumentHelper.createDocument();
			document.setXMLEncoding(encoding);
			Element root = document.addElement(ROOT);
			
			//build primary
			Element primaryElement = root.addElement(PRIMARY);
			//SET DETAIL COUNT
			this.setPrimaryItem(PRIMARY_RECORDCOUNT, StringUtils.leftPad(this.getRecordList().size()+"", 6, "0"));
			for(Entry<String, String> entry : this.getPrimary().entrySet()){
				primaryElement.addElement(entry.getKey())
					.setText(entry.getValue() == null ? "" : entry.getValue());
			}
			
			if(this.getRecordList().size() > 0){
				//build records
				Element detailElement = root.addElement(DETAIL);
				for(Record record : this.getRecordList()){
					Element recordElement = detailElement.addElement(DETAIL_RECORD);
					for(Entry<String,String> recordEntry : record.entrySet()){
						recordElement.addElement(recordEntry.getKey())
						.setText(recordEntry.getValue() == null ? "" : recordEntry.getValue());
					}
				}
			}
			
			
			return document;
		}else{
			return document;
		}
		
		
	}
	
	public String build() {
		OutputFormat format = this.compactForamt ? 
				OutputFormat.createCompactFormat() : OutputFormat.createPrettyPrint();
		format.setEncoding(XmlMessage.ENCODING);
		Writer out = new StringWriter(); 
		XMLWriter xmlWriter = new XMLWriter(out,format);
		try {
			xmlWriter.write(this.build(XmlMessage.ENCODING));
			xmlWriter.flush();
		} catch (IOException e) {
			logger.info("build xml文件出错!", e);
		}
		String ret = out.toString();
		try {
			xmlWriter.close();
		} catch (IOException e) {
			logger.info("build xml文件出错!", e);
		}
		return ret;
	}
	
	public byte[] buildAsBytes() {
		try {
			return this.build().getBytes(XmlMessage.ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static XmlEntity create(){
		return new XmlEntity();
	}
	
	public XmlEntity setPrimaryItem(String key,String value){
		this.primary.put(key, value);
		return this;
	}
	
	public String getPrimaryItem(String key){
		return this.primary.get(key);
	}
	
	public Record createRecord(){
		Record r = new Record();
		r.setXmlEntity(this);
		return r;
	}
	
	public List<Record> addRecord(Record record){
		this.recordList.add(record);
		return this.recordList;
	}
	
	
	//================== setter and getter =======================
	public void setRecordList(List<Record> recordList) {
		this.recordList = recordList;
	}

	public List<Record> getRecordList() {
		return recordList;
	}


	public void setPrimary(Primary primary) {
		this.primary = primary;
	}

	public Primary getPrimary() {
		return primary;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Document getDocument() {
		return document;
	}

	public static void main(String[] args) throws Exception {
		File xmlFile = new File("D:/message.xml");
		
		XmlEntity xmlEntity = XmlEntity.parse(
				FileUtils.readFileToString(xmlFile, XmlMessage.ENCODING));
		
		
		FileUtils.writeStringToFile(
				new File("d:/messageOut.xml"), 
				xmlEntity.build(XmlMessage.ENCODING).asXML(),XmlMessage.ENCODING);
	

	}

	public void setRemoteAddress(InetSocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}
}


