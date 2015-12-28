package net.fortunes.codegenerate.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import net.fortunes.codegenerate.model.Field;
import net.fortunes.core.action.BaseAction;

@Component @Scope("prototype")
public class CodeGenerateAction extends BaseAction {
	public static String PACKAGE_PREFIX_KEY  = "packagePrefix";
	public static String MODEL_NAME_KEY  = "modelName";
	public static String FIELDS_KEY  = "fields";
	public static String CLASS_PATH = "/WEB-INF/classes";
	public static String OUT_PATH = "d:/codeGenerate";
	
	private Configuration cfg;
	
	private String modelName;
	private String packagePrefix;
	private String idType;
	
	private String[] fieldTypes;
	private String[] fieldNames;
	private String[] fieldLabels;
	private String[] fieldExtend; //kind,dateType,maxLength
	private String[] fieldAllowBlank;
	private int[] fieldLengths;
	
	public static void main(String[] args) throws Exception {
		CodeGenerateAction action = new CodeGenerateAction();
		action.init(new File("build/classes/net/fortunes/codegenerate/templates"));
		
		Map<String,Object> root = new HashMap<String, Object>();
		action.packagePrefix = "com.fjdp";
		action.modelName = "User";
		root.put(PACKAGE_PREFIX_KEY, action.packagePrefix);
		root.put(MODEL_NAME_KEY, action.modelName);
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("text","name","名称",false,null));
		fields.add(new Field("dict","sex","性别",false,"sex"));
		fields.add(new Field("date","birthday","生日",true,"date"));
		fields.add(new Field("date","lastLogin","最后登陆时间",true,"dateTime"));
		fields.add(new Field("textArea","addr","地址",true,null));
		fields.add(new Field("intNumber","rank","级别",true,"10"));
		root.put(FIELDS_KEY, fields);
		
		//renerate
		action.generate(root, "Service");
		action.generate(root, "Action");
		action.generate(root, "Model");
		action.generate(root, "Spring-conf");
		action.generate(root, "Widget");
	}
	
	
	public String generate() throws Exception{
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		init(new File(
				rootPath + CLASS_PATH + "/net/fortunes/codegenerate/templates"));
		
		Map<String,Object> root = new HashMap<String, Object>();
		root.put(PACKAGE_PREFIX_KEY, packagePrefix);
		root.put(MODEL_NAME_KEY, modelName.trim());
		root.put(FIELDS_KEY, processFields());
		
		//renerate
		generate(root, "Service");
		generate(root, "Action");
		generate(root, "Model");
		generate(root, "Widget");
		
		setJsonMessage(true, "代码成功生成");
		return render(jo);
	}
	
	private void init(File temlDir) throws Exception{
		cfg = new Configuration();
		
		cfg.setDirectoryForTemplateLoading(temlDir);
		cfg.setOutputEncoding("UTF-8");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		File outPath = new File(OUT_PATH);
		if(outPath.exists()){
			//FileUtils.deleteDirectory(outPath);
		}
	}
	
	private List<Field> processFields(){
		List<Field> fields = new ArrayList<Field>();
		for (int i = 0; i < fieldTypes.length; i++) {
			Field field = new Field();
			field.setType(fieldTypes[i]);
			field.setName(getLower(fieldNames[i]));
			field.setLabel(fieldLabels[i]);
			field.setExtend(fieldExtend[i]);
			field.setAllowEmpty(fieldAllowBlank[i].equals("yes"));
			
			//暂时没用
			/*if(fieldTypes[i].equals(FieldType.text.name())){
			}else if(fieldTypes[i].equals(FieldType.dict.name())){
			}if(fieldTypes[i].equals(FieldType.textArea.name())){
			}*/
			
			fields.add(field);
		}
		return fields;
	}
	
	private void generate(Map<String,Object> root,String fileType) throws Exception{
		Template tpl = cfg.getTemplate(getLower(fileType)+".ftl","UTF-8");
		String fileName = "";
		if(fileType.equals("Action") || fileType.equals("Service")){
			fileName = OUT_PATH + "/src" + "/" + packagePrefix.replace('.', '/') + "/" +
				getLower(fileType) + "/" +modelName.trim() + fileType + ".java";
		}else if(fileType.equals("Model")){
			fileName = OUT_PATH + "/src" + "/" + packagePrefix.replace('.', '/') +  "/" +
				getLower(fileType) + "/" +modelName.trim() + ".java";
		}else if(fileType.equals("Widget")){
			fileName = OUT_PATH + "/WebContent/widget/app/" + getLower(modelName.trim()) + ".js";
		}
		System.out.println(fileName);
		File codeFile = new File(fileName);
		File dir = codeFile.getParentFile();
		if(!dir.exists()){
			dir.mkdirs();
		}
		Writer out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(codeFile),"UTF-8"));
		
		//输出到stdout
		//tpl.process(root, new OutputStreamWriter(System.out));
		//输出到文件
		tpl.process(root, out);
		out.flush();
	}
	
	private String getLower(String s){
		return StringUtils.uncapitalize(s);
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getPackagePrefix() {
		return packagePrefix;
	}

	public void setPackagePrefix(String packagePrefix) {
		this.packagePrefix = packagePrefix;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	public String[] getFieldTypes() {
		return fieldTypes;
	}

	public void setFieldTypes(String[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String[] getFieldLabels() {
		return fieldLabels;
	}

	public void setFieldLabels(String[] fieldLabels) {
		this.fieldLabels = fieldLabels;
	}

	public int[] getFieldLengths() {
		return fieldLengths;
	}

	public void setFieldLengths(int[] fieldLengths) {
		this.fieldLengths = fieldLengths;
	}

	public void setFieldExtend(String[] fieldExtend) {
		this.fieldExtend = fieldExtend;
	}

	public String[] getFieldExtend() {
		return fieldExtend;
	}

	public void setFieldAllowBlank(String[] fieldAllowBlank) {
		this.fieldAllowBlank = fieldAllowBlank;
	}

	public String[] getFieldAllowBlank() {
		return fieldAllowBlank;
	}
}
