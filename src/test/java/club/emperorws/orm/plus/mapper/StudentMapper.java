package club.emperorws.orm.plus.mapper;

import club.emperorws.orm.annotations.Param;
import club.emperorws.orm.annotations.Select;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.plus.BaseMapper;
import club.emperorws.orm.plus.entity.Student;

import java.util.List;

/**
 * StudentMapper
 *
 * @author: EmperorWS
 * @date: 2023/5/29 14:21
 * @description: StudentMapper: StudentMapper
 */
public interface StudentMapper extends BaseMapper<Student> {

    @Select(resultType = "club.emperorws.orm.plus.entity.Student")
    List<Student> selectListTest(SqlSource sqlSource, @Param("keyword") String keyword);
}
