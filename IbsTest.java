package org.ibs;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class IbsTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        // Подключаемся к базе данных H2 в файловом режиме
        connection = DriverManager.getConnection("jdbc:h2:tcp://localhost:9092/mem:testdb", "user", "pass");
    }

    @Test
    @DisplayName("Запрос для получения таблицы со списком")
    void testSelectFoodItems() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Выполняем запрос для получения всех записей из таблицы FOOD
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD");

            // Проверяем наличие записей в таблице
            assertTrue(rs.next(), "Таблица FOOD не должна быть пустой");

            // Выводим результаты в консоль для наглядности (опционально)
            do {
                System.out.println("FOOD_ID: " + rs.getInt("FOOD_ID") +
                        ", FOOD_NAME: " + rs.getString("FOOD_NAME") +
                        ", FOOD_TYPE: " + rs.getString("FOOD_TYPE") +
                        ", FOOD_EXOTIC: " + rs.getBoolean("FOOD_EXOTIC"));
            } while (rs.next());
        }
    }

    @Test
    @DisplayName("Добавление нового товара")
    void testAddFoodItem() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Добавляем новый товар
            int rowsAffected = stmt.executeUpdate("INSERT INTO FOOD (FOOD_ID, FOOD_NAME, FOOD_TYPE, FOOD_EXOTIC) " +
                    "VALUES (5, 'Банан', 'FRUIT', FALSE)");
            assertEquals(1, rowsAffected, "Должна быть добавлена одна строка");

            System.out.println("Товар Банан добавлен в таблицу.");

            // Проверяем наличие товара в таблице
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD WHERE FOOD_ID = 5");
            assertTrue(rs.next(), "Товар с FOOD_ID = 5 должен существовать");
            assertEquals("Банан", rs.getString("FOOD_NAME"));
            assertEquals("FRUIT", rs.getString("FOOD_TYPE"));
            assertFalse(rs.getBoolean("FOOD_EXOTIC"));

            System.out.println("Товар Банан успешно проверен.");
        }
    }

    @Test
    @DisplayName("Удаление товара")
    void testDeleteFoodItem() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Удаляем товар
            int rowsAffected = stmt.executeUpdate("DELETE FROM FOOD WHERE FOOD_ID = 5");
            assertEquals(1, rowsAffected, "Должна быть удалена одна строка");

            System.out.println("Товар Банан удален из таблицы.");

            // Проверяем, что товар был удален
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD WHERE FOOD_ID = 5");
            assertFalse(rs.next(), "Товар с FOOD_ID = 5 не должен существовать");

            System.out.println("Удаление товара Банан успешно проверено.");
        }
    }

    @Test
    @DisplayName("Проверка текущего состояния таблицы")
    void testCheckTableState() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Выполняем запрос для получения всех записей из таблицы FOOD
            ResultSet rs = stmt.executeQuery("SELECT * FROM FOOD");

            // Выводим результаты в консоль для наглядности (опционально)
            while (rs.next()) {
                System.out.println("FOOD_ID: " + rs.getInt("FOOD_ID") +
                        ", FOOD_NAME: " + rs.getString("FOOD_NAME") +
                        ", FOOD_TYPE: " + rs.getString("FOOD_TYPE") +
                        ", FOOD_EXOTIC: " + rs.getBoolean("FOOD_EXOTIC"));
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с базой данных закрыто.");
        }
    }
}
