package com.clickbait.plugin.services;

import com.clickbait.plugin.dao.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class TabDataService {

        private final JdbcTemplate jdbcTemplate;

        @Autowired
        public TabDataService(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

        List<UserTab> getAllTabs() {
                return jdbcTemplate.query("SELECT * FROM plugin.get_tabs(null)", mapTabsFomDb());
        }

        List<UserTab> getUserTabs(UUID userId) {
                return jdbcTemplate.query("SELECT * FROM plugin.get_tabs(?)", mapTabsFomDb(),
                                new Object[] { userId });
        }

        UserTab getUserTab(UUID userId, int index) {
                return jdbcTemplate.queryForObject("SELECT * FROM plugin.get_tab_data(?, ?)", mapTabsFomDb(),
                                new Object[] { userId, index });
        }

        UUID insertTab(UUID userId, String name, int index) {
                return jdbcTemplate.queryForObject("CALL plugin.insert_tab(?, ?, ?)",
                                (resultSet, i) -> UUID.fromString(resultSet.getString("tad_id")),
                                new Object[] { userId, name, index });
        }

        private RowMapper<UserTab> mapTabsFomDb() {
                return (resultSet, i) -> {
                        String userIdStr = resultSet.getString("user_id");
                        UUID userId = UUID.fromString(userIdStr);
                        String domainIdStr = resultSet.getString("domain_id");
                        UUID domainId = UUID.fromString(domainIdStr);
                        int tabId = resultSet.getInt("index");
                        String name = resultSet.getString("name");
                        return new UserTab(userId, domainId, tabId, name, null);
                };
        }
}
