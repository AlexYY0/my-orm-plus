package club.emperorws.orm.plus.config;

import club.emperorws.orm.interfaces.GenId;
import pro.mickey.MickeySnowflake;

import java.util.Random;

/**
 * 雪花ID生成策略
 *
 * @author: EmperorWS
 * @date: 2023/8/5 20:25
 * @description: SnowFlakeIdGen: 雪花ID生成策略
 */
public class SnowFlakeIdGen implements GenId<Long> {
    @Override
    public Long genId() {
        return SnowFlakeId.nextLong();
    }
}
