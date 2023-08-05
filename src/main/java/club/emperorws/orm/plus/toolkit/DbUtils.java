package club.emperorws.orm.plus.toolkit;

import club.emperorws.orm.annotations.AnnModel;
import club.emperorws.orm.interfaces.GenId;
import club.emperorws.orm.metadata.TableModelFieldInfo;
import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.plus.exception.OrmPlusException;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.starter.session.proxy.SqlSessionFactoryProxyBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * 数据库相关工具类
 *
 * @author EmperorWS
 * @date 2022.11.28 11:37
 **/
public class DbUtils {

    /**
     * 实体对象的主键自动生成
     *
     * @param tableInfo 实体对象的相关信息
     * @param entity    实体对象
     * @param <T>       实体对象泛型
     */
    public static <T> void createEntityId(TableModelInfo tableInfo, T entity) {
        try {
            // 获取注解属性
            TableModelFieldInfo idFieldInfo = tableInfo.getTableModelFieldInfo(tableInfo.getPkName());
            Field idField = idFieldInfo.getField();
            AnnModel.AnnField annotation = idField.getAnnotation(AnnModel.AnnField.class);
            // 判断是否需要主键自动生成
            if (annotation.isPkGen()) {
                // 生成主键值
                Class<? extends GenId> genIdClass = annotation.genId();
                Constructor<? extends GenId> genIdConstructor = genIdClass.getDeclaredConstructor();
                GenId genId = genIdConstructor.newInstance();
                Object id = genId.genId();
                // 获取对象实体的元对象，方便设置主键
                MetaObject metaObject = SqlSessionFactoryProxyBean.me().getConfiguration().newMetaObject(entity);
                // 设置主键
                metaObject.setValue(idFieldInfo.getProperty(), id);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new OrmPlusException(e);
        }
    }
}
