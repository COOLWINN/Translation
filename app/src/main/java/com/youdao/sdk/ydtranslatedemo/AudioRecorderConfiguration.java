package com.youdao.sdk.ydtranslatedemo;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Handler;

public class AudioRecorderConfiguration {

    public static final int[] SAMPLE_RATES = {44100, 22050, 11025, 8000};
    public static final boolean RECORDING_UNCOMPRESSED = true;
    public static final boolean RECORDING_COMPRESSED = false;

    private ExtAudioRecorder.RecorderListener listener;
    private boolean uncompressed;
    private  int timerInterval;
    private int rate;
    private int source;
    private int channelConfig;
    private int format;
    private Handler handler;

    /**
     * 创建一个默认的配置 <br />
     * <ul>
     *     <li>uncompressed = false</li>
     *     <li>timerInterval = 120</li>
     *     <li>rate = 8000</li>
     *     <li>source = {@link MediaRecorder.AudioSource#MIC}</li>
     *     <li>channelConfig = {@link AudioFormat#CHANNEL_CONFIGURATION_MONO}</li>
     *     <li>format = {@link AudioFormat#ENCODING_PCM_16BIT}</li>
     * </ul>
     *
     */
    public static AudioRecorderConfiguration createDefaule(){
        return new Builder().builder();
    }


    public ExtAudioRecorder.RecorderListener getRecorderListener(){
        return listener;
    }

    public boolean isUncompressed(){
        return uncompressed;
    }

    public int getTimerInterval(){
        return timerInterval;
    }

    public int getRate(){
        return rate;
    }


    public int getSource(){
        return source;
    }

    public int getFormat(){
        return format;
    }

    public Handler getHandler(){
        return handler;
    }

    public int getChannelConfig(){
        return channelConfig;
    }

    private AudioRecorderConfiguration(Builder builder){
        this.listener = builder.listener;
        this.uncompressed = builder.uncompressed;
        this.timerInterval = builder.timerInterval;
        this.rate = builder.rate;
        this.source = builder.source;
        this.format = builder.format;
        this.handler = builder.handler;
        this.channelConfig = builder.channelConfig;
    }

    public static class Builder{
        private ExtAudioRecorder.RecorderListener listener;
        private boolean uncompressed;
        private int timerInterval = 120;
        private int rate = SAMPLE_RATES[3];
        private int source = MediaRecorder.AudioSource.MIC;
        private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private int format = AudioFormat.ENCODING_PCM_16BIT;
        private Handler handler;

        /** 声道设置 */
        public Builder ChannelConfig(int channelConfig){
            this.channelConfig = channelConfig;
            return this;
        }
        /** 录音失败的监听 */
        public Builder recorderListener(ExtAudioRecorder.RecorderListener listener){
            this.listener = listener;
            return this;
        }
        /** 是否压缩录音 */
        public Builder uncompressed(boolean uncompressed){
            this.uncompressed = uncompressed;
            return this;
        }
        /** 周期的时间间隔 */
        public Builder timerInterval(int timeInterval){
            timerInterval = timeInterval;
            return this;
        }
        /** 采样率 */
        public Builder rate(int rate){
            this.rate = rate;
            return this;
        }
        /** 音频源 */
        public Builder source(int source){
            this.source = source;
            return this;
        }
        /** 编码制式和采样大小 */
        public Builder format(int format){
            this.format = format;
            return this;
        }
        /** 返回what是振幅值 1-13  */
        public Builder handler(Handler handler){
            this.handler = handler;
            return this;
        }

        public AudioRecorderConfiguration builder(){
            return new AudioRecorderConfiguration(this);
        }
    }

}
