package net.fortunes.core;

import java.util.List;

/**
 * 对查询返回值的抽象返回得到的list和total值，注意分页情况下list.size != total
 * @author Neo.Liao
 *
 * @param <E>
 */
public class ListData<E> {
	private List<E> list;
	private int total;
	
	public ListData() {
	}

	public ListData(List<E> list, int total) {
		this.list = list;
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}
}
