package cn.yoha.config;

import java.nio.charset.Charset;

/**
 * JT808协议参数
 */
public class JT808Constant {
    //默认字符集为GBK
    public static final Charset DEFAULT_CHARSET = Charset.forName("GBK");

    //消息分隔符
    public static final byte PKG_DELIMITER = 0x7e;

    // 终端应答
    public static final short TERNIMAL_RESP_COMMON_ = 0x0001; //通用应答

    // 终端消息分类
    public static final short TERNIMAL_MSG_HEARTBEAT = 0x0002; //心跳
    public static final short TERNIMAL_MSG_REGISTER = 0x0100; //注册
    public static final short TERNIMAL_MSG_LOGOUT = 0x0003;//注销
    public static final short TERNIMAL_MSG_AUTH = 0x0102;//鉴权
    public static final short TERNIMAL_MSG_LOCATION = 0x0200;//位置


    //服务器应答
    public static final short SERVER_RESP_COMMON = (short) 0x8001;//通用应答
    public static final short SERVER_RESP_REGISTER = (short) 0x8100;//注册应答
}
