package fit.wenchao.simplechatparent.proto;


import fit.wenchao.simplechatparent.utils.ByteUtils;

public class ProtoMagics {
    public static final ProtoMagic defaultMagic = new ProtoMagic(ByteUtils.ofBytes(1,2,3,4));
}
