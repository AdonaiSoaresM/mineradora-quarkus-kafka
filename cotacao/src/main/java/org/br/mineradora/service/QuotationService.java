package org.br.mineradora.service;

import org.br.mineradora.client.CurrencyPriceClient;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.br.mineradora.dto.QuotationDTO;
import org.br.mineradora.entity.QuotationEntity;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    private final Logger LOG = LoggerFactory.getLogger(QuotationService.class);

    public void getCurrencyPrice() throws IOException, InterruptedException {

        var currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");
        if(currencyPriceInfo == null) return;

        if(updateCurrentInfoPrice(currencyPriceInfo)){
            var usd_brl = currencyPriceInfo.getUSDBRL();
            if(usd_brl == null) return;
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(usd_brl.getBid()))
                    .date(new Date())
                    .build());
        }

    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {
        var usd_brl = currencyPriceInfo.getUSDBRL();
        if(usd_brl == null) return false;
        BigDecimal currentPrice = new BigDecimal(usd_brl.getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();
        if(quotationList.isEmpty()){

            saveQuotation(currencyPriceInfo);
            updatePrice = true;

        } else {

            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size() -1);

            if(currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()){
                System.out.println("Current: " + currentPrice.floatValue());
                System.out.println("Last: " + lastDollarPrice.getCurrencyPrice().floatValue());
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }

        return updatePrice;

    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo){

        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        var bid = new BigDecimal(currencyInfo.getUSDBRL().getBid());
        quotation.setCurrencyPrice(bid);
        quotation.setPctChange(currencyInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }



}
