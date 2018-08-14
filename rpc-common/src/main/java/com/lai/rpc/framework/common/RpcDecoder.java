package com.lai.rpc.framework.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

/**
 * Created by laiweigeng on 2018/8/13.
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

	//构造函数传入需要反序列化的
    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() < 4) {//缓冲区长度小于头长度
            return;
        }
        in.markReaderIndex();//标记当前readIndex的位置
        int dataLength = in.readInt();//读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
        if (dataLength < 0) {//读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {//读到的消息体长度如果小于传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLength];//将ByteBuf转换为byte[]
        in.readBytes(data);
        Object obj = SerializationUtil.deserialize(data, genericClass);//将data转换成object
        out.add(obj);
    }
}
