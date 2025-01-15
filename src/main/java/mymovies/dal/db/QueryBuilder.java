package mymovies.dal.db;

import mymovies.dal.db.connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryBuilder {
    private String selectClause = "";
    private String fromClause = "";
    private final List<String> whereClauses = new ArrayList<>();
    private final List<Object> parameters = new ArrayList<>();
    private final List<String> joinClauses = new ArrayList<>();
    private String orderByClause = "";
    private Integer top = null;
    private final List<String> unionClauses = new ArrayList<>();
    private final List<String> setClauses = new ArrayList<>();
    private boolean delete = false;
    private boolean update = false;
    private boolean get = false;
    private boolean insert = false;
    private final ArrayList<String> insertColumnsPlaceholders;
    private final DBConnection dbConnection;

    public QueryBuilder() {
        this.dbConnection = new DBConnection();
        insertColumnsPlaceholders = new ArrayList<>();
    }

    public QueryBuilder select(String... columns) {
        if (this.selectClause == null) {
            this.selectClause = "";
        }

        if (columns.length == 0) {
            return this;
        }

        if (columns.length == 1 && columns[0].equals("*")) {
            this.selectClause = "*";
        } else {
            String joinedColumns = String.join(", ", columns);
            if (this.selectClause.isEmpty()) {
                this.selectClause = joinedColumns;
            } else {
                this.selectClause += ", " + joinedColumns;
            }
        }
        return this;
    }

    public QueryBuilder from(String table) {
        this.fromClause = table;
        return this;
    }

    public QueryBuilder table(String table) {
        this.fromClause = table;
        return this;
    }

    public QueryBuilder where(String column, String operator, Object value) {
        whereClauses.add(column + " " + operator + " ?");
        parameters.add(value);
        return this;
    }

    public QueryBuilder whereIn(String column, List<Object> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The values for whereIn() cannot be null or empty.");
        }

        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        whereClauses.add(column + " IN (" + placeholders + ")");
        parameters.addAll(values);
        return this;
    }

    public QueryBuilder join(String table, String condition, String type) {
        if (type.isEmpty() || type.equals(" ")) {
            this.joinClauses.add("JOIN " + table + " ON " + condition);
        } else {
            this.joinClauses.add(type.toUpperCase() + " JOIN " + table + " ON " + condition);
        }
        return this;
    }

    public QueryBuilder innerJoin(String table, String condition) {
        return join(table, condition, "INNER");
    }

    public QueryBuilder leftJoin(String table, String condition) {
        return join(table, condition, "LEFT");
    }

    public QueryBuilder rightJoin(String table, String condition) {
        return join(table, condition, "RIGHT");
    }

    public QueryBuilder union(QueryBuilder otherQuery) {
        this.unionClauses.add(otherQuery.build());
        return this;
    }

    public QueryBuilder orderBy(String column, String direction) {
        this.orderByClause = column + " " + direction.toUpperCase();
        return this;
    }

    public QueryBuilder top(int top) {
        this.top = top;
        return this;
    }

    public QueryBuilder set(String column, Object value) {
        String valueString;

        if (value == null) {
            valueString = "NULL";
        } else if (value instanceof String) {
            valueString = "'" + value + "'";
        } else if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            valueString = "'" + ((LocalDateTime) value).format(formatter) + "'";
        } else {
            valueString = value.toString();
        }

        setClauses.add(column + " = " + valueString);
        return this;
    }

    public QueryBuilder set(String column, Object value, boolean raw) {
        String valueString;

        if (raw && value instanceof String) {
            valueString = value.toString(); // Use raw SQL string
        } else if (value instanceof String) {
            valueString = "'" + value + "'";
        } else if (value instanceof LocalDateTime) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            valueString = "'" + ((LocalDateTime) value).format(formatter) + "'";
        } else {
            valueString = value.toString();
        }

        setClauses.add(column + " = " + valueString);
        return this;
    }

    public QueryBuilder insert(String column, Object value) {
        return insert(new String[]{column}, new Object[]{value});
    }

    public QueryBuilder insert(String[] columns, Object[] values) {
        if (columns.length != values.length) {
            throw new IllegalArgumentException("Columns and values must have the same length.");
        }
        for (int i = 0; i < columns.length; i++) {
            insertColumnsPlaceholders.add(columns[i]);
        }

        for (Object value : values) {
            parameters.add(value);
        }

        return this;
    }

    public String build() {
        if (fromClause.isEmpty()) {
            throw new IllegalStateException("FROM clause is required.");
        }

        StringBuilder query = new StringBuilder();

        if (delete) {
            query.append("DELETE ");

            if (top != null) {
                query.append("TOP ").append(top).append(" ");
            }
            query.append(selectClause).append(" FROM ").append(fromClause);

        } else if (update) {
            query.append("UPDATE ").append(fromClause).append(" SET ").append(String.join(", ", setClauses));
        } else if (get) {
            query.append("SELECT ");

            if (top != null) {
                query.append("TOP ").append(top).append(" ");
            }
            if (selectClause.isEmpty()) {
                query.append("*").append(" FROM ").append(fromClause);
            } else {
                query.append(selectClause).append(" FROM ").append(fromClause);
            }
        } else if (insert) {
            query.append("INSERT INTO ").append(fromClause).append(" (").append(String.join(", ", insertColumnsPlaceholders)).append(") VALUES (")
                    .append(String.join(", ", Collections.nCopies(parameters.size(), "?")))
                    .append(")");

        }

        if (!joinClauses.isEmpty()) {
            query.append(" ").append(String.join(" ", joinClauses));
        }

        if (!whereClauses.isEmpty()) {
            query.append(" WHERE ").append(String.join(" AND ", whereClauses));
        }

        if (!orderByClause.isEmpty()) {
            query.append(" ORDER BY ").append(orderByClause);
        }

        if (!unionClauses.isEmpty()) {
            query.append(" ").append(String.join(" UNION ", unionClauses));
        }

        return query.toString();
    }

    public boolean save() {
        this.get = false;
        this.delete = false;
        this.update = false;
        this.insert = true;

        String sql = build();

        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL INSERT Execution Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            resetFields();
        }
    }

    public ResultSet saveAndReturn() {
        this.get = false;
        this.update = false;
        this.delete = false;
        this.insert = true;

        if (insertColumnsPlaceholders.isEmpty()) {
            throw new IllegalStateException("Insert columns must be defined before calling saveAndReturn.");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(fromClause)
                .append(" (").append(String.join(", ", insertColumnsPlaceholders)).append(") ")
                .append("VALUES (").append(String.join(", ", Collections.nCopies(parameters.size(), "?"))).append("); ")
                .append("SELECT SCOPE_IDENTITY() AS id;");

        System.out.println(sql);

        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.err.println("SQL INSERT Execution Error with SCOPE_IDENTITY(): " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            resetFields();
        }
    }


    public ResultSet get() {
        this.insert = false;
        this.update = false;
        this.delete = false;
        this.get = true;
        String sql = build();

        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < this.parameters.size(); i++) {
                preparedStatement.setObject(i + 1, this.parameters.get(i));
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.err.println("SQL SELECT Execution Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            resetFields();
        }
    }

    public boolean update() {
        this.insert = false;
        this.get = false;
        this.delete = false;
        this.update = true;
        this.set("updated_at", LocalDateTime.now());

        String sql = build();

        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            return preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println("SQL UPDATE Execution Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            resetFields();
        }
    }

    public boolean delete() {
        this.insert = false;
        this.update = false;
        this.get = false;
        this.delete = true;

        String sql = build();

        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            return preparedStatement.execute();
        } catch (SQLException e) {
            System.err.println("SQL DELETE Execution Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            resetFields();
        }
    }

    public void resetFields() {
        selectClause = "";
        fromClause = "";
        whereClauses.clear();
        parameters.clear();
        joinClauses.clear();
        setClauses.clear();
        orderByClause = "";
        top = null;
        unionClauses.clear();
        insertColumnsPlaceholders.clear();
    }
}
