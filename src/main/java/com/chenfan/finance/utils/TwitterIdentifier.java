package com.chenfan.finance.utils;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;

import java.util.Random;

/**
 * @Author Wen.Xiao
 * @Description //
 * @Date 2021/5/12  18:27
 * @Version 1.0
 */
public class TwitterIdentifier {

    public static TwitterIdentifier twitterIdentifier=new TwitterIdentifier();

    private static String PREFIX="1476";
    //@Override
    public Long general() {
        return next();
    }

    //@Override
    public String generalIdentifier(String prefix) {
        return prefix+PREFIX + next();

    }

    //@Override
    public String generalIdentifier() {
        return generalIdentifier("");
    }

    private static final long EPOCH = 1264953600000L;

    private static final int BITS_F0 = 1;
    private static final int BITS_F1 = 2;
    private static final int BITS_F2 = 3;

    private static final int SHIFT_F1 = BITS_F0;
    private static final int SHIFT_F2 = (BITS_F0 + BITS_F1);
    private static final int SHIFT_F3 = (BITS_F0 + BITS_F1 + BITS_F2);

    private static final int MASK_F0 = (1 << BITS_F0) - 1;
    private static final int MASK_F1 = (1 << BITS_F1) - 1;
    private static final int MASK_F2 = (1 << BITS_F2) - 1;

    /**
     * 机器ID
     */
    private long machine;
    /**
     * 进程ID
     */
    private long pid;
    /**
     * 序列号
     */
    private long sequence;
    /**
     * 最新的时间戳
     */
    private long latestTs;

    public TwitterIdentifier() {
        this(new Random().nextLong(), new Random().nextLong());
    }

    private TwitterIdentifier(long machine, long pid) {
        this.machine = (machine & MASK_F2) << SHIFT_F2;
        this.pid = (pid & MASK_F1) << SHIFT_F1;
    }

    private synchronized long next() {
        long timestamp = currentMs();
        if (timestamp < latestTs) {
            throw new RuntimeException("older timestamp.");
        }
        if (latestTs == timestamp) {
            sequence = (sequence + 1) & MASK_F0;
            if (sequence == 0) {
                timestamp = nextMs(latestTs);
            }
        } else {
            sequence = 0;
        }
        long id = ((timestamp - EPOCH) << SHIFT_F3) | machine | pid | sequence;
        latestTs = timestamp;
        return id;
    }

    private long currentMs() {
        return System.currentTimeMillis();
    }

    private long nextMs(long latestTs) {
        long timestamp = currentMs();
        while (timestamp <= latestTs) {
            timestamp = currentMs();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        System.out.println(twitterIdentifier.next());;
    }
}
