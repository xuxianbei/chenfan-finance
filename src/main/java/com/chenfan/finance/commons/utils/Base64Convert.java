package com.chenfan.finance.commons.utils;

import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: guangyu
 * @Date: 2019/1/18 12:34
 * @Description:
 */
public class Base64Convert {

    /**
     * @see: 图片IO转为base64
     * @param: [imgStream]
     * @author: xuguangyu@thechenfan.com
     * @date: 2019/1/18 12:36
     */
    public static String getImageStr(InputStream is) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] by = new byte[1024];
        int len = -1;
        while ((len = is.read(by)) != -1) {
            data.write(by, 0, len);
        }
        // 关闭流
        is.close();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }
}
