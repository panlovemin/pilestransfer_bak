package com.piles.common.business.impl;

import com.google.common.primitives.Bytes;
import com.piles.common.business.IPushBusiness;
import com.piles.common.entity.BasePushCallBackResponse;
import com.piles.common.entity.type.ECommandCode;
import com.piles.common.entity.type.EPushResponseCode;
import com.piles.common.util.BytesUtil;
import com.piles.common.util.CRC16Util;
import com.piles.common.util.ChannelMap;
import com.piles.common.util.ChannelResponseCallBackMap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PushBusinessImpl implements IPushBusiness {
    @Override
    public boolean push(byte[] msg, String pileNo, BasePushCallBackResponse basePushRequest, ECommandCode commandCode) {
        //获取连接channel 获取不到无法推送
        Channel channel=ChannelMap.getChannel(pileNo);

        if (null!=channel){
            ChannelResponseCallBackMap.add(channel,Integer.parseInt(  basePushRequest.getSerial()),basePushRequest);
            //拼接报文
            byte[] start=new byte[]{0x68};
            byte[] command=new byte[]{(byte)commandCode.getCode()};
            byte[] serial= BytesUtil.intToBytes( Integer.parseInt(  basePushRequest.getSerial()) );
            byte[] length=BytesUtil.intToBytes( msg.length );
            byte[] temp= Bytes.concat( command,serial,length,msg );
            byte[] crc= BytesUtil.intToBytes( CRC16Util.getCRC( temp ));

            byte[] writeMsg=Bytes.concat( start,temp,crc );

            ChannelFuture channelFuture=channel.writeAndFlush(writeMsg);
            channelFuture.addListener( new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    basePushRequest.setCode( EPushResponseCode.WRITE_OK );
                    basePushRequest.getCountDownLatch().countDown();
                }
            } );
            return true;
        }else {
            log.error(pileNo+"无法获取到长连接,请检查充电桩连接状态");
            return false;
        }
    }
}