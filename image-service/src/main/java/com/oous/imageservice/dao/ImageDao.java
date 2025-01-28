package com.oous.imageservice.dao;

import com.oous.imageservice.dto.Image;

public interface ImageDao {

    void saveImage(Image image);
    Image getImage(String imageHash);
    boolean deleteImage(String imageHash, long accountId);
    long getAccountIdByImageHash(String imageHash);
}
