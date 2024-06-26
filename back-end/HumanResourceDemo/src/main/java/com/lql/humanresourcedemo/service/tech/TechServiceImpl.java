package com.lql.humanresourcedemo.service.tech;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.tech.TechInfo;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.EmployeeTech_;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications.byTechId;

@Service
@RequiredArgsConstructor
public class TechServiceImpl implements TechService {
    private final EmployeeTechRepository employeeTechRepository;
    private final TechRepository techRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidateService validateService;

    @Override
    public Page<Tech> getAllTech(Pageable pageRequest) {
        return techRepository.findAll(pageRequest);
    }

    @Override
    public TechStackResponse getTechStackByEmployeeId(Long empId) {
        validateService.requireExistsEmployee(empId);

        List<EmployeeTech> tech = employeeTechRepository.findBy(byEmployeeId(empId), p -> p.project(EmployeeTech_.TECH).all());

        return new TechStackResponse(
                empId,
                tech.stream()
                        .map(TechInfo::of)
                        .toList()
        );
    }

    @Override
    @Transactional
    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {
        // check if the employee and all the tech is exists
        validateService.requireExistsEmployee(request.employeeId());
        request.techStacks().stream()
                .map(TechStack::techId)
                .forEach(validateService::requireExistsTech);


        List<EmployeeTech> techList = employeeTechRepository.findBy(byEmployeeId(request.employeeId()), FluentQuery.FetchableFluentQuery::all);

        // if the employee already have that tech then update the corresponding year of experience
        // otherwise create a new record in the employee tech table
        List<EmployeeTech> updated = request.techStacks().stream().map(techStack -> {
            for (var i : techList) {
                if (techStack.techId().equals(i.getTech().getId())) {
                    i.setYearOfExperience(techStack.yearOfExperience());
                    return i;
                }
            }
            return new EmployeeTech(employeeRepository.getReferenceById(request.employeeId()), techRepository.getReferenceById(techStack.techId()), techStack.yearOfExperience());
        }).toList();

        return new TechStackResponse(
                request.employeeId(),
                employeeTechRepository.saveAll(updated).stream()
                        .map(TechInfo::of)
                        .toList()
        );
    }
}
