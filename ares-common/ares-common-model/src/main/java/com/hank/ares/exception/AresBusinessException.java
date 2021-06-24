package com.hank.ares.exception;

import lombok.Getter;

@Getter
public class AresBusinessException extends RuntimeException {

    private String resCode;

    private String resMsg;

    private String bizName;

    private String eng;

    private int type;// 0-业务系统, 1-后台

    /**
     * 是否使用枚举类 默认使用
     */
    private boolean useRespMsgEnums = true;


    public AresBusinessException(Throwable cause, String bizName) {
        super(cause);
        this.bizName = bizName;
    }

    /**
     * SzBusinessException
     *
     * @param resCode
     * @param resMsg
     */
    public AresBusinessException(String bizName, String resCode, String resMsg) {
        super(resMsg);
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.bizName = bizName;
    }

    /**
     * SzBusinessException
     *
     * @param resCode
     * @param resMsg
     */
    public AresBusinessException(String bizName, String resCode, String resMsg, int type) {
        super(resMsg);
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.bizName = bizName;
        this.type = type;
    }

    public AresBusinessException(String bizName, String resCode, String resMsg, String eng) {
        super(resMsg);
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.bizName = bizName;
        this.eng = eng;
    }

    /**
     * 用于区分业务系统和后台异常信息结构
     *
     * @param type 0-业务系统, 1-后台
     */
    public AresBusinessException(int type, String resCode, String resMsg) {
        super(resMsg);
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.bizName = "";
        this.type = type;
    }

    public AresBusinessException(String resCode, String resMsg, boolean useRespMsgEnums) {
        super(resMsg);
        this.resCode = resCode;
        this.resMsg = resMsg;
        this.useRespMsgEnums = useRespMsgEnums;
    }

}
