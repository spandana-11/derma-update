package com.clinicadmin.sevice.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.CustomerRatingDomain;
import com.clinicadmin.dto.RatingsDTO;
import com.clinicadmin.dto.Response;
import com.clinicadmin.feignclient.CustomerServiceFeignClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RatingCalculationService {

    @Autowired
    private CustomerServiceFeignClient customerServiceFeignClient;

    public Response calculateAverageRating(String hospitalId, String doctorId) {
        Response response = new Response();

        try {
            ResponseEntity<Response> responseEntity =
                    customerServiceFeignClient.getRatingInfo(hospitalId, doctorId);

            Object ratings = responseEntity.getBody().getData();

            List<CustomerRatingDomain> allRatings = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            // Handle both single object and list
            if (ratings instanceof List) {
                allRatings = mapper.convertValue(ratings, new TypeReference<List<CustomerRatingDomain>>() {});
            } else {
                CustomerRatingDomain single = mapper.convertValue(ratings, CustomerRatingDomain.class);
                allRatings.add(single);
            }

            // Filter ratings by hospitalId and doctorId
            List<CustomerRatingDomain> matchedRatings = allRatings.stream()
                    .filter(r -> r.getHospitalId().equals(hospitalId) && r.getDoctorId().equals(doctorId))
                    .toList();

            if (matchedRatings.isEmpty()) {
                response.setSuccess(false);
                response.setStatus(404);
                response.setMessage("DoctorId or HospitalId is not matched");
                return response;
            }

            double totalDoctorRating = matchedRatings.stream().mapToDouble(CustomerRatingDomain::getDoctorRating).sum();
            double totalHospitalRating = matchedRatings.stream().mapToDouble(CustomerRatingDomain::getHospitalRating).sum();

            double avgDoctorRating = totalDoctorRating / matchedRatings.size();
            double avgHospitalRating = totalHospitalRating / matchedRatings.size();

            RatingsDTO data = new RatingsDTO();
            data.setDoctorId(doctorId);
            data.setHospitalId(hospitalId);
            data.setOverallDoctorRating(avgDoctorRating);
            data.setOverallHospitalRating(avgHospitalRating);
            data.setComments(matchedRatings);

            response.setSuccess(true);
            response.setData(data);
            response.setMessage("Data fetched successfully");
            response.setStatus(200);
            return response;

        } catch (Exception e) {
            response.setSuccess(false);
            response.setData(null);
            response.setMessage("Exception occurred during getting ratings: " + e.getMessage());
            response.setStatus(500);
            return response;
        }
    }
}
