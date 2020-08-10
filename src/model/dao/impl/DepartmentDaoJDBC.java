package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection connection;

    public DepartmentDaoJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String sql = 
                "INSERT INTO department (Name) " +
                "VALUES (?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, obj.getName());
            statement.executeUpdate();
            result = statement.getGeneratedKeys();
            if (result.next()) {
                obj.setId(result.getInt(1));
            }
            else {
                throw new DbException("Unexpected error! No rows affected!");                
            }                
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(result);
            DB.closeStatement(statement);
        }
    }

    @Override
    public void update(Department obj) {
        PreparedStatement statement = null;
        try {
            String sql =
                "UPDATE department " +
                "   SET Name = ? " +
                " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setString(1, obj.getName());
            statement.setInt(2, obj.getId());            
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(statement);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement statement = null;
        try {
            String sql =
                "DELETE FROM department " +
                " WHERE Id = ? ";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("foreign key")) {
                throw new DbException("Unable to delete! This department has reference in other tables!");
            }
            
            throw new DbException(e.getMessage());                
        } finally {
            DB.closeStatement(statement);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement statement = null;
        ResultSet result = null;
        Department department = null;
        try {
            String sql = "SELECT * FROM department WHERE Id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                department = instantiateDepartment(result);                
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(result);
            DB.closeStatement(statement);
        }

        return department;
    }

    private Department instantiateDepartment(ResultSet result) throws SQLException {
        Department department = new Department();
        department.setId(result.getInt("Id"));
        department.setName(result.getString("Name"));
        return department;
    }

    @Override
    public List<Department> findAll() {
        List<Department> departments = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            String sql = "SELECT * FROM department ORDER BY Name";
            statement = connection.prepareStatement(sql);
            result = statement.executeQuery();
            if (result.next()) {
                departments = new ArrayList<>();
                do {
                    departments.add(instantiateDepartment(result));
                } while (result.next());
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(result);
            DB.closeStatement(statement);
        }
        
        return departments;
    }

}
