package io.pivotal.pal.tracker;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class JdbcTimeEntryRepository implements TimeEntryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO time_entries (project_id, user_id, date, hours) " +
                            "VALUES (?, ?, ?, ?)",
                    RETURN_GENERATED_KEYS
            );

            statement.setLong(1, timeEntry.getProjectId());
            statement.setLong(2, timeEntry.getUserId());
            statement.setDate(3, Date.valueOf(timeEntry.getDate()));
            statement.setInt(4, timeEntry.getHours());

            return statement;
        }, generatedKeyHolder);

        return find(generatedKeyHolder.getKey().longValue());
    }

    @Override
    public TimeEntry find(Long id) {
        return jdbcTemplate.query(
                "SELECT id, project_id, user_id, date, hours FROM time_entries WHERE id = ?",
                new Object[]{id},
                extractor);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        jdbcTemplate.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        return find(id);
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}

//package io.pivotal.pal.tracker;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.List;
//
//public class JdbcTimeEntryRepository implements TimeEntryRepository {
//    private JdbcTemplate jdbcTemplate;
//
//    public JdbcTimeEntryRepository(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//    @Override
//    public TimeEntry create(TimeEntry timeEntry) {
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        jdbcTemplate.update(new PreparedStatementCreator() {
//                                @Override
//                                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                                    String sql = "INSERT INTO time_entries (project_id, user_id, date, hours) VALUES ?, ?, ?, ?";
//                                    return buildPreparedStatement(timeEntry, sql, con);
//                                }
//                            }, keyHolder
//
//        );
//        return find(keyHolder.getKeyAs(Long.class));
//    }
//
//    @Override
//    public TimeEntry find(Long id) {
//        return jdbcTemplate.queryForObject("SELECT * FROM time_entries WHERE user_id = " + id.toString(), rowMapper);
//    }
//
//    private RowMapper<TimeEntry> rowMapper = new RowMapper<TimeEntry>() {
//        @Override
//        public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
//            return new TimeEntry(rs.getLong("id"), rs.getLong("project_id"), rs.getLong("user_id"), rs.getDate("date").toLocalDate(), rs.getInt("hours"));
//        }
//    };
//
//    @Override
//    public List<TimeEntry> list() {
//        return jdbcTemplate.query("SELECT * FROM time_entries", rowMapper);
//    }
//
//    @Override
//    public TimeEntry update(Long id, TimeEntry timeEntry) {
//        jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                String sql = "UPDATE time_entries SET project_id = ?, user_id = ?, date = ?, hours = ? WHERE id = " + id.toString();
//                return buildPreparedStatement(timeEntry, sql, con);
//            }
//        });
//        return find(id);
//    }
//
//    private PreparedStatement buildPreparedStatement(TimeEntry timeEntry, String sql, Connection con) throws SQLException {
//        PreparedStatement preparedStatement = con.prepareStatement(sql);
//
//        preparedStatement.setLong(1, timeEntry.getProjectId());
//        preparedStatement.setLong(2, timeEntry.getUserId());
//        preparedStatement.setDate(3, Date.valueOf(timeEntry.getDate()));
//        preparedStatement.setInt(4, timeEntry.getHours());
//        return preparedStatement;
//
//    }
//
//    @Override
//    public void delete(Long id) {
//        jdbcTemplate.execute("DELETE FROM time_entries WHERE id = " + id.toString());
//    }
//}
