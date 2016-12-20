/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package cn.iterlog.xmimagepicker.TL;

public class TLObject {

    public boolean disableFree = false; // 是否禁止释放
    private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new ThreadLocal<NativeByteBuffer>() {
        @Override
        protected NativeByteBuffer initialValue() {
            return new NativeByteBuffer(true);
        }
    };

    public TLObject() {

    }

    /**
     * 解析数据
     * @param stream
     * @param exception
     */
    public void readParams(AbstractSerializedData stream, boolean exception) {

    }

    /**
     * 序列化对象为流
     * @param stream
     */
    public void serializeToStream(AbstractSerializedData stream) {

    }

    /**
     * 反序列化对象
     * @param stream
     * @param constructor
     * @param exception
     * @return
     */
    public TLObject deserializeResponse(AbstractSerializedData stream, int constructor,
            boolean exception) {
        return null;
    }

    /**
     * 释放资源
     */
    public void freeResources() {

    }

    /**
     * 获取对象大小
     * @return
     */
    public int getObjectSize() {
        NativeByteBuffer byteBuffer = sizeCalculator.get();
        byteBuffer.rewind();
        serializeToStream(sizeCalculator.get());
        return byteBuffer.length();
    }
}
