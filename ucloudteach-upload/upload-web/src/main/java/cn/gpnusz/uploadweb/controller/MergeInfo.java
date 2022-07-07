package cn.gpnusz.uploadweb.controller;

import java.io.Serializable;

/**
 * @author h0ss
 * @description
 * @date 2022/4/12 - 20:01
 */
public class MergeInfo implements Serializable {
    private static Long serialVersionUID = 1351063126163421L;

    private String filename;

    private String type;

    private String hash;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
