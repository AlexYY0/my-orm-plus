package club.emperorws.orm.plus;

import club.emperorws.orm.plus.config.MapperProxyBean;
import club.emperorws.orm.plus.entity.Student;
import club.emperorws.orm.plus.mapper.StudentMapper;
import club.emperorws.orm.plus.query.QueryWrapper;
import club.emperorws.orm.plus.toolkit.SqlSourceHelper;
import club.emperorws.orm.plus.toolkit.WrapperHelper;
import club.emperorws.orm.starter.proxy.IMapperProxyBean;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * my-orm的MySQL执行测试
 *
 * @author: EmperorWS
 * @date: 2023/8/5 12:44
 * @description: MySqlOrmTest: my-orm的MySQL执行测试
 */
@Tag("MySQL执行测试@Tag")
@DisplayName("MySQL执行测试@DisplayName")
public class MySqlOrmTest {

    private static final Logger logger = LoggerFactory.getLogger(MySqlOrmTest.class);

    private static IMapperProxyBean mapperProxyBean;

    @BeforeAll
    static void setUp() throws Exception {
        mapperProxyBean = new MapperProxyBean();
        mapperProxyBean.init();
    }

    @DisplayName("MySQL的Select查询测试")
    @Test
    public void mysqlSelectTest() {
        try {
            String sql = "select * from student where name like concat('%',#{keyword},'%')";
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            List<Student> studentList = studentMapper.selectListTest(SqlSourceHelper.createSqlSource(sql), "a");
            studentList.forEach(student -> logger.info(student.toString()));
        } catch (Exception e) {
            logger.error("mysqlSelectTest has an error.", e);
        }
    }

    @DisplayName("MySQL的OrmPlus查询测试")
    @Test
    public void mysqlOrmPlusTest() {
        try {
            QueryWrapper<Student> queryWrapper = WrapperHelper.query(Student.class).like("name", "a");
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            List<Student> studentList = studentMapper.select(queryWrapper);
            studentList.forEach(student -> logger.info(student.toString()));
        } catch (Exception e) {
            logger.error("mysqlOrmPlusTest has an error.", e);
        }
    }

    @DisplayName("MySQL的Insert查询测试")
    @Test
    public void mysqlInsertTest() {
        try {
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            Student student = new Student();
            student.setName("emperorws");
            Integer row = studentMapper.insert(student);
            logger.info(String.valueOf(row));
        } catch (Exception e) {
            logger.error("mysqlInsertTest has an error.", e);
        }
    }

    @DisplayName("MySQL的InsertOrUpdate查询测试")
    @Test
    public void mysqlInsertOrUpdateTest() {
        try {
            StudentMapper studentMapper = mapperProxyBean.getObject(StudentMapper.class);
            Student student = new Student();
            student.setId(778357261887508481L);
            student.setName("emperorwsNewInsert");
            student.setAddress("England");
            Integer row = studentMapper.insertOrUpdate(student);
            logger.info(String.valueOf(row));
        } catch (Exception e) {
            logger.error("mysqlInsertOrUpdateTest has an error.", e);
        }
    }
}
