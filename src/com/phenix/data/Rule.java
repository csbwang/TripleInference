package com.phenix.data;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	/**
	 * 解析数据库中的规则字符串，存储到Rule结构中
	 */
	public List<Triple> leftHandSide;
	public Triple rightHandSide;
	
	private List<Triple> leftHandSideRuleParse(String leftHandSideRule)
	{
		List<Triple> leftHandSideRules = new ArrayList<Triple>();
		String[] leftStringRules = leftHandSideRule.split("\\+");
		for(String stringRule : leftStringRules)
		{
			String[] tmp = stringRule.split("_");
			leftHandSideRules.add(new Triple(tmp[0], tmp[1], tmp[2]));
		}
		return leftHandSideRules;
	}
	
	private Triple rightHandSideRuleParse(String rightHandSideRule)
	{
		String[] tmp = rightHandSideRule.split("_");
		return new Triple(tmp[0], tmp[1], tmp[2]);
	}
	
	public Rule(String leftHandSideRule, String rightHandSideRule)
	{
		this.leftHandSide = this.leftHandSideRuleParse(leftHandSideRule);
		this.rightHandSide = this.rightHandSideRuleParse(rightHandSideRule);
	}
}
