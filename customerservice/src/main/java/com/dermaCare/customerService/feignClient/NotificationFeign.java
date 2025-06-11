package com.dermaCare.customerService.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.dermaCare.customerService.dto.BookingResponse;
import com.dermaCare.customerService.util.ResBody;




@FeignClient(value = "NOTIFICATIONSERVICE")
public interface NotificationFeign {

	@PostMapping("/api/notificationservice/createnotification")
	public ResponseEntity<ResBody<String>> notification(@RequestBody BookingResponse bookingRequset);
}
