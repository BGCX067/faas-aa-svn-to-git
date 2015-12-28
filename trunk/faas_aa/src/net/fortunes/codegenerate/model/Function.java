package net.fortunes.codegenerate.model;

import java.util.ArrayList;
import java.util.List;
import net.fortunes.core.Model;

public class Function extends Model {
	
	private String packagePrefix;
	
	private String modelName;
	
	private List<Field> fields = new ArrayList<Field>();

}
