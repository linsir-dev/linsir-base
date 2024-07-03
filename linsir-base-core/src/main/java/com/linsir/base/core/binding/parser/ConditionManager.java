package com.linsir.base.core.binding.parser;


import com.linsir.base.core.binding.binder.BaseBinder;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：linsir
 * @date ：Created in 2022/3/24 10:43
 * @description：条件表达式的管理器
 * @modified By：
 * @version: 0.0.1
 */
@Slf4j
public class ConditionManager extends BaseConditionManager{

    /**
     * 附加条件到binder
     * @param condition
     * @param binder
     * @throws Exception
     */
    public static <T> void parseConditions(String condition, BaseBinder<T> binder) {
        List<Expression> expressionList = getExpressionList(condition);
        if(V.isEmpty(expressionList)){
            log.warn("无法解析注解条件: {} ", condition);
            return;
        }
        // 解析中间表关联
        String tableName = extractMiddleTableName(expressionList);
        if(tableName != null){
            List<Expression> additionalExpress = parseMiddleTable(binder, expressionList, tableName);
            if(V.notEmpty(additionalExpress)){
                parseDirectRelation(binder, additionalExpress);
            }
        }
        else{
            parseDirectRelation(binder, expressionList);
        }
    }

    /**
     * 解析直接关联
     * @param binder
     * @param expressionList
     * @param <T>
     */
    private static <T> void parseDirectRelation(BaseBinder<T> binder, List<Expression> expressionList) {
        // 解析直接关联
        for(Expression operator : expressionList){
            if(operator instanceof EqualsTo){
                EqualsTo express = (EqualsTo)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    String entityColumn = removeLeftAlias(express.getRightExpression().toString());
                    // xx=this.yy，翻转
                    if(isCurrentObjColumn(express.getRightExpression().toString()) && !isCurrentObjColumn(express.getLeftExpression().toString())){
                        binder.joinOn(entityColumn, annoColumn);
                    }
                    else{
                        binder.joinOn(annoColumn, entityColumn);
                    }
                }
                else{
                    binder.andEQ(annoColumn, express.getRightExpression().toString());
                    binder.additionalCondition(annoColumn + " = " + express.getRightExpression().toString());
                }
                continue;
            }
            if(operator instanceof NotEqualsTo){
                NotEqualsTo express = (NotEqualsTo)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    binder.andApply(annoColumn + " != " + express.getRightExpression().toString());
                }
                else{
                    binder.andNE(annoColumn, express.getRightExpression().toString());
                }
            }
            else if(operator instanceof GreaterThan){
                GreaterThan express = (GreaterThan)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    binder.andApply(annoColumn + " > "+ express.getRightExpression().toString());
                }
                else{
                    binder.andGT(annoColumn, express.getRightExpression().toString());
                }
            }
            else if(operator instanceof GreaterThanEquals){
                GreaterThanEquals express = (GreaterThanEquals)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    binder.andApply(annoColumn + " >= "+ express.getRightExpression().toString());
                }
                else{
                    binder.andGE(annoColumn, express.getRightExpression().toString());
                }
            }
            else if(operator instanceof MinorThan){
                MinorThan express = (MinorThan)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    binder.andApply(annoColumn + " < "+ express.getRightExpression().toString());
                }
                else{
                    binder.andLT(annoColumn, express.getRightExpression().toString());
                }
            }
            else if(operator instanceof MinorThanEquals){
                MinorThanEquals express = (MinorThanEquals)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.getRightExpression() instanceof Column){
                    binder.andApply(annoColumn + " <= "+ express.getRightExpression().toString());
                }
                else{
                    binder.andLE(annoColumn, express.getRightExpression().toString());
                }
            }
            else if(operator instanceof IsNullExpression){
                IsNullExpression express = (IsNullExpression)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.isNot() == false){
                    binder.andIsNull(annoColumn);
                }
                else{
                    binder.andIsNotNull(annoColumn);
                }
            }
            else if(operator instanceof InExpression){
                InExpression express = (InExpression)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.isNot() == false){
                    binder.andApply(annoColumn + " IN " + express.getRightItemsList().toString());
                }
                else{
                    binder.andApply(annoColumn + " NOT IN " + express.getRightItemsList().toString());
                }
            }
            else if(operator instanceof Between){
                Between express = (Between)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                if(express.isNot() == false){
                    binder.andBetween(annoColumn, express.getBetweenExpressionStart().toString(), express.getBetweenExpressionEnd().toString());
                }
                else{
                    binder.andNotBetween(annoColumn, express.getBetweenExpressionStart().toString(), express.getBetweenExpressionEnd().toString());
                }
            }
            else if(operator instanceof LikeExpression){
                LikeExpression express = (LikeExpression)operator;
                String annoColumn = removeLeftAlias(express.getLeftExpression().toString());
                String value = express.getRightExpression().toString();
                if(express.isNot() == false){
                    binder.andLike(annoColumn, value);
                }
                else{
                    binder.andNotLike(annoColumn, value);
                }
            }
            else{
                log.warn("不支持的条件: "+operator.toString());
                continue;
            }
            binder.additionalCondition(operator.toString().replaceAll("^\\w+\\.", ""));
        }
    }

    /**
     * 解析中间表
     * @param expressionList
     * @return
     */
    private static <T> List<Expression> parseMiddleTable(BaseBinder<T> binder, List<Expression> expressionList, String tableName) {
        // 单一条件不是中间表条件
        if(expressionList.size() <= 1){
            return expressionList;
        }
        // 非中间表的附加条件表达式
        List<Expression> additionalExpressions = new ArrayList<>();
        // 提取到表
        MiddleTable middleTable = new MiddleTable(tableName);
        // VO与Entity的关联字段
        String annoObjectForeignKey = null, referencedEntityPrimaryKey = null;
        for(Expression operator : expressionList){
            if(operator instanceof EqualsTo){
                EqualsTo express = (EqualsTo)operator;
                // 均为列
                if(express.getLeftExpression() instanceof Column && express.getRightExpression() instanceof Column){
                    String leftColumn = express.getLeftExpression().toString();
                    String rightColumn = express.getRightExpression().toString();
                    // 如果右侧为中间表字段，如: this.departmentId=department.id
                    if(rightColumn.startsWith(tableName+".")){
                        // 绑定左手边连接列
                        String leftHandColumn = removeLeftAlias(leftColumn);
                        // 对应中间表的关联字段
                        String middleTableCol = removeLeftAlias(rightColumn);
                        // this. 开头的vo对象字段
                        if(isCurrentObjColumn(leftColumn)){
                            // 识别到vo对象的属性 departmentId
                            annoObjectForeignKey = leftHandColumn;
                            middleTable.connectTrunkObj(middleTableCol, leftHandColumn);
                        }
                        else{
                            // 注解关联的entity主键
                            referencedEntityPrimaryKey = leftHandColumn;
                            middleTable.connectBranchObj(middleTableCol, leftHandColumn);
                        }
                        binder.joinOn(annoObjectForeignKey, referencedEntityPrimaryKey);
                    }
                    // 如果左侧为中间表字段，如: Department.orgId=id  (entity=Organization)
                    else if(leftColumn.startsWith(tableName+".")){
                        // 绑定右手边连接列
                        String rightHandColumn = removeLeftAlias(rightColumn);
                        // 对应中间表的关联字段
                        String middleTableCol = removeLeftAlias(leftColumn);
                        if(isCurrentObjColumn(rightColumn)){
                            // 识别到vo对象的属性 departmentId
                            annoObjectForeignKey = rightHandColumn;
                            middleTable.connectTrunkObj(middleTableCol, rightHandColumn);
                        }
                        else{
                            referencedEntityPrimaryKey = rightHandColumn;
                            middleTable.connectBranchObj(middleTableCol, rightHandColumn);
                        }
                        binder.joinOn(annoObjectForeignKey, referencedEntityPrimaryKey);
                    }
                    // this.xxx=yy
                    else{
                        additionalExpressions.add(express);
                    }
                }
                else{ // equals附加条件，暂只支持列在左侧，如 department.level=1
                    String leftExpression = express.getLeftExpression().toString();
                    if(leftExpression != null){
                        if(leftExpression.startsWith(tableName+".")){
                            middleTable.addAdditionalCondition(removeLeftAlias(operator.toString()));
                        }
                        else{
                            additionalExpressions.add(express);
                        }
                    }
                }
            }
            else{
                String leftExpression = null;
                if(operator instanceof NotEqualsTo){
                    NotEqualsTo express = (NotEqualsTo)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof GreaterThan){
                    GreaterThan express = (GreaterThan)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof GreaterThanEquals){
                    GreaterThanEquals express = (GreaterThanEquals)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof MinorThan){
                    MinorThan express = (MinorThan)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof MinorThanEquals){
                    MinorThanEquals express = (MinorThanEquals)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof IsNullExpression){
                    IsNullExpression express = (IsNullExpression)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof InExpression){
                    InExpression express = (InExpression)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof Between){
                    Between express = (Between)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                else if(operator instanceof LikeExpression){
                    LikeExpression express = (LikeExpression)operator;
                    leftExpression = express.getLeftExpression().toString();
                }
                if(leftExpression != null){
                    if(leftExpression.startsWith(tableName+".")){
                        middleTable.addAdditionalCondition(removeLeftAlias(operator.toString()));
                    }
                    else{
                        additionalExpressions.add(operator);
                    }
                }
            }
        }
        binder.withMiddleTable(middleTable);
        return additionalExpressions;
    }

    /**
     * 注解列
     * @return
     */
    private static String removeLeftAlias(String annoColumn){
        if(annoColumn.contains(".")){
            annoColumn = S.substringAfter(annoColumn, ".");
        }
        return annoColumn;
    }
}
