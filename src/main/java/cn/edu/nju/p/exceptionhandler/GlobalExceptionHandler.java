package cn.edu.nju.p.exceptionhandler;

import cn.edu.nju.p.baseresult.BaseResult;
import cn.edu.nju.p.enums.ErrorCode;
import cn.edu.nju.p.exception.DateNotOrderedException;
import cn.edu.nju.p.exception.StockNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

/**
 * controller exception handler
 */
@ControllerAdvice
@Component
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(DateNotOrderedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult handleDateNotOrdered(DateNotOrderedException de) {
        String message = de.getMessage();
        return new BaseResult(ErrorCode.DATE_NOT_ORDERED.getErrorCode(), message);
    }

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResult handleDateParseException(DateTimeParseException de) {
        String message = de.getMessage();
        return new BaseResult(ErrorCode.DATE_PARSE_ERROR.getErrorCode(), message);
    }

    @ExceptionHandler(StockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResult handleStockNotFound(StockNotFoundException se) {
        String message = se.getMessage();
        return new BaseResult(ErrorCode.STOCK_NOT_FOUND.getErrorCode(), message);
    }

}