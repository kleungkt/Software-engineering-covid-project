package comp3111.covid;

/**
 * Error for date input
 */
public class DateIncorrectException extends Exception {
    /**
     * error code type
     */
    enum ErrCode {LOWER_THAN_MIN, LARGER_THAN_INT}

    private final ErrCode errCode;

    /**
     * constructor
     * @param errCode error code
     */
    public DateIncorrectException(ErrCode errCode) {
        super();
        this.errCode = errCode;
    }

    /**
     * get error code
     * @return error code
     */
    public ErrCode getErrCode() {
        return errCode;
    }
}
