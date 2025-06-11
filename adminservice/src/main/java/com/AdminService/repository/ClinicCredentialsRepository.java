package com.AdminService.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.AdminService.entity.ClinicCredentials;


@Repository
public interface ClinicCredentialsRepository extends MongoRepository<ClinicCredentials, String> {
   
    ClinicCredentials findByUserName(String name);
    ClinicCredentials findByPassword(String password);
    ClinicCredentials findByUserNameAndPassword(String name,String password);
    void deleteByUserName(String name);

}

