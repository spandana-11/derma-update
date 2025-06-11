package com.clinicadmin.sevice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.clinicadmin.dto.ReportsDTO;
import com.clinicadmin.dto.ReportsDtoList;
import com.clinicadmin.dto.Response;
import com.clinicadmin.entity.Reports;
import com.clinicadmin.entity.ReportsList;
import com.clinicadmin.repository.ReportsRepository;
import com.clinicadmin.service.ReportsService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Base64;

@Service
public class ReportsServiceImpl implements ReportsService {

	@Autowired
	private ReportsRepository reportsRepository;

	@Override
	public Response saveReports(ReportsDtoList dto) {
		String newReportId = generateNextReportId();
		ReportsList reportsList = new ReportsList();
		List<Reports> reports = new ArrayList<>();
for(ReportsDTO d:dto.getReportsList() ) {
		byte[] decodedFile = Base64.getDecoder().decode(d.getReportFile());
		Reports report = Reports.builder().reportId(newReportId).bookingId(d.getBookingId())
				.reportName(d.getReportName()).reportDate(d.getReportDate()).reportStatus(d.getReportStatus())
				.reportType(d.getReportType()).customerMobileNumber(d.getCustomerMobileNumber())
				.reportFile(decodedFile).build();
		reports.add(report);}
reportsList.setReportsList(reports);
ReportsList saved = reportsRepository.save(reportsList);
		return Response.builder().success(true).data(saved).message("Report uploaded successfully")
				.status(HttpStatus.CREATED.value()).build();
	}

	@Override
	public Response getAllReports() {
		Response res = new Response();
		try {
		List<ReportsList> reportList = reportsRepository.findAll();
		List<ReportsList> toDTO=new ObjectMapper().convertValue(reportList,new TypeReference<List<ReportsList>>(){});
		if( toDTO != null && !toDTO.isEmpty()) {
		res.setStatus(200);
		res.setMessage("records fetched successfully");
		res.setData(toDTO);}
		else {
			res.setStatus(404);
			res.setMessage("records not found");
			res.setData(null);
		}}catch(Exception e) {
			res.setStatus(500);
			res.setMessage(e.getMessage());
			res.setData(null);
		}return res;}
	
	
	public String generateNextReportId() {
		Reports last = reportsRepository.findTopByOrderByReportsListReportIdDesc();
		int lastNum = 0;
		if (last != null) {
			lastNum = Integer.parseInt(last.getReportId().replaceAll("[^0-9]", ""));
		}
		return String.format("R%03d", lastNum + 1);
	}
}
