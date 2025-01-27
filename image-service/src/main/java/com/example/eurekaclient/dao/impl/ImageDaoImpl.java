package com.example.eurekaclient.dao.impl;

import com.example.eurekaclient.dao.ImageDao;
import com.example.eurekaclient.domain.response.Exception.MediaNotFoundException;
import com.example.eurekaclient.domain.response.Exception.UserDoesNotOwnMediaException;
import com.example.eurekaclient.dto.Image;
import com.example.eurekaclient.dto.ImageRowMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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
        String sql = "INSERT INTO images (image_hash, account_id, title, description, type, width, height, size, link)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                image.getImageHash(),
                image.getAccountId(),
                image.getTitle(),
                image.getDescription(),
                image.getType(),
                image.getWidth(),
                image.getHeight(),
                image.getSize(),
                image.getLink()
        );
    }

    @Override
    public Image getImage(String imageHash){
        return jdbcTemplate.queryForObject("SELECT * FROM images where image_hash = ?",
                new Object[]{imageHash}, new ImageRowMapper());
    }

    @Override
    public boolean deleteImage(String imageHash, long accountId) {
        Long ownerId = jdbcTemplate.query(
                "SELECT account_id FROM images WHERE image_hash = ?",
                rs -> rs.next() ? rs.getLong(1) : null, imageHash);

        if (ownerId == null) throw new MediaNotFoundException();
        if (!ownerId.equals(accountId)) throw new UserDoesNotOwnMediaException();

        return jdbcTemplate.update("DELETE FROM images WHERE image_hash = ? AND account_id = ?", imageHash, accountId) > 0;
    }

    @Override
    public long getAccountIdByImageHash(String imageHash) {
        return jdbcTemplate.queryForObject("SELECT account_id WHERE image_hash = ?", Long.class, imageHash);
    }
}
