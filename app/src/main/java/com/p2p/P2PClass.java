package com.p2p;

import java.io.File;


public class P2PClass {
    private static final String TAG = "P2PClass";

    public static int port = 8087;

    public String path = null;

    class init extends Thread {

        final String CardPath;

        init(String str) {
            this.CardPath = str;
        }

        public void run() {
            P2PClass p2PClass = P2PClass.this;
            p2PClass.path = this.CardPath + "/jpali";
            File file = new File(P2PClass.this.path);
            if (!file.exists()) {
                file.mkdirs();
            }
            P2PClass.port = P2PClass.this.doxstarthttpd("TEST3E63BAAECDAA79BEAA91853490A69F08".getBytes(), this.CardPath.getBytes());
        }
    }

    static {
        System.loadLibrary("p2p");
    }

    public P2PClass(String str) {
        path = str + "/jpali";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        port = doxstarthttpd("TEST3E63BAAECDAA79BEAA91853490A69F08".getBytes(), str.getBytes());
        //Executors.newCachedThreadPool().execute(new init(str));
    }


    private final native void XGFilmCloseFile(long j);

    private final native long XGFilmOpenFile(byte[] bArr);

    private final native int XGFilmReadFile(long j, long j2, int i, byte[] bArr);

    private final native int dosetupload(int i);

    private final native void doxSetP2PPauseUpdate(int i);

    private final native int doxadd(byte[] bArr);

    private final native int doxcheck(byte[] bArr);

    private final native int doxdel(byte[] bArr);

    private final native int doxdelall();

    private final native int doxdownload(byte[] bArr);

    private final native int doxendhttpd();

    private final native String doxgetVersion();

    private final native String doxgethostbynamehook(String str);

    private final native String doxgetlocalAddress();

    private final native String doxgettaskstat(int i);

    private final native int doxpause(byte[] bArr);

    private final native int doxsave();

    private final native int doxsetduration(int i);

    private final native int doxstart(byte[] bArr);

    private final native int doxstarthttpd(byte[] bArr, byte[] bArr2);

    private final native int doxterminate();

    private final native long getdownsize(int i);

    private final native long getfilesize(int i);

    private final native long getlocalfilesize(byte[] bArr);

    private final native int getpercent();

    private final native long getspeed(int i);


    public int P2Pdoxstart(byte[] bArr) {
        return doxstart(bArr);
    }

    public int P2Pdoxdownload(byte[] bArr) {
        return doxdownload(bArr);
    }

    public int P2Pdoxterminate() {
        return doxterminate();
    }

    public int P2Pdosetupload(int i) {
        return dosetupload(i);
    }

    public int P2Pdoxcheck(byte[] bArr) {
        return doxcheck(bArr);
    }

    public int P2Pdoxadd(byte[] bArr) {
        return doxadd(bArr);
    }

    public int P2Pdoxpause(byte[] bArr) {
        return doxpause(bArr);
    }

    public int P2Pdoxdel(byte[] bArr) {
        return doxdel(bArr);
    }

    public int P2PdoxdelAll() {
        return doxdelall();
    }

    public long P2Pgetspeed(int i) {
        return getspeed(i);
    }

    public long P2Pgetdownsize(int i) {
        return getdownsize(i);
    }

    public long P2Pgetfilesize(int i) {
        return getfilesize(i);
    }

    public int P2Pgetpercent() {
        return getpercent();
    }

    public long P2Pgetlocalfilesize(byte[] bArr) {
        return getlocalfilesize(bArr);
    }

    public long P2Pdosetduration(int i) {
        return doxsetduration(i);
    }

    public String getServiceAddress() {
        return doxgethostbynamehook("xx0.github.com");
    }

    public int P2Pdoxstarthttpd(byte[] bArr, byte[] bArr2) {
        return doxstarthttpd(bArr, bArr2);
    }

    public int P2Pdoxsave() {
        return doxsave();
    }

    public int P2Pdoxendhttpd() {
        return doxendhttpd();
    }

    public String getVersion() {
        return doxgetVersion();
    }

    public long xGFilmOpenFile(byte[] bArr) {
        return XGFilmOpenFile(bArr);
    }

    public void xGFilmCloseFile(long j) {
        XGFilmCloseFile(j);
    }

    public int xGFilmReadFile(long j, long j2, int i, byte[] bArr) {
        return XGFilmReadFile(j, j2, i, bArr);
    }

    public void setP2PPauseUpdate(int i) {
        doxSetP2PPauseUpdate(i);
    }

    public String getTouPingUrl() {
        return doxgetlocalAddress();
    }

    public String P2Pdoxgettaskstat(int i) {
        return doxgettaskstat(i);
    }
}