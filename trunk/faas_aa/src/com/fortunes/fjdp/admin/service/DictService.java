package com.fortunes.fjdp.admin.service;

import java.io.Reader;
import java.util.List;

import net.fortunes.core.log.annotation.LoggerClass;
import net.fortunes.core.service.GenericService;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;

import com.fortunes.fjdp.admin.model.Dict;
import com.fortunes.fjdp.admin.model.Employee;

@Component
@LoggerClass
public class DictService extends GenericService<Dict> {
	
	public static String KEY_ATTRIBUTE = "key";
	public static String TEXT_ATTRIBUTE = "text";
	public static String TYPE_ATTRIBUTE = "type";
	public static String ROOT_DICT_KEY = "root";
	
	@Override
	public Dict add(Dict entity) throws Exception {
		super.add(entity);
		if(entity.getParent() != null)
			entity.getParent().setLeaf(false);
		return entity;
	}
	
	@Override
	public void del(Dict entity) throws Exception {
		Dict parent = entity.getParent();
		super.del(entity);
		if(parent != null && parent.getChildren().size() <= 0)
			parent.setLeaf(true);
	}
	
	public void initToDb(Reader reader) throws Exception{
		SAXReader xmlReader = new SAXReader(); 
		Document doc = xmlReader.read(reader);
		Element root = doc.getRootElement();
		
		List<Element> elements = root.elements();
		
		Dict rootDict = updateOrCreate(ROOT_DICT_KEY,"根字典",Dict.SYSTEM_DICT,null);
		
		for (Element element : elements) {
			walkTree(element,rootDict);
		}
	}
	
	public List<Dict> getDictsByType(String key) {
		return findByProperty("parent.id", key);
	}
	
	private void walkTree(Element element,Dict parentDict) throws Exception{
		String id = "";
		String type = null;
		if(parentDict.getId().equals(ROOT_DICT_KEY)){
			id = element.attributeValue(KEY_ATTRIBUTE);
		}else{
			id = parentDict.getId()+"_"+element.attributeValue(KEY_ATTRIBUTE);
		}
		if(element.attributeValue(TYPE_ATTRIBUTE)!=null && element.attributeValue(TYPE_ATTRIBUTE).equals(Dict.SYSTEM_DICT)){
			type=Dict.SYSTEM_DICT;
		}
		Dict dict = updateOrCreate(id,element.attributeValue(TEXT_ATTRIBUTE),type,parentDict);
		
		List<Element> subElements = element.elements();
		for(Element e : subElements){
			walkTree(e,dict);
		}
	}
	
	private Dict updateOrCreate(String id,String text,String Type,Dict parentDict) throws Exception{
		Dict dict = new Dict();
		dict.setId(id);
		dict.setText(text);
		dict.setDictType(Type);
		dict.setParent(parentDict);
		this.addOrUpdate(dict);
		if(parentDict != null){
			parentDict.setLeaf(false);
			this.update(parentDict);
		}
		return dict;
	}

	public void refresh(Dict dict) {
		getDefDao().getHibernateTemplate().refresh(dict);
	}
}
  