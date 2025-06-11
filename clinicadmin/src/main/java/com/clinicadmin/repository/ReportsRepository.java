package com.clinicadmin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.clinicadmin.entity.Reports;
import com.clinicadmin.entity.ReportsList;

public interface ReportsRepository extends MongoRepository<ReportsList, String> {
	
    Reports findTopByOrderByReportsListReportIdDesc();
    


}
