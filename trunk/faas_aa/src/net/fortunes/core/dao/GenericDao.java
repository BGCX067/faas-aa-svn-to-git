package net.fortunes.core.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import net.fortunes.exception.DeleteForeignConstrainException;
import net.fortunes.util.GenericsUtil;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * 一个泛型Dao,针对一个Entity提供一系列模板方法．
 * 第一个泛型参数为model类,第二个泛型参数为该model类为主键类型
 * @param <E>　Entity的类型
 * @author Neo
 */
public class GenericDao<E> extends BaseDao {
	
	private TransactionTemplate transactionTemplate;
	private JdbcTemplate jdbcTemplate;


	/**
	 * DetachedCriteria条件查询方法
	 * @param criteria 一个DetachedCriteria对象
	 */
	
	@SuppressWarnings("unchecked")
	public List<E> findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}
		
	/**
	 * 条件+分页查询方法
	 * @param start 数据集起始位置
	 * @param limit 数据集容量
	 * @param criteria 一个DetachedCriteria对象
	 */
	
	@SuppressWarnings("unchecked")
	public List<E> findByCriteria(DetachedCriteria criteria,int start,int limit){
		return getHibernateTemplate().findByCriteria(criteria,start,limit);
	}
	
	
	/**
	 * HQL查询
	 * @param queryString HQL查询字符串
	 * @return List
	 */
	
	@SuppressWarnings("unchecked")
	public List findByQueryString(String queryString,Object...objects) {
		return getHibernateTemplate().find(queryString, objects);
	}
	
	/**
	 * HQL查询
	 * @param queryString HQL查询字符串
	 * @return List
	 */
	
	@SuppressWarnings("unchecked")
	public List findByQueryString(String queryString) {
		return getHibernateTemplate().find(queryString);
	}
	
	@SuppressWarnings("unchecked")
	public List findByQueryString(final String queryString,final int start,final int limit,final Object... values)  {
		return (List) getHibernateTemplate().executeWithNativeSession(new HibernateCallback(){

			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query queryObject = session.createQuery(queryString);
				if (getHibernateTemplate().isCacheQueries()) {
					queryObject.setCacheable(true);
					if (getHibernateTemplate().getQueryCacheRegion() != null) {
						queryObject.setCacheRegion(getHibernateTemplate().getQueryCacheRegion());
					}
				}
				if (getHibernateTemplate().getFetchSize() > 0) {
					queryObject.setFetchSize(getHibernateTemplate().getFetchSize());
				}
				SessionFactoryUtils.applyTransactionTimeout(queryObject, getSessionFactory());
				
				if (start >= 0) {
					queryObject.setFirstResult(start);
				}
				if (limit > 0) {
					queryObject.setMaxResults(limit);
				}
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}
		});
	}
	

	/**
	 * example查询
	 * @param exampleEntity
	 * @return
	 */
	public List<E> findByExample(E exampleEntity) {
		return getHibernateTemplate().findByExample(exampleEntity);
	}
	
	public E getById(Class<E> clazz,Serializable id) {
		Object o = getHibernateTemplate().get(clazz,id);
		return (E)o;
	}
	
	public E loadById(Class<E> clazz,Serializable id) {
		return (E)getHibernateTemplate().load(clazz,id);
	}

	
	public E add(E entity) {
		getHibernateTemplate().save(entity);
		return entity;
	}

	public E update(E entity) {
		getHibernateTemplate().update(entity);
		return entity;
	}
	
	public E saveOrUpdate(E entity) {
		getHibernateTemplate().saveOrUpdate(entity);
		return entity;
	}
	
	public E refresh(E entity){
		getHibernateTemplate().refresh(entity);
		return entity;
	}
	
	public void clearSession(){
		getHibernateTemplate().getSessionFactory().getCurrentSession().clear();
	}

	public void del(E entity) throws DeleteForeignConstrainException{
		try {
			getHibernateTemplate().setFlushMode(HibernateTemplate.FLUSH_EAGER);
			getHibernateTemplate().delete(entity);
		} catch (DataIntegrityViolationException e) {
			throw new DeleteForeignConstrainException();
		}
	}
	
	public int bulkUpdate(String queryHql){
		return getHibernateTemplate().bulkUpdate(queryHql);
	}
	
	public int bulkUpdate(String queryHql,Object...objects){
		return getHibernateTemplate().bulkUpdate(queryHql, objects);
	}
	
	public int getTotal() {
		String entityName = GenericsUtil.getGenericClass(getClass()).getSimpleName();
		return ((Long) getHibernateTemplate().iterate("select count(*) from " + entityName).next()).intValue();
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

}
