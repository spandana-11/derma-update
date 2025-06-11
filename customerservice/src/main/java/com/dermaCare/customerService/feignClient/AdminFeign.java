package com.dermaCare.customerService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dermaCare.customerService.util.Response;

@FeignClient(value = "adminservice" )
public interface AdminFeign {
	 @GetMapping("/admin/getClinicById/{clinicId}")
	    public Response getClinicById(@PathVariable String clinicId) ;

}
