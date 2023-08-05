package club.emperorws.orm.plus.segments;

import club.emperorws.orm.plus.ISqlSegment;
import club.emperorws.orm.plus.enums.SqlJoin;
import club.emperorws.orm.plus.enums.SqlKeyword;
import club.emperorws.orm.plus.enums.WrapperKeyword;

import java.util.function.Predicate;

/**
 * 片段匹配判断
 *
 * @author EmperorWS
 * @date 2022.09.16 23:03
 **/
public enum MatchSegment {
    GROUP_BY(i -> i == SqlKeyword.GROUP_BY),
    ORDER_BY(i -> i == SqlKeyword.ORDER_BY),
    NOT(i -> i == SqlKeyword.NOT),
    AND(i -> i == SqlKeyword.AND),
    OR(i -> i == SqlKeyword.OR),
    AND_OR(i -> i == SqlKeyword.AND || i == SqlKeyword.OR),
    EXISTS(i -> i == SqlKeyword.EXISTS),
    HAVING(i -> i == SqlKeyword.HAVING),
    APPLY(i -> i == WrapperKeyword.APPLY),
    JOIN(i -> i == SqlJoin.FROM || i == SqlJoin.LEFT_JOIN || i == SqlJoin.RIGHT_JOIN || i == SqlJoin.INNER_JOIN || i == SqlJoin.FULL_JOIN);

    private final Predicate<ISqlSegment> predicate;

    MatchSegment(Predicate<ISqlSegment> predicate) {
        this.predicate = predicate;
    }

    public Predicate<ISqlSegment> getPredicate() {
        return predicate;
    }

    public boolean match(ISqlSegment segment) {
        return getPredicate().test(segment);
    }
}
