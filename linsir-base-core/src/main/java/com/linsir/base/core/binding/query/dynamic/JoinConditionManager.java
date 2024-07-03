package com.linsir.base.core.binding.query.dynamic;


import com.linsir.base.core.binding.parser.BaseConditionManager;
import com.linsir.base.core.exception.InvalidUsageException;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: linsir
 * @date: 2022/3/21 17:49
 * @description: Join条件表达式的管理器
 */
@Slf4j
public class JoinConditionManager extends BaseConditionManager {

    /**
     * 解析condition条件
     * @param joiner
     * @throws Exception
     */
    public static void parseJoinCondition(AnnoJoiner joiner) {
        List<Expression> expressionList = getExpressionList(joiner.getCondition());
        if(V.isEmpty(expressionList)){
            log.warn("无法解析注解条件: {} ", joiner.getCondition());
            throw new InvalidUsageException("无法解析注解条件: " + joiner.getCondition());
        }
        // 解析中间表关联
        String tableName = extractMiddleTableName(expressionList);
        if(tableName != null){
            joiner.setMiddleTable(tableName);
        }
        // 解析join
        parseJoinOn(joiner, expressionList);
    }


    /**
     * 解析直接关联
     * @param joiner
     * @param expressionList
     */
    private static void parseJoinOn(AnnoJoiner joiner, List<Expression> expressionList) {
        List<String> segments = new ArrayList<>(), middleTableOnSegments = new ArrayList<>();
        // 解析直接关联
        for(Expression operator : expressionList){
            // 默认当前表条件
            List<String> currentSegments = segments;
            if(operator instanceof BinaryExpression){
                BinaryExpression expression = (BinaryExpression)operator;
                String left = formatColumn(expression.getLeftExpression(), joiner);
                String right = formatColumn(expression.getRightExpression(), joiner);
                // 中间表条件
                if(joiner.getMiddleTable() != null &&
                        (left.startsWith(joiner.getMiddleTableAlias() + ".") || right.startsWith(joiner.getMiddleTableAlias() + "."))){
                    if(left.startsWith(joiner.getAlias()+".") || right.startsWith(joiner.getAlias()+".")){
                    }
                    else{
                        currentSegments = middleTableOnSegments;
                    }
                }
                if(operator instanceof EqualsTo){
                    currentSegments.add(left + " = " + right);
                }
                else if(operator instanceof NotEqualsTo){
                    currentSegments.add(left + " != " + right);
                }
                else if(operator instanceof GreaterThan){
                    currentSegments.add(left + " > " + right);
                }
                else if(operator instanceof GreaterThanEquals){
                    currentSegments.add(left + " >= " + right);
                }
                else if(operator instanceof MinorThan){
                    currentSegments.add(left + " < " + right);
                }
                else if(operator instanceof MinorThanEquals){
                    currentSegments.add(left + " <= " + right);
                }
                else{
                    log.warn("暂不支持的条件: "+ expression.toString());
                }
            }
            else if(operator instanceof IsNullExpression){
                IsNullExpression expression = (IsNullExpression)operator;
                String left = formatColumn(expression.getLeftExpression(), joiner);
                // 中间表条件
                if(joiner.getMiddleTable() != null && left.startsWith(joiner.getMiddleTableAlias() + ".")){
                    currentSegments = middleTableOnSegments;
                }
                if(expression.isNot() == false){
                    currentSegments.add(left + " IS NULL");
                }
                else{
                    currentSegments.add(left + " IS NOT NULL");
                }
            }
            else if(operator instanceof InExpression){
                InExpression expression = (InExpression)operator;
                String left = formatColumn(expression.getLeftExpression(), joiner);
                // 中间表条件
                if(joiner.getMiddleTable() != null && left.startsWith(joiner.getMiddleTableAlias() + ".")){
                    currentSegments = middleTableOnSegments;
                }
                if(expression.isNot() == false){
                    currentSegments.add(left + " IN " + expression.getRightItemsList().toString());
                }
                else{
                    currentSegments.add(left + " NOT IN " + expression.getRightItemsList().toString());
                }
            }
            else if(operator instanceof Between){
                Between expression = (Between)operator;
                String left = formatColumn(expression.getLeftExpression(), joiner);
                // 中间表条件
                if(joiner.getMiddleTable() != null && left.startsWith(joiner.getMiddleTableAlias() + ".")){
                    currentSegments = middleTableOnSegments;
                }
                if(expression.isNot() == false){
                    currentSegments.add(left + " BETWEEN " + expression.getBetweenExpressionStart().toString() + " AND " + expression.getBetweenExpressionEnd().toString());
                }
                else{
                    currentSegments.add(left + " NOT BETWEEN " + expression.getBetweenExpressionStart().toString() + " AND " + expression.getBetweenExpressionEnd().toString());
                }
            }
            else if(operator instanceof LikeExpression){
                LikeExpression expression = (LikeExpression)operator;
                String left = formatColumn(expression.getLeftExpression(), joiner);
                // 中间表条件
                if(joiner.getMiddleTable() != null && left.startsWith(joiner.getMiddleTableAlias() + ".")){
                    currentSegments = middleTableOnSegments;
                }
                if(expression.isNot() == false){
                    currentSegments.add(left + " LIKE " + expression.getRightExpression().toString());
                }
                else{
                    currentSegments.add(left + " NOT LIKE " + expression.getRightExpression().toString());
                }
            }
            else{
                log.warn("不支持的条件: "+operator.toString());
            }
        }
        if(segments.isEmpty() && middleTableOnSegments.isEmpty()){
            return;
        }
        joiner.setOnSegment(S.join(segments, " AND "));
        if(V.notEmpty(middleTableOnSegments)){
            joiner.setMiddleTableOnSegment(S.join(middleTableOnSegments, " AND "));
        }
    }

    /**
     * 格式化左侧
     * @return
     */
    private static String formatColumn(Expression expression, AnnoJoiner joiner){
        if(expression instanceof Column == false){
            return expression.toString();
        }
        // 其他表列
        String annoColumn = S.toSnakeCase(expression.toString());
        if(annoColumn.contains(".")){
            String tableName = S.substringBefore(annoColumn, ".");
            // 当前表替换别名
            if(tableName.equals("this")){
                annoColumn = "self." + S.substringAfter(annoColumn, "this.");
            }
            else if(tableName.equals("self")){
            }
            else if(tableName.equals(joiner.getMiddleTable())){
                annoColumn = joiner.getMiddleTableAlias() + "." + S.substringAfter(annoColumn, ".");
            }
            else{
                log.warn("无法识别的条件: {}", annoColumn);
            }
        }
        // 当前表列
        else{
            annoColumn = joiner.getAlias() + "." + annoColumn;
        }
        return annoColumn;
    }
}
