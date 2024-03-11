package one.digitalinnovation.gof.handler;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.annotation.Resource;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  @Resource
  private MessageSource messageSource;

  private HttpHeaders headers(){
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return headers;
  }

  private ResponseError responseError(String message, HttpStatus statusCode){
    ResponseError responseError = new ResponseError();
    responseError.setStatus("error");
    responseError.setError(message);
    responseError.setStatusCode(statusCode.value());

    return responseError;
  }

  @ExceptionHandler({BusinessException.class})
  private ResponseEntity<Object> handleBusinessException(BusinessException e, WebRequest request) {
    ResponseError error = responseError(e.getMessage(),HttpStatus.UNPROCESSABLE_ENTITY);
    return handleExceptionInternal(e, error, headers(), HttpStatus.UNPROCESSABLE_ENTITY, request);
  }

  @ExceptionHandler({CampoObrigatorioException.class})
  private ResponseEntity<Object> handleCampoObrigatorioException(CampoObrigatorioException e, WebRequest request) {
    ResponseError error = responseError(e.getMessage(),HttpStatus.CONFLICT);
    return handleExceptionInternal(e, error, headers(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(Exception.class)
  private ResponseEntity<Object> handleGeneral(Exception e, WebRequest request) {
    String message = messageSource.getMessage("error.server", new Object[]{e.getMessage()}, null);
    ResponseError error = responseError(message,HttpStatus.INTERNAL_SERVER_ERROR);

    return handleExceptionInternal(e, error, headers(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }
}
