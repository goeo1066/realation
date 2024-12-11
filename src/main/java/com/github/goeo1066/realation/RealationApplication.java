package com.github.goeo1066.realation;

import com.github.goeo1066.realation.core.DatabasePrompt;
import com.github.goeo1066.realation.core.DatabasePromptFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@SpringBootApplication
public class RealationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RealationApplication.class, args);
    }

    @Bean
    public DatabasePromptFactory databasePromptFactory(NamedParameterJdbcTemplate jdbcTemplate) {
        return new DatabasePromptFactory(jdbcTemplate);
    }

    @Bean
    public ApplicationRunner applicationRunner(DatabasePromptFactory factory) {
        return args -> {
            DatabasePrompt<PersonInfo, Integer> prompt = factory.create(PersonInfo.class);

            for (PersonInfo personInfo : prompt.selectBy((column, clause) -> {
                clause.where().in(column.getName(), "Name B", "Name A")
                        .and().ne(column.getIdx(), 1)
                        .and().in(column.getAge(), 70)
                        .orderBy().desc(column.getIdx()).asc(column.getName());
            })) {
                System.out.println(personInfo.toString());
            }

            prompt.selectBy((column, clause) -> {
                clause.where().eq(column.getName(), "Name C")
                        .orderBy().desc(column.getIdx());
            }).forEach(System.out::println);
        };
    }
}
