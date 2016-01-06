package com.phenix.data;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	/**
	 * 解析数据库中的规则字符串，存储到Rule结构中
	 */
	public List<RuleTriple> leftHandSide;
	public RuleTriple rightHandSide;
	
	private List<RuleTriple> leftHandSideRuleParse(String leftHandSideRule)
	{
		List<RuleTriple> leftHandSideRules = new ArrayList<RuleTriple>();
		String[] leftStringRules = leftHandSideRule.split("\\+");
		for(String stringRule : leftStringRules)
		{
			String[] tmp = stringRule.split("_");
			leftHandSideRules.add(new RuleTriple(tmp[0], tmp[1], tmp[2]));
		}
		return leftHandSideRules;
	}
	
	private RuleTriple rightHandSideRuleParse(String rightHandSideRule)
	{
		String[] tmp = rightHandSideRule.split("_");
		return new RuleTriple(tmp[0], tmp[1], tmp[2]);
	}
	
	public Rule(String leftHandSideRule, String rightHandSideRule)
	{
		this.leftHandSide = this.leftHandSideRuleParse(leftHandSideRule);
		this.rightHandSide = this.rightHandSideRuleParse(rightHandSideRule);
	}
}
