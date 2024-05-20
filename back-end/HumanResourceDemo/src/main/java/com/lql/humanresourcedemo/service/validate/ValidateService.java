package com.lql.humanresourcedemo.service.validate;

public interface ValidateService {


     void requireExistsEmployee(Long employeeId);
     void requireExistsProject(Long projectId);
     void requireExistsTech(Long techId);
}
