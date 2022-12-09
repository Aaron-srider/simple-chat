package fit.wenchao.simplechatparent.model.business;


import fit.wenchao.simplechatparent.proto.IMessageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

interface IDataResp {
    public DataRespBody body();
    public String code();
}


class DataResp implements IDataResp{

    DataRespBody dataRespBody;
    String code;

    public DataResp(DataRespBody dataRespBody, String code) {
        this.dataRespBody = dataRespBody;
        this.code = code;
    }

    @Override
    public DataRespBody body() {
        return dataRespBody;
    }

    @Override
    public String code() {
        return code;
    }
}


class DataRespBody{

}

class EmptyBody extends DataRespBody{

}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp implements IMessageData {
    private String code;
    private String username;
}
