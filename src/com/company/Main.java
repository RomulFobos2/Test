package com.company;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        long endTime;
        long timeWork;
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("setting.properties"));
            Worker.setUrl(properties.getProperty("url"));
            Worker.setUsername(properties.getProperty("username"));
            Worker.setPassword(properties.getProperty("password"));
            Worker.setN(Integer.parseInt(properties.getProperty("N")));

            Connection connection = Worker.connectToDB();
            System.out.println("Подключение к БД - успешно");

            String sqlCommand = "DELETE FROM TEST";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlCommand);

            StringBuilder strForExecute = new StringBuilder();
            strForExecute.append("INSERT TEST(Field) VALUES ");

            for (int i = 1; i <= Worker.getN(); i++) {
                strForExecute.append("(" + i + "), ");
                if(i > 1 && i % 1000 == 0){
                    strForExecute.deleteCharAt(strForExecute.length()-2);
                    statement.executeUpdate(strForExecute.toString());
                    strForExecute.setLength(0);
                    strForExecute.append("INSERT TEST(Field) VALUES ");
                }
            }

            if(!strForExecute.toString().equals("INSERT TEST(Field) VALUES ")){
                strForExecute.deleteCharAt(strForExecute.length()-2);
                statement.executeUpdate(strForExecute.toString());
            }
            endTime = System.currentTimeMillis();
            timeWork = (endTime - startTime) / 1000;

            //System.out.println("Длительность формирования записей в БД = " + timeWork);

            Worker.createXML_1();
            Worker.createXML_2();
            Worker.calculateFieldValue();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Подключения к БД - ошибка");
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        timeWork = (endTime - startTime) / 1000;
        System.out.println("Общее время работы программы = " + timeWork);
    }
}
