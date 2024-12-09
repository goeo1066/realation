package com.github.goeo1066.realation;

import com.github.goeo1066.realation.core.DatabasePrompt;
import com.github.goeo1066.realation.core.DatabasePromptFactory;
import com.github.goeo1066.realation.core.TableInfo;
import com.github.goeo1066.realation.core.filter.WhereCreator;
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
    public ApplicationRunner applicationRunner(NamedParameterJdbcTemplate jdbcTemplate) {
        return args -> {
            DatabasePromptFactory factory = new DatabasePromptFactory();
            DatabasePrompt<PersonInfo, Integer> prompt = factory.create(PersonInfo.class, jdbcTemplate);
            PersonInfo personInfo = prompt.select(1);
            System.out.println(personInfo.toString());
//            TableInfo<PersonInfo> tableInfo = TableInfo.retrieveFromEntity(PersonInfo.class);
//            PersonInfo personInfo1 = WhereCreator.WhereCreatorCondition(tableInfo);
//            System.out.println(personInfo1.getName());

            prompt.selectBy((column, clause) -> {
                clause.where()
                        .eq(column.getName(), "goeo").and()
                        .in(column.getAge(), 10, 20, 30)
                ;

            });
        };
    }
}
