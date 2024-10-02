package org.br.mineradora.scheduler;

import io.quarkus.scheduler.Scheduled;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.service.QuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.io.IOException;

@ApplicationScoped
public class QuotationScheduler {

    private final Logger LOG = LoggerFactory.getLogger(QuotationScheduler.class);

    @Inject
    QuotationService quotationService;

    @Transactional
    @Scheduled(every = "30s", identity="task-job")
    void schedule() throws IOException, InterruptedException {
        LOG.info("-- Executando scheduler --");
        quotationService.getCurrencyPrice();
    }
}
