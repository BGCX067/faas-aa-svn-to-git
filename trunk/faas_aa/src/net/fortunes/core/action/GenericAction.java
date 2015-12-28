package net.fortunes.core.action;

import java.text.ParseException;

import net.fortunes.core.ListData;
import net.fortunes.core.Model;
import net.fortunes.core.service.GenericService;
import net.fortunes.exception.DeleteForeignConstrainException;
import net.fortunes.util.GenericsUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 一个泛型Action,针对一个Entity提供一系列模板方法．
 * @param <E>　Entity的类型
 * @author Neo
 */
public abstract class GenericAction<E extends Model> extends BaseAction {
	
	private Class<E> entityClass;
	
	@SuppressWarnings("unchecked")
	public GenericAction() {
		this.entityClass = GenericsUtil.getGenericClass(getClass());
	}
	
	/**
	 * 列出所有的的实体,或者根据查询条件列出所有的实体,当不需要分页时,无必要进行条件查询.
	 * 条件查询有单个和多个两种.
	 * 当进行条件查询时,需要override对应Dao的getConditions方法,单条件查询override 
	 * getConditions(String query),多条件查询override getConditions(Map queryMap),
	 * 多条件查询时同时还需在Action override getQueryMap方法
	 * @return Struts页面
	 * @throws ParseException
	 */
	public String list() throws Exception{
		ListData<E> listData = getDefService().getListData(query, queryMap, start, limit);
		JSONArray ja = new JSONArray();
		for(E entity:listData.getList()){
			ja.add(toJsonObject(entity));
		}
		jo.put(DATA_KEY, ja);
		jo.put(TOTAL_COUNT_KEY, listData.getTotal());
		return render(jo);  
	}
	
	public String listTree() throws Exception{
		E root = getNode().equals("0") ? 
				getDefService().getRoot():getDefService().get(getNode());
		JSONArray ja = walkTree(root);								
		return render(ja);
	}
	
	public String add() throws Exception{
		//todo
		return null;
	}
	
	public String create() throws Exception{
		E entity = getEntityClass().newInstance();
		setEntity(entity);
		getDefService().add(entity);
		jo.put(ENTITY_KEY, toJsonObject(entity));
		setJsonMessage(true, entity.toString().equals("")?
				"新增了一条记录!" : "新增了("+entity+")的记录");
		return render(jo);
	}
	
	public String del() throws Exception{
		E entity = getDefService().get(id);
		try {
			getDefService().del(entity);
			setJsonMessage(true,"记录成功删除!");
		} catch (DeleteForeignConstrainException e) {
			return renderWarning("该项被其它数据所引用，不能删除！");
		}
		return render(jo);
	}
	
	public String edit() throws Exception{
		E entity = getDefService().get(id);
		setJsonMessage(true, toJsonObject(entity));
		return render(jo);
	}
	
	public String update() throws Exception{
		E entity = getDefService().get(id);
		setEntity(entity);
		getDefService().update(entity);
		jo.put(ENTITY_KEY, toJsonObject(entity));
		setJsonMessage(true, entity.toString().equals("")?
				"更新了一条记录!" : "更新了("+entity+")的记录");
		return render(jo);
	}
	
	protected JSONArray walkTree(E entity)throws Exception {
		return null;
	}
	
	protected abstract void setEntity(E entity) throws Exception;
	
	protected abstract JSONObject toJsonObject(E entity) throws Exception;

	public Class<E> getEntityClass() {
		return entityClass;
	}


	public GenericService<E> getDefService() {
		return null;
	}

	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}
	

}
