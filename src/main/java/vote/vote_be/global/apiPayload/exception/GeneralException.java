package vote.vote_be.global.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vote.vote_be.global.apiPayload.code.BaseErrorCode;
import vote.vote_be.global.apiPayload.code.ErrorReasonDTO;


@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

  private BaseErrorCode code;

  public ErrorReasonDTO getErrorReason() {
    return this.code.getReason();
  }

  public ErrorReasonDTO getErrorReasonHttpStatus(){
    return this.code.getReasonHttpStatus();
  }

}
