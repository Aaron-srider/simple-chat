package fit.wenchao.simplechatparent.proto;

public class BusinessTypes {
    public static final BusinessType SEND_MSG = new BusinessType("send-msg");
    public static final BusinessType LOGIN = new BusinessType("login");
    public static final BusinessType LOGIN_RESP = new BusinessType("login-resp");
    public static final BusinessType SEND_TEXT_REQ = new BusinessType("send-text-req");
    public static final IBusinessType SEND_MSG_RESP = new BusinessType("send-msg-resp");
    public static final IBusinessType RECV_MSG_RESP = new BusinessType("recv-msg-resp");
    public static final IBusinessType SEND_FILE_REQ = new BusinessType("send-file-req");

    public static final IBusinessType SEND_FILE_RESP = new BusinessType("send-file-resp");

    public static final IBusinessType RECV_FILE_RESP = new BusinessType("recv-file-resp");
    public static final IBusinessType TRANS_FILE_PART_REQ = new BusinessType("trans-file-part-req");
    public static final IBusinessType NEXT_FILE_PART_INSTRUCT = new BusinessType("next file part instruct");
    public static final IBusinessType SEND_FILE_PART_MSG = new BusinessType("send file part msg");
    public static final IBusinessType RECV_FILE_PART_MSG = new BusinessType("recv file part msg");
    public static final IBusinessType RECV_FILE_PART_RESP =  new BusinessType("recv file part resp");

    public static final IBusinessType SEND_FILE_PACKAGE =  new BusinessType("send file package");

    public static final IBusinessType RECV_FILE_PACKAGE_RESP =  new BusinessType("recv file package resp");

    public static final IBusinessType TELL_CLIENT_B_2_FINISH = new BusinessType("tell client b 2 finish");
    public static final IBusinessType FILE_TRANS_REQ = new BusinessType("file transfer request");
    public static final IBusinessType FILE_TRANS_REQ_RESP = new BusinessType("file transfer resp");
}
