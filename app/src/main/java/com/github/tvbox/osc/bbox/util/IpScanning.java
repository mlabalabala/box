package com.github.tvbox.osc.bbox.util;

import com.github.tvbox.osc.bbox.bean.IpScanningVo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IpScanning {

    private List<IpScanningVo> ipScanningVos = new ArrayList<>();

    /**
     * 线程数
     */

    int corePoolSize = 5;

    /**
     * 最大线程数
     */
    int maximumPoolSize = 10;

    private ThreadPoolExecutor threadPool =
            new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 3,
                    TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>(10),
                    new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 通过IP扫描对应网段中可以使用的网段
     * @param ips 输入的IP
     */
    public List<IpScanningVo> search(String ips, boolean all) {
        //清空缓存
        ipScanningVos.clear();
        int divisionIp = ips.lastIndexOf(".");
        String substring = ips.substring(0, divisionIp + 1);

        String last = ips.substring(divisionIp + 1);
        int end = Integer.parseInt(last) + 30; //搜索范围不是全部，缩小范围
        end = end > 255 ? 255 : end;
        if (all) end = 255;
        int total = end;

        //扫描对应网段中的所有Ip
        BlockingQueue<IpScanningVo> queue = new ArrayBlockingQueue<>(total);
        for (int i = 1; i < total; i++) {
            String iip = substring + i;
            threadPool.submit(new PingIp(iip, queue));
        }
        threadPool.shutdown();
        //判断当前线程是否全部执行完成,防止没有执行完返回结果
        while (!threadPool.isTerminated()){}
        ipScanningVos = new ArrayList<>(queue);
        return ipScanningVos;
    }

    private static class PingIp implements Runnable {
        private String ip;
        private Queue<IpScanningVo> array;

        public PingIp(String ip, Queue<IpScanningVo> array) {
            this.array = array;
            this.ip = ip;
        }

        @Override
        public void run() {
            //遍历IP地址
            InetAddress addip = null;
            try {
                addip = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            //检查设备是否在线，其中1000ms指定的是超时时间
            // 当返回值是true时，说明host是可用的，false则不可。
            boolean status = false;
            try {
                if (addip != null) {
                    status = addip.isReachable(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (status) {
                IpScanningVo ipScanning = new IpScanningVo(addip.getHostName(), ip);
                LOG.i("IP地址为:" + ip + "\t\t设备名称为: " + addip.getHostName() + "\t\t是否可用: " + (status ? "可用" : "不可用"));
                array.add(ipScanning);
            }
        }
    }
}
