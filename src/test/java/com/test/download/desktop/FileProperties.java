package com.test.download.desktop;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class FileProperties {

    private LocalDateTime changedTime;
    private LocalDateTime modifiedTime;
    private long size;
    private String md5;
    private LocalDateTime createdTime;

    @SuppressWarnings("unchecked")
    public FileProperties(Object properties) {
        Map<String, Object> propertiesMap = (Map<String, Object>) properties;
        changedTime = Instant.ofEpochSecond(Long.parseLong(String.valueOf(propertiesMap.get("changed_time")))).atZone(ZoneId.systemDefault()).toLocalDateTime();
        modifiedTime = Instant.ofEpochSecond(Long.parseLong(String.valueOf(propertiesMap.get("modified_time")))).atZone(ZoneId.systemDefault()).toLocalDateTime();
        size = Long.parseLong(String.valueOf(propertiesMap.get("size")));
        md5 = String.valueOf(propertiesMap.get("md5"));
        createdTime = Instant.ofEpochSecond(Long.parseLong(String.valueOf(propertiesMap.get("created_time")))).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public LocalDateTime getChangedTime() {
        return changedTime;
    }

    public LocalDateTime getModifiedTime() {
        return modifiedTime;
    }

    public long getSize() {
        return size;
    }

    public String getMd5() {
        return md5;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return "File Properties: {" +
                "changedTime=" + changedTime +
                ", modifiedTime=" + modifiedTime +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
