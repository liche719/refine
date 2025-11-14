package com.achobeta.types.common;

public class Constants {

    public final static String SPLIT = ",";

    public final static String TRACE_ID = "traceId";

    // 验证码缓存键
    public static final String REDIS_EMAIL_KEY = "user:email:code:";
    public static final String REDIS_EMAIL_RECORD_KEY = "user:email:record:";

    public static final String USER_TOKEN_KEY_PREFIX = "user:token:";
    public static final String USER_ID_KEY_PREFIX = "user:id:";

    public static final String QUESTION_GENERATION_ID_KEY = "question:generationId:";
    // 题目缓存最大时间: 1天
    public static final Long QUESTION_GENERATION_EXPIRED_SECONDS = 60 * 60 * 24L;


    // 验证码发送间隔
    public static final Long SEND_INTERVAL_SECONDS = 60L;

    // 用户token有效期：2天
    public static final Long USER_TOKEN_EXPIRED_SECONDS = 60 * 60 * 24 * 2L;


    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[\\da-zA-Z~!@#$%^&*_]{8,18}$";



}
