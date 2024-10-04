package org.br.mineradora.service;

import org.br.mineradora.dto.OpportunityDTO;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.ByteArrayInputStream;
import java.util.List;

@ApplicationScoped
public interface ReportService {

    ByteArrayInputStream generateCSVOpportunityReport();

    List<OpportunityDTO> getOpportunitiesData();

}
