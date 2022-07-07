package cn.gpnusz.liveservice.util.token.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
