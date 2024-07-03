package com.linsir.base.core.binding.parser;

import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: linsir
 * @date: 2022/3/21 16:29
 * @description: 条件管理器base类
 */

@Slf4j
public class BaseConditionManager {
    /**
     * 表达式缓存Map
     */
    private static final Map<String, List<Expression>> expressionParseResultMap = new ConcurrentHashMap<>();

    /**
     * 获取解析后的Expression列表
     * @param condition
     * @return
     */
    protected static List<Expression> getExpressionList(String condition){
        if(V.isEmpty(condition)){
            return null;
        }
        List<Expression> expressionList = expressionParseResultMap.get(condition);
        if(expressionList == null){
            ConditionParser visitor = new ConditionParser();
            try{
                Expression expression = CCJSqlParserUtil.parseCondExpression(condition);
                expression.accept(visitor);
                expressionList = visitor.getExpressList();
                expressionParseResultMap.put(condition, expressionList);
            }
            catch (Exception e){
                log.error("关联条件解析异常", e);
            }
        }
        return expressionList;
    }

    /**
     * 提取中间表表对象名
     * @param expressionList
     * @return
     */
    protected static String extractMiddleTableName(List<Expression> expressionList){
        Set<String> tableNameSet = new HashSet<>();
        for(Expression operator : expressionList){
            if(operator instanceof EqualsTo){
                EqualsTo express = (EqualsTo)operator;
                // 均为列
                if(express.getLeftExpression() instanceof Column && express.getRightExpression() instanceof Column){
                    // 统计左侧列中出现的表名
                    String leftColumn = express.getLeftExpression().toString();
                    collectTableName(tableNameSet, leftColumn);
                    // 统计右侧列中出现的表名
                    String rightColumn = express.getRightExpression().toString();
                    collectTableName(tableNameSet, rightColumn);
                }
            }
        }
        if(tableNameSet.isEmpty()){
            return null;
        }
        if(tableNameSet.size() > 1){
            log.warn("中间表关联条件暂只支持1张中间表！");
        }
        return tableNameSet.iterator().next();
    }

    /**
     * 统计表名出现的次数
     * @param tableNameSet
     * @param columnStr
     */
    private static void collectTableName(Set<String> tableNameSet, String columnStr) {
        if(!columnStr.contains(".")){
            return;
        }
        // 如果是中间表(非this,self标识的当前表)
        if(!isCurrentObjColumn(columnStr)){
            String tempTableName = S.substringBefore(columnStr, ".");
            tableNameSet.add(tempTableName);
        }
    }

    /**
     * 是否为VO自身属性（以this开头的）
     * @param expression
     * @return
     */
    protected static boolean isCurrentObjColumn(String expression){
        String tempTableName = S.substringBefore(expression, ".");
        // 如果是中间表(非this,self标识的当前表)
        return "this".equals(tempTableName) || "self".equals(tempTableName);
    }
}
