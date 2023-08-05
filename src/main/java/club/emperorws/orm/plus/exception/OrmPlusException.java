package club.emperorws.orm.plus.exception;

/**
 * Type类型处理异常
 *
 * @author: 892638
 * @date: 2023/4/20 11:54
 * @description: TypeException: Type类型处理异常
 */
public class OrmPlusException extends RuntimeException {

    public OrmPlusException() {
        super();
    }

    public OrmPlusException(String message) {
        super(message);
    }

    public OrmPlusException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrmPlusException(Throwable cause) {
        super(cause);
    }
}
