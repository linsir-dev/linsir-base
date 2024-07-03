package com.linsir.base.core.binding.query.dynamic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linsir.base.core.binding.QueryBuilder;
import com.linsir.base.core.binding.parser.ParserCache;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.constant.Cons;
import com.linsir.base.core.util.S;
import com.linsir.base.core.util.V;
import org.apache.ibatis.jdbc.SQL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author ：linsir
 * @date ：Created in 2022/3/23 13:49
 * @description：动态SQL构建Provider
 * @modified By：
 * @version: 0.0.1
 */
public class DynamicSqlProvider {

    /**
     * 构建动态SQL
     * @param ew
     * @return
     */
    public String buildSqlForList(QueryWrapper ew){
        return buildDynamicSql(null, ew);
    }

    /**
     * 构建动态SQL
     * @param page 分页参数，用于MP分页插件AOP，不可删除
     * @param ew
     * @return
     */
    public <DTO> String buildSqlForListWithPage(Page<?> page, QueryWrapper<DTO> ew){
        return buildDynamicSql(page, ew);
    }

    /**
     * 构建动态SQL
     * @param page 分页参数，用于MP分页插件AOP，不可删除
     * @param ew
     * @return
     */
    private <DTO> String buildDynamicSql(Page<?> page, QueryWrapper<DTO> ew){
        DynamicJoinQueryWrapper wrapper = (DynamicJoinQueryWrapper)ew;
        return new SQL() {{
            if(V.isEmpty(ew.getSqlSelect())){
                SELECT_DISTINCT("self.*");
            }
            else{
                SELECT_DISTINCT(formatSqlSelect(ew.getSqlSelect()));
            }
            FROM(wrapper.getEntityTable()+" self");
            //提取字段，根据查询条件中涉及的表，动态join
            List<AnnoJoiner> annoJoinerList = wrapper.getAnnoJoiners();
            if(V.notEmpty(annoJoinerList)){
                Set<String> tempSet = new HashSet<>();
                StringBuilder sb = new StringBuilder();
                for(AnnoJoiner joiner : annoJoinerList){
                    if(V.notEmpty(joiner.getJoin()) && V.notEmpty(joiner.getOnSegment())){
                        if(joiner.getMiddleTable() != null){
                            sb.setLength(0);
                            sb.append(joiner.getMiddleTable()).append(" ").append(joiner.getMiddleTableAlias()).append(" ON ").append(joiner.getMiddleTableOnSegment());
                            String deletedCol = ParserCache.getDeletedColumn(joiner.getMiddleTable());
                            if(deletedCol != null && S.containsIgnoreCase(joiner.getMiddleTable(), " "+deletedCol) == false){
                                sb.append(" AND ").append(joiner.getMiddleTableAlias()).append(".").append(deletedCol).append(" = ").append(BaseConfig.getActiveFlagValue());
                            }
                            String joinSegment = sb.toString();
                            if(!tempSet.contains(joinSegment)){
                                LEFT_OUTER_JOIN(joinSegment);
                                tempSet.add(joinSegment);
                            }
                        }
                        sb.setLength(0);
                        sb.append(joiner.getJoin()).append(" ").append(joiner.getAlias()).append(" ON ").append(joiner.getOnSegment());
                        String deletedCol = ParserCache.getDeletedColumn(joiner.getJoin());
                        if(deletedCol != null && S.containsIgnoreCase(joiner.getOnSegment(), " "+deletedCol) == false){
                            sb.append(" AND ").append(joiner.getAlias()).append(".").append(deletedCol).append(" = ").append(BaseConfig.getActiveFlagValue());
                        }
                        String joinSegment = sb.toString();
                        if(!tempSet.contains(joinSegment)){
                            LEFT_OUTER_JOIN(joinSegment);
                            tempSet.add(joinSegment);
                        }
                    }
                }
                tempSet = null;
            }
            MergeSegments segments = ew.getExpression();
            if(segments != null){
                String normalSql = segments.getNormal().getSqlSegment();
                if(V.notEmpty(normalSql)){
                    WHERE(formatNormalSql(normalSql));
                    // 动态为主表添加is_deleted=0
                    String isDeletedCol = ParserCache.getDeletedColumn(wrapper.getEntityTable());
                    String isDeletedSection = "self."+ isDeletedCol;
                    if(isDeletedCol != null && QueryBuilder.checkHasColumn(segments.getNormal(), isDeletedSection) == false){
                        WHERE(isDeletedSection+ " = " + BaseConfig.getActiveFlagValue());
                    }
                    if(segments.getOrderBy() != null){
                        String orderBySql = segments.getOrderBy().getSqlSegment();
                        int beginIndex = S.indexOfIgnoreCase(orderBySql,"ORDER BY ");
                        if(beginIndex >= 0){
                            orderBySql = S.substring(orderBySql, beginIndex+"ORDER BY ".length());
                            ORDER_BY(orderBySql);
                        }
                    }
                }
            }
        }}.toString();
    }

    /**
     * 格式化sql select列语句
     * @param sqlSelect
     * @return
     */
    private String formatSqlSelect(String sqlSelect){
        String[] columns = S.split(sqlSelect);
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<columns.length; i++){
            String column = S.removeDuplicateBlank(columns[i]).trim();
            if(i>0){
                sb.append(Cons.SEPARATOR_COMMA);
            }
            sb.append("self."+column);
        }
        return sb.toString();
    }

    /**
     * 格式化where条件的sql
     * @param normalSql
     * @return
     */
    private String formatNormalSql(String normalSql){
        if(normalSql.startsWith("(") && normalSql.endsWith(")")){
            return S.substring(normalSql,1,normalSql.length()-1);
        }
        return normalSql;
    }
}
