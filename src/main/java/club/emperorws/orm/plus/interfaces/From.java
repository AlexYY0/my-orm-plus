package club.emperorws.orm.plus.interfaces;

import club.emperorws.orm.plus.enums.SqlJoin;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * sql的from查询，包括from子查询和join查询
 *
 * @author: 892638
 * @date: 2023/4/10 14:59
 * @description: From: sql的from查询，包括from子查询和join查询
 */
public interface From<Param, Children> extends Serializable {

    default Children from(Consumer<Param> consumer) {
        return from(true, consumer);
    }

    Children from(boolean condition, Consumer<Param> consumer);

    default <T> Children leftJoin(Class<T> joinEntity, Consumer<Param> consumer) {
        return join(true, SqlJoin.LEFT_JOIN, joinEntity, consumer);
    }

    default <T> Children rightJoin(Class<T> joinEntity, Consumer<Param> consumer) {
        return join(true, SqlJoin.RIGHT_JOIN, joinEntity, consumer);
    }

    default <T> Children innerJoin(Class<T> joinEntity, Consumer<Param> consumer) {
        return join(true, SqlJoin.INNER_JOIN, joinEntity, consumer);
    }

    default <T> Children fullJoin(Class<T> joinEntity, Consumer<Param> consumer) {
        return join(true, SqlJoin.FULL_JOIN, joinEntity, consumer);
    }

    <T> Children join(boolean condition, SqlJoin sqlJoin, Class<T> joinEntity, Consumer<Param> consumer);

    default Children leftJoin(Consumer<Param> nestedQueryConsumer, Consumer<Param> onConditionConsumer) {
        return join(true, SqlJoin.LEFT_JOIN, nestedQueryConsumer, onConditionConsumer);
    }

    default Children rightJoin(Consumer<Param> nestedQueryConsumer, Consumer<Param> onConditionConsumer) {
        return join(true, SqlJoin.RIGHT_JOIN, nestedQueryConsumer, onConditionConsumer);
    }

    default Children innerJoin(Consumer<Param> nestedQueryConsumer, Consumer<Param> onConditionConsumer) {
        return join(true, SqlJoin.INNER_JOIN, nestedQueryConsumer, onConditionConsumer);
    }

    default Children fullJoin(Consumer<Param> nestedQueryConsumer, Consumer<Param> onConditionConsumer) {
        return join(true, SqlJoin.FULL_JOIN, nestedQueryConsumer, onConditionConsumer);
    }

    Children join(boolean condition, SqlJoin sqlJoin, Consumer<Param> nestedQueryConsumer, Consumer<Param> onConditionConsumer);

    default Children leftJoin(String joinStr) {
        return join(true, SqlJoin.LEFT_JOIN, joinStr);
    }

    default Children rightJoin(String joinStr) {
        return join(true, SqlJoin.RIGHT_JOIN, joinStr);
    }

    default Children innerJoin(String joinStr) {
        return join(true, SqlJoin.INNER_JOIN, joinStr);
    }

    default Children fullJoin(String joinStr) {
        return join(true, SqlJoin.FULL_JOIN, joinStr);
    }

    Children join(boolean condition, SqlJoin sqlJoin, String joinStr);
}
