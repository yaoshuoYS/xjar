package io.xjar;

import io.xjar.key.XKey;
import org.bouncycastle.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

/**
 * JDK内置加密算法的加密器
 *
 * @author Payne 646742615@qq.com
 * 2018/11/22 14:01
 */
public class XJdkEncryptor implements XEncryptor {
    private final String algorithm;

    public XJdkEncryptor(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public void encrypt(XKey key, File src, File dest) throws IOException {
        if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
            throw new IOException("could not make directory: " + dest.getParentFile());
        }
        try (
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest)
        ) {
            encrypt(key, in, out);
        }
    }

    @Override
    public void encrypt(XKey key, InputStream in, OutputStream out) throws IOException {
        CipherInputStream cis = null;
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            byte[] keys = key.getEncryptKey().length <= key.getSize() / 8 ? key.getEncryptKey() : Arrays.copyOf(key.getEncryptKey(), key.getSize() / 8);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys, algorithm), new IvParameterSpec(key.getIvParameter()));
            cis = new CipherInputStream(in, cipher);
            XKit.transfer(cis, out);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            XKit.close(cis);
        }
    }

    @Override
    public InputStream encrypt(XKey key, InputStream in) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            byte[] keys = key.getEncryptKey().length <= key.getSize() / 8 ? key.getEncryptKey() : Arrays.copyOf(key.getEncryptKey(), key.getSize() / 8);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys, algorithm), new IvParameterSpec(key.getIvParameter()));
            return new CipherInputStream(in, cipher);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public OutputStream encrypt(XKey key, OutputStream out) throws IOException {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            byte[] keys = key.getEncryptKey().length <= key.getSize() / 8 ? key.getEncryptKey() : Arrays.copyOf(key.getEncryptKey(), key.getSize() / 8);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys, algorithm), new IvParameterSpec(key.getIvParameter()));
            return new CipherOutputStream(out, cipher);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}