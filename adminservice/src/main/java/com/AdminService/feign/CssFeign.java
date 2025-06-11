package com.AdminService.feign;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.AdminService.dto.CategoryDto;
import com.AdminService.dto.ServicesDto;
import com.AdminService.dto.SubServicesInfoDto;
import com.AdminService.util.Response;
import com.AdminService.util.ResponseStructure;



@FeignClient(name = "category-services")
public interface CssFeign {

    @PostMapping("/api/v1/category/addCategory")
    ResponseEntity<ResponseStructure<CategoryDto>> addNewCategory(@RequestBody CategoryDto dto);

    @GetMapping("/api/v1/category/getCategories")
    ResponseEntity<ResponseStructure<List<CategoryDto>>> getAllCategory();
    
    @GetMapping("/api/v1/category/getCategory/{categoryId}")
	public ResponseEntity<ResponseStructure<CategoryDto>> 
    getCategoryById(@PathVariable("categoryId") String categoryId) ;

    @DeleteMapping("/api/v1/category/deleteCategory/{categoryId}")
    ResponseEntity<ResponseStructure<CategoryDto>> deleteCategoryById(
            @PathVariable("categoryId")  String categoryId);  // Use string for compatibility

    @PutMapping("/api/v1/category/updateCategory/{categoryId}")
    ResponseEntity<ResponseStructure<CategoryDto>> updateCategory(
            @PathVariable("categoryId") ObjectId categoryId,
            @RequestBody CategoryDto updatedCategory);
    
    
    //SERVICES
    
    @PostMapping("/api/v1/services/addService")
	public ResponseEntity<ResponseStructure<ServicesDto>> addService(@RequestBody ServicesDto dto);
	
	@GetMapping("/api/v1/services/getServices/{categoryId}")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getServiceById(@PathVariable String categoryId);

	@GetMapping("/api/v1/services/getService/{serviceId}")
	public ResponseEntity<ResponseStructure<ServicesDto>> getServiceByServiceId(@PathVariable String serviceId);
	
	@DeleteMapping("/api/v1/services/deleteService/{serviceId}")
	public ResponseEntity<ResponseStructure<String>> deleteService(@PathVariable String serviceId);	

	@PutMapping("/api/v1/services/updateService/{serviceId}")
	public ResponseEntity<ResponseStructure<ServicesDto>> updateByServiceId(@PathVariable String serviceId,
			@RequestBody ServicesDto domainServices);
	
	@GetMapping("/api/v1/services/getAllServices")
	public ResponseEntity<ResponseStructure<List<ServicesDto>>> getAllServices();
	
	
	// SUBSERVICES
	
	@PostMapping("/api/v1/SubServicesInfo/addSubService")
	public ResponseEntity<Response> addSubService(@RequestBody SubServicesInfoDto dto);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServiceByIdCategory/{categoryId}")
	public ResponseEntity<Response> getSubServiceByIdCategory(@PathVariable String categoryId);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServicesByServiceId/{serviceId}")
	public ResponseEntity<Response> getSubServicesByServiceId(@PathVariable String serviceId);
	
	@GetMapping("/api/v1/SubServicesInfo/getSubServiceBySubServiceId/{subServiceId}")
	public ResponseEntity<Response> getSubServiceBySubServiceId(@PathVariable String subServiceId);
	
	@GetMapping("/api/v1/SubServicesInfo/getAllSubServices")
	public ResponseEntity<Response> getAllSubServices();
	
	@PutMapping("/api/v1/SubServicesInfo/updateBySubServiceId/{subServiceId}")
	public ResponseEntity<Response> updateBySubServiceId(@PathVariable String subServiceId,
			@RequestBody SubServicesInfoDto domainServices);
	
	@DeleteMapping("/api/v1/SubServicesInfo/deleteSubService/{subServiceId}")
	public ResponseEntity<Response> deleteSubService(@PathVariable String subServiceId);
	
		
    
}
