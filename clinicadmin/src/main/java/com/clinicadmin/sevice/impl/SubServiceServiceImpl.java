package com.clinicadmin.sevice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.ResponseStructure;
import com.clinicadmin.dto.SubServicesDto;
import com.clinicadmin.exceptions.FeignClientException;
import com.clinicadmin.feignclient.ServiceFeignClient;
import com.clinicadmin.service.SubServiceService;

@Service
public class SubServiceServiceImpl implements SubServiceService {

    @Autowired
    private ServiceFeignClient feignClient;

    @Override
    public ResponseEntity<ResponseStructure<SubServicesDto>> addService(String subServiceId, SubServicesDto dto) {
        try {
            ResponseEntity<ResponseStructure<SubServicesDto>> response = feignClient.addService(subServiceId,dto);
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        } catch (FeignClientException ex) {
            return buildErrorResponse(ex.getMessage(), ex.getStatusCode());
        } catch (Exception e) {
            return buildErrorResponse( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServiceByIdCategory(String categoryId) {
        try {
            ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = feignClient.getSubServiceByIdCategory(categoryId);
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        } catch (FeignClientException ex) {
            return buildErrorResponseList( ex.getMessage(), ex.getStatusCode());
        } catch (Exception e) {
            return buildErrorResponseList( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public 	ResponseEntity<ResponseStructure<List<SubServicesDto>>> getSubServicesByServiceId(String serviceId) {
        try {
        	ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = feignClient.getSubServicesByServiceId(serviceId);
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        }  catch (FeignClientException ex) {
            return buildErrorResponseList(ex.getMessage(), ex.getStatusCode());
        }
         catch (Exception e) {
            return buildErrorResponseList( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
@Override
   public  ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceByServiceId(String subServiceId){
	
	 try {
		 ResponseEntity<ResponseStructure<SubServicesDto>> response = feignClient.getSubServiceByServiceId(subServiceId);
         return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

     } catch (FeignClientException ex) {
         return buildErrorResponse(ex.getMessage(), ex.getStatusCode());
     } catch (Exception e) {
         return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
     }
	   
   }
    @Override
    public ResponseEntity<ResponseStructure<SubServicesDto>> deleteSubService(String hospitalId,String subServiceId) {
        try {
            ResponseEntity<ResponseStructure<SubServicesDto>> response = feignClient.deleteSubService(hospitalId,subServiceId);
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        } catch (FeignClientException ex) {
            return buildErrorResponse( ex.getMessage(), ex.getStatusCode());
        } catch (Exception e) {
            return buildErrorResponse( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Override
    public ResponseEntity<ResponseStructure<SubServicesDto>> updateBySubServiceId(String hospitalId,String serviceId,SubServicesDto domainServices) {
        try {
            ResponseEntity<ResponseStructure<SubServicesDto>> response = feignClient.updateBySubServiceId(hospitalId,serviceId, domainServices);
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        } catch (FeignClientException ex) {
            return buildErrorResponse(  ex.getMessage(), ex.getStatusCode());
        } catch (Exception e) {
            return buildErrorResponse( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
	@Override
	public ResponseEntity<ResponseStructure<SubServicesDto>> getSubServiceByServiceId(String hospitalId,
			String subServiceId) {
		  try {
	            ResponseEntity<ResponseStructure<SubServicesDto>> response = feignClient.getSubServiceByServiceId(hospitalId, subServiceId);
	          
	            return ResponseEntity.status(HttpStatus.OK).body(response.getBody());

	        } catch (FeignClientException ex) {
	            return buildErrorResponse(  ex.getMessage(), ex.getStatusCode());
	        } catch (Exception e) {
	            return buildErrorResponse( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
	        }
	}
    @Override
    public ResponseEntity<ResponseStructure<List<SubServicesDto>>> getAllSubServices() {
        try {
            ResponseEntity<ResponseStructure<List<SubServicesDto>>> response = feignClient.getAllSubServices();
            return ResponseEntity.status(response.getBody().getStatusCode()).body(response.getBody());

        } catch (FeignClientException ex) {
            return buildErrorResponseList( ex.getMessage(), ex.getStatusCode());
        } catch (Exception e) {
            return buildErrorResponseList( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    // === Helper methods ===

    private ResponseEntity<ResponseStructure<SubServicesDto>> buildErrorResponse(String message, int statusCode) {
        ResponseStructure<SubServicesDto> errorResponse = ResponseStructure.<SubServicesDto>builder()
                .data(null)
                .message(extractCleanMessage(message))
                .httpStatus(HttpStatus.valueOf(statusCode))
                .statusCode(statusCode)
                .build();
        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    private ResponseEntity<ResponseStructure<List<SubServicesDto>>> buildErrorResponseList(String message, int statusCode) {
        ResponseStructure<List<SubServicesDto>> errorResponse = ResponseStructure.<List<SubServicesDto>>builder()
                .data(null)  // <-- changed from null to empty list
                .message(extractCleanMessage(message))
                .httpStatus(HttpStatus.valueOf(statusCode))
                .statusCode(statusCode)
                .build();
        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    private String extractCleanMessage(String rawMessage) {
        // Try to extract the "message" value from JSON string if included
        try {
            int msgStart = rawMessage.indexOf("\"message\":\"");
            if (msgStart != -1) {
                int start = msgStart + 10;
                int end = rawMessage.indexOf("\"", start);
                return rawMessage.substring(start, end);
            }
        } catch (Exception ignored) {}
        return rawMessage; // fallback if clean extraction fails
    }


}
