package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.dto.model.tech.EmployeeTechDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class EmployeeTechRepositoryTest {

    @Autowired
    private EmployeeTechRepository employeeTechRepository;


    @Test
    public void getTechByEmployeeTest() {
        List<EmployeeTechDTO> techDetails = employeeTechRepository.findTechInfoByEmployeeId(3L);

        techDetails.forEach(tech -> System.out.printf("%s %s %s\n", tech.getTechId(), tech.getTechName(), tech.getYearOfExperience()));
    }

}