package com.example.eurekaclient.dto;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ImageRowMapper implements RowMapper<Image> {
    @Override
    public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
        Image image = new Image();
        image.setImageHash(rs.getString("image_hash"));
        image.setAccountId(rs.getLong("account_id"));
        image.setTitle(rs.getString("title"));
        image.setDescription(rs.getString("description"));
        image.setType(rs.getString("type"));
        image.setWidth(rs.getInt("width"));
        image.setHeight(rs.getInt("height"));
        image.setSize(rs.getLong("size"));
        image.setViews(rs.getInt("views"));
        image.setLink(rs.getString("link"));
        return image;
    }
}
