package com.fortunes.fjdp.admin.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import net.fortunes.core.Model;

@Entity
public class Dict extends Model {
	public static final String SYSTEM_DICT="system";
	@Id 
	private String id;//aka key
	private String text;
	private boolean leaf = true;
	private String dictType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Dict parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Dict> children = new ArrayList<Dict>();
	
	private String description;
	
    public Dict() {
    }
    
    public Dict(String id) {
    	this.setId(id);
    }
    
    public Dict(String key, String text) {
    	this.id = key;
    	this.text = text;
	}
    
    @Override
    public String toString() {
    	return "字典记录:"+text;
    }
    
    // ========================= setter and getter =============================
    
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Dict getParent() {
		return parent;
	}

	public void setParent(Dict parent) {
		this.parent = parent;
	}

	public void setChildren(List<Dict> children) {
		this.children = children;
	}

	public List<Dict> getChildren() {
		return children;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public boolean isLeaf() {
		return leaf;
	}
	public String getDictType() {
		return dictType;
	}

	public void setDictType(String dictType) {
		this.dictType = dictType;
	}

}


