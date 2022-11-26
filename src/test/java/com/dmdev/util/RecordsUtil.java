package com.dmdev.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RecordsUtil
{
    @SneakyThrows
    public int getRowsNumber()
    {
        try (var connection = ConnectionPool.get();
             var preparedStatement =
                 connection.prepareStatement("SELECT COUNT(*) FROM users"))
        {
            var resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    @SneakyThrows
    public int getMaxId()
    {
        try (var connection = ConnectionPool.get();
             var preparedStatement =
                 connection.prepareStatement("SELECT max(id) FROM users"))
        {
            var resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
