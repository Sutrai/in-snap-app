package com.oous.imageservice.dao.impl;

import com.oous.imageservice.dao.ImageDao;
import com.oous.imageservice.domain.response.exception.MediaNotFoundException;
import com.oous.imageservice.dto.Image;
import com.oous.imageservice.dto.ImageRowMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class ImageDaoImpl extends JdbcDaoSupport implements ImageDao {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @PostConstruct
    public void initialize(){
        setDataSource(dataSource);
    }

    public void saveImage(Image image) {
        jdbcTemplate.update("INSERT INTO images (image_hash, account_id, title, description, type, width, height, size, link)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", image.getImageHash(), image.getAccountId(), image.getTitle(),
                image.getDescription(), image.getType(), image.getWidth(), image.getHeight(), image.getSize(), image.getLink()
        );
    }

    @Override
    public Image getImage(String imageHash) {
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM images WHERE image_hash = ?", new ImageRowMapper(), imageHash);
        } catch (EmptyResultDataAccessException e) {
            throw new MediaNotFoundException();
        }

    }

    @Override
    public boolean deleteImage(String imageHash, long accountId) {
        return jdbcTemplate.update("DELETE FROM images WHERE image_hash = ? AND account_id = ?", imageHash, accountId) > 0;
    }

    @Override
    public long getAccountIdByImageHash(String imageHash) {
        try{
            return jdbcTemplate.queryForObject("SELECT account_id WHERE image_hash = ?",
                    Long.class, imageHash);
        } catch (EmptyResultDataAccessException e){
            throw new MediaNotFoundException();
        }

    }
}
